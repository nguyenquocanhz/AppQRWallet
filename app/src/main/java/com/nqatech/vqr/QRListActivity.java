package com.nqatech.vqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nqatech.vqr.adapter.VietQRAdapter;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.entity.Recipient;
import com.nqatech.vqr.theme.ThemeManager;
import java.util.ArrayList;
import java.util.List;

public class QRListActivity extends AppCompatActivity {

    private RecyclerView rvQRList;
    private VietQRAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_list);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvQRList = findViewById(R.id.rvQRList);
        rvQRList.setLayoutManager(new LinearLayoutManager(this));

        loadQRList();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadQRList();
    }

    private void loadQRList() {
        List<Recipient> recipients = AppDatabase.getDatabase(this).recipientDao().getAllRecipients();
        
        List<VietQRAdapter.VietQRItem> items = new ArrayList<>();
        if (recipients != null && !recipients.isEmpty()) {
            for (Recipient recipient : recipients) {
                // Use a fuller date format for the list view
                String dateStr = android.text.format.DateFormat.format("dd/MM/yyyy", recipient.createdAt).toString();
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
                items.add(item);
            }
        }

        adapter = new VietQRAdapter(items, item -> {
            Intent intent = new Intent(QRListActivity.this, QRDetailActivity.class);
            intent.putExtra("RECIPIENT_ID", item.id);
            intent.putExtra("BANK_NAME", item.bankName);
            intent.putExtra("BANK_BIN", item.bin);
            intent.putExtra("ACCOUNT_NUMBER", item.accountNumber);
            intent.putExtra("ACCOUNT_NAME", item.accountName);
            intent.putExtra("AMOUNT", item.amount);
            intent.putExtra("CONTENT", item.content);
            startActivity(intent);
        });
        rvQRList.setAdapter(adapter);
    }
}