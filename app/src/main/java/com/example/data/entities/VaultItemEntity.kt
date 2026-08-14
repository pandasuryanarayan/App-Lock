package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val secretContent: String,
    val category: String, // "NOTE", "PASSWORD", "PICTURE"
    val createdAt: Long = System.currentTimeMillis()
)
