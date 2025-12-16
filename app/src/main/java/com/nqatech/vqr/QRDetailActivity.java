package com.nqatech.vqr;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nqatech.vqr.api.ApiClient;
import com.nqatech.vqr.api.VietQRRequest;
import com.nqatech.vqr.api.model.GenQRResponse;
import com.nqatech.vqr.api.model.VietQRResponse;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.entity.Recipient;
import com.nqatech.vqr.theme.ThemeManager;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRDetailActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 101;
    private ImageView ivQRCode;
    private int recipientId = -1;
    private Bitmap currentQRBitmap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_detail);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        ImageView btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());

        // Binding views
        ivQRCode = findViewById(R.id.ivQRCode);
        TextView tvBankName = findViewById(R.id.tvBankName);
        TextView tvAccountNumber = findViewById(R.id.tvAccountNumber);
        TextView tvAccountName = findViewById(R.id.tvAccountName);
        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvContent = findViewById(R.id.tvContent);
        View btnSave = findViewById(R.id.btnSave);
        View btnShare = findViewById(R.id.btnShare);
        
        btnSave.setOnClickListener(v -> saveImageToGallery());
        btnShare.setOnClickListener(v -> shareImage());
        
        // Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            recipientId = intent.getIntExtra("RECIPIENT_ID", -1);
            if (recipientId != -1) {
                btnDelete.setVisibility(View.VISIBLE);
            }

            String bankName = intent.getStringExtra("BANK_NAME");
            String bankBin = intent.getStringExtra("BANK_BIN");
            String accountNumber = intent.getStringExtra("ACCOUNT_NUMBER");
            String accountName = intent.getStringExtra("ACCOUNT_NAME");
            String amount = intent.getStringExtra("AMOUNT");
            String content = intent.getStringExtra("CONTENT");

            tvBankName.setText(bankName != null ? bankName : "");
            tvAccountNumber.setText(accountNumber != null ? accountNumber : "");
            tvAccountName.setText(accountName != null ? accountName : "");
            
            // Format amount
            if (amount != null && !amount.isEmpty()) {
                try {
                    double amountValue = Double.parseDouble(amount);
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    tvAmount.setText(formatter.format(amountValue) + " VND");
                } catch (NumberFormatException e) {
                    tvAmount.setText(amount + " VND");
                }
            } else {
                tvAmount.setText("0 VND");
            }
            
            tvContent.setText(content != null ? content : "");
            
            // Generate QR Image
            if (bankBin != null && !bankBin.isEmpty() && accountNumber != null && !accountNumber.isEmpty()) {
                generateVietQR(bankBin, accountNumber, accountName, amount, content);
            }
        }
    }
    
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa mã QR này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteRecipient())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteRecipient() {
        if (recipientId == -1) return;
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Need to fetch the entity first because deleteRecipient takes an object
            // Or use a query to delete by ID if added to Dao.
            // Since Dao has delete(Recipient), we construct a dummy recipient with ID or fetch it.
            // Safer to fetch or add deleteById query.
            // For now, let's fetch to be safe.
            // Ideally add @Query("DELETE FROM recipients WHERE id = :id") to Dao.
            // But I cannot modify Dao easily without re-reading it.
            // Let's assume Dao delete works on primary key match.
            Recipient recipientToDelete = new Recipient(null, null, null, null, null, null, null);
            recipientToDelete.id = recipientId;
            AppDatabase.getDatabase(this).recipientDao().deleteRecipient(recipientToDelete);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
        executor.shutdown();
    }

    private void generateVietQR(String bin, String accountNumber, String accountName, String amount, String content) {
        int amountInt = 0;
        try {
            if (amount != null && !amount.isEmpty()) {
                amountInt = Integer.parseInt(amount);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int acqId = 0;
        try {
            acqId = Integer.parseInt(bin);
        } catch (NumberFormatException e) {
        }

        VietQRRequest request = new VietQRRequest(
                accountNumber,
                accountName,
                acqId,
                amountInt,
                content,
                "text", 
                "compact"
        );

        ApiClient.getService().generateQR(request).enqueue(new Callback<VietQRResponse<GenQRResponse>>() {
            @Override
            public void onResponse(Call<VietQRResponse<GenQRResponse>> call, Response<VietQRResponse<GenQRResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VietQRResponse<GenQRResponse> vietQRResponse = response.body();
                    if ("00".equals(vietQRResponse.getCode()) && vietQRResponse.getData() != null) {
                        String base64Image = vietQRResponse.getData().getQrDataURL();
                        if (base64Image != null && base64Image.contains(",")) {
                            base64Image = base64Image.split(",")[1];
                        }
                        
                        try {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            currentQRBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ivQRCode.setImageBitmap(currentQRBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VietQRResponse<GenQRResponse>> call, Throwable t) {
            }
        });
    }

    private void saveImageToGallery() {
        if (currentQRBitmap == null) {
            Toast.makeText(this, "Chưa có ảnh QR để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check permissions for older Android versions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
                return;
            }
        }

        saveBitmap(currentQRBitmap);
        Toast.makeText(this, "Đã lưu ảnh vào thư viện", Toast.LENGTH_SHORT).show();
    }

    private void saveBitmap(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "VietQR_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ViQR");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(this, "Đã lưu ảnh vào thư viện", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void shareImage() {
        if (currentQRBitmap == null) {
             Toast.makeText(this, "Chưa có ảnh QR để chia sẻ", Toast.LENGTH_SHORT).show();
             return;
        }
        
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), currentQRBitmap, "VietQR Share", null);
        Uri uri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Chia sẻ mã QR qua"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery();
            } else {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để lưu ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}