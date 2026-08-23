package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppIcons
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAndPermissionsScreen(
    isUsagePermissionGranted: Boolean,
    isOverlayPermissionGranted: Boolean,
    isAccessibilityGranted: Boolean,
    isProtectionActive: Boolean,
    onGrantUsagePermission: () -> Unit,
    onGrantOverlayPermission: () -> Unit,
    onGrantAccessibilityPermission: () -> Unit,
    onToggleProtectionService: (Boolean) -> Unit,
    onNavigateToLockConfig: () -> Unit,
    onNavigateToIntruderLogs: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = { Text("App Locker Settings & Protection", color = Color.White, fontWeight = FontWeight.Bold) },
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
            // Real-Time Protection Engine Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth().testTag("realtime_engine_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isProtectionActive) ShieldGreen.copy(alpha = 0.2f) else AlertRed.copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isProtectionActive) AppIcons.Security else AppIcons.Shield,
                                        contentDescription = null,
                                        tint = if (isProtectionActive) ShieldGreen else AlertRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Real-Time Protection Service",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isProtectionActive) "Active (Monitoring app launches)" else "Paused / Service Off",
                                    fontSize = 12.sp,
                                    color = if (isProtectionActive) ShieldGreen else TextSecondaryDark
                                )
                            }
                        }

                        Switch(
                            checked = isProtectionActive,
                            onCheckedChange = onToggleProtectionService,
                            modifier = Modifier.testTag("protection_service_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "When active, App Locker runs a lightweight background monitor that intercepts locked applications immediately as soon as they open on your device.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 16.sp
                    )
                }
            }

            // System Permissions Required for Real Locking
            Text(
                text = "Required System Permissions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Usage Access Permission
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = AppIcons.DataUsage, contentDescription = null, tint = CyberCyanLight)
                            Column {
                                Text("Usage Access", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Detects when locked apps open in system", fontSize = 12.sp, color = TextSecondaryDark)
                            }
                        }

                        if (isUsagePermissionGranted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ShieldGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "GRANTED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShieldGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = onGrantUsagePermission,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("settings_grant_usage_btn")
                            ) {
                                Text("GRANT", fontSize = 11.sp)
                            }
                        }
                    }

                    Divider(color = SlateSurfaceVariant)

                    // Draw Over Apps Permission
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = AppIcons.Layers, contentDescription = null, tint = CyberCyanLight)
                            Column {
                                Text("Display Over Other Apps", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Shows lock screen over protected apps", fontSize = 12.sp, color = TextSecondaryDark)
                            }
                        }

                        if (isOverlayPermissionGranted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ShieldGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "GRANTED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShieldGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = onGrantOverlayPermission,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("settings_grant_overlay_btn")
                            ) {
                                Text("GRANT", fontSize = 11.sp)
                            }
                        }
                    }

                    Divider(color = SlateSurfaceVariant)

                    // Accessibility Service
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = AppIcons.Accessibility, contentDescription = null, tint = CyberCyanLight)
                            Column {
                                Text("Accessibility Service (Instant Lock)", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Instant 0ms window detection", fontSize = 12.sp, color = TextSecondaryDark)
                            }
                        }

                        if (isAccessibilityGranted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ShieldGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShieldGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = onGrantAccessibilityPermission,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("settings_grant_access_btn")
                            ) {
                                Text("ENABLE", fontSize = 11.sp, color = CyberCyanLight)
                            }
                        }
                    }
                }
            }

            // Quick Navigation Shortcuts
            Text(
                text = "Security Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Card(
                onClick = onNavigateToLockConfig,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth().testTag("nav_lock_config_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(imageVector = AppIcons.Password, contentDescription = null, tint = CyberCyanLight)
                        Column {
                            Text("Change Lock Method & Code", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Update PIN, Pattern, Password & Biometrics", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                    Icon(imageVector = AppIcons.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
                }
            }

            Card(
                onClick = onNavigateToIntruderLogs,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth().testTag("nav_intruder_logs_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(imageVector = AppIcons.Warning, contentDescription = null, tint = AlertRed)
                        Column {
                            Text("Intruder Security Logs", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("View unauthorized unlock attempts", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                    Icon(imageVector = AppIcons.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
                }
            }
        }
    }
}
