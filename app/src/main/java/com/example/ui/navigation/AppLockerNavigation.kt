package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.*
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateSurface
import com.example.viewmodel.AppLockerViewModel

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
    APPS("App Locks", Icons.Default.Apps, "nav_tab_apps"),
    LOCK_METHOD("Security", Icons.Default.Password, "nav_tab_security"),
    VAULT("Vault", Icons.Default.VpnKey, "nav_tab_vault"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_tab_settings")
}

@Composable
fun AppLockerMainNavigation(
    viewModel: AppLockerViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentTab by remember { mutableStateOf(MainTab.APPS) }
    var showIntruderLogs by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Check onboarding completion
    if (!uiState.isSetupCompleted) {
        OnboardingSetupScreen(
            onCompleteSetup = { type, pin, pattern, pass, q, a ->
                viewModel.completeOnboardingSetup(type, pin, pattern, pass, q, a)
            }
        )
        return
    }

    // Intruder Logs Screen Modal/View
    if (showIntruderLogs) {
        Scaffold(
            containerColor = ObsidianDark,
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Intruder Security Logs", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { showIntruderLogs = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        if (uiState.intruderLogs.isNotEmpty()) {
                            IconButton(onClick = viewModel::clearIntruderLogs) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Red)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianDark)
                )
            }
        ) { padding ->
            IntruderLogsScreen(
                logs = uiState.intruderLogs,
                onClearLogs = viewModel::clearIntruderLogs
            )
        }
        return
    }

    // Interactive Lock Overlay Screen (When testing overlay on a specific app inside app)
    uiState.activeSimulationPackage?.let { simApp ->
        LockOverlayScreen(
            appInfo = simApp,
            settings = uiState.securitySettings,
            onSuccessUnlock = {
                viewModel.unlockSimulationSuccess()
                viewModel.closeLockSimulation()
            },
            onFailedAttempt = { input ->
                viewModel.recordFailedUnlockAttempt(input)
            },
            onDismiss = {
                viewModel.closeLockSimulation()
            }
        )
        return
    }

    // Main Scaffold with Bottom Navigation Bar
    Scaffold(
        containerColor = ObsidianDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = SlateSurface,
                contentColor = Color.White
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberCyanLight,
                            selectedTextColor = CyberCyanLight,
                            indicatorColor = CyberCyanLight.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = ObsidianDark
        ) {
            when (currentTab) {
                MainTab.APPS -> {
                    DashboardAppsScreen(
                        installedApps = uiState.installedApps,
                        lockedAppsMap = uiState.lockedAppsMap,
                        searchQuery = uiState.searchQuery,
                        selectedCategory = uiState.selectedCategoryFilter,
                        isUsagePermissionGranted = uiState.isUsagePermissionGranted,
                        isOverlayPermissionGranted = uiState.isOverlayPermissionGranted,
                        isAccessibilityGranted = uiState.isAccessibilityGranted,
                        isProtectionActive = uiState.isProtectionActive,
                        onSearchQueryChange = viewModel::setSearchQuery,
                        onCategorySelect = viewModel::setCategoryFilter,
                        onToggleLock = viewModel::toggleAppLock,
                        onLockBanking = viewModel::lockAllBankingApps,
                        onLockAll = viewModel::lockAllApps,
                        onUnlockAll = viewModel::unlockAllApps,
                        onTestLockOverlay = viewModel::launchLockSimulation,
                        onLaunchRealApp = viewModel::launchRealApp,
                        onGrantUsagePermission = viewModel::requestUsageAccessPermission,
                        onGrantOverlayPermission = viewModel::requestOverlayPermission,
                        onGrantAccessibilityPermission = viewModel::requestAccessibilityPermission
                    )
                }
                MainTab.LOCK_METHOD -> {
                    LockTypeConfigScreen(
                        settings = uiState.securitySettings,
                        onSaveSettings = viewModel::updateLockSettings
                    )
                }
                MainTab.VAULT -> {
                    SecurityVaultScreen(
                        vaultItems = uiState.vaultItems,
                        onAddVaultItem = viewModel::addVaultItem,
                        onDeleteVaultItem = viewModel::deleteVaultItem
                    )
                }
                MainTab.SETTINGS -> {
                    SettingsAndPermissionsScreen(
                        isUsagePermissionGranted = uiState.isUsagePermissionGranted,
                        isOverlayPermissionGranted = uiState.isOverlayPermissionGranted,
                        isAccessibilityGranted = uiState.isAccessibilityGranted,
                        isProtectionActive = uiState.isProtectionActive,
                        onGrantUsagePermission = viewModel::requestUsageAccessPermission,
                        onGrantOverlayPermission = viewModel::requestOverlayPermission,
                        onGrantAccessibilityPermission = viewModel::requestAccessibilityPermission,
                        onToggleProtectionService = viewModel::toggleProtectionService,
                        onNavigateToLockConfig = { currentTab = MainTab.LOCK_METHOD },
                        onNavigateToIntruderLogs = { showIntruderLogs = true }
                    )
                }
            }
        }
    }
}
