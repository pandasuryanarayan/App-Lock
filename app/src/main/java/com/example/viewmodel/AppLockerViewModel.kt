package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppLockerDatabase
import com.example.data.entities.IntruderLogEntity
import com.example.data.entities.LockedAppEntity
import com.example.data.entities.SecuritySettingsEntity
import com.example.repository.AppLockerRepository
import com.example.security.AppItemInfo
import com.example.security.AppLockStateManager
import com.example.security.PermissionHelper
import com.example.security.ProtectionPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AppLockerUiState(
    val isInitializing: Boolean = true,
    val isSetupCompleted: Boolean = false,
    val securitySettings: SecuritySettingsEntity = SecuritySettingsEntity(),
    val installedApps: List<AppItemInfo> = emptyList(),
    val lockedAppsMap: Map<String, Boolean> = emptyMap(),
    val intruderLogs: List<IntruderLogEntity> = emptyList(),
    val selectedCategoryFilter: String = "ALL", // "ALL", "BANKING", "SOCIAL", "SYSTEM", "LOCKED"
    val searchQuery: String = "",
    val activeSimulationPackage: AppItemInfo? = null, // Holds app info currently testing overlay lock
    val simulationUnlocked: Boolean = false,
    val isOverlayPermissionGranted: Boolean = false,
    val isUsagePermissionGranted: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = false,
    val isProtectionActive: Boolean = false,
    // Ordered-flow helpers: Overlay (Step 1 mandatory) + (Accessibility OR Usage) = ready.
    val isProtectionReady: Boolean = false,
    val nextMissingStep: String? = null, // "OVERLAY", "DETECTION", or null when ready
    val snackbarMessage: String? = null
)

class AppLockerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppLockerDatabase.getInstance(application)
    private val repository = AppLockerRepository(application, db.appLockerDao())

    private val _uiState = MutableStateFlow(AppLockerUiState())
    val uiState: StateFlow<AppLockerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Fetch initial settings or seed default
            val settings = repository.getInitialSettings()
            val apps = repository.getInstalledApps()

            _uiState.update {
                it.copy(
                    isInitializing = false,
                    isSetupCompleted = settings.isSetupCompleted,
                    securitySettings = settings,
                    installedApps = apps
                )
            }

            refreshPermissions()

            // Observe room DB locked apps
            launch {
                repository.lockedAppsInDb.collect { lockedEntities ->
                    val lockedMap = lockedEntities.associate { it.packageName to it.isLocked }
                    val activeLockedPackages = lockedEntities.filter { it.isLocked }.map { it.packageName }.toSet()
                    AppLockStateManager.updateLockedPackages(activeLockedPackages)

                    _uiState.update { it.copy(lockedAppsMap = lockedMap) }

                    // Auto-start protection service if any app is locked and permissions are available
                    if (activeLockedPackages.isNotEmpty()) {
                        startProtectionServiceIfPermitted()
                    }
                }
            }

            // Observe settings
            launch {
                repository.securitySettings.collect { newSettings ->
                    if (newSettings != null) {
                        _uiState.update {
                            it.copy(
                                isSetupCompleted = newSettings.isSetupCompleted,
                                securitySettings = newSettings
                            )
                        }
                    }
                }
            }

            // Observe intruder logs
            launch {
                repository.intruderLogs.collect { logs ->
                    _uiState.update { it.copy(intruderLogs = logs) }
                }
            }

            // Observe protection active flow
            launch {
                AppLockStateManager.isProtectionActive.collect { active ->
                    _uiState.update { it.copy(isProtectionActive = active) }
                }
            }
        }
    }

    fun refreshPermissions() {
        val app = getApplication<Application>()
        val hasUsage = PermissionHelper.hasUsageAccess(app)
        val hasOverlay = PermissionHelper.hasOverlayPermission(app)
        val hasAccessibility = PermissionHelper.isAccessibilityServiceEnabled(app)

        // Keep the in-memory master switch in sync with the persisted choice.
        AppLockStateManager.isMasterEnabled = ProtectionPreferences.isProtectionEnabled(app)

        if (hasAccessibility && AppLockStateManager.isMasterEnabled) {
            AppLockStateManager.setAccessibilityEngineActive(true)
        }

        _uiState.update {
            it.copy(
                isUsagePermissionGranted = hasUsage,
                isOverlayPermissionGranted = hasOverlay,
                isAccessibilityPermissionGranted = hasAccessibility,
                isProtectionReady = hasOverlay && (hasAccessibility || hasUsage),
                nextMissingStep = when {
                    !hasOverlay -> "OVERLAY"
                    !(hasAccessibility || hasUsage) -> "DETECTION"
                    else -> null
                }
            )
        }
    }

    /**
     * Picks the best available engine for the enabled protection:
     *  1. Accessibility  -> event-driven low battery engine (nothing to keep running).
     *  2. Usage Access   -> screen-on-only polling fallback.
     *  3. Neither        -> nothing runs until a permission is granted.
     */
    private fun startProtectionServiceIfPermitted() {
        val app = getApplication<Application>()
        if (!ProtectionPreferences.isProtectionEnabled(app)) return
        // Version-aware: overlay blocks only on Android 10+.
        if (PermissionHelper.isOverlayMandatory() && !PermissionHelper.hasOverlayPermission(app)) return

        if (PermissionHelper.isAccessibilityServiceEnabled(app)) {
            // The event-driven engine owns interception; make sure the polling
            // fallback (and its permanent notification) is not running.
            PermissionHelper.stopRealTimeProtectionService(app)
            AppLockStateManager.setPollingEngineActive(false)
        } else if (PermissionHelper.hasUsageAccess(app)) {
            PermissionHelper.startRealTimeProtectionService(app)
        }
    }

    fun requestUsageAccessPermission() {
        PermissionHelper.openUsageAccessSettings(getApplication())
    }

    /**
     * Compulsory gate (sideload build, version-aware): Step 1 Overlay mandatory
     * only on Android 10+, Step 2 one detection always (★ recommended).
     */
    private fun ensureMandatoryPermissions(): Boolean {
        val ctx = getApplication<Application>()
        if (PermissionHelper.isOverlayMandatory() && !PermissionHelper.hasOverlayPermission(ctx)) {
            showSnackbar("Step 1 required on ${PermissionHelper.androidVersionLabel()}: allow Display first.")
            PermissionHelper.openOverlaySettings(ctx)
            return false
        }
        if (!PermissionHelper.hasAnyMonitoringPermission(ctx)) {
            showSnackbar("Step 2 required: enable Accessibility (recommended) or Usage Access.")
            PermissionHelper.openAccessibilitySettings(ctx)
            return false
        }
        return true
    }

    fun requestAccessibilityPermission() {
        PermissionHelper.openAccessibilitySettings(getApplication())
    }

    fun requestOverlayPermission() {
        PermissionHelper.openOverlaySettings(getApplication())
    }

    fun openAppDetailsSettings() {
        PermissionHelper.openAppDetailsSettings(getApplication())
    }

    /**
     * The "Real-Time Protection" toggle drives both engines:
     *  - ON  -> activate the best available engine (Accessibility first).
     *  - OFF -> stop the polling service and make the accessibility service
     *           ignore every event, so nothing runs in the background.
     *
     * Order is enforced: Step 1 Overlay (mandatory) must be granted first,
     * then Step 2 one detection (Accessibility recommended, Usage fallback).
     */
    fun toggleProtectionService(enable: Boolean) {
        val app = getApplication<Application>()

        if (!enable) {
            ProtectionPreferences.setProtectionEnabled(app, false)
            AppLockStateManager.isMasterEnabled = false
            PermissionHelper.stopRealTimeProtectionService(app)
            AppLockStateManager.setAccessibilityEngineActive(false)
            AppLockStateManager.setPollingEngineActive(false)
            showSnackbar("Protection paused. Locked apps are not intercepted.")
            return
        }

        // Compulsory gate: version-aware mandatory permissions first.
        if (PermissionHelper.isOverlayMandatory() && !PermissionHelper.hasOverlayPermission(app)) {
            showSnackbar("Step 1 required on ${PermissionHelper.androidVersionLabel()}: allow Display first.")
            PermissionHelper.openOverlaySettings(app)
            return
        }
        if (!PermissionHelper.hasAnyMonitoringPermission(app)) {
            showSnackbar("Step 2 required: enable Accessibility (recommended) or Usage Access.")
            PermissionHelper.openAccessibilitySettings(app)
            return
        }

        ProtectionPreferences.setProtectionEnabled(app, true)
        AppLockStateManager.isMasterEnabled = true
        refreshPermissions()

        val hasAccessibility = PermissionHelper.isAccessibilityServiceEnabled(app)
        val hasUsage = PermissionHelper.hasUsageAccess(app)

        when {
            hasAccessibility -> {
                // Event-driven: nothing needs to stay running in the background.
                PermissionHelper.stopRealTimeProtectionService(app)
                AppLockStateManager.setAccessibilityEngineActive(true)
                showSnackbar("Low Battery Mode active - event-driven protection enabled.")
            }
            hasUsage -> {
                PermissionHelper.startRealTimeProtectionService(app)
                showSnackbar("Compatibility Mode active (screen-on monitor). Grant Accessibility for low battery usage.")
            }
            else -> {
                showSnackbar("Enable Accessibility (or Usage Access) to activate protection.")
                PermissionHelper.openAccessibilitySettings(app)
            }
        }
    }

    fun launchRealApp(app: AppItemInfo) {
        val context = getApplication<Application>()
        val isLocked = _uiState.value.lockedAppsMap[app.packageName] ?: false

        if (isLocked) {
            if (AppLockStateManager.shouldIntercept(app.packageName)) {
                // Intercept with lock screen
                launchLockSimulation(app)
                return
            }
        }

        val launched = PermissionHelper.launchApp(context, app.packageName)
        if (!launched) {
            showSnackbar("${app.appName} is unlocked and running.")
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun toggleAppLock(app: AppItemInfo) {
        if (!ensureMandatoryPermissions()) return
        viewModelScope.launch {
            val isCurrentlyLocked = _uiState.value.lockedAppsMap[app.packageName] ?: false
            repository.toggleAppLock(app.packageName, app.appName, app.category, isCurrentlyLocked)

            if (!isCurrentlyLocked) {
                // User is locking the app
                AppLockStateManager.addLockedPackage(app.packageName)
                startProtectionServiceIfPermitted()
                showSnackbar("Locked ${app.appName}. Real-time protection active.")
            } else {
                AppLockStateManager.removeLockedPackage(app.packageName)
                showSnackbar("Unlocked ${app.appName}.")
            }
        }
    }

    fun lockAllBankingApps() {
        if (!ensureMandatoryPermissions()) return
        viewModelScope.launch {
            repository.lockAllBankingApps(_uiState.value.installedApps)
            startProtectionServiceIfPermitted()
            showSnackbar("Protected all Banking & Payment apps in real time!")
        }
    }

    fun lockAllApps() {
        if (!ensureMandatoryPermissions()) return
        viewModelScope.launch {
            repository.lockAllApps(_uiState.value.installedApps)
            startProtectionServiceIfPermitted()
            showSnackbar("All applications locked with real protection!")
        }
    }

    fun unlockAllApps() {
        viewModelScope.launch {
            repository.unlockAllApps(_uiState.value.installedApps)
            AppLockStateManager.relockAll()
            showSnackbar("Unlocked all applications.")
        }
    }

    fun completeOnboardingSetup(
        lockType: String,
        pinCode: String,
        patternCode: String,
        passwordCode: String,
        securityQuestion: String,
        securityAnswer: String
    ) {
        viewModelScope.launch {
            val updated = _uiState.value.securitySettings.copy(
                activeLockType = lockType,
                pinCode = pinCode,
                patternCode = patternCode,
                passwordCode = passwordCode,
                securityQuestion = securityQuestion,
                securityAnswer = securityAnswer,
                isBiometricEnabled = if (lockType == com.example.data.entities.LockType.BIOMETRIC.name) true else _uiState.value.securitySettings.isBiometricEnabled,
                isSetupCompleted = true
            )
            repository.updateSecuritySettings(updated)
            // Auto lock sensitive banking apps on first setup
            repository.lockAllBankingApps(_uiState.value.installedApps)
            startProtectionServiceIfPermitted()
            showSnackbar("Master security lock configured!")
        }
    }

    fun updateLockSettings(updatedSettings: SecuritySettingsEntity) {
        viewModelScope.launch {
            repository.updateSecuritySettings(updatedSettings)
            showSnackbar("Security preferences updated.")
        }
    }

    fun launchLockSimulation(app: AppItemInfo) {
        _uiState.update {
            it.copy(
                activeSimulationPackage = app,
                simulationUnlocked = false
            )
        }
    }

    fun closeLockSimulation() {
        _uiState.update {
            it.copy(
                activeSimulationPackage = null,
                simulationUnlocked = false
            )
        }
    }

    fun unlockSimulationSuccess() {
        val app = _uiState.value.activeSimulationPackage
        if (app != null) {
            AppLockStateManager.markTemporarilyUnlocked(app.packageName)
            val context = getApplication<Application>()
            PermissionHelper.launchApp(context, app.packageName)
        }
        _uiState.update { it.copy(simulationUnlocked = true) }
        showSnackbar("App unlocked successfully!")
    }

    fun recordFailedUnlockAttempt(input: String) {
        viewModelScope.launch {
            val currentApp = _uiState.value.activeSimulationPackage
            val appName = currentApp?.appName ?: "System Application"
            val pkgName = currentApp?.packageName ?: "com.android.system"
            val lockType = _uiState.value.securitySettings.activeLockType

            repository.logIntruderAttempt(appName, pkgName, lockType, input)
            showSnackbar("Incorrect lock code! Unauthorized attempt recorded.")
        }
    }

    fun clearIntruderLogs() {
        viewModelScope.launch {
            repository.clearIntruderLogs()
            showSnackbar("Intruder log history cleared.")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
