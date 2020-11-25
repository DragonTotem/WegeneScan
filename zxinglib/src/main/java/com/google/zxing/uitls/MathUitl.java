package com.google.zxing.uitls;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Author       :xietinghui
 * Date         :2019/1/29
 * Version      :1.0.0
 * Description  :
 */
public class MathUitl {

    public static int stringToInt(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0;
        }
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static float stringToFloat(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0f;
        }
        try {
            return Float.parseFloat(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0f;
        }
    }

    public static double stringToDouble(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0d;
        }
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0d;
        }
    }

    public static long stringToLong(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0;
        }
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getPercent(String ratio) {
        return getPercent(ratio, true);
    }

    /**
     * 根据报告比例返回保留两位小数的百分比
     *
     * @param ratio
     * @param isScale 是否限制小数位
     * @return
     */
    public static String getPercent(String ratio, boolean isScale) {
        double result = stringToDouble(ratio);
        BigDecimal bg1 = BigDecimal.valueOf(result);
        BigDecimal bg2 = BigDecimal.valueOf(100);
        double percent;
        if (isScale) {
            percent = bg1.multiply(bg2).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return new DecimalFormat("0.00").format(percent) + "%";
        } else {
            percent = bg1.multiply(bg2).doubleValue();
            return percent + "%";
        }
    }

    /**
     * double只保留两位小数
     *
     * @param ratio
     * @return
     */
    public static String getPercent(double ratio) {
        return BigDecimal.valueOf(ratio).setScale(2, BigDecimal.ROUND_DOWN) + "%";
    }

    /**
     * 获取金额
     *
     * @param value
     * @return
     */
    public static String getAmount(double value) {
        DecimalFormat format = new DecimalFormat("0.00");
        format.setRoundingMode(RoundingMode.DOWN);
        return format.format(value);
    }

    public static double toFixed(double value, int scale) {
        return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
