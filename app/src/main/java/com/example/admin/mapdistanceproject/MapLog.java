package com.example.admin.mapdistanceproject;

import android.util.Log;

public class MapLog {

    public static void debug(String str) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (str.length() > 4000) {
            Log.d("MapDistance", str.substring(0, 4000));
            debug(str.substring(4000));
        } else {
            Log.d("MapDistance", str);
        }
    }

    public static void error(String str) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (str.length() > 4000) {
            Log.e("MapDistance", str.substring(0, 4000));
            error(str.substring(4000));
        } else {
            Log.e("MapDistance", str);
        }
    }

    public static void debug(String TAG, String str) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (str.length() > 4000) {
            Log.d(TAG, str.substring(0, 4000));
            debug(TAG, str.substring(4000));
        } else {
            Log.d(TAG, str);
        }
    }

    public static void error(String TAG, String str) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (str.length() > 4000) {
            Log.e(TAG, str.substring(0, 4000));
            error(TAG, str.substring(4000));
        } else {
            Log.e(TAG, str);
        }
    }
}
