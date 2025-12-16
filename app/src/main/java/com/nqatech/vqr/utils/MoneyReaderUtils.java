package com.nqatech.vqr.utils;

import java.text.DecimalFormat;

public class MoneyReaderUtils {

    public static String readMoney(double amount) {
        if (amount < 1000) {
            return String.valueOf((int) amount) + " đồng";
        }

        long amountLong = (long) amount;
        StringBuilder result = new StringBuilder();

        long ty = amountLong / 1000000000;
        long trieu = (amountLong % 1000000000) / 1000000;
        long ngan = (amountLong % 1000000) / 1000;
        long dong = amountLong % 1000;

        if (ty > 0) {
            result.append(ty).append(" tỷ ");
        }
        if (trieu > 0) {
            result.append(trieu).append(" triệu ");
        }
        if (ngan > 0) {
            result.append(ngan).append(" ngàn ");
        }
        if (dong > 0) {
            result.append(dong).append(" đồng");
        }

        return result.toString().trim();
    }
}