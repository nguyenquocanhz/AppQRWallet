package com.nqatech.vqr.service.parser;

public class BankParserFactory {
    public static IBankParser getParser(String packageName) {
        if (packageName == null) return new DefaultParser();

        String pkg = packageName.toLowerCase();

        // Kiểm tra nếu là VCB
        if (pkg.contains("com.vietcombank") || pkg.contains("vcb")) {
            return new VCBParser();
        }

        // Thêm các ngân hàng khác ở đây trong tương lai
        // if (pkg.contains("mb")) return new MBParser();

        // Mặc định
        return new DefaultParser();
    }
}