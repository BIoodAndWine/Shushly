# Build and Release Guide

This guide covers building, testing, packaging, and releasing Shushly for Android and Windows.

---

## 1. Prerequisites

- **Java Development Kit (JDK):** JDK 21 LTS (Temurin, Azul Zulu, or OpenJDK).
- **Android SDK:** Compile SDK 35, Min SDK 24, Build Tools 35.0.0+ (for Android builds).
- **Gradle:** Bundled wrapper via `./gradlew` (Linux/macOS) or `.\gradlew.bat` (Windows).

Verify your environment:
```bash
java -version
```

---

## 2. Running Automated Tests

Run the shared domain and DoH integration test suite:

```bash
# Linux / macOS
./gradlew :composeApp:desktopTest

# Windows
.\gradlew.bat :composeApp:desktopTest
```

Test reports are generated at `composeApp/build/reports/tests/desktopTest/index.html`.

---

## 3. Building Android Artifacts

### Debug APK (for testing)
```bash
./gradlew :composeApp:assembleDebug
```
Output path:
`composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### Release APK
```bash
./gradlew :composeApp:assembleRelease
```

### Android App Bundle (AAB for Google Play)
```bash
./gradlew :composeApp:bundleRelease
```
Output path:
`composeApp/build/outputs/bundle/release/composeApp-release.aab`

### Keystore Configuration (for Release Signing)
Set keystore credentials in your local `~/.gradle/gradle.properties`:
```properties
SHUSHLY_KEYSTORE_PATH=/path/to/shushly-release.jks
SHUSHLY_KEYSTORE_PASSWORD=your_store_password
SHUSHLY_KEY_ALIAS=shushly
SHUSHLY_KEY_PASSWORD=your_key_password
```

---

## 4. Building Windows Desktop Artifacts

### Standalone Portable Application Folder
Generates a self-contained runtime directory with `Shushly.exe`:
```powershell
.\gradlew.bat :composeApp:createDistributable
```
Output directory:
`composeApp\build\compose\binaries\main\app\Shushly\Shushly.exe`

### MSI Installer (Requires WiX Toolset)
```powershell
.\gradlew.bat :composeApp:packageMsi
```
Output MSI:
`composeApp\build\compose\binaries\main\msi\Shushly-1.0.0.msi`

---

## 5. Automated GitHub Releases

Releases are triggered automatically when a version tag matching `v*` (e.g. `v0.1.0`) is pushed to the repository:

```bash
git tag v0.1.0
git push origin v0.1.0
```

The GitHub Actions release workflow:
1. Compiles the Android APK and computes its SHA-256 hash.
2. Builds the standalone Windows application and MSI package, computing their SHA-256 hashes.
3. Consolidates checksums into `SHA256SUMS.txt`.
4. Creates a published GitHub Release with attached binaries and release notes.
