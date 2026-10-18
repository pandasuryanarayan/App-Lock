package com.example.security

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ui.screens.LockOverlayActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Central, in-memory state for the app lock. No database access happens here,
 * so every lock decision is a couple of hash lookups.
 *
 * Two engines can drive interception:
 *  - [AppLockAccessibilityService]: event-driven and dormant until the OS reports
 *    a window change. This is the low-battery path and never polls.
 *  - [AppLockForegroundService]: compatibility fallback that only polls while the
 *    screen is ON, for users who do not want to grant Accessibility access.
 *
 * Both engines share the caches below and only one of them should be active at a
 * time (the ViewModel/BootReceiver enforce this).
 */
object AppLockStateManager {

    private const val TAG = "AppLockStateManager"

    /** Safety window for temporary unlocks when no explicit re-lock policy applies. */
    private const val DEFAULT_UNLOCK_TIMEOUT_MS = 2 * 60 * 1000L

    private val lockedPackages = ConcurrentHashMap.newKeySet<String>()
    private val temporarilyUnlockedPackages = ConcurrentHashMap<String, Long>()

    private val _isProtectionActive = MutableStateFlow(false)
    val isProtectionActive = _isProtectionActive.asStateFlow()

    @Volatile
    private var accessibilityEngineActive = false

    @Volatile
    private var pollingEngineActive = false

    /** Master switch driven by the user's "Real-Time Protection" toggle. */
    @Volatile
    var isMasterEnabled: Boolean = true

    @Volatile
    var lastForegroundPackage: String? = null

    @Volatile
    var isOverlayShowing: Boolean = false

    /** true = a temporary unlock is dropped as soon as the user leaves the app (IMMEDIATE policy). */
    @Volatile
    var relockOnLeave: Boolean = true

    /** 0 = no timed expiry; otherwise the maximum lifetime of a temporary unlock. */
    @Volatile
    var unlockTimeoutMs: Long = 0L

    fun setAccessibilityEngineActive(active: Boolean) {
        accessibilityEngineActive = active
        publishProtectionState()
    }

    fun setPollingEngineActive(active: Boolean) {
        pollingEngineActive = active
        publishProtectionState()
    }

    private fun publishProtectionState() {
        _isProtectionActive.value = accessibilityEngineActive || pollingEngineActive
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

    fun isPackageLocked(pkg: String): Boolean = lockedPackages.contains(pkg)

    fun markTemporarilyUnlocked(packageName: String) {
        temporarilyUnlockedPackages[packageName] = System.currentTimeMillis()
        Log.d(TAG, "Temporarily unlocked: $packageName")
    }

    fun relockAll() {
        temporarilyUnlockedPackages.clear()
        // The next foreground observation must be treated as a fresh app entry.
        lastForegroundPackage = null
        Log.d(TAG, "Relocked all temporarily unlocked packages")
    }

    /**
     * Applies the persisted re-lock policy:
     *  - IMMEDIATE  : re-lock as soon as the user leaves the app (default).
     *  - AFTER_1MIN : keep the unlock for 1 minute after unlocking.
     *  - SCREEN_OFF : keep the unlock until the screen is turned off.
     */
    fun applyRelockPolicy(policy: String?) {
        when (policy?.trim()?.uppercase()) {
            "IMMEDIATE" -> {
                relockOnLeave = true
                unlockTimeoutMs = 0L
            }
            "AFTER_1MIN" -> {
                relockOnLeave = false
                unlockTimeoutMs = 60_000L
            }
            "SCREEN_OFF" -> {
                relockOnLeave = false
                unlockTimeoutMs = Long.MAX_VALUE
            }
            else -> {
                relockOnLeave = false
                unlockTimeoutMs = DEFAULT_UNLOCK_TIMEOUT_MS
            }
        }
        Log.d(TAG, "Re-lock policy=$policy relockOnLeave=$relockOnLeave timeoutMs=$unlockTimeoutMs")
    }

    fun shouldIntercept(packageName: String): Boolean {
        if (!lockedPackages.contains(packageName)) return false

        val unlockTime = temporarilyUnlockedPackages[packageName] ?: return true

        if (unlockTimeoutMs > 0L) {
            val elapsed = System.currentTimeMillis() - unlockTime
            if (elapsed < unlockTimeoutMs) return false
            // Expired: drop the stale entry and lock the app again.
            temporarilyUnlockedPackages.remove(packageName)
            return true
        }

        // No timed expiry: the entry is only dropped when the user leaves the app
        // (IMMEDIATE policy) or the screen turns off.
        return false
    }

    /**
     * Called for every foreground transition by both engines. Quick-returns when
     * the package did not change, so polling stays cheap.
     */
    fun checkAndIntercept(context: Context, foregroundPackage: String, appName: String? = null) {
        if (foregroundPackage == context.packageName) {
            // It's App Locker itself.
            lastForegroundPackage = foregroundPackage
            return
        }

        if (foregroundPackage == lastForegroundPackage) {
            // Same app still in front. Nothing to do unless a temporary unlock
            // exists or the lock screen is already up - otherwise the previous
            // launch attempt may have failed, so we retry.
            if (temporarilyUnlockedPackages.containsKey(foregroundPackage) || isOverlayShowing) return
            if (shouldIntercept(foregroundPackage)) {
                launchLockOverlay(context, foregroundPackage, appName)
            }
            return
        }

        // Foreground changed. With the IMMEDIATE policy the unlock state does not
        // survive leaving the app.
        val previous = lastForegroundPackage
        if (relockOnLeave && previous != null && previous != context.packageName) {
            temporarilyUnlockedPackages.remove(previous)
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
