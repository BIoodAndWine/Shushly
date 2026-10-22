# Build and Release Guide

This guide covers building, testing, signing, packaging, and releasing Shushly for Android and Windows.

---

## 1. Release Artifact Naming

All GitHub Release downloads use stable, clean filenames without version numbers, debug suffixes, or architecture tags:

| Platform / Asset | Download Filename | Format / Description |
|---|---|---|
| **Android** | `Shushly.apk` | Release APK signed with persistent production keystore (v1 + v2 schemes) |
| **Windows MSI Installer** | `Shushly.msi` | System-wide Windows Installer package built via WiX Toolset |
| **Windows EXE Installer** | `Shushly-Setup.exe` | Self-contained Windows setup bootstrapper installer |
| **Windows Portable** | `Shushly-Windows-Portable.zip` | Standalone portable runtime archive with `Shushly.exe` (no install required) |
| **Integrity Checksums** | `SHA256SUMS.txt` | Consolidated SHA-256 digests for all release files |

Version numbers are preserved in Git tags (e.g. `v0.1.2`), release titles, and in-app metadata, while download URLs remain consistent and friendly.

---

## 2. Prerequisites

- **Java Development Kit (JDK):** JDK 21 LTS (Eclipse Temurin, Azul Zulu, or OpenJDK).
- **Android SDK:** Platforms 34/35, Build Tools 35.0.0+ with `apksigner`.
- **WiX Toolset:** WiX 3.11+ (for Windows MSI and EXE installer creation).
- **Windows SDK:** Windows 10/11 SDK with `signtool.exe` (for Authenticode signing).
- **Gradle:** Bundled wrapper via `./gradlew` (Linux/macOS) or `.\gradlew.bat` (Windows).

---

## 3. Automated Test Suite

Run unit and DNS-over-HTTPS (DoH) integration tests before packaging:

```bash
# Linux / macOS
./gradlew :composeApp:desktopTest

# Windows
.\gradlew.bat :composeApp:desktopTest
```

---

## 4. Android Production Signing

Shushly uses a persistent release keystore across all builds to guarantee update compatibility between releases. Ephemeral signing identities generated per-build are strictly forbidden.

### 4.1 Production Certificate Fingerprints

- **Distinguished Name (DN):** `CN=Karl Molles, OU=Shushly, O=Karl Molles, L=Global, ST=OpenSource, C=US`
- **Algorithm:** 2048-bit RSA with SHA384withRSA
- **SHA-256 Fingerprint:**
  ```
  83:91:04:25:A6:53:C8:1C:42:0F:AF:2F:9A:49:E4:BF:B9:02:D4:5F:12:60:30:41:8F:E5:3A:2A:DB:8E:96:DA
  ```
- **SHA-1 Fingerprint:**
  ```
  37:28:C9:7C:D0:5D:68:8F:92:F6:75:61:FE:2D:D0:CE:2F:6E:29:69
  ```

### 4.2 Local Build Configuration

Set credentials in local environment variables or `~/.gradle/gradle.properties` (never commit these):

```properties
SHUSHLY_KEYSTORE_PATH=/path/to/shushly-release.jks
SHUSHLY_KEYSTORE_PASSWORD=your_store_password
SHUSHLY_KEY_ALIAS=shushly
SHUSHLY_KEY_PASSWORD=your_key_password
```

Build the release APK:
```bash
./gradlew :composeApp:assembleRelease
```

### 4.3 Signature Verification

Verify the APK signature schemes (v1 + v2) and certificate fingerprint:

```bash
apksigner verify --verbose --print-certs composeApp/build/outputs/apk/release/composeApp-release.apk
```

Expected verification output:
```text
Verifies
Verified using v1 scheme (JAR signing): true
Verified using v2 scheme (APK Signature Scheme v2): true
Number of signers: 1
Signer #1 certificate DN: CN=Karl Molles, OU=Shushly, O=Karl Molles, L=Global, ST=OpenSource, C=US
Signer #1 certificate SHA-256 digest: 83910425a653c81c420faf2f9a49e4bfb902d45f126030418fe53a2adb8e96da
```

---

## 5. Windows Code Signing (Microsoft Authenticode)

Shushly supports production Authenticode signing using `signtool.exe` with RFC 3161 timestamps.

### 5.1 Signing Process

When configured, the release pipeline signs:
1. The portable application binary: `Shushly.exe` (before compression into `Shushly-Windows-Portable.zip`).
2. The MSI installer: `Shushly.msi`.
3. The EXE setup bootstrapper: `Shushly-Setup.exe`.

Command executed by the pipeline:
```powershell
signtool sign /f cert.pfx /p $CERT_PASSWORD /fd SHA256 /tr http://timestamp.digicert.com /td SHA256 Shushly.exe
```

### 5.2 Signature Verification

Verify signatures with Authenticode policy validation:

```powershell
signtool verify /pa /v Shushly.msi
signtool verify /pa /v Shushly-Setup.exe
```

### 5.3 Certificate Availability & Transparency

- A self-signed Windows certificate is **not** equivalent to a publicly trusted code-signing certificate and is not used.
- If a trusted Authenticode certificate (`WINDOWS_CERT_BASE64`) is configured in repository secrets, all binaries and installers are signed and verified.
- If no Authenticode certificate is configured, the pipeline builds authentic production binaries and explicitly reports their unsigned status without falsifying certificate identities or bypassing verification.
- Note: Code signing does not immediately prevent Microsoft Defender SmartScreen reputation prompts. SmartScreen reputation is accrued organically over time as more users download and run the binary.

---

## 6. GitHub Actions Secrets Reference

Configure the following repository secrets under **Settings > Secrets and variables > Actions**:

| Secret Name | Required For | Description |
|---|---|---|
| `SHUSHLY_KEYSTORE_BASE64` | Android Release | Base64-encoded persistent production `shushly-release.jks` |
| `SHUSHLY_KEYSTORE_PASSWORD` | Android Release | Keystore storage password |
| `SHUSHLY_KEY_ALIAS` | Android Release | Key alias (`shushly`) |
| `SHUSHLY_KEY_PASSWORD` | Android Release | Private key password |
| `WINDOWS_CERT_BASE64` | Windows Signing *(Optional)* | Base64-encoded PKCS#12 (`.pfx`) Authenticode code-signing certificate |
| `WINDOWS_CERT_PASSWORD` | Windows Signing *(Optional)* | Password for the `.pfx` code-signing certificate |

---

## 7. Publishing a New Release

1. Update the version number in:
   - `composeApp/build.gradle.kts` (`versionCode`, `versionName`, `packageVersion`)
   - `composeApp/src/commonMain/.../SettingsViewModel.kt` (`appVersion`)
   - `CHANGELOG.md`
2. Commit and push the changes:
   ```bash
   git commit -am "chore(release): bump version to 0.1.2"
   git push origin main
   ```
3. Create and push the Git version tag:
   ```bash
   git tag v0.1.2
   git push origin v0.1.2
   ```
4. The automated Release workflow will:
   - Build and verify `Shushly.apk` with the persistent production keystore.
   - Build `Shushly.msi`, `Shushly-Setup.exe`, and `Shushly-Windows-Portable.zip`.
   - Apply Authenticode signing if credentials are provided.
   - Consolidate SHA-256 hashes into `SHA256SUMS.txt`.
   - Publish the assets to the GitHub Release.
