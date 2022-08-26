package cn.cc1w.app.ui.utils.videocut;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.ViewGroup;

import org.xutils.common.util.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by Administrator on 2017/7/14 0014.
 */

public class VideoSurface extends TextureView implements TextureView.SurfaceTextureListener {
    //采样率
    int mFrequency = 44100;
    //声道
    int mChannel = AudioFormat.CHANNEL_OUT_MONO;
    //采样精度
    int mSampBit = AudioFormat.ENCODING_PCM_16BIT;
    private int minBufSize;
    private byte[] data1;
    private AudioTrack mAudioTrack;
    private long flag;
    private boolean isInitComplete;
    private Context mContext;
    public OnCompleteListener onCompleteListener;
    public OnPreparedListener onPreparedListener;
    // 视频方向改变监听器
    public OnOrientationChange onOrientationChange;
    public OnPlaying onPlaying;

//    int STATE;
//    int PLAYING = 1;
//    int PAUSE = 2;

    AtomicBoolean audioFlag = new AtomicBoolean(true);
    AtomicBoolean rotationFlag = new AtomicBoolean(true);
    public boolean isSetSoft;

    public boolean isSetSoft() {
        return isSetSoft;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    public void setOnOrientationChange(OnOrientationChange onOrientationChange) {
        this.onOrientationChange = onOrientationChange;
    }

    public void setOnPlaying(OnPlaying onPlaying) {
        this.onPlaying = onPlaying;
    }

    public VideoSurface(Context context, ViewGroup layout) {

        super(context);
        mContext = context;
        layout.removeAllViews();
        initVideo();
    }

    public VideoSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initVideo();
    }

