package cn.cc1w.app.ui.ui.home.video;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.luck.picture.lib.entity.LocalMedia;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoFilterListener;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.qiniu.pili.droid.shortvideo.PLWatermarkSetting;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.filter.FilterListAdapter;
import cn.cc1w.app.ui.adapter.video_frame.VideoFrameAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.home.upload.PaikewVideoUploadActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.widget.dialog.CustomProgressDialog;
import cn.cc1w.app.ui.widget.relate.SquareGLSurfaceView;

import static cn.cc1w.app.ui.utils.RecordSettings.RECORD_SPEED_ARRAY;

/**
 * 视频编辑
 */
public class VideoRedactActivity extends CustomActivity implements FilterListAdapter.FilterClickListener, VideoFrameAdapter.FrameClickListener, PLVideoSaveListener {
    @BindView(R.id.txt_speed_slowest_video_redact)
    TextView slowestSpeedTv;
    @BindView(R.id.txt_speed_slow_video_redact)
    TextView slowSpeedTv;
    @BindView(R.id.txt_speed_normal_video_redact)
    TextView normalSpeedTv;
    @BindView(R.id.txt_speed_fast_video_redact)
    TextView fastSpeedTv;
    @BindView(R.id.txt_speed_fastest_video_redact)
    TextView fastestSpeedTv;
    @BindView(R.id.gridLayout_video_redact)
    GridLayout effectGridLayout;
    @BindView(R.id.surfaceView_video_redact)
    SquareGLSurfaceView surfaceView;
    @BindView(R.id.recycle_video_redact)
    RecyclerView filterRecycleView;
    @BindView(R.id.recycle_cover_video_redact)
    RecyclerView coverRecycleView;
    private PLMediaFile mediaFile;
    private PLShortVideoEditor shortVideoEditor;
    private CustomProgressDialog processingDialog;
    private static final int POS_SPEED_DEF = 0;
    private static final int POS_SPEED_SLOWEST = 1;
    private static final int POS_SPEED_SLOW = 2;
    private static final int POS_SPEED_NORMAL = 3;
    private static final int POS_SPEED_FAST = 4;
    private static final int POS_SPEED_FASTEST = 5;
    private Unbinder unbinder;
    private String videoPath;
    private PLShortVideoEditorStatus mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
    private PLWatermarkSetting mPreviewWatermarkSetting;
    private PLWatermarkSetting mWatermarkSetting;
    private PLWatermarkSetting mSaveWatermarkSetting;
    private FilterListAdapter filterAdapter;
    private VideoFrameAdapter frameAdapter;
    private static final String SUFFIX_PIC = ".jpg";
    // 图片压缩比例
    private static final int COMPRESS_QUALITY = 70;
    private long lastTime;
    private static final int SLICE_COUNT = 10;
    private long durationMs = 0;
    private int sliceEdge;
    private int slicesTotalLength = 0;
    private float px;
    private final List<PLVideoFrame> plVideoFrameList = new ArrayList<>();
    private String filePath;
    // 选中的封面位置
    private int selectCoverPos = 0;

