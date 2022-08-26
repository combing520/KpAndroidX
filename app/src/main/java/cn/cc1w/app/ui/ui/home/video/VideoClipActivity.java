package cn.cc1w.app.ui.ui.home.video;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.dialog.CustomProgressDialog;

/**
 * 视频剪辑
 * @author kpinfo
 */
public class VideoClipActivity extends CustomActivity {
    @BindView(R.id.preview)
    VideoView videoPreviewView;
    @BindView(R.id.handler_left)
    View handlerLeft;
    @BindView(R.id.handler_right)
    View handlerRight;
    @BindView(R.id.duration)
    TextView durationTv;
    @BindView(R.id.range)
    TextView rangeTv;
    @BindView(R.id.video_frame_list)
    LinearLayout frameListView;
    @BindView(R.id.tv_time_select)
    TextView selectRangTv;
    @BindView(R.id.btn_next_video_clip)
    TextView nextBtn;
    private CustomProgressDialog processingDialog;
    Unbinder unbinder;
    private long selectedBeginMs;
    private long selectedEndMs;
    private long durationMs;
    private final Handler handler = new Handler(Looper.myLooper());
    private static final int SLICE_COUNT = 8;
    private int slicesTotalLength;
    private PLShortVideoTrimmer shortVideoTrimmer;
    private PLMediaFile mediaFile;
    private int sliceEdge;
    private float px;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clip);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initData();
        initDialogInfo();
        initClipView();
        nextBtn.setClickable(false);
    }

    /**
     * 初始化 clip
     */
    private void initClipView() {
        videoPreviewView.setVideoPath(videoPath);
        videoPreviewView.setOnCompletionListener(mediaPlayer -> play());
        initVideoTrimmerInfo(videoPath);
        play();
    }

    private void initData() {
        videoPath = getIntent().getStringExtra("videoPath");
    }

    /**
     * 初始化 dialog
     */
    private void initDialogInfo() {
        processingDialog = new CustomProgressDialog(this);
        processingDialog.setCancelable(false);
        processingDialog.setOnCancelListener(dialog -> {
            if (null != shortVideoTrimmer) {
                shortVideoTrimmer.cancelTrim();
            }
        });
    }

    /**
     * 初始化 视频处理工具
     */
    private void initVideoTrimmerInfo(String videoPath) {
        shortVideoTrimmer = new PLShortVideoTrimmer(this, videoPath, getExternalCacheDir().getAbsolutePath() + File.separator+ System.currentTimeMillis() + "trimmed.mp4");
        mediaFile = new PLMediaFile(videoPath);
        selectedEndMs = durationMs = mediaFile.getDurationMs();
        // 视频 帧的数量
        int videoFrameCount = mediaFile.getVideoFrameCount(false);
        LogUtil.e("videoFrameCount = " + videoFrameCount);
        initVideoFrameList();
    }

    /**
     * 初始化 视频的帧 列表
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initVideoFrameList() {
        handlerLeft.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            float viewX = v.getX();
            float movedX = event.getX();
            float finalX = viewX + movedX;
            updateHandlerLeftPosition(finalX);
            if (action == MotionEvent.ACTION_UP) {
                calculateRange();
            }
            return true;
        });
        handlerRight.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            float viewX = v.getX();
            float movedX = event.getX();
            float finalX = viewX + movedX;
            updateHandlerRightPosition(finalX);
            if (action == MotionEvent.ACTION_UP) {
                calculateRange();
            }
            return true;
        });

        frameListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                sliceEdge = frameListView.getWidth() / SLICE_COUNT;
                slicesTotalLength = sliceEdge * SLICE_COUNT;
                LogUtil.e("slice edge: " + sliceEdge);
                px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                VideoClipTask task = new VideoClipTask();
                task.execute();
            }
        });
    }

    /**
     * 播放视频
     */
    private void play() {
        if (videoPreviewView != null) {
            videoPreviewView.seekTo((int) selectedBeginMs);
            videoPreviewView.start();
            startTrackPlayProgress();
        }
    }

    private void startTrackPlayProgress() {
        stopTrackPlayProgress();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != videoPreviewView) {
                    if (videoPreviewView.getCurrentPosition() >= selectedEndMs) {
                        videoPreviewView.seekTo((int) selectedBeginMs);
                    }
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    private void stopTrackPlayProgress() {
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 更新左侧的位置
     *
     * @param movedPosition 左侧移动到的位置
     */
    private void updateHandlerLeftPosition(float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) handlerLeft.getLayoutParams();
        if ((movedPosition + handlerLeft.getWidth()) > handlerRight.getX()) {
            lp.leftMargin = (int) (handlerRight.getX() - handlerLeft.getWidth());
        } else if (movedPosition < 0) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        handlerLeft.setLayoutParams(lp);
    }

    /**
     * 更新右侧位置
     *
     * @param movedPosition 右侧移动到的位置
     */
    private void updateHandlerRightPosition(float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) handlerRight.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (movedPosition < (handlerLeft.getX() + handlerLeft.getWidth())) {
            lp.leftMargin = (int) (handlerLeft.getX() + handlerLeft.getWidth());
        } else if ((movedPosition + (handlerRight.getWidth() / 2)) > (frameListView.getX() + slicesTotalLength)) {
            lp.leftMargin = (int) ((frameListView.getX() + slicesTotalLength) - (handlerRight.getWidth() / 2));
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        handlerRight.setLayoutParams(lp);
    }

    /**
     * 计算区域
     */
    private void calculateRange() {
        float beginPercent = 1.0f * ((handlerLeft.getX() + handlerLeft.getWidth() / 2) - frameListView.getX()) / slicesTotalLength;
        float endPercent = 1.0f * ((handlerRight.getX() + handlerRight.getWidth() / 2) - frameListView.getX()) / slicesTotalLength;
        beginPercent = clamp(beginPercent);
        endPercent = clamp(endPercent);

        LogUtil.e("begin percent: " + beginPercent + " end percent: " + endPercent);

        selectedBeginMs = (long) (beginPercent * durationMs);
        selectedEndMs = (long) (endPercent * durationMs);

        LogUtil.e("new range: " + selectedBeginMs + "-" + selectedEndMs);
        updateRangeText();
        play();
    }

    private float clamp(float origin) {
        if (origin < 0) {
            return 0;
        }
        if (origin > 1) {
            return 1;
        }
        return origin;
    }

    /**
     * 更新视频的选中区域时长
     */
    private void updateRangeText() {
//        rangeTv.setText("剪裁范围: " + formatTime(selectedBeginMs) + " - " + formatTime(selectedEndMs));
        selectRangTv.setText("已选 " + (formatTime(selectedEndMs - selectedBeginMs)) + "s");
    }

    /**
     * 格式化时间
     *
     * @param timeMs 毫秒
     * @return 时间
     */
    private String formatTime(long timeMs) {
        return String.format(Locale.CHINA, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMs),
                TimeUnit.MILLISECONDS.toSeconds(timeMs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs))
        );
    }

    // 裁剪视频
    private void clipVideo() {
        processingDialog.show();
        // 视频截取的时候使用 精确模式
        PLShortVideoTrimmer.TRIM_MODE mode = PLShortVideoTrimmer.TRIM_MODE.ACCURATE;
        shortVideoTrimmer.trim(selectedBeginMs, selectedEndMs, mode, new PLVideoSaveListener() {
            @Override
            public void onSaveVideoSuccess(String videoPath) {
                processingDialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("videoPath", videoPath);
                IntentUtil.startActivity(VideoClipActivity.this, VideoRedactActivity.class, bundle);
                finish();
            }

            @Override
            public void onSaveVideoFailed(int i) {

                runOnUiThread(() -> {
                    processingDialog.dismiss();
                    ToastUtil.showShortToast("处理失败");
                });
            }

            @Override
            public void onSaveVideoCanceled() {
                processingDialog.dismiss();
            }

            @Override
            public void onProgressUpdate(float percentage) {
                runOnUiThread(() -> {
                    LogUtil.e("百分比 = " + percentage);
                    processingDialog.setProgress((int) (100 * percentage));
                });
            }
        });
    }

    /**
     * 视频获取帧列表task
     */
    @SuppressLint("StaticFieldLeak")
    private class VideoClipTask extends AsyncTask<Void, PLVideoFrame, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... v) {
            for (int i = 0; i < SLICE_COUNT; ++i) {
                PLVideoFrame frame = mediaFile.getVideoFrameByTime((long) ((1.0f * i / SLICE_COUNT) * durationMs), true, sliceEdge, sliceEdge);
                publishProgress(frame);
            }
            return null;
        }

        @SuppressLint("InflateParams")
        @Override
        protected void onProgressUpdate(PLVideoFrame... values) {
            super.onProgressUpdate(values);
            PLVideoFrame frame = values[0];
            if (frame != null) {
                View root = LayoutInflater.from(VideoClipActivity.this).inflate(R.layout.frame_item, null);
                int rotation = frame.getRotation();
                ImageView thumbnail = root.findViewById(R.id.thumbnail);
                thumbnail.setImageBitmap(Bitmap.createScaledBitmap(frame.toBitmap(), 100, 100, false));
                thumbnail.setRotation(rotation);

                FrameLayout.LayoutParams thumbnailLP = (FrameLayout.LayoutParams) thumbnail.getLayoutParams();
                if (rotation == 90 || rotation == 270) {
                    thumbnailLP.leftMargin = thumbnailLP.rightMargin = (int) px;
                } else {
                    thumbnailLP.topMargin = thumbnailLP.bottomMargin = (int) px;
                }
                thumbnail.setLayoutParams(thumbnailLP);
                LinearLayout.LayoutParams rootLP = new LinearLayout.LayoutParams(sliceEdge, sliceEdge);
                frameListView.addView(root, rootLP);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRangeText();
            handlerLeft.setVisibility(View.VISIBLE);
            handlerRight.setVisibility(View.VISIBLE);
            nextBtn.setClickable(true);
        }
    }

    @OnClick({R.id.btn_next_video_clip, R.id.btn_cancel_video_clip})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_next_video_clip) {
            clipVideo();
        } else if (id == R.id.btn_cancel_video_clip) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (videoPreviewView != null) {
                videoPreviewView.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (null != videoPreviewView) {
                videoPreviewView.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        unbinder.unbind();
    }
}