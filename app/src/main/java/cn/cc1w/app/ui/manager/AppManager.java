package cn.cc1w.app.ui.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * AppManager
 * @author kpinfo
 */
public class AppManager {
    private static Stack<Activity> activityStack = new Stack<Activity>();

    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    public static void setActivityStack(Stack<Activity> activityStack) {
        AppManager.activityStack = activityStack;
    }

    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(Activity activity) {
        if (null != activityStack && !activityStack.contains(activity)) {
            activityStack.push(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        Activity activity = activityStack.pop();
        activity.finish();
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (Activity activity : activityStack) {
            if (activity != null) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 查看 Stack 是有有对应的Activity
     *
     * @param cls 指定的Activity
     */
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    /**
     * 退出应用程序
     */
    public static void appExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            manager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}