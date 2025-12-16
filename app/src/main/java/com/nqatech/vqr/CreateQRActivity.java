package com.nqatech.vqr;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.nqatech.vqr.adapter.BankAdapter;
import com.nqatech.vqr.api.ApiClient;
import com.nqatech.vqr.api.VietQRService;
import com.nqatech.vqr.api.model.Bank;
import com.nqatech.vqr.api.model.VietQRResponse;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.entity.Recipient;
import com.nqatech.vqr.theme.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class CreateQRActivity extends AppCompatActivity {

    private RecyclerView rvBanks;
    private BankAdapter bankAdapter;
    private TextInputEditText etAccountNumber;
    private TextInputEditText etAccountName;
    private TextInputEditText etAmount;
    private TextInputEditText etContent;
    private List<Bank> bankList = new ArrayList<>();
    
    private Bank selectedBank = null; 

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvBanks = findViewById(R.id.rvBanks);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        etAccountName = findViewById(R.id.etAccountName);
        etAmount = findViewById(R.id.etAmount);
        etContent = findViewById(R.id.etContent);

        setupBankList();
        setupGenerateButton();
    }

    private void setupBankList() {
        VietQRService service = ApiClient.getService();
        service.getBanks().enqueue(new Callback<VietQRResponse<List<Bank>>>() {
            @Override
            public void onResponse(Call<VietQRResponse<List<Bank>>> call, Response<VietQRResponse<List<Bank>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VietQRResponse<List<Bank>> vietQRResponse = response.body();
                    if ("00".equals(vietQRResponse.getCode())) {
                        bankList = vietQRResponse.getData();
                        
                        bankAdapter = new BankAdapter(bankList, bank -> {
                            selectedBank = bank;
                            Toast.makeText(CreateQRActivity.this, "Đã chọn: " + bank.getShortName(), Toast.LENGTH_SHORT).show();
                        });
                        rvBanks.setAdapter(bankAdapter);
                    } else {
                         Toast.makeText(CreateQRActivity.this, "Failed to load banks: " + vietQRResponse.getDesc(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                     Toast.makeText(CreateQRActivity.this, "Error loading banks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VietQRResponse<List<Bank>>> call, Throwable t) {
                Toast.makeText(CreateQRActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGenerateButton() {
        TextView btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(v -> {
            String accountNumber = etAccountNumber.getText() != null ? etAccountNumber.getText().toString() : "";
            String accountName = etAccountName.getText() != null ? etAccountName.getText().toString() : "";
            String amount = etAmount.getText() != null ? etAmount.getText().toString() : "";
            String content = etContent.getText() != null ? etContent.getText().toString() : "";

            if (selectedBank == null || accountNumber.isEmpty() || accountName.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngân hàng và điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String bin = selectedBank.getBin();
            String bankCode = selectedBank.getCode();
            String bankNameStr = selectedBank.getShortName() + " - " + selectedBank.getName();

            // Save to Room Database
            Recipient recipient = new Recipient(bankNameStr, bankCode, bin, accountNumber, accountName, amount, content);
            AppDatabase.getDatabase(this).recipientDao().insertRecipient(recipient);

            Toast.makeText(this, "Đã tạo QR thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}