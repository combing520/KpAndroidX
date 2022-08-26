package cn.cc1w.app.ui.utils;

import android.text.TextUtils;
import android.util.Log;

import cn.cc1w.app.ui.BuildConfig;

/**
 * 日志输出工具类
 *
 * @author kpinfo
 */
public class LogUtil {

    private static final String TAG = "kpInfo";
    private static final boolean IS_DEBUG = BuildConfig.DEBUG;

    /**
     * 打印控制台日志 打印的日志的文字颜色为红色
     *
     * @param msg 打印的内容
     */
    public static void e(String msg) {
        if (IS_DEBUG && !TextUtils.isEmpty(msg)) {
            Log.e(TAG, msg);
        }
    }

    /**
     * 打印控制台日志 打印的日志的文字为 绿色
     *
     * @param msg 消息
     */
    public static void d(String msg) {
        if (IS_DEBUG && !TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }

    public static void print(String tag, String msg) {
        if (IS_DEBUG && !TextUtils.isEmpty(msg)) {
            Log.d(tag, msg);
        }
    }
}
