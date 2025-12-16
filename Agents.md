# AI Agents Context & Documentation

This file serves as a context guide for AI assistants (like Gemini, Copilot) and developers working on the **Ví QR** project.

## 1. Project Overview
*   **Name:** Ví QR (VietQR Wallet)
*   **Description:** An Android application for managing QR codes, making payments, and storing recipient information.
*   **Root Path:** `D:/AndroidApp/VQR`

## 2. Tech Stack & Libraries
*   **Language:** Java
*   **Minimum SDK:** 24 (Android 7.0)
*   **Target SDK:** 36
*   **UI Toolkit:** XML Layouts with Material Design 3.
*   **Database:** Room Database (`androidx.room`).
*   **Camera & QR:** CameraX (`androidx.camera`) + ZXing (`com.google.zxing`) for scanning and decoding.
*   **Build System:** Gradle (Kotlin DSL).

## 3. Architecture & Structure
The project currently follows a standard Activity-based structure (can be evolved into MVVM).

*   **Activities:**
    *   `SplashActivity`: Entry point, logo animation.
    *   `HomeActivity`: Main dashboard with "Create QR", "Wallet", and "History".
    *   `ScanQRActivity`: Camera interface for scanning QR codes.
    *   `SettingsActivity`: User preferences, Dark/Light mode toggle.
*   **Database:**
    *   Location: `com.nqatech.vqr.database`
    *   Main Class: `AppDatabase` (Singleton).
    *   Entities: `User`, etc.
*   **Theme Management:**
    *   Location: `com.nqatech.vqr.theme.ThemeManager` (Handles Dark/Light/System modes).

## 4. Coding Conventions
*   **Naming:** Use `camelCase` for variables/methods, `PascalCase` for classes, `snake_case` for resources (layouts, drawables).
*   **UI:** Prefer `ConstraintLayout` for complex screens. Use `strings.xml` for all text.
*   **Database:** Always access Room Database via `AppDatabase.getDatabase(context)`. Do not run queries on the main thread in production code (use Executors/Coroutines).

## 5. Agent Instructions (Prompts)
*   **When creating a new Activity:** Always register it in `AndroidManifest.xml` and creating a corresponding layout file in `res/layout`.
*   **When modifying UI:** Check `res/values/colors.xml` to ensure color consistency (Dark Green theme).
*   **On Error:** If an import is missing, check `libs.versions.toml` first to see if the library is declared.

---
*Last Updated: Project Initialization Phase*