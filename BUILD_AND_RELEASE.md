# Build and release

This guide covers building, signing, and releasing Shushly for Android and Windows.

## Android release preparation

### 1. Generating a release keystore

Run `keytool` from your JDK installation:

```bash
keytool -genkey -v -keystore shushly-release.jks -alias shushly -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configuring signing in `gradle.properties`

Store your credentials in your local `~/.gradle/gradle.properties`:

```properties
SHUSHLY_KEYSTORE_PATH=/path/to/shushly-release.jks
SHUSHLY_KEYSTORE_PASSWORD=your_store_password
SHUSHLY_KEY_ALIAS=shushly
SHUSHLY_KEY_PASSWORD=your_key_password
```

### 3. Building release artifacts

- **Release APK:**
  ```bash
  ./gradlew :composeApp:assembleRelease
  ```
- **Android App Bundle (AAB for Google Play):**
  ```bash
  ./gradlew :composeApp:bundleRelease
  ```

Output path for AAB:
`composeApp/build/outputs/bundle/release/composeApp-release.aab`

## Windows desktop release preparation

### 1. Building the standalone application

```powershell
.\gradlew.bat :composeApp:createDistributable
```

The resulting folder `composeApp\build\compose\binaries\main\app\Shushly` contains `Shushly.exe` and a bundled minimal Java runtime.

### 2. Building the MSI installer

```powershell
.\gradlew.bat :composeApp:packageMsi
```

The installer is generated at:
`composeApp\build\compose\binaries\main\msi\Shushly-1.0.0.msi`

### 3. Code signing for Windows

To sign the executable and MSI with a trusted Authenticode certificate:

```powershell
signtool sign /f certificate.pfx /p "password" /fd SHA256 /tr http://timestamp.digicert.com /td SHA256 "composeApp\build\compose\binaries\main\msi\Shushly-1.0.0.msi"
```

## Continuous integration

GitHub Actions workflows are located in `.github/workflows/ci.yml`. On every push to `main`, the workflow executes:
1. Unit tests and live DNS-over-HTTPS queries on Linux.
2. Android debug APK compilation.
3. Windows executable and MSI packaging on Windows.
4. Kotlin Multiplatform shared compilation on macOS.
