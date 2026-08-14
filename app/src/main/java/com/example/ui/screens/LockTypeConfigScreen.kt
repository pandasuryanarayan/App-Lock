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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.LockType
import com.example.data.entities.SecuritySettingsEntity
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
    var isFakeErrorEnabled by remember { mutableStateOf(settings.isFakeErrorEnabled) }
    var vibrationEnabled by remember { mutableStateOf(settings.vibrationEnabled) }

    var question by remember { mutableStateOf(settings.securityQuestion) }
    var answer by remember { mutableStateOf(settings.securityAnswer) }

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
                LockType.PIN.name to ("4-Digit PIN" to Icons.Default.Pin),
                LockType.PATTERN.name to ("3x3 Pattern" to Icons.Default.Pattern),
                LockType.PASSWORD.name to ("Password" to Icons.Default.Password),
                LockType.BIOMETRIC.name to ("Biometric (Fingerprint/Face)" to Icons.Default.Fingerprint)
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

            Divider(color = SlateSurfaceVariant)

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
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyanLight)
                    )
                }
                else -> {
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { if (it.length <= 6) pinCode = it },
                        label = { Text("PIN Code (4-6 digits)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyanLight)
                    )
                }
            }

            Divider(color = SlateSurfaceVariant)

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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fake Error Decoy Screen", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Show 'App keeps stopping' fake crash message first", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Switch(
                            checked = isFakeErrorEnabled,
                            onCheckedChange = { isFakeErrorEnabled = it },
                            modifier = Modifier.testTag("toggle_fake_error_switch")
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
                        isFakeErrorEnabled = isFakeErrorEnabled,
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
