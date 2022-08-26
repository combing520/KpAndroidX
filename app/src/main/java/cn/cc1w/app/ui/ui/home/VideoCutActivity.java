package cn.cc1w.app.ui.ui.home;

import android.content.ClipData;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.videoclip.ClipUtils;
import cn.cc1w.app.ui.utils.videocut.PublicUtils;
import cn.cc1w.app.ui.utils.videocut.RangeSeekBar;
import cn.cc1w.app.ui.utils.videocut.VideoSurface;
import cn.jzvd.JzvdStd;
import cn.cc1w.app.ui.R;

/**
 * 视频剪裁
 *
 * @author kpinfo
 */
public class VideoCutActivity extends AppCompatActivity implements RangeSeekBar.OnRangeChangedListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, VideoSurface.OnOrientationChange, VideoSurface.OnCompleteListener {
    @BindView(R.id.img_video_cut)
    ImageView videoPost;
    @BindView(R.id.tv_time_start_cut)
    TextView startTimeTv;
    @BindView(R.id.tv_time_total_cut)
    TextView totalTimeTv;
    @BindView(R.id.tv_time_end_cut)
    TextView endTimeTv;
    @BindView(R.id.range_bar_cut)
    RangeSeekBar rangeSeekBar;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.video_cut)
    JzvdStd videoPlayer;
    @BindView(R.id.txt_start_seek)
    TextView startSeekTimeTv;
    @BindView(R.id.txt_end_seek)
    TextView endSeekTimeTv;

    private LoadingDialog loading;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private Handler DrawThumHandler;
    private static final long TIME_MIN_VIDEO = 3000; // 视频的最小时间
    private long videoCutStartTime; //  视频剪裁开始时间
    private long videoCutEndTime; //    视频剪裁结束时间
    private Unbinder unbinder;
    private String videoPath;// 视频的地址
    private long videoDuration;// 视频的时长
    //    private static final String PATH_VIDEO_SAVE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constant.DIR_CACHE + File.separator + "videos";
    private String PATH_VIDEO_SAVE;
    private String fileName;
    private View mDrapView; // 拖拽的View
    private boolean isEnd;
    private boolean isPlay; // 是否正在拨打
    private boolean isPause; // 是否暂停

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cut);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initData();
        initNavigation();
        initLoading();
        initVideoPlayer();
        mediaMetadataRetriever = new MediaMetadataRetriever();
        HandlerThread mCheckMsgThread = new HandlerThread("drawBack");
        mCheckMsgThread.start();
        DrawThumHandler = new Handler(mCheckMsgThread.getLooper());
        resetRangBar();
        // 手势监听
        rangeSeekBar.setOnRangeChangedListener(this);
        LogUtil.e("videoPath = " + videoPath);
        AppUtil.loadNewsImg("file://" + videoPath, videoPost);
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    private void resetRangBar() {
        long startTime = 0;
        long endTime = videoDuration;
        long durationTime = videoDuration;
        setRangeSeekBarBack(videoPath, startTime, endTime);
        rangeSeekBar.setRules(0, durationTime);
        float mReservePercent = TIME_MIN_VIDEO / (float) durationTime;
        rangeSeekBar.setReservePercent(mReservePercent);
        rangeSeekBar.reset();

        startTimeTv.setText(String.valueOf(startTime / 1000.0));
        totalTimeTv.setText(String.valueOf(durationTime / 1000.0));
        endTimeTv.setText(String.valueOf(endTime / 1000.0));

        videoCutStartTime = startTime;
        videoCutEndTime = endTime;

        startSeekTimeTv.setText("0");
        endSeekTimeTv.setText(String.valueOf(endTime / 1000.0));
        videoPlayer.setOnClickListener(this);
    }


    /**
     * 初始化播放器
     */
    private void initVideoPlayer() {
        videoPlayer.setUp(videoPath, "", JzvdStd.SCREEN_NORMAL);
        videoPlayer.backButton.setVisibility(View.GONE);
        videoPlayer.titleTextView.setVisibility(View.GONE);
        videoPlayer.startVideo();

        long duration = videoPlayer.getDuration();
        LogUtil.e("duration = " + duration);

    }

    /**
     * 初始化信息
     */
    private void initData() {
        videoPath = getIntent().getStringExtra("videoPath");
        videoDuration = getIntent().getLongExtra("videoDuration", 0);
    }

    /**
     * 初始化导航头信息
     */
    private void initNavigation() {
        titleTv.setText("视频剪辑");
    }


    @Override
    public void onRangeChanged(RangeSeekBar view, float min, float max) {
        videoCutStartTime = (long) min;
        videoCutEndTime = (long) (max - 0.5);

        startTimeTv.setText(String.valueOf(new BigDecimal(min / 1000.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        endTimeTv.setText(String.valueOf(new BigDecimal(max / 1000.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        startSeekTimeTv.setText(convertTime(progress, 1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        pauseVideo();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onOrientationChange(VideoSurface surface, String message) {

    }

    @Override
    public void onComplete(VideoSurface surface) {
    }


    private class DrapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            ClipData data = ClipData.newPlainText("", "");
            MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
                    mDrapView);
            mDrapView.startDrag(data, shadowBuilder, mDrapView, 0);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private String convertTime(float milliseconds, int time_format) {
        return PublicUtils.convertTime(milliseconds, time_format);
    }


    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private final WeakReference<View> mView;
        public MyDragShadowBuilder(View view) {
            super(view);
            mView = new WeakReference<View>(view);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            canvas.scale(1.5F, 1.5F);
            super.onDrawShadow(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            final View view = mView.get();
            if (view != null) {
                shadowSize.set((int) (view.getWidth() * 1.5F),
                        (int) (view.getHeight() * 1.5F));
                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
            }
        }
    }

    /**
     * 停止播放
     */
    private void pauseVideo() {
        if (videoPlayer != null) {
            rangeSeekBar.stopDrawPlay();
        }
    }

    private void setRangeSeekBarBack(final String path, long startTime, long endTime) {
        isEnd = true;
        final Paint mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
        final Bitmap capture = getVideoThumb(path, startTime);
        int height = capture.getHeight();
        int weight = capture.getWidth();
        int eachBackBitmapWeight = height > weight ? weight : height;
        final int nubBitmap = getWindowWidth() / eachBackBitmapWeight + 2;
        final Bitmap resultBitmap = Bitmap.createBitmap(getWindowWidth(),
                capture.getHeight(), Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(resultBitmap);// 使用空白图片生成canvas
        // 注意时间单位
        final float average = (endTime - startTime) * 1f / 1000 / nubBitmap;
        isEnd = false;
        DrawThumHandler.post(() -> {
            for (int i = 0; i < nubBitmap; i++) {
                if (isEnd) {
                    return;
                }
                canvas.drawBitmap(getVideoThumb(path, average * i), capture.getWidth() * i, 0, mBitPaint);
                runOnUiThread(() -> rangeSeekBar.setBack(resultBitmap));
            }
        });
    }

    /**
     * 获取视频的 缩略图
     *
     * @param path 视频地址
     * @param time 视频的时长
     * @return 视频的缩略图
     */
    public Bitmap getVideoThumb(String path, float time) {
        mediaMetadataRetriever.setDataSource(path);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime((long) (time * 1000) * 1000);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int newWidth = height > width ? width : height;
        int scaleSize = height / PublicUtils.dip2px(45);

        if (scaleSize < 1) {
            scaleSize = 1;
        }
        Bitmap newBitmap;
        if (height > width) {
            newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth / scaleSize, PublicUtils.dip2px(45), false);
        } else {
            newBitmap = Bitmap.createScaledBitmap(bitmap, PublicUtils.dip2px(45), PublicUtils.dip2px(45), false);
        }
        bitmap.recycle();
        return newBitmap;
    }

    /**
     * 获取屏幕宽度
     */
    private int getWindowWidth() {
        return PublicUtils.getWindowWidth(this).widthPixels;
    }

    /**
     * 剪裁视频
     */
    private void doVideoCut() {
        if (getExternalCacheDir() != null) {
            PATH_VIDEO_SAVE = getExternalCacheDir() + File.separator + "videos";
        }
        // 文件夹不存在就创建
        File fileParent = new File(PATH_VIDEO_SAVE);

        File fileParent2 = fileParent.getParentFile();
        if (!fileParent2.exists()) {
            fileParent2.mkdirs();
        }
        fileName = System.currentTimeMillis() + ".mp4";
        cutMp4(videoCutStartTime, videoCutEndTime, videoPath, PATH_VIDEO_SAVE, fileName);
    }

    /**
     * 视频剪切
     *
     * @param startTime   视频剪切的开始时间
     * @param endTime     视频剪切的结束时间
     * @param FilePath    被剪切视频的路径
     * @param WorkingPath 剪切成功保存的视频路径
     * @param fileName    剪切成功保存的文件名
     */
    private void cutMp4(final long startTime, final long endTime, final String FilePath, final String WorkingPath, final String fileName) {
        VideoClipTask task = new VideoClipTask();
        task.execute();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onResume();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @OnClick({R.id.img_back_header_not_title, R.id.btn_cut})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_cut) {
            doVideoCut();
        }
    }

    private class VideoClipTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LogUtil.e("onPreExecute" + " loading == null ?" + (loading == null));
            if (null != loading) {
                loading.show();
                LogUtil.e("不为空呀 ！！！");
            }
            try {
                ClipUtils videoClip = new ClipUtils();//实例化VideoClip类
                videoClip.setFilePath(videoPath);//设置被编辑视频的文件路径  FileUtil.getMediaDir()+"/test/laoma3.mp4"
                videoClip.setWorkingPath(PATH_VIDEO_SAVE);//设置被编辑的视频输出路径  FileUtil.getMediaDir()
                videoClip.setStartTime(videoCutStartTime);//设置剪辑开始的时间
                videoClip.setEndTime(videoCutEndTime);//设置剪辑结束的时间
                videoClip.setOutName(fileName);//设置输出的文件名称
                videoClip.clip();//调用剪辑并保存视频文件方法（建议作为点击保存时的操作并加入等待对话框）
            } catch (Exception e) {
                e.printStackTrace();
                if (null != loading && loading.isShow()) {
                    loading.close();
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            // 通知 已经剪裁完成
            File file = new File(PATH_VIDEO_SAVE + "/" + fileName);
            if (file.isFile() && !TextUtils.isEmpty(fileName)) {
                LogUtil.e("存在 " + file.getAbsolutePath());
                EventBus.getDefault().post(new EventMessage("uploadVideo", file.getAbsolutePath()));
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        JzvdStd.releaseAllVideos();
        if (DrawThumHandler != null) {
            DrawThumHandler.removeCallbacksAndMessages(null);
        }
        unbinder.unbind();
        super.onDestroy();
    }
}