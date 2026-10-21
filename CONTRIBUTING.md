# Contributing to Shushly

Thank you for your interest in contributing to Shushly! We welcome bug reports, feature suggestions, documentation improvements, and code contributions.

---

## Code of Conduct

All contributors and participants are expected to adhere to our [Code of Conduct](CODE_OF_CONDUCT.md). Please read it before participating.

---

## Development Setup

### Prerequisites

- **Java Development Kit:** JDK 21 LTS (Temurin, Azul Zulu, or OpenJDK).
- **Android SDK:** Platforms 34/35, Build Tools 35.0.0+ (for Android builds).
- **IDE:** Android Studio Ladybug+ or IntelliJ IDEA 2024.2+ with the Compose Multiplatform plugin.
- **Git:** Latest version.

### Cloning & Building

1. Clone the repository:
   ```bash
   git clone https://github.com/BIoodAndWine/Shushly.git
   cd Shushly
   ```

2. Run the shared test suite:
   ```bash
   ./gradlew :composeApp:desktopTest
   ```

3. Build the Android debug APK:
   ```bash
   ./gradlew :composeApp:assembleDebug
   ```

4. Run Compose Desktop locally:
   ```bash
   ./gradlew :composeApp:run
   ```

---

## Workflow & Guidelines

### Branching Strategy

- Always create a descriptive branch for your work:
  - `fix/issue-description` for bug fixes
  - `feat/feature-name` for enhancements
  - `docs/topic` for documentation updates
- Avoid opening PRs directly from your fork's `main` branch.

### Code Style & Architecture

- **Language:** Kotlin 2.1+ adhering to official Kotlin coding conventions.
- **UI Framework:** Compose Multiplatform with Material 3 tokens.
- **Architecture:** Clean MVVM with reactive `StateFlow` and unidirectional data flow.
- **Privacy First:** Never introduce external tracking libraries, analytical telemetry, or server dependencies.
- **Design Quality:** Respect Shushly's minimalist aesthetic, smooth animations, and clear typography.

### Testing Requirements

- Any new DNS parsing or routing logic must include unit tests under `composeApp/src/commonTest/`.
- Ensure all existing tests pass before submitting your PR:
  ```bash
  ./gradlew :composeApp:desktopTest
  ```

---

## Submitting a Pull Request

1. Push your branch to your fork.
2. Open a Pull Request targeting the `main` branch of `BIoodAndWine/Shushly`.
3. Complete all sections of the [Pull Request Template](.github/PULL_REQUEST_TEMPLATE.md).
4. Ensure CI checks pass. If a check fails, review the logs and update your branch.
5. Address reviewer feedback constructively.

---

## Questions and Discussions

Have a question or idea that isn't ready for a pull request? Start a conversation in [GitHub Discussions](https://github.com/BIoodAndWine/Shushly/discussions).
