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
 * Nothing is started here and nothing stays running after boot:
 *  - The locked list + re-lock policy are simply loaded into memory.
 *  - The event-driven accessibility engine is automatically rebound by the
 *    system whenever the user has enabled it, so there is no service to start.
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
                // Warm the in-memory caches only. No service, no polling.
                ProtectionBootstrapper.prime(appContext)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
