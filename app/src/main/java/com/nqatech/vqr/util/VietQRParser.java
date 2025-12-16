package com.nqatech.vqr.util;

import com.nqatech.vqr.database.entity.Recipient;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VietQRParser {

    public static Recipient parse(String rawQR) {
        if (rawQR == null || rawQR.isEmpty()) return null;

        if (rawQR.startsWith("http")) {
            return parseQuickLink(rawQR);
        }

        return parseEMVCo(rawQR);
    }

    private static Recipient parseQuickLink(String url) {
        try {
            // Pattern: ...image/{BIN}-{ACCOUNT}-...
            Pattern pattern = Pattern.compile("image/(\\d+)-([a-zA-Z0-9]+)-");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                String bin = matcher.group(1);
                String accNum = matcher.group(2);
                
                String amount = "0";
                String content = "";
                
                if (url.contains("amount=")) {
                    try {
                        String temp = url.split("amount=")[1];
                        amount = temp.split("&")[0];
                    } catch (Exception e) {}
                }
                if (url.contains("addInfo=")) {
                     try {
                        String temp = url.split("addInfo=")[1];
                        content = temp.split("&")[0];
                     } catch (Exception e) {}
                }

                // Note: We don't have bankName or bankCode from QuickLink URL directly unless we look it up.
                // We'll set generic values.
                return new Recipient("Ngân hàng (Auto)", "", bin, accNum, "Người nhận", amount, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Recipient parseEMVCo(String qr) {
        try {
            // Check GUID VietQR: A000000727
            // This is a simplified check. Real parser should walk through the TLV.
            if (!qr.contains("A000000727")) return null;

            Map<String, String> tags = parseTLV(qr);
            String tag38 = tags.get("38");
            
            if (tag38 != null) {
                Map<String, String> subTags38 = parseTLV(tag38);
                String tag01 = subTags38.get("01"); // Beneficiary Org
                
                if (tag01 != null) {
                    Map<String, String> subTags01 = parseTLV(tag01);
                    String bin = subTags01.get("00");
                    String accNum = subTags01.get("01");

                    String amount = tags.get("54"); // Transaction Amount
                    if (amount == null) amount = "0";
                    
                    String content = tags.get("62"); // Additional Data Field
                    if (content != null) {
                         Map<String, String> subTags62 = parseTLV(content);
                         content = subTags62.get("08"); // Reference Label (often used for content)
                         if (content == null) content = "";
                    } else {
                        content = "";
                    }

                    return new Recipient("Ngân hàng (Auto)", "", bin, accNum, "Người nhận", amount, content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, String> parseTLV(String data) {
        Map<String, String> result = new HashMap<>();
        int i = 0;
        while (i < data.length()) {
            if (i + 4 > data.length()) break;
            try {
                String tag = data.substring(i, i + 2);
                int len = Integer.parseInt(data.substring(i + 2, i + 4));
                if (i + 4 + len > data.length()) break;
                String value = data.substring(i + 4, i + 4 + len);
                result.put(tag, value);
                i += 4 + len;
            } catch (NumberFormatException e) {
                break;
            }
        }
        return result;
    }
}