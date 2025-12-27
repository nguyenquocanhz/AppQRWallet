# AI Agents Context & Documentation

This file serves as a context guide for AI assistants (like Gemini, Copilot) and developers working on the **Ví QR** project.

## 1. Project Overview
*   **Name:** Ví QR (VietQR Wallet)
*   **Description:** An Android application for managing QR codes, making payments, and storing recipient information, secured with Google Passkey and biometric authentication.
*   **Root Path:** `E:/BOT01/AppQRWallet`

### Key Features
*   **Modern Secure Login:**
    *   Login with Google **Passkey** via Android's Credential Manager.
    *   Fallback to traditional Google Sign-In.
*   **Biometric App Lock:** App is secured using Fingerprint/Face ID for quick and secure access after initial login.
*   **Smart QR Scanning:** Fast QR code scanning using CameraX and ZXing.
*   **Personal QR Creation:** Generate custom VietQR codes.
*   **Dynamic User Profile:** Fetches and displays user's Google account name and avatar.

## 2. Tech Stack & Libraries
*   **Language:** Java
*   **Authentication:** AndroidX Credential Manager, Google Identity Services (for Passkey and Google Sign-In), AndroidX Biometric.
*   **Database:** Room Database.
*   **Build System:** Gradle (Kotlin DSL).
*   **UI:** XML Layouts, Material Design 3.
*   **Image Loading:** Glide.

## 3. Architecture & Structure
The project follows an MVVM pattern where applicable, with a focus on modern Android practices.

### Key Components
*   `SplashActivity`: Entry point. Checks login status and prompts for biometric authentication if logged in.
*   `LoginActivity`: Handles user authentication using the **Credential Manager API**.
*   `HomeActivity`: Main dashboard, displaying user info, pinned QR, and recent QR codes.
*   `SettingsActivity`: User preferences, profile management, and logout.

## 4. Development Workflow & Rules (MANDATORY)

This section contains critical rules that all agents and developers **must** follow.

### Rule 1: Dependency Management
*   **ACTION:** When introducing code that uses a new external library or a new component from an existing library family (e.g., `androidx.credentials`, `com.google.android.material`), you **must immediately** check the `app/build.gradle.kts` file.
*   **VERIFY:** Ensure the necessary `implementation` or `annotationProcessor` lines are present.
*   **ADD/UPDATE:** If the dependency is missing, add it before concluding the task. This prevents build errors.

### Rule 2: Security Best Practices
*   **Data Storage:**
    *   **DO NOT** store sensitive data (tokens, API keys, personal info) in standard `SharedPreferences` as plaintext.
    *   **MUST USE:** `EncryptedSharedPreferences` for all sensitive key-value data.
*   **Secure Screen Content:**
    *   In any Activity or Fragment displaying sensitive information, **MUST USE** `getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)` to prevent screenshots and screen recording.
*   **Logging:**
    *   **DO NOT** log sensitive information in release builds. Use a logging library like Timber or implement checks to ensure logs are stripped.
*   **Code Obfuscation:**
    *   For all `release` builds, ProGuard/R8 **must be enabled** (`isMinifyEnabled = true`) to obfuscate code.

## 5. General Instructions
*   **Architecture:** Adhere to the existing patterns. Prefer modern Android practices (e.g., using official AndroidX libraries).
*   **User Experience:** Keep the UI clean, responsive, and consistent with Material Design.
*   **Code Style:** Maintain consistency with the existing Java code style.

---
*Last Updated to reflect Passkey/Biometric authentication flow and include dependency management rules.*
