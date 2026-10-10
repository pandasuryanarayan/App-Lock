package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AppLockerDao
import com.example.data.entities.IntruderLogEntity
import com.example.data.entities.LockedAppEntity
import com.example.data.entities.SecuritySettingsEntity

@Database(
    entities = [
        LockedAppEntity::class,
        SecuritySettingsEntity::class,
        IntruderLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppLockerDatabase : RoomDatabase() {

    abstract fun appLockerDao(): AppLockerDao

    companion object {
        @Volatile
        private var INSTANCE: AppLockerDatabase? = null

        fun getInstance(context: Context): AppLockerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppLockerDatabase::class.java,
                    "app_locker_db"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
