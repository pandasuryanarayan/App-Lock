package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.LockType
import com.example.data.entities.SecuritySettingsEntity
import com.example.security.AppItemInfo
import com.example.ui.components.AppIconImage
import com.example.ui.components.AppIcons
import com.example.ui.components.PatternLockCanvas
import com.example.ui.components.PinPadView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockOverlayScreen(
    appInfo: AppItemInfo,
    settings: SecuritySettingsEntity,
    onSuccessUnlock: () -> Unit,
    onFailedAttempt: (String) -> Unit,
    onDismiss: () -> Unit,
    onTriggerBiometric: (() -> Unit)? = null
) {
    var enteredPin by remember { mutableStateOf("") }
    var enteredPass by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showRecoveryDialog by remember { mutableStateOf(false) }
    var recoveryAnswerInput by remember { mutableStateOf("") }
    var recoveryError by remember { mutableStateOf<String?>(null) }

    var activeLockMode by remember { mutableStateOf(settings.activeLockType) }

    // Auto-prompt biometric on first load if enabled
    LaunchedEffect(Unit) {
        if (settings.isBiometricEnabled || activeLockMode == LockType.BIOMETRIC.name) {
            onTriggerBiometric?.invoke()
        }
    }

    // Main Full-screen Lock Overlay UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianDark,
                        SlateBackground,
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar with App Title & Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = AppIcons.Close, contentDescription = "Close", tint = Color.White)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.Lock,
                        contentDescription = null,
                        tint = CyberCyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "App Locker Security",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberCyanLight
                    )
                }
                IconButton(onClick = { showRecoveryDialog = true }) {
                    Icon(imageVector = AppIcons.HelpOutline, contentDescription = "Recovery", tint = Color.White)
                }
            }

            // Target Protected App info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppIconImage(
                    packageName = appInfo.packageName,
                    appName = appInfo.appName,
                    category = appInfo.category,
                    sizeDp = 72.dp,
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = appInfo.appName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (appInfo.category == "BANKING") "Banking Level Encryption Active" else "Protected Application",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )

                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = err,
                        color = AlertRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Lock Input Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (activeLockMode) {
                    LockType.PATTERN.name -> {
                        PatternLockCanvas(
                            modifier = Modifier
                                .size(300.dp)
                                .padding(16.dp),
                            isStealth = settings.isStealthPattern,
                            isError = isError,
                            onPatternStart = {
                                isError = false
                                errorMessage = null
                            },
                            onPatternComplete = { pattern ->
                                if (pattern == settings.patternCode) {
                                    onSuccessUnlock()
                                } else {
                                    isError = true
                                    errorMessage = "Wrong Pattern Code!"
                                    onFailedAttempt(pattern)
                                }
                            }
                        )
                    }
                    LockType.PASSWORD.name -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            OutlinedTextField(
                                value = enteredPass,
                                onValueChange = {
                                    enteredPass = it
                                    isError = false
                                    errorMessage = null
                                },
                                label = { Text("Enter Master Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                isError = isError,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("overlay_pass_input"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyanLight)
                            )
                            Button(
                                onClick = {
                                    if (enteredPass == settings.passwordCode) {
                                        onSuccessUnlock()
                                    } else {
                                        isError = true
                                        errorMessage = "Invalid Password!"
                                        onFailedAttempt(enteredPass)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("overlay_pass_unlock_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                            ) {
                                Text("Unlock App")
                            }
                        }
                    }
                    LockType.BIOMETRIC.name -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CyberCyan.copy(alpha = 0.15f),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        if (onTriggerBiometric != null) {
                                            onTriggerBiometric()
                                        } else {
                                            onSuccessUnlock()
                                        }
                                    }
                                    .testTag("overlay_biometric_large_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = AppIcons.Fingerprint,
                                        contentDescription = "Scan Fingerprint",
                                        tint = CyberCyanLight,
                                        modifier = Modifier.size(56.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Touch Fingerprint Sensor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Tap icon or touch device biometric sensor",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    activeLockMode = LockType.PIN.name
                                }
                            ) {
                                Text("Use Backup PIN / Password")
                            }
                        }
                    }
                    else -> {
                        // PIN (default)
                        PinPadView(
                            enteredPin = enteredPin,
                            isError = isError,
                            onPinChange = {
                                enteredPin = it
                                isError = false
                                errorMessage = null
                            },
                            onPinComplete = { pin ->
                                if (pin == settings.pinCode) {
                                    onSuccessUnlock()
                                } else {
                                    isError = true
                                    errorMessage = "Incorrect PIN Code!"
                                    onFailedAttempt(pin)
                                    enteredPin = ""
                                }
                            }
                        )
                    }
                }
            }

            // Bottom Alternative Auth Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (settings.isBiometricEnabled && activeLockMode != LockType.BIOMETRIC.name) {
                    TextButton(
                        onClick = {
                            activeLockMode = LockType.BIOMETRIC.name
                            errorMessage = null
                            isError = false
                            onTriggerBiometric?.invoke()
                        },
                        modifier = Modifier.testTag("overlay_biometric_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = AppIcons.Fingerprint, contentDescription = null, tint = CyberCyanLight)
                            Text("Fingerprint", color = CyberCyanLight)
                        }
                    }
                }

                // Switch Lock Mode Option
                TextButton(
                    onClick = {
                        activeLockMode = when(activeLockMode) {
                            LockType.PIN.name -> LockType.PATTERN.name
                            LockType.PATTERN.name -> LockType.PASSWORD.name
                            LockType.PASSWORD.name -> if (settings.isBiometricEnabled) LockType.BIOMETRIC.name else LockType.PIN.name
                            else -> LockType.PIN.name
                        }
                        errorMessage = null
                        isError = false
                    }
                ) {
                    Text("Switch Lock Type", color = TextSecondaryDark)
                }
            }
        }
    }

    // Recovery Dialog
    if (showRecoveryDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryDialog = false },
            title = { Text("Reset / Recovery Check") },
            text = {
                Column {
                    Text(
                        text = settings.securityQuestion,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = recoveryAnswerInput,
                        onValueChange = { recoveryAnswerInput = it },
                        label = { Text("Answer") },
                        singleLine = true
                    )
                    recoveryError?.let { err ->
                        Text(text = err, color = AlertRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (recoveryAnswerInput.equals(settings.securityAnswer, ignoreCase = true)) {
                            showRecoveryDialog = false
                            onSuccessUnlock()
                        } else {
                            recoveryError = "Incorrect answer!"
                        }
                    }
                ) {
                    Text("Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoveryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
