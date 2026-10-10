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
import com.example.ui.components.AppIcons
import com.example.ui.screens.*
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateSurface
import com.example.viewmodel.AppLockerViewModel

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
    APPS("App Locks", AppIcons.Apps, "nav_tab_apps"),
    LOCK_METHOD("Security", AppIcons.Password, "nav_tab_security"),
    SETTINGS("Settings", AppIcons.Settings, "nav_tab_settings")
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
                            Icon(AppIcons.ArrowBack, contentDescription = "Back", tint = Color.White)
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
        val context = androidx.compose.ui.platform.LocalContext.current
        val activity = context as? androidx.fragment.app.FragmentActivity

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
            },
            onTriggerBiometric = {
                if (activity != null) {
                    val promptManager = com.example.security.BiometricPromptManager(activity)
                    promptManager.showBiometricPrompt(
                        title = "Unlock ${simApp.appName}",
                        subtitle = "Touch sensor or scan face to unlock",
                        negativeButtonText = "Use Backup PIN"
                    ) { result ->
                        when (result) {
                            is com.example.security.BiometricResult.AuthenticationSuccess -> {
                                viewModel.unlockSimulationSuccess()
                                viewModel.closeLockSimulation()
                            }
                            is com.example.security.BiometricResult.AuthenticationFailed -> {
                                viewModel.recordFailedUnlockAttempt("Biometric mismatch")
                            }
                            else -> Unit
                        }
                    }
                } else {
                    viewModel.unlockSimulationSuccess()
                    viewModel.closeLockSimulation()
                }
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
                        onGrantOverlayPermission = viewModel::requestOverlayPermission
                    )
                }
                MainTab.LOCK_METHOD -> {
                    LockTypeConfigScreen(
                        settings = uiState.securitySettings,
                        onSaveSettings = viewModel::updateLockSettings
                    )
                }
                MainTab.SETTINGS -> {
                    SettingsAndPermissionsScreen(
                        isUsagePermissionGranted = uiState.isUsagePermissionGranted,
                        isOverlayPermissionGranted = uiState.isOverlayPermissionGranted,
                        isProtectionActive = uiState.isProtectionActive,
                        onGrantUsagePermission = viewModel::requestUsageAccessPermission,
                        onGrantOverlayPermission = viewModel::requestOverlayPermission,
                        onOpenAppDetails = viewModel::openAppDetailsSettings,
                        onToggleProtectionService = viewModel::toggleProtectionService,
                        onNavigateToLockConfig = { currentTab = MainTab.LOCK_METHOD },
                        onNavigateToIntruderLogs = { showIntruderLogs = true }
                    )
                }
            }
        }
    }
}
