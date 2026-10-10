package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.LockType
import com.example.data.entities.SecuritySettingsEntity
import com.example.ui.components.AppIcons
import com.example.ui.components.PatternLockCanvas
import com.example.ui.components.PinPadView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockTypeConfigScreen(
    settings: SecuritySettingsEntity,
    onSaveSettings: (SecuritySettingsEntity) -> Unit
) {
    var activeType by remember { mutableStateOf(settings.activeLockType) }
    var pinCode by remember { mutableStateOf(settings.pinCode) }
    var patternCode by remember { mutableStateOf(settings.patternCode) }
    var passCode by remember { mutableStateOf(settings.passwordCode) }

    var isBiometricEnabled by remember { mutableStateOf(settings.isBiometricEnabled) }
    var isStealthPattern by remember { mutableStateOf(settings.isStealthPattern) }
    var vibrationEnabled by remember { mutableStateOf(settings.vibrationEnabled) }

    var question by remember { mutableStateOf(settings.securityQuestion) }
    var answer by remember { mutableStateOf(settings.securityAnswer) }

    // Sync local editable state when persisted settings load/change
    // (settings are loaded async from Room, so initial remember() would otherwise stay empty
    // and the saved backup PIN would appear "not visible").
    LaunchedEffect(settings) {
        activeType = settings.activeLockType
        pinCode = settings.pinCode
        patternCode = settings.patternCode
        passCode = settings.passwordCode
        isBiometricEnabled = settings.isBiometricEnabled
        isStealthPattern = settings.isStealthPattern
        vibrationEnabled = settings.vibrationEnabled
        question = settings.securityQuestion
        answer = settings.securityAnswer
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = { Text("Security & Lock Method", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Choose Lock Type",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Lock Type Selector Cards
            val lockTypes = listOf(
                LockType.PIN.name to ("4-Digit PIN" to AppIcons.Pin),
                LockType.PATTERN.name to ("3x3 Pattern" to AppIcons.Pattern),
                LockType.PASSWORD.name to ("Password" to AppIcons.Password),
                LockType.BIOMETRIC.name to ("Biometric (Fingerprint/Face)" to AppIcons.Fingerprint)
            )

            lockTypes.forEach { (type, info) ->
                val (title, icon) = info
                val isSelected = activeType == type

                Card(
                    onClick = { activeType = type },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CyberCyan.copy(alpha = 0.25f) else SlateSurface
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("config_lock_type_$type")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = CyberCyanLight)
                            Text(text = title, color = Color.White, fontSize = 16.sp)
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { activeType = type }
                        )
                    }
                }
            }

            HorizontalDivider(color = SlateSurfaceVariant)

            // Code customizers
            Text(
                text = "Configure Credentials",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            when (activeType) {
                LockType.PATTERN.name -> {
                    Text(text = "Draw new pattern:", color = TextSecondaryDark)
                    PatternLockCanvas(
                        modifier = Modifier.size(240.dp).padding(16.dp),
                        onPatternComplete = { newP ->
                            patternCode = newP
                        }
                    )
                }
                LockType.PASSWORD.name -> {
                    OutlinedTextField(
                        value = passCode,
                        onValueChange = { passCode = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth().testTag("password_input"),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = TextSecondaryDark,
                            focusedContainerColor = SlateSurface,
                            unfocusedContainerColor = SlateSurface,
                            disabledContainerColor = SlateSurface,
                            cursorColor = CyberCyanLight,
                            focusedBorderColor = CyberCyanLight,
                            unfocusedBorderColor = SlateSurfaceVariant,
                            focusedLabelColor = CyberCyanLight,
                            unfocusedLabelColor = TextSecondaryDark,
                            focusedPlaceholderColor = TextSecondaryDark,
                            unfocusedPlaceholderColor = TextSecondaryDark
                        )
                    )
                }
                LockType.BIOMETRIC.name -> {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val activity = context as? androidx.fragment.app.FragmentActivity
                    var testStatus by remember { mutableStateOf<String?>(null) }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = AppIcons.Fingerprint, contentDescription = null, tint = CyberCyanLight)
                                Text("Biometric Authentication Primary", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                "Locked apps will trigger your device fingerprint or face sensor. A backup PIN code is required below for fallback:",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )

                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pinCode = it },
                                label = { Text("Backup PIN Code (4-6 digits)") },
                                placeholder = { Text("Enter backup PIN") },
                                modifier = Modifier.fillMaxWidth().testTag("backup_pin_input"),
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = TextSecondaryDark,
                                    focusedContainerColor = SlateSurface,
                                    unfocusedContainerColor = SlateSurface,
                                    disabledContainerColor = SlateSurface,
                                    cursorColor = CyberCyanLight,
                                    focusedBorderColor = CyberCyanLight,
                                    unfocusedBorderColor = SlateSurfaceVariant,
                                    focusedLabelColor = CyberCyanLight,
                                    unfocusedLabelColor = TextSecondaryDark,
                                    focusedPlaceholderColor = TextSecondaryDark,
                                    unfocusedPlaceholderColor = TextSecondaryDark
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (activity != null) {
                                            val mgr = com.example.security.BiometricPromptManager(activity)
                                            val status = mgr.checkBiometricStatus()
                                            if (status == com.example.security.BiometricStatus.NOT_ENROLLED) {
                                                testStatus = "No fingerprint enrolled in device settings"
                                                com.example.security.BiometricPromptManager.openBiometricEnrollSettings(context)
                                            } else if (status == com.example.security.BiometricStatus.NO_HARDWARE) {
                                                testStatus = "No biometric hardware detected on this device"
                                            } else {
                                                mgr.showBiometricPrompt(
                                                    title = "Test Biometric Sensor",
                                                    subtitle = "Touch your sensor to verify"
                                                ) { res ->
                                                    testStatus = when (res) {
                                                        is com.example.security.BiometricResult.AuthenticationSuccess -> "Biometric Sensor Verified Successfully!"
                                                        is com.example.security.BiometricResult.AuthenticationFailed -> "Fingerprint not recognized"
                                                        is com.example.security.BiometricResult.AuthenticationCancelled -> "Test cancelled"
                                                        is com.example.security.BiometricResult.AuthenticationError -> "Error: ${res.error}"
                                                        else -> "Biometric not ready"
                                                    }
                                                }
                                            }
                                        } else {
                                            testStatus = "Sensor test ready"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                    modifier = Modifier.testTag("test_biometric_btn")
                                ) {
                                    Text("Test Fingerprint Sensor")
                                }
                            }

                            testStatus?.let { status ->
                                Text(
                                    text = status,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (status.contains("Successfully")) ShieldGreen else AlertRed
                                )
                            }
                        }
                    }
                }
                else -> {
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pinCode = it },
                        label = { Text("PIN Code (4-6 digits)") },
                        placeholder = { Text("Enter PIN") },
                        modifier = Modifier.fillMaxWidth().testTag("pin_input"),
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = TextSecondaryDark,
                            focusedContainerColor = SlateSurface,
                            unfocusedContainerColor = SlateSurface,
                            disabledContainerColor = SlateSurface,
                            cursorColor = CyberCyanLight,
                            focusedBorderColor = CyberCyanLight,
                            unfocusedBorderColor = SlateSurfaceVariant,
                            focusedLabelColor = CyberCyanLight,
                            unfocusedLabelColor = TextSecondaryDark,
                            focusedPlaceholderColor = TextSecondaryDark,
                            unfocusedPlaceholderColor = TextSecondaryDark
                        )
                    )
                }
            }

            HorizontalDivider(color = SlateSurfaceVariant)

            // Advanced Security Features
            Text(
                text = "Advanced Features",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Biometric Sensor", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Use Fingerprint or Face Scan to unlock", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { isBiometricEnabled = it },
                            modifier = Modifier.testTag("toggle_biometric_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Stealth Pattern Lines", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Hide pattern trail lines while drawing", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Switch(
                            checked = isStealthPattern,
                            onCheckedChange = { isStealthPattern = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updated = settings.copy(
                        activeLockType = activeType,
                        pinCode = pinCode,
                        patternCode = patternCode,
                        passwordCode = passCode,
                        isBiometricEnabled = isBiometricEnabled,
                        isStealthPattern = isStealthPattern,
                        vibrationEnabled = vibrationEnabled
                    )
                    onSaveSettings(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_config_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
            ) {
                Text("Save Security Preferences", fontSize = 16.sp)
            }
        }
    }
}
