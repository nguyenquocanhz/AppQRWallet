# AI Agents Context & Documentation

This file serves as a context guide for AI assistants (like Gemini, Copilot) and developers working on the **Ví QR** project.

## 1. Project Overview
*   **Name:** Ví QR (VietQR Wallet)
*   **Description:** An Android application for managing QR codes, making payments, scanning NFC documents, and storing recipient information.
*   **Root Path:** `D:/AndroidApp/VQR`

## 2. Tech Stack & Libraries
*   **Language:** Java
*   **Minimum SDK:** 24 (Android 7.0)
*   **Target SDK:** 36
*   **UI Toolkit:** XML Layouts with Material Design 3.
*   **Database:** Room Database (`androidx.room`).
*   **Network:** Retrofit 2 + Gson.
*   **Camera & QR:** CameraX (`androidx.camera`) + ZXing (`com.google.zxing`) for scanning and decoding.
*   **NFC & ID:** JMRTD, Scuba (for reading ePassport/ID Cards).
*   **Services:** Google Firebase (Messaging, Analytics).
*   **Build System:** Gradle (Kotlin DSL).

## 3. Architecture & Structure
The project currently follows a standard Activity-based structure (transitioning to MVVM where appropriate).

*   **Activities:**
    *   `SplashActivity`: Entry point, logo animation.
    *   `HomeActivity`: Main dashboard with "Create QR", "Wallet", "History", and settings access.
    *   `ScanQRActivity`: Main camera interface for scanning QR codes (CameraX + ZXing).
    *   `CreateQRActivity`: Interface for generating personal/bank QR codes.
    *   `SettingsActivity`: User preferences, Theme toggle, Biometric settings.
    *   `NfcReaderActivity`: Handles NFC reading for ID cards/Passports.
    *   `QRDetailActivity`, `AlertActivity`, `LoginActivity`, `QRListActivity`: Auxiliary screens.
*   **Database (`com.nqatech.vqr.database`):**
    *   `AppDatabase`: Singleton Room database instance.
    *   **Entities:** `User`, `Recipient`.
    *   **DAOs:** `UserDao`, `RecipientDao`.
*   **API (`com.nqatech.vqr.api`):**
    *   `ApiClient`: Retrofit instance management.
    *   `VietQRApiService`: Interfaces for external API calls (e.g., VietQR).
*   **Helpers:**
    *   `QRScannerHelper`, `QRCodeAnalyzer`: Logic for QR detection and processing.
*   **Services:**
    *   `MyFirebaseMessagingService`: Handles push notifications.
    *   `QRScanTileService`: Quick Settings tile for fast scanning.
    *   `QRWidgetProvider`: Home screen widget.

## 4. Coding Conventions
*   **Naming:**
    *   Variables/Methods: `camelCase`
    *   Classes: `PascalCase`
    *   Resources (layouts, drawables, ids): `snake_case` (e.g., `activity_home.xml`, `tv_user_name`).
*   **UI:**
    *   Prefer `ConstraintLayout` for complex screens.
    *   Use `strings.xml` for all text content.
    *   Adhere to Material Design 3 guidelines.
*   **Database/Network:**
    *   Always access Room Database via `AppDatabase.getDatabase(context)`.
    *   Perform DB and Network operations on background threads (Executors/Coroutines).
*   **Logging:** Use `Log.d(TAG, message)` for debugging.

## 5. Agent Instructions (Prompts)
*   **When creating a new Activity:**
    *   Create the Java class extending `AppCompatActivity`.
    *   Create the corresponding layout file in `res/layout`.
    *   **Crucial:** Register the activity in `AndroidManifest.xml`.
*   **When modifying UI:**
    *   Check `res/values/colors.xml` and `themes.xml` to ensure consistency with the app's theme (Dark Green primary).
*   **On Error:**
    *   If an import is missing, check `libs.versions.toml` or `build.gradle.kts` to verify dependencies.
    *   For Camera/NFC issues, verify permissions in `AndroidManifest.xml`.

---
*Last Updated: Active Development Phase*
