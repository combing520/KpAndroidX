package cn.cc1w.app.ui.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.cc1w.app.ui.R;

import cn.cc1w.app.ui.application.AppContext;

/**
 *
 * @author kpinfo
 * Toast 弹窗工具类
 */
public class ToastUtil {
    /**
     * 显示时常较短的弹窗
     *
     * @param msg 弹窗的信息
     */
    public static void showShortToast(String msg) {

        Context context = AppContext.getAppContext();
        if (null != context) {

            TextView tv = new TextView(context);
            tv.setBackgroundColor(Color.parseColor("#000000"));
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setPadding(AppUtil.dip2px(context, 20), AppUtil.dip2px(context, 8), AppUtil.dip2px(context, 20), AppUtil.dip2px(context, 8));
            tv.setBackgroundResource(R.drawable.bg_container_toast);
            tv.setText(msg);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(tv);
            toast.show();
        }
    }

    /**
     * 显示时常较长的弹窗
     *
     * @param msg 弹窗的信息
     */
    public static void showLongToast(String msg) {

        Context context = AppContext.getAppContext();
        if (null != context && !TextUtils.isEmpty(msg)) {

            TextView tv = new TextView(context);
            tv.setBackgroundColor(Color.parseColor("#000000"));
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setPadding(AppUtil.dip2px(context, 20), AppUtil.dip2px(context, 8), AppUtil.dip2px(context, 20), AppUtil.dip2px(context, 8));
            tv.setBackgroundResource(R.drawable.bg_container_toast);
            tv.setText(msg);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(tv);
            toast.show();
        }
    }


    /**
     * @param msg 弹窗的信息
     */
    public static void showSuccessToast(String msg) {
        Context context = AppContext.getAppContext();
        if (null != context && !TextUtils.isEmpty(msg)) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_toast_success, null);
            TextView tv = view.findViewById(R.id.tv);
            tv.setText(msg);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(view);
            toast.show();
        }
    }
}