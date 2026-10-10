package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.LockType
import com.example.ui.components.AppIcons
import com.example.ui.components.PatternLockCanvas
import com.example.ui.components.PinPadView
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextSecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingSetupScreen(
    onCompleteSetup: (lockType: String, pin: String, pattern: String, pass: String, q: String, a: String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Choose Lock Type, 2: Set Code, 3: Security Question
    var selectedLockType by remember { mutableStateOf(LockType.PIN.name) }

    var enteredPin by remember { mutableStateOf("") }
    var confirmedPin by remember { mutableStateOf("") }

    var enteredPattern by remember { mutableStateOf("") }
    var confirmedPattern by remember { mutableStateOf("") }

    var enteredPass by remember { mutableStateOf("") }
    var confirmedPass by remember { mutableStateOf("") }

    var securityQuestion by remember { mutableStateOf("What is your first pet's name?") }
    var securityAnswer by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = ObsidianDark
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ObsidianDark,
                            Color(0xFF0F172A),
                            CyberCyan.copy(alpha = 0.15f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Header Shield Graphic
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = SlateSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = AppIcons.Security,
                            contentDescription = "Security Shield",
                            tint = CyberCyanLight,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "App Locker Setup",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Configure your master protection to lock sensitive apps, banking, and private data.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Step Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(1 to "Type", 2 to "Set Code", 3 to "Recovery").forEach { (idx, label) ->
                        FilterChip(
                            selected = step == idx,
                            onClick = { },
                            label = { Text("$idx. $label") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Step 1: Choose Lock Type
                AnimatedVisibility(visible = step == 1) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Select Master Lock Method",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val lockOptions = listOf(
                            LockType.PIN.name to ("4-Digit PIN" to AppIcons.Pin),
                            LockType.PATTERN.name to ("3x3 Pattern" to AppIcons.Pattern),
                            LockType.PASSWORD.name to ("Alphanumeric Password" to AppIcons.Password),
                            LockType.BIOMETRIC.name to ("Biometric (Fingerprint / Face)" to AppIcons.Fingerprint)
                        )

                        lockOptions.forEach { (type, pair) ->
                            val (title, icon) = pair
                            val isSelected = selectedLockType == type
                            Card(
                                onClick = { selectedLockType = type },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CyberCyan.copy(alpha = 0.3f) else SlateSurface
                                ),
                                border = if (isSelected) CardDefaults.outlinedCardBorder(true) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("lock_type_option_$type")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = title,
                                            tint = if (isSelected) CyberCyanLight else Color.Gray,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Text(
                                            text = title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedLockType = type }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { step = 2 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_next_step1"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Text("Next: Create Security Code", fontSize = 16.sp)
                        }
                    }
                }

                // Step 2: Set Credentials based on type
                AnimatedVisibility(visible = step == 2) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Set Your " + when(selectedLockType) {
                                LockType.PATTERN.name -> "3x3 Pattern"
                                LockType.PASSWORD.name -> "Password"
                                else -> "4-Digit PIN"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedLockType == LockType.PIN.name || selectedLockType == LockType.BIOMETRIC.name) {
                            Text(
                                text = "Enter 4 digits for PIN code",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PinPadView(
                                enteredPin = enteredPin,
                                onPinChange = { enteredPin = it },
                                onPinComplete = {
                                    // Done
                                }
                            )
                        } else if (selectedLockType == LockType.PATTERN.name) {
                            Text(
                                text = "Connect dots to set master pattern",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PatternLockCanvas(
                                modifier = Modifier
                                    .size(280.dp)
                                    .padding(16.dp),
                                onPatternComplete = { pattern ->
                                    enteredPattern = pattern
                                }
                            )
                            if (enteredPattern.isNotEmpty()) {
                                Text(
                                    text = "Pattern recorded (${enteredPattern.split(",").size} points)",
                                    color = CyberCyanLight,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = enteredPass,
                                onValueChange = { enteredPass = it },
                                label = { Text("Master Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_pass_input"),
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
                                    unfocusedLabelColor = TextSecondaryDark
                                )
                            )
                        }

                        errorMessage?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { step = 1 },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back")
                            }
                            Button(
                                onClick = {
                                    when (selectedLockType) {
                                        LockType.PATTERN.name -> {
                                            if (enteredPattern.length < 3) {
                                                errorMessage = "Connect at least 3 dots for pattern"
                                                return@Button
                                            }
                                        }
                                        LockType.PASSWORD.name -> {
                                            if (enteredPass.length < 4) {
                                                errorMessage = "Password must be at least 4 characters"
                                                return@Button
                                            }
                                        }
                                        else -> {
                                            if (enteredPin.length < 4) {
                                                errorMessage = "PIN must be 4 digits"
                                                return@Button
                                            }
                                        }
                                    }
                                    errorMessage = null
                                    step = 3
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_next_step2"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                            ) {
                                Text("Next")
                            }
                        }
                    }
                }

                // Step 3: Security Question & Recovery
                AnimatedVisibility(visible = step == 3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Recovery Security Question",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Used to reset your lock in case you forget your code.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = securityQuestion,
                            onValueChange = { securityQuestion = it },
                            label = { Text("Security Question") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_sq_input"),
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
                                unfocusedLabelColor = TextSecondaryDark
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = securityAnswer,
                            onValueChange = { securityAnswer = it },
                            label = { Text("Your Secret Answer") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_sa_input"),
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
                                unfocusedLabelColor = TextSecondaryDark
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (securityAnswer.isBlank()) {
                                    errorMessage = "Please enter a secret security answer"
                                    return@Button
                                }
                                onCompleteSetup(
                                    selectedLockType,
                                    enteredPin.ifEmpty { "1234" },
                                    enteredPattern.ifEmpty { "0,1,2,4,6,7,8" },
                                    enteredPass.ifEmpty { "SecurePass1" },
                                    securityQuestion,
                                    securityAnswer
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_complete_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                                Text("Enable Master App Locker", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
