package cn.cc1w.app.ui.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/**
 * 倒计时
 *
 * @author kpinfo
 */
public abstract class MyCountDownTimer {
    private final long mMillisInFuture;

    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private boolean mCancelled = false;

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized final MyCountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);
    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    private static final int MSG = 1;

    // handles counting down
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            synchronized (MyCountDownTimer.this) {
                if (mCancelled) {
                    return;
                }
                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
//                LogUtil.e("millisLeft = " + millisLeft);
                if (millisLeft <= 0) {
                    onFinish();
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);
                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();
                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) {
                        delay += mCountdownInterval;
                    }
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };
}