    public VideoSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initVideo();
    }

    private void initVideo() {
        setSurfaceTextureListener(this);

//        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//
        minBufSize = 2048;
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mFrequency,
                mChannel,
                mSampBit,
                minBufSize,
                AudioTrack.MODE_STREAM);
        data1 = new byte[minBufSize];
    }

    private void startGetRotation() {
        LogUtil.e("startGetRotation1");
        rotationFlag.set(true);
        ThreadPool2.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (rotationFlag.get()) {
//                    String msg = IjkMediaPlayer.getRotation(flag);
//
//                    LogUtil.e("startGetRotation--msg=%s",msg);
//
//                    if (TextUtils.isEmpty(msg)) {
//                        continue;
//                    }
//                    if (msg.equals("continue")) {
//                       LogUtil.e("startGetRotation--continue");
//                        continue;
//                    } else if (msg.equals("threadStop")) {
//                       LogUtil.e("startGetRotation--threadStop");
//                        stopGetRotation();
//                        return;
//                    }
//
//                    if (onOrientationChange != null) {
//                        onOrientationChange.onOrientationChange(VideoSurface.this,msg);
//                    }
                }
            }
        });
    }

    private void stopGetRotation() {
        rotationFlag.set(false);
    }

    Matrix Mmatrix=new Matrix();

    public Matrix getMmatrix() {
        return Mmatrix;
    }

    // 设置内容的变形矩阵
    public void setMatrix(final Matrix transform, Activity context) {
       LogUtil.e("setMatrix1");
        Mmatrix = transform;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTransform(transform);
//                postInvalidate();
            }
        });
    }

    private void startAudio() {
        ThreadPool2.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                mAudioTrack.play();
                audioFlag.set(true);
                while (audioFlag.get()) {
                    mAudioTrack.write(data1, 0, minBufSize);
                    if (onPlaying!=null) {
                        onPlaying.onPlaying(VideoSurface.this);
                    }
//                    int result = IjkMediaPlayer.playerGetAudioBuffer(flag, data1, minBufSize);
//                   LogUtil.e("startAudio:result=%s", result);
//                    if (result == -1) {
//                        audioFlag.set(false);
//                        mAudioTrack.pause();
//                        if (onCompleteListener != null) {
//                            onCompleteListener.onComplete(VideoSurface.this);
//                        }
//                    }
                }
            }
        });
    }


    private void pauseAudio() {
        if (isInitComplete && mAudioTrack != null && mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioFlag.set(false);
            mAudioTrack.pause();
        }
    }

    public int firstFrameRotate;
    public int firstFrameWidth=0,firstFrameHeight=0;

    public int getFirstFrameWidth() {
        return firstFrameWidth;
    }

    public int getFirstFrameHeight() {
        return firstFrameHeight;
    }

    // 播放视频
    public void setSoft(String mVideoPath, long startTimeInVideo, long endTimeInVideo, long startTimeInAll, long endTimeInAll) {
        if (isInitComplete) {
//            IjkMediaPlayer.playerSetDecoderSoft(flag, mVideoPath, startTimeInAll / 1000f, endTimeInAll / 1000f, startTimeInVideo / 1000f, endTimeInVideo / 1000f, mVideoPath.length());
//			if (firstFrameWidth == 0) {
//                    firstFrameWidth = PublicUtils.getVideoWidth(mVideoPath);
//                }
//                if (firstFrameHeight == 0) {
//                    firstFrameHeight = PublicUtils.getVideoHeight(mVideoPath);
//                }
//           LogUtil.e("setSoft：startTimeInVideo=%s,endTimeInVideo=%s,startTimeInAll=%s,endTimeInAll=%s", startTimeInVideo / 1000f, endTimeInVideo / 1000f, startTimeInAll / 1000f, endTimeInAll / 1000f);
//            isSetSoft = true;
        }
    }

    public boolean playVideo() {
        if (isInitComplete && isSetSoft) {
            startAudio();
            if (getIsPlayerEnd()) {
                seekStart();
            }
//            IjkMediaPlayer.playerPause(flag, false);
            //startGetRotation();
            return true;
        }
        return false;
    }

    boolean initPlayFirst;


    public boolean playVideoFirst() {
        if (isInitComplete && isSetSoft) {
            initPlayFirst = true;
            startAudio();
//            IjkMediaPlayer.playerStartPlay(flag);
            startGetRotation();
            return true;
        }
        return false;
    }

    public void pauseVideo() {
        if (isInitComplete) {
            if (!getIsPause()) {
                pauseAudio();
//                stopGetRotation();
//                IjkMediaPlayer.playerPause(flag, true);
            }
        }
    }

    // 销毁对象--start开始和此方法为一对，都只能调一次(playerStopPlay 可在异步线程中调用，小概率会卡住)
    public void stopPlay(ViewGroup layout) {
        if (isInitComplete) {
           LogUtil.e("startGetRotation--msg=stopPlay");

            isInitComplete = false;
            isSetSoft = false;
            audioFlag.set(false);
            stopGetRotation();
            if (mAudioTrack != null) {
                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
            }

            ThreadPool2.getInstance().execute(new Runnable() {
                @Override
                public void run() {
//                    IjkMediaPlayer.playerStopPlay(flag);
//                    IjkMediaPlayer.playerDelete(flag);
                }
            });

//            layout.removeAllViews();
        }
    }

    // 异步调用seek，可在滑动进度条时调用此方法
    public void seekTo(int startTime) {
        if (isInitComplete) {
            double time = startTime / 1000d;
//            IjkMediaPlayer.playerSeekToTime(flag, time);
        }
    }

    public void seekStart() {
        if (isInitComplete) {
//            IjkMediaPlayer.playerSeekToTimeSync(flag, 0.0);
        }
    }

    public void seekSync(int startTime) {
        if (isInitComplete) {
            double time = startTime / 1000d;
//            IjkMediaPlayer.playerSeekToTimeSync(flag, time);
        }
    }

    public int getCurrentTime() {
        if (isInitComplete) {
//            return (int) (IjkMediaPlayer.playerGetCurrentTime(flag) * 1000);
        }
        return 0;
    }

    //获取播放器是否播放到末尾的状态
    public boolean getIsPlayerEnd() {
        if (isInitComplete) {
//            return IjkMediaPlayer.playerGetIsPlayerEnd(flag);
        }
        return false;
    }

    //获取当前暂停状态
    public boolean getIsPause() {
        if (isInitComplete) {
//            return IjkMediaPlayer.playerGetPauseState(flag);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public interface OnCompleteListener {
        void onComplete(VideoSurface surface);

    }

    public interface OnPreparedListener {
        void onPrepared(VideoSurface surface);
    }

    public interface OnOrientationChange{
        void onOrientationChange(VideoSurface surface, String message);
    }

    public interface OnPlaying{
        void onPlaying(VideoSurface surface);
    }
}
