# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- **Passkey Authentication**: Integrated Google Passkey for a secure and passwordless login experience using the `CredentialManager` API.
- **Biometric App Lock**: The app is now secured with Biometric authentication (Fingerprint/Face ID) after the initial login, replacing the previous PIN code system.
- **Dynamic User Info on Home**: The home screen now dynamically displays the user's Google account name and avatar.
- **QR Pinning Flow**: Implemented a new flow where users can select a specific QR code from their list to pin to the home screen.

### Changed
- **Modernized Login Flow**: The entire login and authentication process has been refactored.
- The app now uses `CredentialManager` as the primary method for authentication.
- **UI Update on Home**: The home screen UI was updated to accommodate the user's avatar and name.

### Removed
- **PIN Authentication**: The 6-digit PIN login system (`PinActivity` and related logic) has been completely removed in favor of biometric security.
- **Legacy Google Sign-In**: The old `startActivityForResult` method for Google Sign-In has been replaced by the `CredentialManager` API.
