package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.AppItemInfo
import com.example.ui.components.AppIconImage
import com.example.ui.components.AppIcons
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAppsScreen(
    installedApps: List<AppItemInfo>,
    lockedAppsMap: Map<String, Boolean>,
    searchQuery: String,
    selectedCategory: String,
    isUsagePermissionGranted: Boolean,
    isOverlayPermissionGranted: Boolean,
    isAccessibilityPermissionGranted: Boolean,
    isProtectionActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onToggleLock: (AppItemInfo) -> Unit,
    onLockBanking: () -> Unit,
    onLockAll: () -> Unit,
    onUnlockAll: () -> Unit,
    onTestLockOverlay: (AppItemInfo) -> Unit,
    onLaunchRealApp: (AppItemInfo) -> Unit,
    onGrantUsagePermission: () -> Unit,
    onGrantOverlayPermission: () -> Unit,
    onGrantAccessibilityPermission: () -> Unit
) {
    val lockedCount = lockedAppsMap.count { it.value }
    val totalApps = installedApps.size
    // Accessibility or Usage Access is enough to detect app launches; the
    // overlay permission keeps the lock screen launch reliable in the background.
    val hasMonitoringPermission = isAccessibilityPermissionGranted || isUsagePermissionGranted
    // Version-aware: overlay blocks only on Android 10+ (BAL rule).
    val isOverlayMandatory = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    val versionLabel = com.example.security.PermissionHelper.androidVersionLabel()
    val overlayWhy = com.example.security.PermissionHelper.overlayWhyText()
    val detectionWhy = com.example.security.PermissionHelper.detectionWhyText()

    val filteredApps = remember(installedApps, lockedAppsMap, searchQuery, selectedCategory) {
        installedApps.filter { app ->
            val isLocked = lockedAppsMap[app.packageName] ?: false

            val matchesSearch = searchQuery.isBlank() ||
                    app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategory) {
                "BANKING" -> app.category == "BANKING" || app.isSensitive
                "SOCIAL" -> app.category == "SOCIAL"
                "SYSTEM" -> app.category == "SYSTEM" || app.isSystemApp
                "LOCKED" -> isLocked
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Security,
                            contentDescription = null,
                            tint = CyberCyanLight
                        )
                        Text(
                            text = "App Locker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianDark
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Ordered permission banner: version-aware Step 1 -> Step 2.
            // On 10+: Step 1 Overlay mandatory. Below 10: only detection blocks.
            val needOverlayStep = isOverlayMandatory && !isOverlayPermissionGranted
            val needDetectionStep = (isOverlayPermissionGranted || !isOverlayMandatory) && !hasMonitoringPermission
            if (needOverlayStep || needDetectionStep) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("permission_alert_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AlertRed)
                            Text(
                                text = if (needOverlayStep)
                                    "Step 1 of 2: Display (Must allow on $versionLabel)"
                                else
                                    "Step 2 of 2: How to detect (need 1 on $versionLabel)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = if (needOverlayStep) overlayWhy else detectionWhy,
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            lineHeight = 16.sp
                        )

                        // Step 1: Overlay first, compulsory on 10+.
                        if (needOverlayStep) {
                            Button(
                                onClick = onGrantOverlayPermission,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.fillMaxWidth().testTag("grant_overlay_perm_btn")
                            ) {
                                Text("Allow Display (Mandatory on $versionLabel)", fontSize = 11.sp)
                            }
                        }

                        // Step 2: after Step 1 (or directly below 10).
                        if (needDetectionStep) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = onGrantAccessibilityPermission,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ShieldGreen),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).testTag("grant_accessibility_perm_btn")
                                ) {
                                    Text("Accessibility ★", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = onGrantUsagePermission,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).testTag("grant_usage_perm_btn")
                                ) {
                                    Text("Usage Access", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }

                        if (isOverlayPermissionGranted && isUsagePermissionGranted && !isAccessibilityPermissionGranted) {
                            TextButton(
                                onClick = onGrantAccessibilityPermission,
                                modifier = Modifier.testTag("switch_accessibility_btn")
                            ) {
                                Text("Switch to Accessibility (Recommended)", fontSize = 11.sp, color = CyberCyanLight)
                            }
                        }
                    }
                }
            }

            // Protection Overview Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    SlateSurface,
                                    CyberCyan.copy(alpha = 0.25f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (lockedCount > 0) "Real-Time Protection Active" else "Setup App Locks",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "$lockedCount of $totalApps apps protected",
                                    fontSize = 13.sp,
                                    color = TextSecondaryDark
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = if (lockedCount > 0) ShieldGreen else AlertRed,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (lockedCount > 0) AppIcons.Shield else AppIcons.GppMaybe,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Action Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onLockBanking,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .testTag("quick_lock_banking_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = AppIcons.AccountBalance,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text("Lock Banking", fontSize = 12.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = onLockAll,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .weight(0.95f)
                                    .testTag("quick_lock_all_btn")
                            ) {
                                Text("Lock All", fontSize = 12.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = onUnlockAll,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .weight(0.95f)
                                    .testTag("quick_unlock_all_btn")
                            ) {
                                Text("Unlock All", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search apps (e.g. Whatsapp)...", color = TextSecondaryDark) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryDark)
                        }
                    }
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("app_search_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = TextSecondaryDark,
                    focusedBorderColor = CyberCyanLight,
                    unfocusedBorderColor = SlateSurfaceVariant,
                    focusedContainerColor = SlateSurface,
                    unfocusedContainerColor = SlateSurface,
                    disabledContainerColor = SlateSurface,
                    cursorColor = CyberCyanLight,
                    focusedLeadingIconColor = CyberCyanLight,
                    unfocusedLeadingIconColor = TextSecondaryDark,
                    focusedTrailingIconColor = CyberCyanLight,
                    unfocusedTrailingIconColor = TextSecondaryDark,
                    focusedPlaceholderColor = TextSecondaryDark,
                    unfocusedPlaceholderColor = TextSecondaryDark,
                    focusedLabelColor = CyberCyanLight,
                    unfocusedLabelColor = TextSecondaryDark
                ),
                shape = RoundedCornerShape(16.dp)
            )

            // Category Filter Chips
            val categories = listOf(
                "ALL" to "All Apps",
                "BANKING" to "Banking & Pay",
                "SOCIAL" to "Social & Chat",
                "SYSTEM" to "System",
                "LOCKED" to "Locked ($lockedCount)"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                items(categories) { (code, title) ->
                    val isSelected = selectedCategory == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(code) },
                        label = { Text(title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = Color.White,
                            containerColor = SlateSurface,
                            labelColor = TextSecondaryDark
                        ),
                        modifier = Modifier.testTag("filter_chip_$code")
                    )
                }
            }

            // Apps List
            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.SearchOff,
                            contentDescription = null,
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No applications found",
                            fontSize = 16.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 4.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        val isLocked = lockedAppsMap[app.packageName] ?: false

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLocked) SlateSurface else SlateSurface.copy(alpha = 0.6f)
                            ),
                            onClick = { onLaunchRealApp(app) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_item_${app.packageName}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // App Logo / Icon Container
                                    AppIconImage(
                                        packageName = app.packageName,
                                        appName = app.appName,
                                        category = app.category,
                                        sizeDp = 44.dp,
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = app.appName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                            if (app.category == "BANKING") {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = CyberCyan.copy(alpha = 0.3f),
                                                    modifier = Modifier.padding(horizontal = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "BANK",
                                                        fontSize = 9.sp,
                                                        color = CyberCyanLight,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = app.packageName,
                                            fontSize = 11.sp,
                                            color = TextSecondaryDark,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // "Launch / Test App" button
                                    IconButton(
                                        onClick = { onLaunchRealApp(app) },
                                        modifier = Modifier.testTag("launch_app_btn_${app.packageName}")
                                    ) {
                                        Icon(
                                            imageVector = AppIcons.Launch,
                                            contentDescription = "Launch App",
                                            tint = if (isLocked) CyberCyanLight else TextSecondaryDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Switch to lock/unlock
                                    Switch(
                                        checked = isLocked,
                                        onCheckedChange = { onToggleLock(app) },
                                        modifier = Modifier.testTag("lock_toggle_${app.packageName}")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
