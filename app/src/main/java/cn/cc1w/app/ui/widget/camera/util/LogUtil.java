package cn.cc1w.app.ui.widget.camera.util;

import android.util.Log;

import cn.cc1w.app.ui.BuildConfig;


/**
 * @author kpinfo
 */
public class LogUtil {
    private static final String DEFAULT_TAG = "Camera";

    public static void i(String tag, String msg) {
//        if (DEBUG)
        Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public static void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public static void d(String msg) {
        d(DEFAULT_TAG, msg);
    }

    public static void e(String msg) {
        e(DEFAULT_TAG, msg);
    }
}
