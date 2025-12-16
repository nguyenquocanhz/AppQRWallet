package com.nqatech.vqr;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nqatech.vqr.adapter.NotificationHistoryAdapter;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.entity.NotificationHistory;
import com.nqatech.vqr.theme.ThemeManager;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmpty;
    private NotificationHistoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        tvEmpty = findViewById(R.id.tvEmpty);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadHistory();
    }

    private void setupRecyclerView() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadHistory() {
        // Database operations should be on a background thread in a real app
        // Using allowMainThreadQueries for simplicity as seen in AppDatabase
        List<NotificationHistory> historyList = AppDatabase.getDatabase(this).notificationHistoryDao().getAll();

        if (historyList == null || historyList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            adapter = new NotificationHistoryAdapter(historyList);
            rvHistory.setAdapter(adapter);
        }
    }
}