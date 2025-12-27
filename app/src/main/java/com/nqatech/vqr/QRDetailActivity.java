package com.nqatech.vqr;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.android.material.textfield.TextInputEditText;
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
    private static final String TAG = "QRDetailActivity";
    private ImageView ivQRCode;
    private TextView tvAmount, tvContent;
    private int recipientId = -1;
    private Bitmap currentQRBitmap = null;
    
    // Hold current data to regenerate
    private String currentBankBin;
    private String currentAccountNumber;
    private String currentAccountName;
    private String currentAmount;
    private String currentContent;
    private String currentBankName;
    private String currentBankCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        SecurityUtils.secureActivity(this);
        setContentView(R.layout.activity_qr_detail);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        ImageView btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        
        ImageView btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> showEditDialog());

        // Binding views
        ivQRCode = findViewById(R.id.ivQRCode);
        TextView tvBankName = findViewById(R.id.tvBankName);
        TextView tvAccountNumber = findViewById(R.id.tvAccountNumber);
        TextView tvAccountName = findViewById(R.id.tvAccountName);
        tvAmount = findViewById(R.id.tvAmount);
        tvContent = findViewById(R.id.tvContent);
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
                btnEdit.setVisibility(View.VISIBLE);
            }

            currentBankName = intent.getStringExtra("BANK_NAME");
            currentBankCode = intent.getStringExtra("BANK_CODE");
            currentBankBin = intent.getStringExtra("BANK_BIN");
            currentAccountNumber = intent.getStringExtra("ACCOUNT_NUMBER");
            currentAccountName = intent.getStringExtra("ACCOUNT_NAME");
            currentAmount = intent.getStringExtra("AMOUNT");
            currentContent = intent.getStringExtra("CONTENT");

            tvBankName.setText(currentBankName != null ? currentBankName : "");
            tvAccountNumber.setText(currentAccountNumber != null ? currentAccountNumber : "");
            tvAccountName.setText(currentAccountName != null ? currentAccountName : "");
            
            updateAmountUI(currentAmount);
            tvContent.setText(currentContent != null ? currentContent : "");
            
            // Generate QR Image
            if (currentBankBin != null && !currentBankBin.isEmpty() && currentAccountNumber != null && !currentAccountNumber.isEmpty()) {
                generateVietQR(currentBankBin, currentAccountNumber, currentAccountName, currentAmount, currentContent, false);
            } else {
                Toast.makeText(this, "Lỗi: Dữ liệu QR không hợp lệ.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Missing required data to generate QR. BIN: " + currentBankBin + ", AccountNumber: " + currentAccountNumber);
            }
        } else {
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Intent is null, cannot display QR details.");
            finish();
        }
    }
    
    private void updateAmountUI(String amount) {
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
    }
    
    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_edit_qr, null);
        final TextInputEditText inputAmount = viewInflated.findViewById(R.id.etEditAmount);
        final TextInputEditText inputContent = viewInflated.findViewById(R.id.etEditContent);
        final TextInputEditText inputCTK = viewInflated.findViewById(R.id.editCTK);
        // stk
        final TextInputEditText inputSTK = viewInflated.findViewById(R.id.editSTK);

        inputSTK.setText(currentAccountNumber);
        inputCTK.setText(currentAccountName);
        inputAmount.setText(currentAmount);
        inputContent.setText(currentContent);

        builder.setView(viewInflated);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            // Update data
            String newCTK = inputCTK.getText().toString();
            String newSTK = inputSTK.getText().toString();
            String newAmount = inputAmount.getText().toString();
            String newContent = inputContent.getText().toString();
            
            updateRecipientInfo(newCTK, newSTK, newAmount, newContent);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    
    private void updateRecipientInfo(String newCTK,String newSTK, String newAmount, String newContent) {
        currentAccountName = newCTK;
        currentAccountNumber = newSTK;
        currentAmount = newAmount;
        currentContent = newContent;
        
        // Update UI
        updateAmountUI(currentAmount);
        tvContent.setText(currentContent);
        
        // Regenerate QR and Update DB
        generateVietQR(currentBankBin, currentAccountNumber, currentAccountName, currentAmount, currentContent, true);
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

    private void generateVietQR(String bin, String accountNumber, String accountName, String amount, String content, boolean updateDb) {
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
        
        if (updateDb) {
            Toast.makeText(this, "Đang cập nhật QR...", Toast.LENGTH_SHORT).show();
        }

        ApiClient.getService().generateQR(request).enqueue(new Callback<VietQRResponse<GenQRResponse>>() {
            @Override
            public void onResponse(Call<VietQRResponse<GenQRResponse>> call, Response<VietQRResponse<GenQRResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VietQRResponse<GenQRResponse> vietQRResponse = response.body();
                    if ("00".equals(vietQRResponse.getCode()) && vietQRResponse.getData() != null) {
                        String base64Image = vietQRResponse.getData().getQrDataURL();
                        String rawBase64 = base64Image; // Keep full string for DB if needed, usually just need data part
                        
                        if (base64Image != null && base64Image.contains(",")) {
                            base64Image = base64Image.split(",")[1];
                        }
                        
                        try {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            currentQRBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ivQRCode.setImageBitmap(currentQRBitmap);
                            
                            if (updateDb && recipientId != -1) {
                                updateDatabase(rawBase64);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VietQRResponse<GenQRResponse>> call, Throwable t) {
                if (updateDb) {
                     Toast.makeText(QRDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void updateDatabase(String qrDataURL) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Recipient recipient = AppDatabase.getDatabase(this).recipientDao().getRecipientById(recipientId);
            if (recipient != null) {
                recipient.accountName = currentAccountName;
                recipient.accountNumber = currentAccountNumber;
                recipient.amount = currentAmount;
                recipient.content = currentContent;
                recipient.qrDataURL = qrDataURL;
                AppDatabase.getDatabase(this).recipientDao().updateRecipient(recipient);
                
                runOnUiThread(() -> Toast.makeText(this, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show());
            }
        });
        executor.shutdown();
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