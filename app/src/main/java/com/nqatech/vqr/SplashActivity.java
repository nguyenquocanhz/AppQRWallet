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
    private static final String PREFS_NAME = "vqr_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginAndProceed, SPLASH_DURATION);
    }

    private void checkLoginAndProceed() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);

        if (isLoggedIn) {
            // If logged in, check for biometric availability and prompt if needed
            if (BiometricUtil.isBiometricAvailable(this)) {
                showBiometricPromptToUnlock();
            } else {
                // If no biometrics on device, go straight to home.
                // This is a security tradeoff: allows access on non-biometric devices without a PIN.
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
