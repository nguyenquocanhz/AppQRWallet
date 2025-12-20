# AI Agents Context & Documentation

This file serves as a context guide for AI assistants (like Gemini, Copilot) and developers working on the **Ví QR** project.

## 1. Project Overview
*   **Name:** Ví QR (VietQR Wallet)
*   **Description:** An Android application for managing QR codes, making payments, and storing recipient information, secured with Google Sign-In and a PIN.
*   **Root Path:** `E:/QRWallet/AppQRWallet`

### Key Features
*   **Secure Login:** Google Sign-In with session management and a 6-digit PIN lock.
*   **Smart QR Scanning:** Fast QR code scanning using CameraX and ZXing.
*   **Personal QR Creation:** Generate custom VietQR codes.
*   **App Widget & Quick Settings Tile:** Quick access to scanning features.
*   **Biometric Security:** Fingerprint/Face authentication for added convenience and security.

## 2. Tech Stack & Libraries
*   **Language:** Java
*   **Security:** Google Sign-In, AndroidX Biometric, AndroidX Security (for EncryptedSharedPreferences).
*   **Database:** Room Database.
*   **Build System:** Gradle (Kotlin DSL).

## 3. Architecture & Structure
The project follows an MVVM pattern where applicable.

### Key Components
*   `SplashActivity`: Entry point, checks login & PIN status.
*   `LoginActivity`: Handles Google Sign-In flow.
*   `PinActivity`: Handles PIN creation and verification.
*   `HomeActivity`: Main dashboard.
*   `SettingsActivity`: User preferences, profile, and logout.

## 4. Security Risks & Best Practices (MANDATORY)

*   **CRITICAL - PIN/Sensitive Data Storage:**
    *   **DO NOT** store PINs, tokens, or any sensitive user data in standard `SharedPreferences` as plaintext. 
    *   **MUST USE:** `EncryptedSharedPreferences` for storing all sensitive key-value data, including the user's PIN.
    *   **For relational data:** The Room database should be encrypted using SQLCipher.

*   **Code Obfuscation:**
    *   For all `release` builds, ProGuard/R8 **must be enabled** (`isMinifyEnabled = true`) in `build.gradle.kts` to obfuscate code and deter reverse engineering.

*   **Secure Screen Content:**
    *   In any Activity or Fragment that displays sensitive information (e.g., `PinActivity`, `QRDetailActivity`), **MUST USE** `getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)` to prevent screenshots and screen recording.

*   **Logging:**
    *   **DO NOT** log sensitive information (tokens, PINs, user data) in any circumstance. Use a logging library like Timber that automatically strips logs from release builds.

## 5. Agent Instructions (Prompts)

*   **Architecture:** Adhere to MVVM for new features.
*   **Security First:** Before implementing any new feature, first consider its security implications based on the rules in Section 4.
*   **New Sensitive Screen:** When creating a screen that will handle user secrets, immediately apply the `FLAG_SECURE` window flag.
*   **Storing Data:** When storing any form of data, first determine its sensitivity. If it is sensitive, use `EncryptedSharedPreferences` or an encrypted database.

---
*Last Updated: Active Development Phase, v5*
