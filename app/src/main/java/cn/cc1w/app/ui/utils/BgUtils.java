package cn.cc1w.app.ui.utils;


import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author kpinfo
 */
public class BgUtils {
    /**
     * 用于执行定时任务
     */
    private Timer timer = null;

    /**
     * 用于保存当前任务
     */
    private List<MyTimerTask> tasks = null;

    /**
     * 唯一实例
     */
    private static BgUtils atyModule;

    private BgUtils() {
        // 初始化
        tasks = new ArrayList<MyTimerTask>();
        timer = new Timer();
    }

    /**
     * 获取唯一实例
     *
     * @return 唯一实例
     */
    public static BgUtils getinstance() {
        if (atyModule == null) {
            atyModule = new BgUtils();
        }
        return atyModule;
    }

    /**
     * 在activity的onPause()方法中调用
     */
    public void onPause(final Activity activity) {
        MyTimerTask task = new MyTimerTask(activity);
        tasks.add(task);
        timer.schedule(task, 1000);
    }

    /**
     * 在activity的onResume()方法中调用
     */
    public void onResume() {
        if (tasks.size() > 0) {
            tasks.get(tasks.size() - 1).setCanRun(false);
            tasks.remove(tasks.size() - 1);
        }
    }

    /**
     * 自定义TimerTask类
     */
    class MyTimerTask extends TimerTask {
        /**
         * 任务是否有效
         */
        private boolean canRun = true;
        private Activity activity;

        public void setCanRun(boolean canRun) {
            this.canRun = canRun;
        }

        public MyTimerTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (canRun) {
                        // 程序退到后台，进行风险警告
                        ToastUtil.showLongToast("温馨提示：开屏新闻已切换至后台运行");
                        tasks.remove(MyTimerTask.this);
                    }
                }
            });
        }
    }
}
