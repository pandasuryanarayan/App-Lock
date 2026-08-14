package com.example.security

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.data.db.AppLockerDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class AppLockAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "AppLockAccessibility"
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "AppLockAccessibilityService connected")
        AppLockStateManager.setProtectionActive(true)

        // Observe locked packages
        serviceScope.launch {
            try {
                val db = AppLockerDatabase.getInstance(applicationContext)
                db.appLockerDao().getAllLockedApps().collectLatest { lockedList ->
                    val lockedPackages = lockedList.filter { it.isLocked }.map { it.packageName }.toSet()
                    AppLockStateManager.updateLockedPackages(lockedPackages)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in Accessibility DB observation", e)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            val className = event.className?.toString()

            // Skip own package or system dialog transients
            if (packageName == applicationContext.packageName) return

            AppLockStateManager.checkAndIntercept(applicationContext, packageName)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "AppLockAccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "AppLockAccessibilityService destroyed")
    }
}
