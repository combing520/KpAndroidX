package cn.cc1w.app.ui.kpush;

import android.content.Context;

import com.mpush.api.Logger;

import cn.ccwb.push.mpush.MPushLog;

/**
 * @author ohun
 * @date 16/9/22
 */
public class MyLog implements Logger {
    private final Context mContext;
    private final MPushLog mPushLog;

    public MyLog(Context context) {
        this.mContext = context;
        this.mPushLog = new MPushLog();
    }

    @Override
    public void enable(boolean b) {
        this.mPushLog.enable(true);
    }

    @Override
    public void d(String s, Object... objects) {
        mPushLog.d(s, objects);
    }

    @Override
    public void i(String s, Object... objects) {
        mPushLog.i(s, objects);
    }

    @Override
    public void w(String s, Object... objects) {
        mPushLog.w(s, objects);
    }

    @Override
    public void e(Throwable throwable, String s, Object... objects) {
        mPushLog.e(throwable, s, objects);
    }
}
