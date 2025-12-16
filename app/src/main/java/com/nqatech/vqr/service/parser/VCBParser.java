package com.nqatech.vqr.service.parser;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VCBParser implements IBankParser {
    private static final String TAG = "VCBParser";

    @Override
    public double parseAmount(String title, String content) {
        // VCB thường có format: "Số dư TK VCB ... +2,000 VND ..."
        // Chúng ta sẽ tìm dấu + hoặc - đi liền với số tiền
        
        String combinedText = (title + " " + content); // Giữ nguyên case hoặc toLowerCase tùy nhu cầu, nhưng VCB viết hoa VND
        
        // Regex:
        // [+-] : Tìm dấu cộng hoặc trừ
        // \s*  : Có thể có khoảng trắng
        // ([\d,.]+) : Group 1 - Số tiền
        // \s*  : Khoảng trắng
        // (?:VND|đ|VNĐ) : Đơn vị tiền tệ (không bắt buộc)
        
        String regex = "[+-]\\s*([\\d,.]+)\\s*(?:VND|đ|VNĐ)?";
        
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(combinedText);

        if (matcher.find()) {
            try {
                String amountStr = matcher.group(1);
                Log.d(TAG, "Found VCB amount string: " + amountStr);
                
                if (amountStr == null) return 0;

                // VCB dùng dấu phẩy cho hàng nghìn (2,000) hoặc dấu chấm tùy cài đặt
                // Nhưng thường là 2,000. Chúng ta xóa hết ký tự không phải số
                String cleanAmount = amountStr.replaceAll("[^0-9]", "");
                
                if (cleanAmount.isEmpty()) return 0;
                
                return Double.parseDouble(cleanAmount);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing VCB: " + e.getMessage());
            }
        }
        
        // Nếu không tìm thấy bằng regex trên, thử fallback về logic tìm từ khóa "số dư" 
        // nhưng phải cẩn thận tránh số tài khoản.
        // Tuy nhiên format "+2,000" của VCB khá chuẩn, nên regex trên là đủ.
        
        return 0;
    }
}