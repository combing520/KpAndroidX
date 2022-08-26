package cn.cc1w.app.ui.record;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;

import cn.cc1w.app.ui.utils.LogUtil;

/**
 * 录音工具类
 */
public class AudioRecoderUtils {
    private String filePath;
    private String FolderPath = "";
    private MediaRecorder mMediaRecorder;
    private final String TAG = "fan";
    private static final int MAX_LENGTH = 1000 * 60 * 10;
    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    public AudioRecoderUtils(Context context) {
        if (context.getExternalCacheDir() != null) {
            String filePath = context.getExternalCacheDir().getAbsolutePath() + "/record/";
            File path = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }
            this.FolderPath = filePath;
        }
    }

    public AudioRecoderUtils(String filePath) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        this.FolderPath = filePath;
    }

    private long startTime;

    /**
     * 开始录音 使用amr格式
     */
    public void startRecord() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            filePath = FolderPath + System.currentTimeMillis() + ".mp3";
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            updateMicStatus();
            LogUtil.e("startTime" + startTime);
        } catch (IllegalStateException e) {
            LogUtil.e("call startAmr(File mRecAudioFile) failed!");
        } catch (IOException e) {
            LogUtil.e("call startAmr(File mRecAudioFile) failed!");
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mMediaRecorder == null) {
            return 0L;
        }
        long endTime = System.currentTimeMillis();
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            audioStatusUpdateListener.onStop(filePath);
            filePath = "";
        } catch (RuntimeException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            filePath = "";
        }
        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        if (null == mMediaRecorder) {
            return;
        }
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

        } catch (RuntimeException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        filePath = "";
    }

    private final Handler mHandler = new Handler(Looper.myLooper());
    private final Runnable mUpdateMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            int BASE = 1;
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            int SPACE = 100;
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param filePath 保存路径
         */
        void onStop(String filePath);
    }
}