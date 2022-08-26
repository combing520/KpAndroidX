package cn.cc1w.app.ui.ui.usercenter.broke;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;

/**
 * 音频播放 工具类
 * @author kpinfo
 */
public class VoiceplayerUtil {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;
    private static String audioPath;
    /**
     * 播放
     */
    public static void play(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                mMediaPlayer.reset();
                return false;
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            audioPath = filePath;
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    public static String getCurrentAudioPath() {
        return audioPath;
    }
}