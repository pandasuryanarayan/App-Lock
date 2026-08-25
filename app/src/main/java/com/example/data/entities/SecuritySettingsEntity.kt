package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LockType {
    PIN,
    PATTERN,
    PASSWORD,
    BIOMETRIC
}

@Entity(tableName = "security_settings")
data class SecuritySettingsEntity(
    @PrimaryKey val id: Int = 1,
    val activeLockType: String = LockType.PIN.name,
    val pinCode: String = "1234",
    val patternCode: String = "0,1,2,4,6,7,8", // Comma separated dot indices 0..8
    val passwordCode: String = "SecurePass1",
    val isBiometricEnabled: Boolean = true,
    val securityQuestion: String = "What is your favorite color?",
    val securityAnswer: String = "Blue",
    val isStealthPattern: Boolean = false, // Hide pattern trail when drawing
    val relockPolicy: String = "IMMEDIATE", // "IMMEDIATE", "SCREEN_OFF", "AFTER_1MIN"
    val vibrationEnabled: Boolean = true,
    val intruderSelfieEnabled: Boolean = true,
    val maxFailedAttempts: Int = 3,
    val isSetupCompleted: Boolean = false
)
