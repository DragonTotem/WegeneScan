package com.google.zxing.uitls;

import android.util.Log;

import com.google.zxing.client.android.BuildConfig;

/**
 * Author       :zbt
 * Date         :2018/8/31
 * Version      :1.0.0
 * Description  :Logcat工具类
 */

public class LogUtil {
    private static boolean isLogEnable = BuildConfig.DEBUG;

    private static String tag = "Scan";

    public static void debug(boolean isEnable) {
        debug(tag, isEnable);
    }

    public static void debug(String logTag, boolean isEnable) {
        tag = logTag;
        isLogEnable = isEnable;
    }

    public static void v(String msg) {
        v(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isLogEnable) Log.v(tag, msg);
    }

    public static void d(String msg) {
        d(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isLogEnable) {
            //信息太长,分段打印
            /*因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
              把4*1024的MAX字节打印长度改为2001字符数*/
            int maxStrLength = 2001 - tag.length();
            while (msg.length() > maxStrLength) {
                Log.d(tag, msg.substring(0, maxStrLength));
                msg = msg.substring(maxStrLength);
            }
            //剩余部分
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        i(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isLogEnable) Log.i(tag, msg);
    }

    public static void w(String msg) {
        w(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isLogEnable) Log.w(tag, msg);
    }

    public static void e(String msg) {
        e(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isLogEnable) Log.e(tag, msg);
    }

    public static void printStackTrace(Throwable t) {
        if (isLogEnable && t != null) t.printStackTrace();
    }

    public static void e(Throwable e) {
        if (!isLogEnable) {
            return;
        }
        if (e != null && e.getStackTrace() != null) {
            if (e.getCause() != null) {
                LogUtil.e("cause:" + e.getCause().toString());
            }
            LogUtil.e("message:" + e.getMessage());
            for (StackTraceElement item :
                    e.getStackTrace()) {
                LogUtil.e(item.toString());
            }
        }
    }

    public static boolean isIsLogEnable() {
        return isLogEnable;
    }
}
