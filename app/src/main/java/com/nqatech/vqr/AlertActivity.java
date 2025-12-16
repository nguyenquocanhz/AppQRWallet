package com.nqatech.vqr;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.nqatech.vqr.theme.ThemeManager;

public class AlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        // This is a placeholder for Firebase integration
        // In a real app, you would fetch notifications from Firebase Messaging service or a local database
        TextView tvAlertTitle = findViewById(R.id.tvAlertTitle);
        TextView tvAlertContent = findViewById(R.id.tvAlertContent);
        
        tvAlertTitle.setText("Thông báo hệ thống");
        tvAlertContent.setText("Chào mừng bạn đến với Ví QR! Hiện tại hệ thống đang hoạt động ổn định. Các tính năng mới sẽ sớm được cập nhật.");
    }
}