package com.nqatech.vqr;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.nqatech.vqr.adapter.BankAdapter;
import com.nqatech.vqr.api.ApiClient;
import com.nqatech.vqr.api.VietQRRequest;
import com.nqatech.vqr.api.VietQRService;
import com.nqatech.vqr.api.model.Bank;
import com.nqatech.vqr.api.model.GenQRResponse;
import com.nqatech.vqr.api.model.VietQRResponse;
import com.nqatech.vqr.database.entity.Recipient;
import com.nqatech.vqr.theme.ThemeManager;
import com.nqatech.vqr.utils.CurrencyUtils;
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
    private TextInputEditText etSearchBank;
    private ChipGroup chipGroupTemplate;
    private QrFirestoreRepository qrFirestoreRepository;
    
    private List<Bank> originalBankList = new ArrayList<>();
    private List<Bank> displayedBankList = new ArrayList<>();
    
    private Bank selectedBank = null; 

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);

        qrFirestoreRepository = QrFirestoreRepository.getInstance(this);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvBanks = findViewById(R.id.rvBanks);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        etAccountName = findViewById(R.id.etAccountName);
        etAmount = findViewById(R.id.etAmount);
        etContent = findViewById(R.id.etContent);
        etSearchBank = findViewById(R.id.etSearchBank);
        chipGroupTemplate = findViewById(R.id.chipGroupTemplate);
        
        // Add scan icon listener
        ImageView ivScan = findViewById(R.id.ivScan);
        if (ivScan != null) {
            ivScan.setOnClickListener(v -> {
                // Implement scan functionality later or navigate to ScanActivity
                Toast.makeText(this, "Tính năng quét đang cập nhật", Toast.LENGTH_SHORT).show();
            });
        }

        setupBankList();
        setupSearchBank();
        setupAmountInput();
        setupSuggestionChips();
        setupGenerateButton();
        setupAccountName();
    }

    private void setupBankList() {
        VietQRService service = ApiClient.getService();
        service.getBanks().enqueue(new Callback<VietQRResponse<List<Bank>>>() {
            @Override
            public void onResponse(@NonNull Call<VietQRResponse<List<Bank>>> call, @NonNull Response<VietQRResponse<List<Bank>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VietQRResponse<List<Bank>> vietQRResponse = response.body();
                    if ("00".equals(vietQRResponse.getCode())) {
                        originalBankList = vietQRResponse.getData();
                        displayedBankList = new ArrayList<>(originalBankList);
                        
                        bankAdapter = new BankAdapter(displayedBankList, bank -> {
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
            public void onFailure(@NonNull Call<VietQRResponse<List<Bank>>> call, @NonNull Throwable t) {
                Toast.makeText(CreateQRActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchBank() {
        etSearchBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterBanks(s.toString());
            }
        });
    }

    private void filterBanks(String query) {
        if (originalBankList == null || originalBankList.isEmpty()) return;

        displayedBankList.clear();
        if (query.isEmpty()) {
            displayedBankList.addAll(originalBankList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Bank bank : originalBankList) {
                if (bank.getShortName().toLowerCase().contains(lowerQuery) || 
                    bank.getName().toLowerCase().contains(lowerQuery) ||
                    bank.getCode().toLowerCase().contains(lowerQuery)) {
                    displayedBankList.add(bank);
                }
            }
        }
        
        if (bankAdapter != null) {
            bankAdapter.updateData(displayedBankList);
        }
    }

    private void setupAmountInput() {
        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    // auto TEXT UPPERCASE for account name
    private void setupAccountName(){
        etAccountName.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    etAccountName.removeTextChangedListener(this);
                    String cleanStr = s.toString().toUpperCase();
                    etAccountName.setText(cleanStr);
                    etAccountName.setSelection(cleanStr.length());
                    etAccountName.addTextChangedListener(this);
                    current = cleanStr;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupSuggestionChips() {
        Chip chip100k = findViewById(R.id.chip100k);
        Chip chip200k = findViewById(R.id.chip200k);
        Chip chip500k = findViewById(R.id.chip500k);
        Chip chip1m = findViewById(R.id.chip1m);

        chip100k.setOnClickListener(v -> setAmount("100000"));
        chip200k.setOnClickListener(v -> setAmount("200000"));
        chip500k.setOnClickListener(v -> setAmount("500000"));
        chip1m.setOnClickListener(v -> setAmount("1000000"));
    }

    private void setAmount(String amount) {
        // The TextWatcher will format it
        etAmount.setText(amount);
    }

    private void setupGenerateButton() {
        TextView btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(v -> {
            String accountNumber = etAccountNumber.getText() != null ? etAccountNumber.getText().toString() : "";
            String accountName = etAccountName.getText() != null ? etAccountName.getText().toString() : "";
            String amountStr = etAmount.getText() != null ? etAmount.getText().toString() : "";
            String content = etContent.getText() != null ? etContent.getText().toString() : "";

            if (selectedBank == null) {
                Toast.makeText(this, "Vui lòng chọn ngân hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (accountNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tài khoản", Toast.LENGTH_SHORT).show();
                return;
            }
            if (accountName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên chủ tài khoản", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get selected template
            String template = "compact"; // Default
            int checkedId = chipGroupTemplate.getCheckedChipId();
            if (checkedId == R.id.chipCompact2) {
                template = "compact2";
            } else if (checkedId == R.id.chipQrOnly) {
                template = "qr_only";
            } else if (checkedId == R.id.chipPrint) {
                template = "print";
            }
            
            String bin = selectedBank.getBin();
            String bankCode = selectedBank.getCode();
            String bankNameStr = selectedBank.getShortName() + " - " + selectedBank.getName();
            
            int amountInt = 0;
            // 100.000 to 100000
            if (!amountStr.isEmpty()) {
                amountInt = (int) CurrencyUtils.parseAmount(amountStr);
            }
            
            int acqId = 0;
            try {
                acqId = Integer.parseInt(bin);
            } catch (NumberFormatException e) {}

            VietQRRequest request = new VietQRRequest(
                    accountNumber,
                    accountName,
                    acqId,
                    amountInt,
                    content,
                    "text", 
                    template
            );
            
            Toast.makeText(this, "Đang tạo mã QR...", Toast.LENGTH_SHORT).show();

            String finalAmountStr = amountStr;
            ApiClient.getService().generateQR(request).enqueue(new Callback<VietQRResponse<GenQRResponse>>() {
                @Override
                public void onResponse(@NonNull Call<VietQRResponse<GenQRResponse>> call, @NonNull Response<VietQRResponse<GenQRResponse>> response) {
                    String qrDataURL = null;
                    if (response.isSuccessful() && response.body() != null) {
                        VietQRResponse<GenQRResponse> res = response.body();
                        if ("00".equals(res.getCode()) && res.getData() != null) {
                            qrDataURL = res.getData().getQrDataURL();
                            // Clean Base64 prefix if exists
                            if (qrDataURL != null && qrDataURL.contains("base64,")) {
                                qrDataURL = qrDataURL.substring(qrDataURL.indexOf("base64,") + 7);
                            }
                        }
                    }
                    
                    // Save to Room Database
                    Recipient recipient = new Recipient(bankNameStr, bankCode, bin, accountNumber, accountName, finalAmountStr, content);
                    if (qrDataURL != null) {
                        recipient.qrDataURL = qrDataURL;
                    }
                    
                    qrFirestoreRepository.saveRecipient(recipient);

                    Toast.makeText(CreateQRActivity.this, "Đã tạo và lưu QR thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<VietQRResponse<GenQRResponse>> call, Throwable t) {
                    Toast.makeText(CreateQRActivity.this, "Lỗi kết nối khi tạo QR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Recipient recipient = new Recipient(bankNameStr, bankCode, bin, accountNumber, accountName, finalAmountStr, content);
                    qrFirestoreRepository.saveRecipient(recipient);
                    finish();
                }
            });
        });
    }
}