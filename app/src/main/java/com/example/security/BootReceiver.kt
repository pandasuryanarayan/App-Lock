package com.example.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            if (PermissionHelper.hasUsageAccess(context) || PermissionHelper.hasOverlayPermission(context)) {
                PermissionHelper.startRealTimeProtectionService(context)
            }
        }
    }
}
