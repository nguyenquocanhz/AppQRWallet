package com.nqatech.vqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nqatech.vqr.theme.ThemeManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;
    private static final String PREFS_NAME = "vqr_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginStatus, SPLASH_DURATION);
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);

        Intent intent;
        if (isLoggedIn) {
            // If logged in, go to the PIN screen for authentication.
            intent = new Intent(SplashActivity.this, PinActivity.class);
        } else {
            // If not logged in, go to the Login screen.
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}
