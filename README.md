# 🛡️ App Locker

> **Secure your apps. Stay private — fully on-device.**

A modern, high-performance Android App Locker built with **Jetpack Compose**, **Room Database**, and **Material Design 3**. Protect sensitive applications, files, and personal data using **PIN**, **3×3 Pattern**, **Alphanumeric Password**, or **Hardware Biometrics** (Fingerprint & Face Unlock) with real-time foreground detection and instant overlay protection.

[![Platform: Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](#-requirements)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue)](#-requirements)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36%20(Android%2016)-blue)](#-requirements)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-orange)](#-architecture)
[![License: MIT](https://img.shields.io/badge/License-MIT-green)](LICENSE.md)
[![APK Size](https://img.shields.io/badge/APK%20Size-%3C%205MB-success)](#-performance)

---

## 📑 Table of Contents

1. [✨ Features](#-features)
2. [🏗️ Architecture](#-architecture)
3. [📁 Project Structure](#-project-structure)
4. [🧩 Key Components](#-key-components)
5. [📱 Permissions Required](#-permissions-required)
6. [🚀 Getting Started](#-getting-started)
7. [🧪 Testing](#-testing)
8. [📦 Build Variants & Signing](#-build-variants--signing)
9. [⚡ Performance](#-performance)
10. [🐛 Troubleshooting](#-troubleshooting)
11. [🗺️ Roadmap](#-roadmap)
12. [🤝 Contributing](#-contributing)
13. [🔒 Security & Privacy](#-security--privacy)
14. [📄 License](#-license)
15. [🙏 Acknowledgments](#-acknowledgments)

---

## ✨ Features

- 🔐 **Multiple Lock Types**:
  - **4-Digit PIN** — Custom tactile numpad with haptic feedback.
  - **3×3 Pattern Lock** — Smooth gesture-based pattern connector canvas.
  - **Alphanumeric Password** — Full password protection with visibility toggles.
  - **Biometric Authentication** — Seamless Android `BiometricPrompt` integration (Fingerprint / Face Unlock).
- ⚡ **Real-Time Detection Engine**:
  - **Foreground Monitoring** — High-precision detection via Android `UsageStatsManager` with battery-optimized polling.
- 🖼️ **Non-Intrusive Overlay Screen**:
  - System Alert Window (`SYSTEM_ALERT_WINDOW`) overlay presenting authentication without killing background processes.
  - Quick-dismiss and emergency recovery questions.
- 🚨 **Intruder Detection & Security Audit Logs**:
  - Captures failed unlock attempts with exact timestamps and target app names.
  - Chronological activity history with quick-clear and review capabilities.
- 🎨 **Sleek Cyber-Dark Theme**:
  - Custom Material 3 dark color scheme featuring Neon Cyan accents (`#00F5FF`) and Shield Emerald security indicators.
  - Fully responsive across phones, foldables, and tablets.
- 📦 **Ultra Lightweight (< 5MB)**:
  - Vectorized custom icon system and R8 optimization for minimal disk footprint and lightning-fast cold start.

---

## 🏗️ Architecture

App Locker follows a pragmatic **MVVM + Repository** architecture with a unidirectional data flow, designed to keep responsibilities clearly separated and testable.

```
┌──────────────────────────────────────────────────────────────────┐
│                              UI Layer                            │
│  Compose Screens ──► ViewModel (StateFlow) ──► Navigation Graph  │
└────────────────────────────┬─────────────────────────────────────┘
                             │ coroutines / Flow
┌────────────────────────────▼─────────────────────────────────────┐
│                         Domain / Repository                      │
│            AppLockerRepository (single source of truth)          │
└────────────────────────────┬─────────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────────┐
│                          Data Sources                            │
│   Room DAO  •  InstalledAppsManager  •  BiometricPromptManager   │
└──────────────────────────────────────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────────┐
│                      System Services Layer                       │
│  AppLockForegroundService (specialUse)  •  BootReceiver  •  OS   │
└──────────────────────────────────────────────────────────────────┘
```

### 🎯 Design Principles

| Principle | How it's applied |
| :--- | :--- |
| **Single source of truth** | All UI state is exposed by `AppLockerViewModel` as immutable `StateFlow`. |
| **Unidirectional data flow** | Composables emit events upward; the ViewModel owns the state. |
| **Reactive persistence** | Room queries return `Flow<…>`; the UI recomposes automatically on DB changes. |
| **Testability** | Pure Kotlin ViewModel + repository interfaces are unit-testable with Robolectric/JUnit. |
| **No business logic in Composables** | Screens delegate to the ViewModel; no `runBlocking`, no DB calls in `@Composable`. |
| **System-aware** | Service lifecycle tied to boot + protection toggle, never left dangling. |

### 🛠️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| **UI** | [Jetpack Compose](https://developer.android.com/jetpack/compose) · Material Design 3 · Compose BOM `2024.09.00` |
| **Language** | [Kotlin `2.2.10`](https://kotlinlang.org/) · Coroutines · StateFlow / Flow |
| **Architecture** | MVVM + Repository · unidirectional data flow |
| **Persistence** | [Room `2.7.0`](https://developer.android.com/training/data-storage/room) with [KSP `2.3.5`](https://github.com/google/ksp) |
| **Biometrics** | `androidx.biometric:biometric:1.1.0` |
| **Lifecycle** | `androidx.lifecycle:lifecycle-runtime-compose:2.8.7` |
| **Build** | Android Gradle Plugin `9.1.1` · Gradle Toolchains (Java 11 source/target) |
| **Tests** | JUnit 4 · Robolectric `4.16.1` · Roborazzi `1.59.0` (screenshot testing) · Espresso `3.7.0` |
| **Secrets** | [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin) for `.env` injection |

> All dependency versions are centrally declared in [`gradle/libs.versions.toml`](gradle/libs.versions.toml) — upgrade with confidence.

---

## 📁 Project Structure

```
App-Lock/
├── app/
│   ├── build.gradle.kts                # Module config, signing, R8, deps
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # Permissions, FGS, receivers, overlay activity
│       │   ├── java/com/example/
│       │   │   ├── data/
│       │   │   │   ├── db/             # Room database & type converters
│       │   │   │   ├── dao/            # AppLockerDao — single source of DB access
│       │   │   │   └── entities/       # LockedApp, SecuritySettings, IntruderLog
│       │   │   ├── repository/         # AppLockerRepository — domain layer
│       │   │   ├── security/           # AppLockForegroundService, AppLockStateManager,
│       │   │   │                       # BiometricPromptManager, InstalledAppsManager,
│       │   │   │                       # PermissionHelper, BootReceiver
│       │   │   ├── viewmodel/          # AppLockerViewModel — UI state & business logic
│       │   │   └── ui/
│       │   │       ├── theme/          # Material 3 cyber-dark theme & color tokens
│       │   │       ├── components/     # AppIconImage, AppIcons,
│       │   │       │                   # PinPadView, PatternLockCanvas
│       │   │       ├── navigation/     # Compose Navigation graph
│       │   │       └── screens/        # OnboardingSetup, DashboardApps, LockTypeConfig,
│       │   │                           # LockOverlay (Activity + Screen),
│       │   │                           # IntruderLogs, SettingsAndPermissions
│       │   └── res/
│       │       ├── drawable/           # Vector icons & custom backgrounds
│       │       ├── mipmap-*/           # Adaptive launcher icons
│       │       ├── values/             # Strings, colors, themes
│       │       └── xml/                # Backup & data extraction rules
│       ├── test/                       # Local unit tests (Robolectric)
│       └── androidTest/                # Instrumented tests (Espresso)
├── gradle/
│   └── libs.versions.toml              # Version catalog (single source for deps)
├── build.gradle.kts                    # Root build script
├── settings.gradle.kts                 # Project settings & repositories
├── metadata.json                       # AI Studio / app metadata
├── release/                            # Versioned signed APKs (tracked, e.g. AppLock-v1.0-release.apk)
├── .env.example                        # Template for local secrets (e.g. Gemini API key)
├── my-upload-key.jks                   # ⚠️ Local upload key — gitignored
├── LICENSE.md
└── README.md
```

---

## 🧩 Key Components

| Component | Role | Source |
| :--- | :--- | :--- |
| **`AppLockerViewModel`** | Exposes UI state as `StateFlow`; orchestrates lock configuration and app selection. | `viewmodel/AppLockerViewModel.kt` |
| **`AppLockerRepository`** | Single source of truth wrapping the Room DAO; translates entities into domain models. | `repository/AppLockerRepository.kt` |
| **`AppLockerDao`** | All Room queries (locked apps, security settings, intruder logs). | `data/dao/AppLockerDao.kt` |
| **Entities** | `LockedAppEntity`, `SecuritySettingsEntity`, `IntruderLogEntity`. | `data/entities/` |
| **`AppLockForegroundService`** | `specialUse` foreground service that polls `UsageStatsManager` and triggers the lock overlay when a protected app comes to the front. | `security/AppLockForegroundService.kt` |
| **`AppLockStateManager`** | Tracks the global "protection active" state and current foreground package. | `security/AppLockStateManager.kt` |
| **`BiometricPromptManager`** | Wraps `androidx.biometric` and surfaces success/error results to the ViewModel. | `security/BiometricPromptManager.kt` |
| **`InstalledAppsManager`** | Queries `PackageManager` to build the user's pickable app list. | `security/InstalledAppsManager.kt` |
| **`PermissionHelper`** | Centralized runtime + special-access permission checks and rationale flows. | `security/PermissionHelper.kt` |
| **`BootReceiver`** | Re-enables protection after device reboot so the lock is never silently disabled. | `security/BootReceiver.kt` |
| **`LockOverlayActivity`** | Transparent `singleInstance` Activity launched over the target app that hosts `LockOverlayScreen`. | `ui/screens/LockOverlayActivity.kt` |
| **`PinPadView`** | Custom Compose canvas for tactile 4-digit PIN entry with haptics. | `ui/components/PinPadView.kt` |
| **`PatternLockCanvas`** | Custom Compose canvas for gesture-based 3×3 pattern input. | `ui/components/PatternLockCanvas.kt` |

### 🧭 Screen Map

| Screen | File | Purpose |
| :--- | :--- | :--- |
| Onboarding | `OnboardingSetupScreen.kt` | First-run flow: choose lock type, set credential, grant permissions. |
| Dashboard | `DashboardAppsScreen.kt` | List of installed apps with toggles to add/remove protection. |
| Lock Type Config | `LockTypeConfigScreen.kt` | Configure PIN / Pattern / Password / Biometric. |
| Lock Overlay | `LockOverlayScreen.kt` (+ `LockOverlayActivity`) | The full-screen unlock surface shown over protected apps. |
| Intruder Logs | `IntruderLogsScreen.kt` | Chronological history of failed unlock attempts. |
| Settings & Permissions | `SettingsAndPermissionsScreen.kt` | Toggle protection, manage permissions, jump to system settings. |

---

## 📱 Permissions Required

App Locker requires the following Android system permissions to deliver on-device protection:

| Permission | Purpose | Requested In |
| :--- | :--- | :--- |
| `PACKAGE_USAGE_STATS` | Detects when protected apps are launched in the foreground. | `SettingsAndPermissionsScreen` (special access) |
| `SYSTEM_ALERT_WINDOW` | Displays the security lock overlay above locked applications. | `SettingsAndPermissionsScreen` (special access) |
| `USE_BIOMETRIC` / `USE_FINGERPRINT` | Enables fingerprint and biometric unlock prompts. | `LockTypeConfigScreen` |
| `POST_NOTIFICATIONS` | Displays the active protection foreground service notification. | Runtime (API 33+) |
| `VIBRATE` | Provides haptic feedback during unlock interactions. | Manifest |
| `RECEIVE_BOOT_COMPLETED` | Restarts protection automatically after device reboot. | `BootReceiver` |
| `QUERY_ALL_PACKAGES` | Enumerates installed apps for the lock selection list. | Manifest (API 30+) |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_SPECIAL_USE` | Runs the persistent monitoring service. | `AppLockForegroundService` |
| `REORDER_TASKS` | Helps bring the overlay to the front when a protected app is launched. | Manifest |
| `CAMERA` | Reserved for future biometric / intruder-capture features. | Manifest |

> **Why does App Locker need Usage Access and Display Over Other Apps?**
> These are *Special App Access* permissions, not runtime permissions. Android will never show a normal dialog — the user must opt in from system settings. This is by design and is the only way third-party app lockers can intercept foreground events on modern Android.

### ⚠️ Note for Sideloaded Release APKs (Android 13+)

When manually installing an APK from GitHub or local storage, Android 13+ may restrict Special App Access permissions by default ("Restricted setting"). To grant them:

1. Go to **Settings → Apps → App Locker** (or tap **Open App Info** inside the app's Settings tab).
2. Tap the **⋮** overflow menu in the top-right corner.
3. Select **Allow restricted settings** and authenticate with your lock screen.
4. Return to App Locker to toggle **Usage Access** and **Display Over Other Apps**.

---

## 🚀 Getting Started

### 📋 Requirements

| Tool | Version |
| :--- | :--- |
| **Android Studio** | Ladybug / Meerkat or newer |
| **JDK** | Java 17 or 21 (project compiles to Java 11 bytecode) |
| **Min SDK** | Android 7.0 (API 24) |
| **Target / Compile SDK** | Android 16 (API 36) |
| **Kotlin** | `2.2.10` (managed by Gradle) |

### ⚙️ Build & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/pandasuryanarayan/App-Lock.git
   cd App-Lock
   ```

2. **Open in Android Studio**
   - Launch Android Studio → **File → Open** → select the `App-Lock` directory.
   - Let Gradle finish syncing.

3. **Build a debug APK**
   ```bash
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`

4. **Install on a device / emulator**
   ```bash
   ./gradlew installDebug
   ```
   Or press **Run ▶** in Android Studio. Make sure USB debugging is enabled on a real device.

5. **(Optional) Build a signed release APK**
   The release build is signed with an upload key (`my-upload-key.jks`, **not** tracked in git). Provide credentials via environment variables:
   ```bash
   export KEYSTORE_PATH="$PWD/my-upload-key.jks"
   export STORE_PASSWORD="your-store-password"
   export KEY_PASSWORD="your-key-password"
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release.apk`

   > **🔐 Keep your signing key safe.** The `my-upload-key.jks` file is excluded from git (see `.gitignore`). Back it up securely — if you lose it, you will **not** be able to update your app on the Play Store under the same identity. For Eg:- applock.

### 🔑 Local Secrets

The project uses the [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin) for `.env`-style local config (e.g. a future `GEMINI_API_KEY`). Copy the template and edit locally:

```bash
cp .env.example .env
```

`.env` is gitignored. The plugin will fall back to `.env.example` if `.env` is missing, so the build never breaks.

---

## 🧪 Testing

| Scope | Tooling | Location |
| :--- | :--- | :--- |
| **Unit tests** | JUnit 4 · Robolectric · `kotlinx-coroutines-test` | `app/src/test/` |
| **Compose UI tests** | `androidx.compose.ui.test.junit4` | `app/src/test/` & `app/src/androidTest/` |
| **Screenshot regression** | Roborazzi `1.59.0` | `app/src/test/` |
| **Instrumented tests** | Espresso `3.7.0` · AndroidX Runner | `app/src/androidTest/` |

### Run the full local test suite

```bash
./gradlew test
```

### Run instrumented tests on a connected device

```bash
./gradlew connectedAndroidTest
```

### Record / verify Roborazzi screenshots

```bash
./gradlew recordRoborazziDebug      # capture golden images
./gradlew verifyRoborazziDebug      # assert against goldens
```

---

## 📦 Build Variants & Signing

| Variant | Minify | Shrink Resources | Signing |
| :--- | :---: | :---: | :--- |
| `debug` | ✅ | ✅ | `debugConfig` (auto-generated `debug.keystore`) |
| `release` | ✅ | ✅ | `release` keystore via env vars (fallback: `my-upload-key.jks`) |

Both build types run R8 with `proguard-android-optimize.txt` plus project rules in `app/proguard-rules.pro`. R8 + resource shrinking is what keeps the final APK under 5 MB.

---

## ⚡ Performance

- **APK size < 5 MB** — vector drawables only, R8 + resource shrinking, no third-party image-loading library in the runtime graph.
- **Cold start** — Compose-only UI, no legacy `View` inflation; `MainActivity` boots straight into the navigation graph.
- **Battery** — `UsageStatsManager` polled at an adaptive interval inside the foreground service; idle when no protected apps are selected.
- **Memory** — All state lives in a single `ViewModel`; no leaked observers because all flows are collected in `repeatOnLifecycle(STARTED)`.

---

## 🐛 Troubleshooting

<details>
<summary><b>Overlay never shows on top of other apps</b></summary>

1. Open **App Locker → Settings → Permissions → Display Over Other Apps** and ensure it's **On**.
2. If toggling is greyed out on Android 13+, follow the **Allow restricted settings** steps in [Permissions Required](#-permissions-required).
3. Some manufacturer skins (Xiaomi/MIUI, Oppo/ColorOS, Vivo/Funtouch) require an additional "Pop-up view" or "Floating windows" toggle. Look in the vendor's permission manager.

</details>

<details>
<summary><b>App doesn't re-protect after reboot</b></summary>

Verify that `RECEIVE_BOOT_COMPLETED` is allowed for App Locker (some OEMs disable it by default for sideloaded apps). On MIUI, also enable **Autostart** for App Locker in the security center.

</details>

<details>
<summary><b>Biometric prompt never appears</b></summary>

Confirm the device has biometrics enrolled (Settings → Security → Biometrics). On Android 14+ the system requires at least one enrolled biometric or device credential to use `BiometricPrompt`.

</details>

<details>
<summary><b>Build fails on Apple Silicon / ARM Linux</b></summary>

Gradle Toolchains + the `foojay-resolver` plugin (already configured in `settings.gradle.kts`) will auto-download a matching JDK. Make sure your network allows access to `api.foojay.io`.

</details>

---

## 🗺️ Roadmap

- [ ] **Intruder selfie** — silent front-camera capture on N failed attempts using CameraX.
- [ ] **Time-based unlock rules** — auto-disable protection during trusted hours / locations.
- [ ] **Wear OS companion** — quick-unlock from a paired smartwatch.
- [ ] **App usage insights** — opt-in charts of which locked apps you open most.
- [ ] **Localization** — strings extraction for hi, es, de, fr, ja, zh.
- [ ] **Material You dynamic color** — optional theme that follows the system wallpaper.

---

## 🤝 Contributing

Contributions are welcome and appreciated! 💚

1. **Fork** the repo and create a feature branch:
   ```bash
   git checkout -b feat/awesome-thing
   ```
2. **Follow the existing style** — Kotlin official style, Compose API guidelines, 4-space indent, no wildcard imports.
3. **Write tests** for any new ViewModel or repository logic. Add a Roborazzi golden for any new screen.
4. **Run the full check suite before pushing:**
   ```bash
   ./gradlew test lint detekt
   ```
5. **Open a Pull Request** with a clear description and screenshots/GIFs for UI changes.

Please open an issue first for any non-trivial change so we can discuss the approach.

### 🧭 Code of Conduct

Be respectful, constructive, and inclusive. We follow the [Contributor Covenant](https://www.contributor-covenant.org/).

---

## 🔒 Security & Privacy

- **100% Offline & Local** — All credentials and secret questions are stored strictly on-device in a local SQLite database powered by Room.
- **Zero Cloud Tracking** — No external telemetry, advertising trackers, or unauthorized internet transmission of user data.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE.md).

```
MIT License — Copyright (c) 2026 App Locker Contributors
```

---

## 🙏 Acknowledgments

- The Android team for [Jetpack Compose](https://developer.android.com/jetpack/compose) and [Room](https://developer.android.com/training/data-storage/room).
- The [Roborazzi](https://github.com/takahirom/roborazzi) project for making screenshot regression testing trivial.
- The open-source community for libraries that make projects like this possible.

---

<p align="center">
  Made with ❤️ for a more private Android. If App Locker helped you, consider giving it a ⭐ on GitHub.
</p>
