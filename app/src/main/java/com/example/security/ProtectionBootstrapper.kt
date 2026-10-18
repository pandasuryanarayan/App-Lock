package com.example.security

import android.content.Context
import android.util.Log
import com.example.data.db.AppLockerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Warm-up + cache maintenance for the lock engines.
 *
 * The DB is only read once per engine start (and then kept in sync via a Flow),
 * so per-event interception never touches Room. This is what makes the
 * event-driven engine cheap enough for low-end phones.
 */
object ProtectionBootstrapper {

    private const val TAG = "ProtectionBootstrapper"
    private const val DEFAULT_RELOCK_POLICY = "IMMEDIATE"

    /**
     * One-shot warm-up used by the boot receiver, before any engine starts.
     * Loads the locked package set and the re-lock policy into memory.
     */
    suspend fun prime(context: Context) = withContext(Dispatchers.IO) {
        try {
            val dao = AppLockerDatabase.getInstance(context).appLockerDao()
            val locked = dao.getOnlyLockedApps().first()
            AppLockStateManager.updateLockedPackages(locked.map { it.packageName })
            AppLockStateManager.applyRelockPolicy(dao.getSecuritySettingsSync()?.relockPolicy ?: DEFAULT_RELOCK_POLICY)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prime protection caches", e)
        }
    }

    /**
     * Keeps the in-memory caches hot for as long as [scope] lives. Call this from
     * a service onCreate/onServiceConnected and cancel the scope in onDestroy.
     */
    fun attach(context: Context, scope: CoroutineScope) {
        val appContext = context.applicationContext
        scope.launch {
            try {
                val dao = AppLockerDatabase.getInstance(appContext).appLockerDao()

                launch {
                    dao.getOnlyLockedApps().collect { locked ->
                        AppLockStateManager.updateLockedPackages(locked.map { it.packageName })
                    }
                }

                launch {
                    dao.getSecuritySettings().collect { settings ->
                        AppLockStateManager.applyRelockPolicy(settings?.relockPolicy ?: DEFAULT_RELOCK_POLICY)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to attach protection cache observer", e)
            }
        }
    }
}
