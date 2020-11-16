package com.iview.common.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;


/**
 * 项目父Application
 */
public class BaseApplication extends Application {
    private final static String TAG = "BaseApplication";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "common/BaseApplication");

        context=getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
