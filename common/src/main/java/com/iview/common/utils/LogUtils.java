package com.iview.common.utils;

import android.util.Log;

public class LogUtils {

    private static Boolean bDebug = true;

    public static void i(String tag, String log) {
        if (bDebug) {
            Log.i(tag, log);
        }
    }

    public static void d(String tag, String log) {
        if (bDebug) {
            Log.d(tag, log);
        }
    }

    public static void w(String tag, String log) {
        Log.w(tag, log);
    }

    public static void e(String tag, String log) {
        Log.e(tag, log);
    }
}
