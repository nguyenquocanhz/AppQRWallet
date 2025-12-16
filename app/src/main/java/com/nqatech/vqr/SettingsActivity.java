package com.nqatech.vqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.nqatech.vqr.theme.ThemeManager;
import com.nqatech.vqr.util.BiometricUtil;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "vqr_prefs";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        // Setup Theme RadioGroup
        RadioGroup rgTheme = findViewById(R.id.rgTheme);
        String currentMode = ThemeManager.getCurrentTheme(this);
        
        if (ThemeManager.MODE_LIGHT.equals(currentMode)) {
            rgTheme.check(R.id.rbLight);
        } else if (ThemeManager.MODE_DARK.equals(currentMode)) {
            rgTheme.check(R.id.rbDark);
        } else {
            rgTheme.check(R.id.rbSystem);
        }
        
        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbLight) {
                ThemeManager.setTheme(SettingsActivity.this, ThemeManager.MODE_LIGHT);
            } else if (checkedId == R.id.rbDark) {
                ThemeManager.setTheme(SettingsActivity.this, ThemeManager.MODE_DARK);
            } else {
                ThemeManager.setTheme(SettingsActivity.this, ThemeManager.MODE_SYSTEM);
            }
            recreate();
        });

        // Biometric Settings
        LinearLayout layoutBiometric = findViewById(R.id.layoutBiometric);
        SwitchMaterial switchBiometric = findViewById(R.id.switchBiometric);
        
        if (BiometricUtil.isBiometricAvailable(this)) {
            layoutBiometric.setVisibility(View.VISIBLE);
            
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isEnabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
            switchBiometric.setChecked(isEnabled);
            
            switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Require authentication to enable
                    BiometricUtil.showBiometricPrompt(this, "Xác thực để bật bảo mật", "Sử dụng vân tay hoặc Face ID", new BiometricUtil.BiometricCallback() {
                        @Override
                        public void onSuccess() {
                            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, true).apply();
                            Toast.makeText(SettingsActivity.this, "Đã bật bảo mật sinh trắc học", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            switchBiometric.setChecked(false); // Revert switch
                            Toast.makeText(SettingsActivity.this, "Xác thực thất bại: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Disable directly (or ask for confirmation)
                    prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, false).apply();
                }
            });
        } else {
            layoutBiometric.setVisibility(View.GONE);
        }

        // Setup Bottom Nav Interactions
        LinearLayout navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        FrameLayout btnScanCenter = findViewById(R.id.btnScanCenter);
        btnScanCenter.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ScanQRActivity.class);
            startActivity(intent);
        });
    }
}