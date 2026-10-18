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
    isOverlayPermissionGranted: Boolean,
    isAccessibilityPermissionGranted: Boolean,
    isProtectionActive: Boolean,
    onGrantOverlayPermission: () -> Unit,
    onGrantAccessibilityPermission: () -> Unit,
    onOpenAppDetails: () -> Unit,
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
                                    text = when {
                                        !isProtectionActive -> "Paused / Not Intercepting"
                                        isAccessibilityPermissionGranted -> "Active - Low Battery Mode (event-driven)"
                                        else -> "Active - Compatibility Mode (screen-on monitor)"
                                    },
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
                        text = "When active, App Locker intercepts locked applications the moment they open. " +
                                "With Accessibility access the engine is fully event-driven and uses almost no battery " +
                                "(no background polling, no permanent notification). Without it, a lightweight " +
                                "screen-on monitor is used as a fallback.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 16.sp
                    )
                }
            }

            // System Permissions Required for Real Locking — version-aware Step 1 -> Step 2.
            Text(
                text = "Required System Permissions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = com.example.security.PermissionHelper.versionNoteText(),
                fontSize = 12.sp,
                color = TextSecondaryDark,
                lineHeight = 16.sp
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Version-aware gating: below 10 overlay is recommended only.
                    val overlayMandatory = com.example.security.PermissionHelper.isOverlayMandatory()
                    val overlayWhy = com.example.security.PermissionHelper.overlayWhyText()
                    val step2Enabled = isOverlayPermissionGranted || !overlayMandatory
                    // Step 1 (version-aware): Display — blocking on 10+, recommended below.
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
                                Text(
                                    if (overlayMandatory) "Step 1: Display (Must allow)" else "Step 1: Display (Recommended)",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(overlayWhy, fontSize = 12.sp, color = TextSecondaryDark)
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
                                Text(if (overlayMandatory) "GRANT (MUST)" else "GRANT", fontSize = 11.sp)
                            }
                        }
                    }

                    HorizontalDivider(color = SlateSurfaceVariant)

                    // Step 2 (mandatory): Accessibility — the only event-driven detection API.
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
                            Icon(imageVector = AppIcons.Accessibility, contentDescription = null, tint = ShieldGreen)
                            Column {
                                Text("Step 2a: Accessibility ★ Best", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Sees app opens instantly. Almost no battery.", fontSize = 12.sp, color = TextSecondaryDark)
                            }
                        }

                        if (isAccessibilityPermissionGranted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ShieldGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "GRANTED ★",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShieldGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = onGrantAccessibilityPermission,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ShieldGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                enabled = step2Enabled,
                                modifier = Modifier.testTag("settings_grant_accessibility_btn")
                            ) {
                                Text("GRANT ★", fontSize = 11.sp)
                            }
                        }
                    }

                    if (!step2Enabled) {
                        Text(
                            text = "Allow Step 1 Display first — Step 2 unlocks after that (10+ rule).",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            // Troubleshooting & Sideloaded APK Permission Guide
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("troubleshoot_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = AppIcons.Info, contentDescription = null, tint = CyberCyanLight)
                        Text(
                            text = "Permission Setup & Android 13+ Guide (on ${com.example.security.PermissionHelper.androidVersionLabel()})",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "• Step 1 Display: GRANT above, then allow App Locker to display over apps. " +
                                com.example.security.PermissionHelper.overlayWhyText() + "\n\n" +
                                "• Step 2a ★ Accessibility: GRANT above, then enable 'App Locker' in Settings > Accessibility. Instant, low battery.\n\n" +
                                "• Step 2b Usage (if you skip ★): allow usage tracking for App Locker. Runs only while screen is on.\n\n" +
                                "• Greyed out? On Android 13+ open App Info > 3 dots > 'Allow restricted settings', then come back.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 17.sp
                    )

                    OutlinedButton(
                        onClick = onOpenAppDetails,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_app_info_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = AppIcons.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                if (com.example.security.PermissionHelper.needsRestrictedSettingsGuide())
                                    "Open App Info (Allow restricted settings if grey)"
                                else
                                    "Open App Info",
                                fontSize = 12.sp,
                                color = CyberCyanLight
                            )
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
