package com.example.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Device boot hook.
 *
 * Boot does NOT start a 24/7 service any more:
 *  - The locked list + re-lock policy are simply loaded into memory.
 *  - The accessibility engine is automatically rebound by the system when the
 *    user has enabled it, so there is nothing to start.
 *  - The polling fallback is only started when Accessibility is NOT available,
 *    Usage Access is granted and the user left protection enabled.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON"
        ) {
            return
        }

        val appContext = context.applicationContext
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Warm the in-memory caches without starting any engine.
                ProtectionBootstrapper.prime(appContext)

                if (!ProtectionPreferences.isProtectionEnabled(appContext)) return@launch

                // The event-driven engine is rebound by the OS on its own.
                if (PermissionHelper.isAccessibilityServiceEnabled(appContext)) return@launch

                // Compatibility fallback only (screen-on polling monitor).
                if (PermissionHelper.hasUsageAccess(appContext)) {
                    PermissionHelper.startRealTimeProtectionService(appContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
