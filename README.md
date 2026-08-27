# 🛡️ App Locker

A modern, high-performance Android App Locker application built with **Jetpack Compose**, **Room Database**, and **Material Design 3**. Protect sensitive applications, files, and personal data using PIN, 3x3 Pattern, Alphanumeric Password, or Hardware Biometrics (Fingerprint & Face Unlock) with real-time foreground detection and instant overlay protection.

---

## ✨ Features

- 🔐 **Multiple Lock Types**:
  - **4-Digit PIN**: Custom tactile numpad with haptic feedback.
  - **3x3 Pattern Lock**: Smooth gesture-based pattern connector canvas.
  - **Alphanumeric Password**: Full password protection with visibility toggles.
  - **Biometric Authentication**: Seamless Android `BiometricPrompt` integration (Fingerprint / Face Unlock).
- ⚡ **Real-Time Detection Engine**:
  - **Foreground Monitoring**: High-precision detection via Android `UsageStatsManager` with battery-optimized polling.
- 🖼️ **Non-Intrusive Overlay Screen**:
  - System Alert Window (`SYSTEM_ALERT_WINDOW`) overlay presenting authentication without killing background processes.
  - Quick-dismiss and emergency recovery questions.
- 🗄️ **Encrypted Security Vault**:
  - Store secret notes, passwords, and private text records securely on-device.
- 🚨 **Intruder Detection & Security Audit Logs**:
  - Captures failed unlock attempts with exact timestamps and target app names.
  - Chronological activity history with quick-clear and review capabilities.
- 🎨 **Sleek Cyber-Dark Theme**:
  - Custom Material 3 dark color scheme featuring Neon Cyan accents (`#00F5FF`) and Shield Emerald security indicators.
  - Fully responsive across phones, foldables, and tablets.
- 📦 **Ultra Lightweight (< 5MB)**:
  - Vectorized custom icon system and R8 optimization for minimal disk footprint and lightning-fast cold start.

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
- **Language**: [Kotlin](https://kotlinlang.org/) (Coroutines, StateFlow)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) with KSP
- **Biometrics**: `androidx.biometric:biometric-ktx`
- **Security & Services**:
  - `UsageStatsManager` & `AppOpsManager` for foreground app usage monitoring
  - `WindowManager` & `SYSTEM_ALERT_WINDOW` for custom overlay rendering
  - `ForegroundService` with `specialUse` type for persistent real-time monitoring

---

## 📱 Permissions Required

App Locker requires the following Android system permissions to protect applications:

| Permission | Purpose |
| :--- | :--- |
| `PACKAGE_USAGE_STATS` | Detects when protected apps are launched in the foreground. |
| `SYSTEM_ALERT_WINDOW` | Displays the security lock overlay above locked applications. |
| `USE_BIOMETRIC` | Enables fingerprint and biometric unlock prompts. |
| `POST_NOTIFICATIONS` | Displays the active protection foreground service notification. |
| `VIBRATE` | Provides haptic feedback during unlock interactions. |

> **Note for Sideloaded Release APKs (Android 13+)**:
> When manually installing an APK from GitHub or storage, Android 13+ may restrict Special App Access permissions by default ("Restricted setting"). To grant them:
> 1. Go to **Settings > Apps > App Locker** (or tap **Open App Info** inside the app's Settings tab).
> 2. Tap the **3 vertical dots (⋮)** in the top-right corner.
> 3. Select **Allow restricted settings** and authenticate with your lock screen.
> 4. Return to App Locker to toggle **Usage Access** and **Display Over Other Apps**.

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Android Studio Ladybug / Meerkat (or newer)
- **Minimum SDK**: Android 7.0 (API Level 24)
- **Target SDK**: Android 16 (API Level 36)
- **JDK**: Java 17 or Java 21

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/pandasuryanarayan/App-Lock.git
   cd App-Lock
   ```

2. **Open in Android Studio**:
   - Open Android Studio and select **Open**, then select the project directory.

3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   The generated APK will be located at:
   `app/build/outputs/apk/debug/app-debug.apk`

4. **Run on Device / Emulator**:
   - Connect an Android device (with USB debugging enabled) or start an Android Virtual Device (AVD).
   - Press **Run ▶** in Android Studio or execute:
     ```bash
     ./gradlew installDebug
     ```

5. **Build Signed Release APK** (optional):
   The release build is signed with an upload key stored at `my-upload-key.jks` (not tracked in git — see below). Provide the signing credentials via environment variables to avoid hardcoding secrets:
   ```bash
   export KEYSTORE_PATH="$PWD/my-upload-key.jks"
   export STORE_PASSWORD="your-store-password"
   export KEY_PASSWORD="your-key-password"
   ./gradlew assembleRelease
   ```
   The signed APK will be located at:
   `app/build/outputs/apk/release/app-release.apk`

   > **⚠️ Keep your signing key safe**: The `my-upload-key.jks` file is excluded from git (see `.gitignore`). Back it up securely — if you lose it, you will **not** be able to update your app on the Play Store under the same identity. For Eg:- applock

---

## 🔒 Security & Privacy

- **100% Offline & Local**: All credentials, secret questions, and vault notes are stored strictly on-device in a local SQLite database powered by Room.
- **Zero Cloud Tracking**: No external telemetry, advertising trackers, or unauthorized internet transmission of user data.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE.md).
