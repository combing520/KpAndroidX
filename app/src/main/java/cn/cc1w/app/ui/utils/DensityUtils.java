package cn.cc1w.app.ui.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 *
 * @author tongchao
 * @date 17/5/22
 */
public class DensityUtils {
    private DensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static int _SCREEN_WIDTH = 0;

    /**
     * 屏幕的宽,像素
     */
    public static int SCREEN_WIDTH(Context context) {
        if (_SCREEN_WIDTH == 0) {
            _SCREEN_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
        }
        return _SCREEN_WIDTH;
    }

    private static int _SCREEN_HEIGHT = 0;

    /**
     * 屏幕的高,像素
     */
    public static int SCREEN_HEIGHT(Context context) {
        if (_SCREEN_HEIGHT == 0) {
            _SCREEN_HEIGHT = context.getResources().getDisplayMetrics().heightPixels;
        }
        return _SCREEN_HEIGHT;
    }

    /**
     * dp转px
     *
     * @param context
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int dp2sp(Context context, float dpVal) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics()));
    }
}