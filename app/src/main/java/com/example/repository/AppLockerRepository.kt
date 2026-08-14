package com.example.repository

import android.content.Context
import com.example.data.dao.AppLockerDao
import com.example.data.entities.IntruderLogEntity
import com.example.data.entities.LockedAppEntity
import com.example.data.entities.SecuritySettingsEntity
import com.example.data.entities.VaultItemEntity
import com.example.security.AppItemInfo
import com.example.security.InstalledAppsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class AppLockerRepository(
    private val context: Context,
    private val dao: AppLockerDao
) {
    val securitySettings: Flow<SecuritySettingsEntity?> = dao.getSecuritySettings()
    val intruderLogs: Flow<List<IntruderLogEntity>> = dao.getIntruderLogs()
    val vaultItems: Flow<List<VaultItemEntity>> = dao.getVaultItems()
    val lockedAppsInDb: Flow<List<LockedAppEntity>> = dao.getAllLockedApps()

    suspend fun getInitialSettings(): SecuritySettingsEntity {
        return withContext(Dispatchers.IO) {
            val existing = dao.getSecuritySettingsSync()
            if (existing != null) {
                existing
            } else {
                val defaultSettings = SecuritySettingsEntity()
                dao.saveSecuritySettings(defaultSettings)
                defaultSettings
            }
        }
    }

    suspend fun updateSecuritySettings(settings: SecuritySettingsEntity) {
        withContext(Dispatchers.IO) {
            dao.saveSecuritySettings(settings)
        }
    }

    suspend fun toggleAppLock(packageName: String, appName: String, category: String, isCurrentlyLocked: Boolean) {
        withContext(Dispatchers.IO) {
            val newStatus = !isCurrentlyLocked
            val appEntity = LockedAppEntity(
                packageName = packageName,
                appName = appName,
                category = category,
                isLocked = newStatus,
                isSensitive = category == "BANKING" || category == "SOCIAL"
            )
            dao.insertOrUpdateApp(appEntity)
        }
    }

    suspend fun lockAllBankingApps(installedApps: List<AppItemInfo>) {
        withContext(Dispatchers.IO) {
            val bankingApps = installedApps.filter { it.category == "BANKING" || it.isSensitive }
            val entities = bankingApps.map {
                LockedAppEntity(
                    packageName = it.packageName,
                    appName = it.appName,
                    category = it.category,
                    isLocked = true,
                    isSensitive = true
                )
            }
            dao.insertAllApps(entities)
        }
    }

    suspend fun lockAllApps(installedApps: List<AppItemInfo>) {
        withContext(Dispatchers.IO) {
            val entities = installedApps.map {
                LockedAppEntity(
                    packageName = it.packageName,
                    appName = it.appName,
                    category = it.category,
                    isLocked = true,
                    isSensitive = it.isSensitive
                )
            }
            dao.insertAllApps(entities)
        }
    }

    suspend fun unlockAllApps(installedApps: List<AppItemInfo>) {
        withContext(Dispatchers.IO) {
            val entities = installedApps.map {
                LockedAppEntity(
                    packageName = it.packageName,
                    appName = it.appName,
                    category = it.category,
                    isLocked = false,
                    isSensitive = it.isSensitive
                )
            }
            dao.insertAllApps(entities)
        }
    }

    suspend fun logIntruderAttempt(appName: String, packageName: String, lockType: String, attemptInput: String) {
        withContext(Dispatchers.IO) {
            val log = IntruderLogEntity(
                appName = appName,
                packageName = packageName,
                failedAttemptCount = 1,
                lockTypeUsed = lockType,
                attemptInput = attemptInput,
                note = "Unauthorized access attempt blocked"
            )
            dao.insertIntruderLog(log)
        }
    }

    suspend fun clearIntruderLogs() {
        withContext(Dispatchers.IO) {
            dao.clearIntruderLogs()
        }
    }

    suspend fun addVaultItem(title: String, secretContent: String, category: String) {
        withContext(Dispatchers.IO) {
            val item = VaultItemEntity(
                title = title,
                secretContent = secretContent,
                category = category
            )
            dao.insertVaultItem(item)
        }
    }

    suspend fun deleteVaultItem(id: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteVaultItem(id)
        }
    }

    fun getInstalledApps(): List<AppItemInfo> {
        return InstalledAppsManager.fetchInstalledApps(context)
    }
}
