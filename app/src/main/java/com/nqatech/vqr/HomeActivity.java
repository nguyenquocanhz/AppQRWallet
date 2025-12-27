package com.nqatech.vqr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nqatech.vqr.adapter.VietQRAdapter;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.entity.Recipient;
import com.nqatech.vqr.theme.ThemeManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int SELECT_QR_REQUEST_CODE = 1;

    private RecyclerView rvRecentRecipients;
    private VietQRAdapter adapter;
    private ImageView ivGreetingIcon;
    private TextView tvGreeting;
    private TextView tvUserName;
    private ImageView ivAvatar;
    private TextView tvPinnedBank;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Header Views
        ivGreetingIcon = findViewById(R.id.ivGreetingIcon);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserName = findViewById(R.id.tvUserName);
        ivAvatar = findViewById(R.id.ivAvatar);
        updateGreeting();

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AlertActivity.class);
            startActivity(intent);
        });

        // Navigation
        LinearLayout navSettings = findViewById(R.id.navSettings);
        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Setup Floating Scan Button
        FrameLayout btnScanCenter = findViewById(R.id.btnScanCenter);
        btnScanCenter.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ScanQRActivity.class);
            startActivity(intent);
        });

        // Utilities
        LinearLayout btnCreateQR = findViewById(R.id.btnCreateQR);
        btnCreateQR.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateQRActivity.class);
            startActivity(intent);
        });

        LinearLayout btnQuickScan = findViewById(R.id.btnQuickScan);
        btnQuickScan.setOnClickListener(v -> {
             Intent intent = new Intent(HomeActivity.this, ScanQRActivity.class);
             startActivity(intent);
        });
        
        // History Utility
        LinearLayout btnNotificationHistory = findViewById(R.id.btnNotificationHistory);
        btnNotificationHistory.setOnClickListener(v ->
        {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Pinned QR
        tvPinnedBank = findViewById(R.id.tvPinnedBank);
        View cardPinnedQR = findViewById(R.id.cardPinnedQR);
        cardPinnedQR.setOnClickListener(v -> {
             Intent intent = new Intent(HomeActivity.this, QRListActivity.class);
             intent.putExtra("SELECTION_MODE", true);
             startActivityForResult(intent, SELECT_QR_REQUEST_CODE);
        });
        
        loadPinnedQR();

        // Setup RecyclerView
        rvRecentRecipients = findViewById(R.id.rvRecentRecipients);
        rvRecentRecipients.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        TextView tvSeeAll = findViewById(R.id.tvSeeAll);
        tvSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, QRListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentRecipients();
        loadPinnedQR();
        updateGreeting(); // Refresh greeting in case of name change
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            int recipientId = data.getIntExtra("SELECTED_RECIPIENT_ID", -1);
            if (recipientId != -1) {
                // Inefficient to scan all, but works for now. Better to have getById.
                List<Recipient> list = AppDatabase.getDatabase(this).recipientDao().getAllRecipients();
                for (Recipient r : list) {
                    if (r.id == recipientId) {
                        pinRecipient(r);
                        break;
                    }
                }
            }
        }
    }

    private void updateGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String greetingText;
        if (timeOfDay >= 6 && timeOfDay < 18) {
            greetingText = "Chào buổi sáng";
            ivGreetingIcon.setImageResource(R.drawable.ic_sun);
        } else {
            greetingText = "Chào buổi tối";
            ivGreetingIcon.setImageResource(R.drawable.ic_moon);
        }
        tvGreeting.setText(greetingText);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        String displayName = "Admin"; // Default name
        Uri photoUrl = null;

        if (acct != null) {
            if (acct.getDisplayName() != null && !acct.getDisplayName().isEmpty()) {
                displayName = acct.getDisplayName();
            }
            photoUrl = acct.getPhotoUrl();
        } else {
            // Fallback to SharedPreferences if Google account is not available
            SharedPreferences prefs = getSharedPreferences("vqr_prefs", MODE_PRIVATE);
            displayName = prefs.getString("user_name", "Admin");
        }
        
        tvUserName.setText(displayName);

        if (photoUrl != null) {
            Glide.with(this)
                 .load(photoUrl)
                 .placeholder(R.drawable.ic_widgets) // Default icon
                 .error(R.drawable.ic_widgets)     // Icon on error
                 .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_widgets);
        }
    }
    
    private void pinRecipient(Recipient recipient) {
        SharedPreferences prefs = getSharedPreferences("vqr_prefs", MODE_PRIVATE);
        prefs.edit().putInt("pinned_qr_id", recipient.id).apply();
        loadPinnedQR();
        Toast.makeText(this, "Đã ghim: " + recipient.bankName, Toast.LENGTH_SHORT).show();
    }
    
    private void loadPinnedQR() {
        SharedPreferences prefs = getSharedPreferences("vqr_prefs", MODE_PRIVATE);
        int pinnedId = prefs.getInt("pinned_qr_id", -1);
        
        if (pinnedId != -1) {
            // Inefficient scan, should be replaced with a direct DB query
            List<Recipient> list = AppDatabase.getDatabase(this).recipientDao().getAllRecipients();
            for (Recipient r : list) {
                if (r.id == pinnedId) {
                    tvPinnedBank.setText(r.bankName + "\n" + r.accountNumber);
                    // Set click listener to open detail directly
                    findViewById(R.id.cardPinnedQR).setOnClickListener(v -> {
                        Intent intent = new Intent(HomeActivity.this, QRDetailActivity.class);
                        intent.putExtra("RECIPIENT_ID", r.id);
                        intent.putExtra("BANK_NAME", r.bankName);
                        intent.putExtra("BANK_BIN", r.bin);
                        intent.putExtra("ACCOUNT_NUMBER", r.accountNumber);
                        intent.putExtra("ACCOUNT_NAME", r.accountName);
                        intent.putExtra("AMOUNT", r.amount);
                        intent.putExtra("CONTENT", r.content);
                        startActivity(intent);
                    });
                    return;
                }
            }
        }
        // If not found or not set, reset the listener to selection mode
        tvPinnedBank.setText("Chạm để chọn mã QR mặc định");
        findViewById(R.id.cardPinnedQR).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, QRListActivity.class);
            intent.putExtra("SELECTION_MODE", true);
            startActivityForResult(intent, SELECT_QR_REQUEST_CODE);
        });
    }

    private void loadRecentRecipients() {
        List<Recipient> recipients = AppDatabase.getDatabase(this).recipientDao().getAllRecipients();
        
        List<VietQRAdapter.VietQRItem> items = new ArrayList<>();
        if (recipients != null && !recipients.isEmpty()) {
            for (Recipient recipient : recipients) {
                String dateStr = android.text.format.DateFormat.format("MM/yy", recipient.createdAt).toString();
                VietQRAdapter.VietQRItem item = new VietQRAdapter.VietQRItem(
                        recipient.id,
                        recipient.bankName,
                        recipient.bankCode,
                        recipient.bin,
                        recipient.accountNumber,
                        recipient.accountName,
                        dateStr
                );
                item.amount = recipient.amount;
                item.content = recipient.content;
                item.qrDataURL = recipient.qrDataURL; // Added mapping
                
                Log.d("HomeActivity", "Recipient ID: " + recipient.id + ", QR URL Length: " + 
                     (recipient.qrDataURL != null ? recipient.qrDataURL.length() : "null"));
                     
                items.add(item);
            }
        }

        adapter = new VietQRAdapter(items, item -> {
            Intent intent = new Intent(HomeActivity.this, QRDetailActivity.class);
            intent.putExtra("RECIPIENT_ID", item.id);
            intent.putExtra("BANK_NAME", item.bankName);
            intent.putExtra("BANK_BIN", item.bin);
            intent.putExtra("ACCOUNT_NUMBER", item.accountNumber);
            intent.putExtra("ACCOUNT_NAME", item.accountName);
            intent.putExtra("AMOUNT", item.amount);
            intent.putExtra("CONTENT", item.content);
            startActivity(intent);
        });
        rvRecentRecipients.setAdapter(adapter);
    }
}