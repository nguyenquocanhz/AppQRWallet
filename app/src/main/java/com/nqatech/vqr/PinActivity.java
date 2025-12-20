package com.nqatech.vqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nqatech.vqr.theme.ThemeManager;

public class PinActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "vqr_prefs";
    private static final String KEY_PIN = "user_pin";

    private EditText etPin;
    private TextView tvPinTitle;
    private Button btnConfirmPin;

    private boolean isCreatingPin = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        etPin = findViewById(R.id.etPin);
        tvPinTitle = findViewById(R.id.tvPinTitle);
        btnConfirmPin = findViewById(R.id.btnConfirmPin);

        // Check if a PIN already exists
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_PIN)) {
            isCreatingPin = false;
            tvPinTitle.setText("Nhập mã PIN của bạn");
        } else {
            isCreatingPin = true;
            tvPinTitle.setText("Tạo mã PIN gồm 6 chữ số");
        }

        btnConfirmPin.setOnClickListener(v -> handlePinConfirmation());
    }

    private void handlePinConfirmation() {
        String pin = etPin.getText().toString();
        if (pin.length() != 6) {
            Toast.makeText(this, "Mã PIN phải có 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (isCreatingPin) {
            // Save the new PIN
            prefs.edit().putString(KEY_PIN, pin).apply();
            Toast.makeText(this, "Đã tạo mã PIN thành công", Toast.LENGTH_SHORT).show();
            navigateToHome();
        } else {
            // Verify the existing PIN
            String savedPin = prefs.getString(KEY_PIN, "");
            if (pin.equals(savedPin)) {
                navigateToHome();
            } else {
                Toast.makeText(this, "Mã PIN không chính xác", Toast.LENGTH_SHORT).show();
                etPin.setText("");
            }
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(PinActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
