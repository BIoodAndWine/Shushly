# Shushly

Shushly is a one-click DNS ad and tracker blocker built with Kotlin Multiplatform and Compose Multiplatform. It blocks advertisements, telemetry, and tracking domains system-wide across mobile and desktop devices without requiring user accounts, configurations, or subscriptions.

## Features

- **One-click protection:** Tap the power toggle once to activate device-level ad blocking.
- **Three protection levels:**
  - **Default:** Everyday ad and tracker blocking using Control D Ads & Tracking (`https://freedns.controld.com/p2`).
  - **Balanced:** Optimal balance of filtering and website compatibility using Control D OISD Full (`https://freedns.controld.com/x-oisd`). Default for new installations.
  - **Strong:** Strict filtering for maximum privacy using Control D HaGeZi Pro (`https://freedns.controld.com/x-hagezi-pro`).
- **Free forever:** Unlimited ad blocking without advertisements, artificial query limits, or registration.
- **Lifetime Premium ($5 USD one-time):** Unlocks custom primary and secondary DNS resolvers, custom DoH endpoints, regional recommendations, and live resolver latency testing.
- **Truthful engine states:** The UI reflects the real status of the network engine: `DISABLED`, `CONNECTING`, `CONNECTED`, `RECONNECTING`, and `ERROR`.
- **Zero data collection:** No query logs, browsing histories, or user metrics are recorded or transmitted.

## Project structure

```
Shushly/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/         # Shared Compose Multiplatform UI, ViewModels, and domain logic
│   │   │   └── kotlin/com/shushly/app/
│   │   │       ├── domain/model/        # ProtectionLevel, Status, DnsProvider, Region, Entitlement
│   │   │       ├── domain/engine/       # DnsPacketParser, DohClient, DnsProtectionController
│   │   │       ├── data/repository/     # SettingsRepository, DnsDirectoryRepository, BillingRepository
│   │   │       └── presentation/        # Screens (Home, Premium, Settings), components, theme
│   │   ├── commonTest/         # Shared test suite (parser, validation, live DoH tests)
│   │   ├── androidMain/        # Android VpnService, AndroidDnsProtectionController, MainActivity
│   │   └── desktopMain/        # Windows 11 DNS engine, LocalDnsForwarder, Compose Desktop entry point
├── gradle/                     # Gradle version catalog and wrapper
└── .github/workflows/          # Cross-platform CI/CD automation
```

## Quick start

### Prerequisites

- Java 21 LTS JDK
- Android SDK (platform 35, build-tools 35.0.0+) for Android builds
- Gradle 8.11+ (bundled via `./gradlew`)

### Running tests

Run the test suite, including live RFC 8484 DNS-over-HTTPS queries and ad-blocking tests:

```bash
./gradlew :composeApp:desktopTest
```

### Building the Android APK

Generate the debug APK:

```bash
./gradlew :composeApp:assembleDebug
```

The APK is produced at:
`composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### Building the Windows desktop application

To create the standalone Windows application folder containing `Shushly.exe`:

```powershell
.\gradlew.bat :composeApp:createDistributable
```

Output directory:
`composeApp/build/compose/binaries/main/app/Shushly/Shushly.exe`

To generate the standalone Windows MSI installer:

```powershell
.\gradlew.bat :composeApp:packageMsi
```

Output MSI:
`composeApp/build/compose/binaries/main/msi/Shushly-1.0.0.msi`

## License

This software is released under the Apache License 2.0.
