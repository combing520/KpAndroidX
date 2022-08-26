package cn.cc1w.app.ui.utils;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cn.cc1w.app.ui.application.AppContext;


/**
 *
 * @author kpinfo
 * 输入法管理类
 */
public class KeybordUtil {


    /**
     * 获取输入法是否 打开
     *
     * @return 输入法的打开状态
     */
    public static boolean isInputMethodActive() {

        Context context = AppContext.getAppContext();
        if (null != context) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            return manager.isActive();
        }

        return false;
    }

    /**
     * 关闭 输入法
     */
    public static void closeInputMethod(Activity context) {
        if (isInputMethodActive()) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != manager) {
                if (null!= context.getCurrentFocus() && null != context.getCurrentFocus().getWindowToken()) {
                    manager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    public static void hideSoftInput(Fragment fragment) {
        View v = fragment.getActivity().getCurrentFocus();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) AppContext.getAppContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = manager.isActive();
            if (isOpen) {
                manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideSoftInput(Context context, View view) {
        if (view == null || context == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出软键盘
     */
    public static void showSoftInput(Context context, View view) {
        try {
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}