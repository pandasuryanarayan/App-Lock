package com.example.security

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ui.screens.LockOverlayActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

object AppLockStateManager {

    private const val TAG = "AppLockStateManager"

    private val lockedPackages = ConcurrentHashMap.newKeySet<String>()
    private val temporarilyUnlockedPackages = ConcurrentHashMap<String, Long>()

    private val _isProtectionActive = MutableStateFlow(false)
    val isProtectionActive = _isProtectionActive.asStateFlow()

    @Volatile
    var lastForegroundPackage: String? = null

    @Volatile
    var isOverlayShowing: Boolean = false

    // Grace period in milliseconds (0 means immediate re-lock when leaving app)
    var relockGracePeriodMs: Long = 0L

    fun setProtectionActive(active: Boolean) {
        _isProtectionActive.value = active
    }

    fun updateLockedPackages(packages: Collection<String>) {
        lockedPackages.clear()
        lockedPackages.addAll(packages)
        Log.d(TAG, "Updated locked packages count: ${lockedPackages.size}")
    }

    fun addLockedPackage(pkg: String) {
        lockedPackages.add(pkg)
    }

    fun removeLockedPackage(pkg: String) {
        lockedPackages.remove(pkg)
        temporarilyUnlockedPackages.remove(pkg)
    }

    fun isPackageLocked(pkg: String): Boolean {
        return lockedPackages.contains(pkg)
    }

    fun markTemporarilyUnlocked(packageName: String) {
        temporarilyUnlockedPackages[packageName] = System.currentTimeMillis()
        Log.d(TAG, "Temporarily unlocked: $packageName")
    }

    fun relockAll() {
        temporarilyUnlockedPackages.clear()
        Log.d(TAG, "Relocked all temporary unlocked packages")
    }

    fun shouldIntercept(packageName: String): Boolean {
        if (!lockedPackages.contains(packageName)) {
            return false
        }

        val unlockTime = temporarilyUnlockedPackages[packageName]
        if (unlockTime != null) {
            if (relockGracePeriodMs <= 0L) {
                // If grace period is 0, we still allow the app while it remains the foreground package
                return false
            }
            val elapsed = System.currentTimeMillis() - unlockTime
            if (elapsed < relockGracePeriodMs) {
                return false
            } else {
                temporarilyUnlockedPackages.remove(packageName)
            }
        }

        return true
    }

    fun checkAndIntercept(context: Context, foregroundPackage: String, appName: String? = null) {
        if (foregroundPackage == context.packageName) {
            // It's App Locker itself
            lastForegroundPackage = foregroundPackage
            return
        }

        // If user navigated away from a previously unlocked app to a different app, re-lock the old app
        val previous = lastForegroundPackage
        if (previous != null && previous != foregroundPackage && previous != context.packageName) {
            if (relockGracePeriodMs <= 0L) {
                temporarilyUnlockedPackages.remove(previous)
            }
        }
        lastForegroundPackage = foregroundPackage

        if (shouldIntercept(foregroundPackage)) {
            if (!isOverlayShowing) {
                launchLockOverlay(context, foregroundPackage, appName)
            }
        }
    }

    fun launchLockOverlay(context: Context, packageName: String, appName: String? = null) {
        try {
            val intent = Intent(context, LockOverlayActivity::class.java).apply {
                putExtra(LockOverlayActivity.EXTRA_PACKAGE_NAME, packageName)
                putExtra(LockOverlayActivity.EXTRA_APP_NAME, appName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch LockOverlayActivity", e)
        }
    }
}
