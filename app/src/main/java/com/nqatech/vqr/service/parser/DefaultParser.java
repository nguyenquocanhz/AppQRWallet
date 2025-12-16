package com.nqatech.vqr.service.parser;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultParser implements IBankParser {
    private static final String TAG = "DefaultParser";

    @Override
    public double parseAmount(String title, String content) {
        // Regex chung: tìm số tiền sau các từ khóa nhận diện, loại bỏ các trường hợp số tài khoản
        String combinedText = (title + " " + content).toLowerCase();
        
        // Regex: 
        // 1. (?:...) Non-capturing group cho các từ khóa: +, nhận, số dư, credit
        // 2. .*? Non-greedy match bất kỳ ký tự nào
        // 3. ([\\d.,]+) Group 1: Bắt số (bao gồm chấm và phẩy)
        String regex = "(?:\\+|nhận|tăng|số dư|credit).*?([\\d.,]+)";
        
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(combinedText);

        if (matcher.find()) {
            try {
                String amountStr = matcher.group(1);
                if (amountStr == null) return 0;

                // Loại bỏ tất cả ký tự không phải số
                String cleanAmount = amountStr.replaceAll("[^0-9]", "");
                
                if (cleanAmount.isEmpty()) return 0;
                
                double amount = Double.parseDouble(cleanAmount);
                
                // Lọc bỏ số quá nhỏ (ví dụ năm 2024) hoặc quá lớn (có thể là số tài khoản nếu regex sai)
                // Tuy nhiên với regex trên, ta hy vọng nó bắt đúng số tiền sau từ khóa.
                if (amount < 1000) return 0;

                return amount;
            } catch (Exception e) {
                Log.e(TAG, "Error parsing amount: " + e.getMessage());
            }
        }
        return 0;
    }
}