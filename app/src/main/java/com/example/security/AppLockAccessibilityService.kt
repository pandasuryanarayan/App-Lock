package com.example.security

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Event-driven lock engine (low battery mode).
 *
 * Android keeps this service registered, but it does ZERO work until a window
 * actually changes. When a protected app comes to the foreground the OS invokes
 * [onAccessibilityEvent], we check the in-memory locked set, and only then do we
 * start the lock screen activity. No polling loop, no wake locks, no permanent
 * overlay - roughly 0% CPU while idle.
 *
 * The service retrieves no window content; see res/xml/accessibility_service_config.xml.
 */
class AppLockAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "AppLockA11yService"

        /** Ignore repeated events for the same package inside this window. */
        private const val DEBOUNCE_WINDOW_MS = 1000L

        /** System packages that should never trigger a lock check. */
        private val IGNORED_PACKAGES = setOf("android", "com.android.systemui")
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var receiverRegistered = false
    private var lastEventPackage: String? = null
    private var lastEventTime = 0L

    /**
     * When the screen turns off there is no app to lock, so we only clear the
     * temporary unlocks and let the system stop delivering events.
     */
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                Log.d(TAG, "Screen off -> clearing temporary unlocks")
                AppLockStateManager.relockAll()
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility engine connected")

        // Restore the user's master switch after a process restart.
        AppLockStateManager.isMasterEnabled = ProtectionPreferences.isProtectionEnabled(this)
        AppLockStateManager.setAccessibilityEngineActive(AppLockStateManager.isMasterEnabled)

        if (!receiverRegistered) {
            ContextCompat.registerReceiver(
                this,
                screenReceiver,
                IntentFilter(Intent.ACTION_SCREEN_OFF),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            receiverRegistered = true
        }

        // Cache the locked list + re-lock policy in memory once, then keep them hot.
        ProtectionBootstrapper.attach(this, serviceScope)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (!AppLockStateManager.isMasterEnabled) return

        val pkg = event.packageName?.toString() ?: return

        // Never react to ourselves, the system UI or plain android windows.
        if (pkg == packageName || IGNORED_PACKAGES.contains(pkg)) return

        val now = System.currentTimeMillis()
        if (pkg == lastEventPackage && now - lastEventTime < DEBOUNCE_WINDOW_MS) return
        lastEventPackage = pkg
        lastEventTime = now

        // Fast in-memory check; also handles re-locking apps that were left.
        AppLockStateManager.checkAndIntercept(this, pkg)
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        Log.d(TAG, "Accessibility engine destroyed")
        AppLockStateManager.setAccessibilityEngineActive(false)
        if (receiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            receiverRegistered = false
        }
        serviceScope.cancel()
        super.onDestroy()
    }
}