    // 视频编辑器预览状态
    private enum PLShortVideoEditorStatus {
        Idle,
        Playing,
        Paused,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_redact);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initData();
        initSpeedInfo();
        initWatermarkSetting();
        initVideoEditor();
        initProcessingDialog();
        initList();
    }

    /**
     * 初始化处理的进度框
     */
    private void initProcessingDialog() {
        processingDialog = new CustomProgressDialog(this);
        processingDialog.setCancelable(false);
        processingDialog.setOnCancelListener(dialog -> shortVideoEditor.cancelSave());
    }

    /**
     * 初始化 recycleView
     */
    private void initList() {
        filterAdapter = new FilterListAdapter(this, shortVideoEditor.getBuiltinFilterList());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterRecycleView.setLayoutManager(manager);
        filterRecycleView.setAdapter(filterAdapter);
        filterAdapter.setOnFilterClickListener(this);
        lastTime = System.currentTimeMillis();

        frameAdapter = new VideoFrameAdapter(this);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        coverRecycleView.setLayoutManager(manager1);
        coverRecycleView.setAdapter(frameAdapter);
        frameAdapter.setOnFrameClickListener(this);
    }

    /**
     * 初始化视频编辑器
     */
    private void initVideoEditor() {
        shortVideoEditor = new PLShortVideoEditor(surfaceView);
        PLVideoEditSetting setting = new PLVideoEditSetting();
        setting.setSourceFilepath(videoPath);
        if (getExternalCacheDir() != null) {
            setting.setDestFilepath(getExternalCacheDir() + File.separator + System.currentTimeMillis() + "edited.mp4");
        }
        shortVideoEditor.setVideoEditSetting(setting);
        shortVideoEditor.setVideoSaveListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        videoPath = getIntent().getStringExtra("videoPath");
        LogUtil.e("videoPath = " + videoPath);
        initVideoTrimmerInfo(videoPath);
    }

    /**
     * 初始化 视频处理工具
     */
    private void initVideoTrimmerInfo(String videoPath) {
        mediaFile = new PLMediaFile(videoPath);
        initVideoFrameList();
    }

    /**
     * 初始化视频帧
     */
    private void initVideoFrameList() {
        coverRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                coverRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                sliceEdge = coverRecycleView.getWidth() / SLICE_COUNT;
                slicesTotalLength = sliceEdge * SLICE_COUNT;
                LogUtil.e("slice edge: " + sliceEdge);
                px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                VideoClipTask task = new VideoClipTask();
                task.execute();
            }
        });
    }

    private void initSpeedInfo() {
        setSelectEffect(POS_SPEED_DEF);
    }

    private void initWatermarkSetting() {
        mWatermarkSetting = createWatermarkSetting();
        mPreviewWatermarkSetting = createWatermarkSetting();
        mSaveWatermarkSetting = createWatermarkSetting();
    }

    // 水印
    private PLWatermarkSetting createWatermarkSetting() {
        PLWatermarkSetting watermarkSetting = new PLWatermarkSetting();
        watermarkSetting.setResourceId(R.mipmap.ic_paikew_watermark);
        watermarkSetting.setPosition(0.02f, 0.04f);
        return watermarkSetting;
    }

    /**
     * 设置选中的速度特效
     *
     * @param pos 选中的位置
     */
    private void setSelectEffect(int pos) {
        if (pos == POS_SPEED_SLOWEST) {
            slowestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            slowSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            normalSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else if (pos == POS_SPEED_SLOW) {
            slowestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            slowSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            normalSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else if (pos == POS_SPEED_NORMAL) {
            slowestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            slowSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            normalSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            fastSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else if (pos == POS_SPEED_FAST) {
            slowestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            slowSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            normalSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            fastestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else if (pos == POS_SPEED_FASTEST) {
            slowestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            slowSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            normalSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            slowestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            slowSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            normalSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            fastestSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
    }

    /**
     * 启动预览
     */
    private void startPlayback() {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Idle) {
            shortVideoEditor.startPlayback(new PLVideoFilterListener() {
                @Override
                public void onSurfaceCreated() {

                }

                @Override
                public void onSurfaceChanged(int width, int height) {

                }

                @Override
                public void onSurfaceDestroy() {

                }

                @Override
                public int onDrawFrame(int texId, int texWidth, int texHeight, long timestampNs, float[] transformMatrix) {
                    int time = shortVideoEditor.getCurrentPosition();
                    shortVideoEditor.updateSaveWatermark(mPreviewWatermarkSetting);
                    return texId;
                }
            });
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        } else if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Paused) {
            shortVideoEditor.resumePlayback();
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        }
    }

    @OnClick({
            R.id.ll_effect_video_redact, R.id.ll_cover_video_redact, R.id.ll_filter_video_redact,
            R.id.ll_record_video_redact, R.id.ll_speed_slowest_video_redact, R.id.ll_speed_slow_video_redact,
            R.id.ll_speed_normal_video_redact, R.id.ll_speed_fast_video_redact, R.id.ll_speed_fastest_video_redact,
            R.id.btn_next_video_redact, R.id.img_back_video_redact
    })
    public void onClick(View v) {
        int id = v.getId();// 录音
        if (id == R.id.ll_effect_video_redact) { // 特效
            filterRecycleView.setVisibility(View.GONE);
            coverRecycleView.setVisibility(View.GONE);
            effectGridLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.ll_cover_video_redact) { // 封面
            effectGridLayout.setVisibility(View.GONE);
            coverRecycleView.setVisibility(View.VISIBLE);
            filterRecycleView.setVisibility(View.GONE);
        } else if (id == R.id.ll_filter_video_redact) { // 滤镜
            effectGridLayout.setVisibility(View.GONE);
            coverRecycleView.setVisibility(View.GONE);
            filterRecycleView.setVisibility(View.VISIBLE);
        } else if (id == R.id.ll_record_video_redact) {
        } else if (id == R.id.ll_speed_slowest_video_redact) { //极慢速度
            setSelectEffect(POS_SPEED_SLOWEST);
            shortVideoEditor.setSpeed(RECORD_SPEED_ARRAY[0], true);
        } else if (id == R.id.ll_speed_slow_video_redact) { // 慢速
            setSelectEffect(POS_SPEED_SLOW);
            shortVideoEditor.setSpeed(RECORD_SPEED_ARRAY[1], true);
        } else if (id == R.id.ll_speed_normal_video_redact) { //正常速度
            setSelectEffect(POS_SPEED_NORMAL);
            shortVideoEditor.setSpeed(RECORD_SPEED_ARRAY[2], true);
        } else if (id == R.id.ll_speed_fast_video_redact) { // 快速
            setSelectEffect(POS_SPEED_FAST);
            shortVideoEditor.setSpeed(RECORD_SPEED_ARRAY[3], true);
        } else if (id == R.id.ll_speed_fastest_video_redact) { // 级快速递
            setSelectEffect(POS_SPEED_FASTEST);
            shortVideoEditor.setSpeed(RECORD_SPEED_ARRAY[4], true);
        } else if (id == R.id.btn_next_video_redact) { // 处理视频
            handleVideo();
        } else if (id == R.id.img_back_video_redact) {
            finish();
        }
    }

    /**
     * 处理视频
     */
    private void handleVideo() {
        processingDialog.show();
        shortVideoEditor.setWatermark(mWatermarkSetting);
        shortVideoEditor.save(new PLVideoFilterListener() {
            @Override
            public void onSurfaceCreated() {

            }

            @Override
            public void onSurfaceChanged(int width, int height) {

            }

            @Override
            public void onSurfaceDestroy() {

            }

            @Override
            public int onDrawFrame(int texId, int texWidth, int texHeight, long timestampNs, float[] transformMatrix) {
                shortVideoEditor.updateSaveWatermark(mSaveWatermarkSetting);
                return texId;
            }
        });
    }

    /**
     * 滤镜点击
     */
    @Override
    public void onFilterClickListener(int pos, View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            if (pos == 0) {
                shortVideoEditor.setBuiltinFilter(null);
            } else {
                String filterName = filterAdapter.getItem(pos).getName();
                shortVideoEditor.setBuiltinFilter(TextUtils.isEmpty(filterName) ? "" : filterName);
            }
        }
        lastTime = currentTime;
    }

    @Override
    public void onFrameChooseListener(int pos, View v) {
        if (selectCoverPos != pos) {
            frameAdapter.setSelectItem(pos);
        }
        selectCoverPos = pos;
    }

    /**
     * 视频获取帧列表task
     */
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

        @Override
        protected void onProgressUpdate(PLVideoFrame... values) {
            super.onProgressUpdate(values);
            PLVideoFrame frame = values[0];
            if (frame != null) {
                plVideoFrameList.add(frame);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LogUtil.e("执行完成!!!");
            if (!plVideoFrameList.isEmpty()) {
                frameAdapter.setDataSet(plVideoFrameList);
                frameAdapter.notifyDataSetChanged();
                frameAdapter.setSelectItem(0);
                selectCoverPos = 0;
            }
        }
    }

    @Override
    public void onSaveVideoSuccess(String saveVideoPath) {
        LogUtil.e("视频编辑成功  onSaveVideoSuccess saveVideoPath = " + saveVideoPath);
        if (!TextUtils.isEmpty(saveVideoPath)) {
            int pos = frameAdapter.getSelectPos();
            PLVideoFrame item = frameAdapter.getItem(pos);
            Bitmap bm = item.toBitmap();
            if (null != bm) {
                saveBitmap2File(bm);
            }
            Bundle bundle = new Bundle();
            LocalMedia media = new LocalMedia();
            media.setPath(saveVideoPath);
            bundle.putParcelable("video", media);
            bundle.putString("coverPath", filePath);
            IntentUtil.startActivity(this, PaikewVideoUploadActivity.class, bundle);
            finish();
        }
        processingDialog.dismiss();
    }

    /**
     * 将 bitmap 保存为 file
     *
     * @param bitmap 目标 bitmap
     */
    private void saveBitmap2File(Bitmap bitmap) {
        // 图片地址
        filePath = getExternalCacheDir().getAbsolutePath() + File.separator + "pic" + System.currentTimeMillis() + SUFFIX_PIC;
        File file = new File(filePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveVideoFailed(int errorCode) {
        runOnUiThread(() -> {
            processingDialog.dismiss();
            LogUtil.e("视频保存错误  errorCode  = " + errorCode);
        });
    }

    private void stopPlayback() {
        shortVideoEditor.stopPlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
    }

    @Override
    public void onSaveVideoCanceled() {
        processingDialog.dismiss();
    }

    @Override
    public void onProgressUpdate(float percentage) {
        runOnUiThread(() -> processingDialog.setProgress((int) (100 * percentage)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shortVideoEditor.setWatermark(mWatermarkSetting);
        startPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}