# AI Agents Context & Documentation

This file serves as a context guide for AI assistants (like Gemini, Copilot) and developers working on the **Ví QR** project.

## 1. Project Overview
*   **Name:** Ví QR (VietQR Wallet)
*   **Description:** An Android application for managing QR codes, making payments, and storing recipient information, secured with Google Sign-In and biometric authentication.
*   **Root Path:** `E:/BOT01/AppQRWallet`

### Key Features
*   **Secure Login:** Google Sign-In with session management.
*   **Biometric Lock:** The app is locked and unlocked using Fingerprint/Face ID instead of a PIN.
*   **Dynamic User Profile:** Displays the user's name and avatar from their Google account.
*   **Smart QR Scanning:** Fast QR code scanning using CameraX and ZXing.
*   **Personal QR Creation:** Generate custom VietQR codes.

## 2. Tech Stack & Libraries
*   **Language:** Java
*   **Security:** Google Sign-In, **AndroidX Biometric**.
*   **Database:** Room Database.
*   **Build System:** Gradle (Kotlin DSL).

## 3. Architecture & Structure
The project follows an MVVM pattern where applicable.

### Key Components
*   `SplashActivity`: Entry point, checks login status and **prompts for biometric authentication**.
*   `LoginActivity`: Handles Google Sign-In flow.
*   `HomeActivity`: Main dashboard, showing user info and QR codes.
*   `SettingsActivity`: User preferences, profile, and logout.
*   `PinActivity`: **(DEPRECATED/REMOVED)** This component is no longer used.

## 4. Security Risks & Best Practices (MANDATORY)

*   **CRITICAL - Sensitive Data Storage:**
    *   **DO NOT** store tokens or any sensitive user data in standard `SharedPreferences` as plaintext. 
    *   **MUST USE:** `EncryptedSharedPreferences` for storing all sensitive key-value data.

*   **Code Obfuscation:**
    *   For all `release` builds, ProGuard/R8 **must be enabled** (`isMinifyEnabled = true`) in `build.gradle.kts` to obfuscate code and deter reverse engineering.

*   **Secure Screen Content:**
    *   In any Activity or Fragment that displays sensitive information (e.g., `QRDetailActivity`), **MUST USE** `getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)` to prevent screenshots and screen recording.

*   **Logging:**
    *   **DO NOT** log sensitive information in release builds.

## 5. Agent Instructions (Prompts)

*   **Architecture:** Adhere to the existing Activity-based architecture. Keep logic separated and clean.
*   **Security First:** Before implementing any new feature, first consider its security implications based on the rules in Section 4.
*   **Data Storage:** When storing any form of data, first determine its sensitivity. If it is sensitive, use `EncryptedSharedPreferences`.

---
*Last Updated to reflect the removal of PIN and the implementation of Biometric-only authentication.*
