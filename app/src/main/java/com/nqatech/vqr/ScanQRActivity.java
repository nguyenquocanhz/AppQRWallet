package com.nqatech.vqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.entity.Recipient;
import com.nqatech.vqr.qr.QRCodeAnalyzer;
import com.nqatech.vqr.qr.QRScannerHelper;
import com.nqatech.vqr.theme.ThemeManager;
import com.nqatech.vqr.util.VietQRParser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanQRActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;
    
    private PreviewView previewView;
    private QRScannerHelper qrScannerHelper;
    private ExecutorService dbExecutor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        
        dbExecutor = Executors.newSingleThreadExecutor();

        previewView = findViewById(R.id.previewView);
        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());

        LinearLayout btnImport = findViewById(R.id.btnImport);
        btnImport.setOnClickListener(v -> openGallery());

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        qrScannerHelper = new QRScannerHelper(this, previewView, this, new QRCodeAnalyzer.QRCodeListener() {
            @Override
            public void onQRCodeFound(String qrCode) {
                // Stop camera immediately to avoid multiple detections
                qrScannerHelper.stopCamera();
                
                runOnUiThread(() -> Toast.makeText(ScanQRActivity.this, "Đang xử lý...", Toast.LENGTH_SHORT).show());

                // Process in background thread
                dbExecutor.execute(() -> {
                    Recipient recipient = VietQRParser.parse(qrCode);
                    
                    if (recipient != null) {
                        // Insert into Room Database
                        AppDatabase.getDatabase(ScanQRActivity.this).recipientDao().insertRecipient(recipient);
                        
                        // Navigate to Detail Activity
                        runOnUiThread(() -> {
                             Intent intent = new Intent(ScanQRActivity.this, QRDetailActivity.class);
                             intent.putExtra("BANK_NAME", recipient.bankName);
                             intent.putExtra("BANK_BIN", recipient.bin);
                             intent.putExtra("ACCOUNT_NUMBER", recipient.accountNumber);
                             intent.putExtra("ACCOUNT_NAME", recipient.accountName);
                             intent.putExtra("AMOUNT", recipient.amount);
                             intent.putExtra("CONTENT", recipient.content);
                             startActivity(intent);
                             finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(ScanQRActivity.this, "Mã QR không hợp lệ hoặc không phải VietQR", Toast.LENGTH_SHORT).show();
                            // Restart camera if needed or finish
                            // For better UX, we could delay and restart
                            qrScannerHelper.startCamera();
                        });
                    }
                });
            }

            @Override
            public void onQRCodeNotFound() {
                // Keep scanning
            }
        });
        qrScannerHelper.startCamera();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbExecutor != null) {
            dbExecutor.shutdown();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            // In a real app, we would use ML Kit or ZXing to decode the bitmap from Uri
            Toast.makeText(this, "Chức năng quét ảnh từ thư viện chưa được tích hợp", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}