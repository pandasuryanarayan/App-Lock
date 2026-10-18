package com.example.security

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast

object PermissionHelper {

    fun openAppDetailsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open App Info settings", Toast.LENGTH_SHORT).show()
        }
    }

    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun openOverlaySettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (ex: Exception) {
                    Toast.makeText(context, "Could not open Overlay settings", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * True when the user has enabled App Locker's accessibility service in
     * Settings > Accessibility. This is the event-driven engine — the only
     * detection path, and the reason nothing has to stay running.
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        return try {
            val expected = ComponentName(context, AppLockAccessibilityService::class.java)
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val splitter = TextUtils.SimpleStringSplitter(':')
            splitter.setString(enabledServices)
            while (splitter.hasNext()) {
                val component = ComponentName.unflattenFromString(splitter.next())
                if (component != null && component == expected) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Open Settings > Accessibility and enable App Locker",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * The ONE detection path: the event-driven accessibility service.
     *
     * There is deliberately no Usage Access fallback. UsageStatsManager exposes no
     * event/callback API, so it can only be polled — and polling means keeping a
     * foreground service resident forever, which is exactly what this build avoids.
     */
    fun hasAnyMonitoringPermission(context: Context): Boolean {
        return isAccessibilityServiceEnabled(context)
    }

    // ---- Ordered / mandatory logic (sideload build) ----
    // Step 1 (MANDATORY on 10+): Overlay — AOSP BackgroundActivityStartController
    // only lets a background app start an activity via BAL_ALLOW_SAW_PERMISSION /
    // BAL_ALLOW_NON_APP_VISIBLE_WINDOW, so LockOverlayActivity needs this. It is a
    // static grant and keeps nothing running.
    // Step 2 (MANDATORY): Accessibility — the only event-driven detection API.
    //
    // Version-aware (minSdk 24, target 36):
    //  - API < 29 (7.0-9): background launches still allowed, overlay is
    //    RECOMMENDED (reliability) but not blocking.
    //  - API >= 29 (10+): overlay is MANDATORY (BAL exemption).
    //  - API >= 33 (13+): sideloaded builds may need "Allow restricted settings".

    /** Overlay is blocking only on Android 10+. Below 10 it is recommended. */
    fun isOverlayMandatory(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /** Sideload "Allow restricted settings" lock only exists on Android 13+. */
    fun needsRestrictedSettingsGuide(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun androidVersionLabel(): String {
        val api = Build.VERSION.SDK_INT
        val name = when {
            api >= 36 -> "Android 16"
            api >= 35 -> "Android 15"
            api >= 34 -> "Android 14"
            api >= 33 -> "Android 13"
            api >= 32 -> "Android 12L"
            api >= 31 -> "Android 12"
            api >= 30 -> "Android 11"
            api >= 29 -> "Android 10"
            api >= 28 -> "Android 9"
            api >= 26 -> "Android 8"
            else -> "Android 7"
        }
        return "$name (API $api)"
    }

    /** Short version-aware why-text for Step 1, so users are not confused. */
    fun overlayWhyText(): String {
        return if (isOverlayMandatory()) {
            "Must allow on ${androidVersionLabel()} — otherwise the lock never appears."
        } else {
            "Recommended on ${androidVersionLabel()} for a reliable lock. Must allow on Android 10+."
        }
    }

    /** Why Step 2 (Accessibility) is required — the only detection path. */
    fun detectionWhyText(): String {
        return "Wakes only when a window changes. No polling, no background service, ~0% battery while idle."
    }

    /** One-line note shown at top of Settings, customized per version. */
    fun versionNoteText(): String {
        val v = androidVersionLabel()
        return when {
            Build.VERSION.SDK_INT >= 33 ->
                "Running on $v. Needs: Step 1 Display (must) + Step 2 Accessibility (must). If toggles are grey, allow restricted settings in App Info. Nothing runs in the background."
            Build.VERSION.SDK_INT >= 29 ->
                "Running on $v. Needs: Step 1 Display (must, background-launch rule) + Step 2 Accessibility (must). Nothing runs in the background."
            else ->
                "Running on $v. Needs: Step 2 Accessibility (must). Step 1 Display is recommended but optional here. Nothing runs in the background."
        }
    }

    /** True only when lock can actually work (version-aware). */
    fun isProtectionReady(context: Context): Boolean {
        val monitoring = hasAnyMonitoringPermission(context)
        return if (isOverlayMandatory()) {
            hasOverlayPermission(context) && monitoring
        } else {
            monitoring // overlay only recommended below Android 10
        }
    }

    /** Returns "OVERLAY", "DETECTION", or null when everything mandatory is done. */
    fun nextMissingStep(context: Context): String? {
        if (isOverlayMandatory() && !hasOverlayPermission(context)) return "OVERLAY"
        if (!hasAnyMonitoringPermission(context)) return "DETECTION"
        return null
    }

    fun launchApp(context: Context, packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                Toast.makeText(context, "Cannot open package: $packageName", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
