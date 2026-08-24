package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppLockerDatabase
import com.example.data.entities.IntruderLogEntity
import com.example.data.entities.LockedAppEntity
import com.example.data.entities.SecuritySettingsEntity
import com.example.data.entities.VaultItemEntity
import com.example.repository.AppLockerRepository
import com.example.security.AppItemInfo
import com.example.security.AppLockStateManager
import com.example.security.PermissionHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AppLockerUiState(
    val isSetupCompleted: Boolean = false,
    val securitySettings: SecuritySettingsEntity = SecuritySettingsEntity(),
    val installedApps: List<AppItemInfo> = emptyList(),
    val lockedAppsMap: Map<String, Boolean> = emptyMap(),
    val intruderLogs: List<IntruderLogEntity> = emptyList(),
    val vaultItems: List<VaultItemEntity> = emptyList(),
    val selectedCategoryFilter: String = "ALL", // "ALL", "BANKING", "SOCIAL", "SYSTEM", "LOCKED"
    val searchQuery: String = "",
    val activeSimulationPackage: AppItemInfo? = null, // Holds app info currently testing overlay lock
    val simulationUnlocked: Boolean = false,
    val isOverlayPermissionGranted: Boolean = false,
    val isUsagePermissionGranted: Boolean = false,
    val isAccessibilityGranted: Boolean = false,
    val isProtectionActive: Boolean = false,
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

            // Observe vault
            launch {
                repository.vaultItems.collect { items ->
                    _uiState.update { it.copy(vaultItems = items) }
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
        val hasAccess = PermissionHelper.isAccessibilityServiceEnabled(app)

        _uiState.update {
            it.copy(
                isUsagePermissionGranted = hasUsage,
                isOverlayPermissionGranted = hasOverlay,
                isAccessibilityGranted = hasAccess
            )
        }
    }

    private fun startProtectionServiceIfPermitted() {
        val app = getApplication<Application>()
        val hasUsage = PermissionHelper.hasUsageAccess(app)
        val hasOverlay = PermissionHelper.hasOverlayPermission(app)
        if (hasUsage || hasOverlay) {
            PermissionHelper.startRealTimeProtectionService(app)
        }
    }

    fun requestUsageAccessPermission() {
        PermissionHelper.openUsageAccessSettings(getApplication())
    }

    fun requestOverlayPermission() {
        PermissionHelper.openOverlaySettings(getApplication())
    }

    fun requestAccessibilityPermission() {
        PermissionHelper.openAccessibilitySettings(getApplication())
    }

    fun openAppDetailsSettings() {
        PermissionHelper.openAppDetailsSettings(getApplication())
    }

    fun toggleProtectionService(enable: Boolean) {
        val app = getApplication<Application>()
        if (enable) {
            PermissionHelper.startRealTimeProtectionService(app)
            showSnackbar("Real-Time App Lock Service Started")
        } else {
            PermissionHelper.stopRealTimeProtectionService(app)
            showSnackbar("Real-Time App Lock Service Paused")
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
        viewModelScope.launch {
            repository.lockAllBankingApps(_uiState.value.installedApps)
            startProtectionServiceIfPermitted()
            showSnackbar("Protected all Banking & Payment apps in real time!")
        }
    }

    fun lockAllApps() {
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

    fun addVaultItem(title: String, secretContent: String, category: String) {
        viewModelScope.launch {
            repository.addVaultItem(title, secretContent, category)
            showSnackbar("Saved item to Private Vault.")
        }
    }

    fun deleteVaultItem(id: Long) {
        viewModelScope.launch {
            repository.deleteVaultItem(id)
            showSnackbar("Removed item from vault.")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
