package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.data.db.AppLockerDatabase
import com.example.data.entities.IntruderLogEntity
import com.example.data.entities.SecuritySettingsEntity
import com.example.security.AppItemInfo
import com.example.security.AppLockStateManager
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LockOverlayActivity : FragmentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_target_package_name"
        const val EXTRA_APP_NAME = "extra_target_app_name"
    }

    private var targetPackageName: String = ""
    private var targetAppName: String = ""
    private var appItemInfo: AppItemInfo = AppItemInfo("unknown", "Protected Application", "SYSTEM", true, true)
    private var securitySettings: SecuritySettingsEntity = SecuritySettingsEntity()
    private var biometricPromptManager: com.example.security.BiometricPromptManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLockStateManager.isOverlayShowing = true
        enableEdgeToEdge()

        biometricPromptManager = com.example.security.BiometricPromptManager(this)

        targetPackageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        targetAppName = intent.getStringExtra(EXTRA_APP_NAME) ?: ""

        resolveAppInfo()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sendUserToHomeScreen()
            }
        })

        lifecycleScope.launch {
            val db = AppLockerDatabase.getInstance(applicationContext)
            val settings = withContext(Dispatchers.IO) {
                db.appLockerDao().getSecuritySettingsSync()
            } ?: SecuritySettingsEntity()
            securitySettings = settings

            setContent {
                MyApplicationTheme {
                    LockOverlayScreen(
                        appInfo = appItemInfo,
                        settings = securitySettings,
                        onSuccessUnlock = {
                            handleSuccessUnlock()
                        },
                        onFailedAttempt = { input ->
                            handleFailedAttempt(input)
                        },
                        onDismiss = {
                            sendUserToHomeScreen()
                        },
                        onTriggerBiometric = {
                            triggerBiometricPrompt()
                        }
                    )
                }
            }

            // If biometric is enabled or active lock type is BIOMETRIC, trigger biometric check on start
            if (securitySettings.isBiometricEnabled || securitySettings.activeLockType == com.example.data.entities.LockType.BIOMETRIC.name) {
                triggerBiometricPrompt()
            }
        }
    }

    private fun triggerBiometricPrompt() {
        val manager = biometricPromptManager ?: com.example.security.BiometricPromptManager(this).also {
            biometricPromptManager = it
        }

        manager.showBiometricPrompt(
            title = "Unlock ${appItemInfo.appName}",
            subtitle = "Touch sensor or scan face to unlock",
            negativeButtonText = "Use Backup PIN/Pass"
        ) { result ->
            when (result) {
                is com.example.security.BiometricResult.AuthenticationSuccess -> {
                    handleSuccessUnlock()
                }
                is com.example.security.BiometricResult.AuthenticationFailed -> {
                    handleFailedAttempt("Biometric mismatch")
                }
                else -> {
                    // Cancelled or fallback to PIN/Pass pad
                }
            }
        }
    }

    private fun resolveAppInfo() {
        if (targetPackageName.isNotEmpty()) {
            try {
                val pm = packageManager
                val appInfo = pm.getApplicationInfo(targetPackageName, 0)
                val label = pm.getApplicationLabel(appInfo).toString()
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                val lower = targetPackageName.lowercase()
                val category = when {
                    lower.contains("bank") || lower.contains("pay") || lower.contains("wallet") -> "BANKING"
                    lower.contains("whatsapp") || lower.contains("instagram") || lower.contains("social") -> "SOCIAL"
                    isSystem -> "SYSTEM"
                    else -> "OTHER"
                }

                appItemInfo = AppItemInfo(
                    packageName = targetPackageName,
                    appName = if (targetAppName.isNotEmpty()) targetAppName else label,
                    category = category,
                    isSystemApp = isSystem,
                    isSensitive = true
                )
            } catch (e: Exception) {
                appItemInfo = AppItemInfo(
                    packageName = targetPackageName,
                    appName = if (targetAppName.isNotEmpty()) targetAppName else targetPackageName,
                    category = "SYSTEM",
                    isSystemApp = true,
                    isSensitive = true
                )
            }
        }
    }

    private fun handleSuccessUnlock() {
        if (targetPackageName.isNotEmpty()) {
            AppLockStateManager.markTemporarilyUnlocked(targetPackageName)
            try {
                val pm = packageManager
                val launchIntent = pm.getLaunchIntentForPackage(targetPackageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(launchIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        AppLockStateManager.isOverlayShowing = false
        finish()
    }

    private fun handleFailedAttempt(input: String) {
        vibratePhone()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppLockerDatabase.getInstance(applicationContext)
                db.appLockerDao().insertIntruderLog(
                    IntruderLogEntity(
                        appName = appItemInfo.appName,
                        packageName = appItemInfo.packageName,
                        failedAttemptCount = 1,
                        lockTypeUsed = securitySettings.activeLockType,
                        attemptInput = input,
                        note = "Real-time overlay blocked unauthorized unlock"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun vibratePhone() {
        try {
            if (securitySettings.vibrationEnabled) {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(200)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendUserToHomeScreen() {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(homeIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            AppLockStateManager.isOverlayShowing = false
            finish()
        }
    }

    override fun onDestroy() {
        AppLockStateManager.isOverlayShowing = false
        super.onDestroy()
    }
}
