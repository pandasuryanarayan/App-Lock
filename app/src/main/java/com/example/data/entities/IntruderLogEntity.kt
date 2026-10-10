package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intruder_logs")
data class IntruderLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val appName: String,
    val packageName: String,
    val failedAttemptCount: Int,
    val lockTypeUsed: String,
    val attemptInput: String = "****",
    val note: String = "Failed unlock attempt recorded"
)
