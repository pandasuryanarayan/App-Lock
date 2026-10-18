package com.example.security

import android.content.Context

/**
 * Lightweight SharedPreferences store for the master protection switch.
 *
 * Kept out of Room on purpose so no schema migration is needed, and so the
 * accessibility service can read the user's choice instantly after a process
 * restart (the system rebinds accessibility services without launching the UI).
 */
object ProtectionPreferences {

    private const val PREFS_NAME = "app_locker_protection_prefs"
    private const val KEY_PROTECTION_ENABLED = "protection_enabled"

    fun isProtectionEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PROTECTION_ENABLED, true)

    fun setProtectionEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_PROTECTION_ENABLED, enabled).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
