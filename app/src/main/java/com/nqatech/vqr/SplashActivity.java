package com.nqatech.vqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nqatech.vqr.theme.ThemeManager;
import com.nqatech.vqr.util.BiometricUtil;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500; // Shorter duration
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginAndProceed, SPLASH_DURATION);
    }

    private void checkLoginAndProceed() {
        SharedPreferences prefs = SecurityUtils.getEncryptedSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);
        boolean isBiometricEnabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);

        if (isLoggedIn) {
            // If logged in, check for biometric availability and user preference
            if (isBiometricEnabled && BiometricUtil.isBiometricAvailable(this)) {
                showBiometricPromptToUnlock();
            } else {
                // If biometric is not enabled or not available, go to home.
                navigateToHome();
            }
        } else {
            // If not logged in, go to the Login screen.
            navigateToLogin();
        }
    }

    private void showBiometricPromptToUnlock() {
        BiometricUtil.showBiometricPrompt(this,
                "Xác thực để mở Ví QR",
                "Sử dụng vân tay hoặc Face ID của bạn",
                new BiometricUtil.BiometricCallback() {
                    @Override
                    public void onSuccess() {
                        navigateToHome();
                    }

                    @Override
                    public void onError(String error) {
                        // If user cancels or fails, close the app for security.
                        Toast.makeText(SplashActivity.this, "Xác thực thất bại: " + error, Toast.LENGTH_SHORT).show();
                        finish(); // Close app if authentication fails
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
