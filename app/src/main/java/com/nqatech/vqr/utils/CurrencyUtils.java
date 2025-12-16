package com.nqatech.vqr.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtils {

    public static String formatVND(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }

    public static String formatVND(String amount) {
        try {
            long value = Long.parseLong(cleanAmount(amount));
            return formatVND(value);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    public static String cleanAmount(String amount) {
        if (amount == null) return "";
        return amount.replaceAll("[^0-9]", "");
    }

    public static long parseAmount(String amount) {
        try {
            return Long.parseLong(cleanAmount(amount));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
