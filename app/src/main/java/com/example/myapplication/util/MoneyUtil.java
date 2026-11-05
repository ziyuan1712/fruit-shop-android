package com.example.myapplication.util;

import java.util.Locale;

/**
 * 金额格式化：商品单价最多保留 1 位小数，合计保留 2 位小数。
 */
public final class MoneyUtil {

    private MoneyUtil() {
    }

    public static String price(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.001) {
            return String.format(Locale.CHINA, "%.0f", value);
        }
        return String.format(Locale.CHINA, "%.1f", value);
    }

    public static String amount(double value) {
        return String.format(Locale.CHINA, "¥%.2f", value);
    }
}
