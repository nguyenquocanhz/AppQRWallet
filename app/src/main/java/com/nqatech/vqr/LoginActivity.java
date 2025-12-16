package com.nqatech.vqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nqatech.vqr.theme.ThemeManager;
import com.nqatech.vqr.util.BiometricUtil;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView btnBiometric = findViewById(R.id.btnBiometric);
        btnBiometric.setOnClickListener(v -> authenticate());
        
        // Auto trigger on start if desired
        authenticate();
    }

    private void authenticate() {
        if (BiometricUtil.isBiometricAvailable(this)) {
            BiometricUtil.showBiometricPrompt(this, "Mở khóa Ví QR", "Xác thực để truy cập", new BiometricUtil.BiometricCallback() {
                @Override
                public void onSuccess() {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
             Toast.makeText(this, "Thiết bị không hỗ trợ hoặc chưa cài đặt vân tay", Toast.LENGTH_SHORT).show();
             // Fallback to password or PIN if implemented, or just let them in for dev purpose
             // startActivity(new Intent(LoginActivity.this, HomeActivity.class));
             // finish();
        }
    }
}