package com.example.data.dao

import androidx.room.*
import com.example.data.entities.IntruderLogEntity
import com.example.data.entities.LockedAppEntity
import com.example.data.entities.SecuritySettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLockerDao {

    // Locked Apps
    @Query("SELECT * FROM locked_apps ORDER BY appName ASC")
    fun getAllLockedApps(): Flow<List<LockedAppEntity>>

    @Query("SELECT * FROM locked_apps WHERE isLocked = 1")
    fun getOnlyLockedApps(): Flow<List<LockedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApp(app: LockedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllApps(apps: List<LockedAppEntity>)

    @Query("UPDATE locked_apps SET isLocked = :isLocked WHERE packageName = :packageName")
    suspend fun updateAppLockStatus(packageName: String, isLocked: Boolean)

    @Query("DELETE FROM locked_apps WHERE packageName = :packageName")
    suspend fun deleteAppByPackage(packageName: String)

    // Security Settings
    @Query("SELECT * FROM security_settings WHERE id = 1")
    fun getSecuritySettings(): Flow<SecuritySettingsEntity?>

    @Query("SELECT * FROM security_settings WHERE id = 1")
    suspend fun getSecuritySettingsSync(): SecuritySettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSecuritySettings(settings: SecuritySettingsEntity)

    // Intruder Logs
    @Query("SELECT * FROM intruder_logs ORDER BY timestamp DESC")
    fun getIntruderLogs(): Flow<List<IntruderLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntruderLog(log: IntruderLogEntity)

    @Query("DELETE FROM intruder_logs")
    suspend fun clearIntruderLogs()
}
