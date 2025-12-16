package com.nqatech.vqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nqatech.vqr.theme.ThemeManager;
import com.nqatech.vqr.util.BiometricUtil;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;
    private static final String PREFS_NAME = "vqr_prefs";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply theme immediately to ensure window background is correct before inflation
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginRequirement, SPLASH_DURATION);
    }

    private void checkLoginRequirement() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isBiometricEnabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);

        Intent intent;
        if (isBiometricEnabled && BiometricUtil.isBiometricAvailable(this)) {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, HomeActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}