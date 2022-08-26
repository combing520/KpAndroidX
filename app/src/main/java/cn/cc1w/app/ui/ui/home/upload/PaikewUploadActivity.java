package cn.cc1w.app.ui.ui.home.upload;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.pictureSelector.GlideEngine;
import cn.cc1w.app.ui.utils.pictureSelector.MeSandboxFileEngine;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.ui.home.video.VideoClipActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.camera.JCameraView;
import cn.cc1w.app.ui.widget.camera.listener.ClickListener;
import cn.cc1w.app.ui.widget.camera.listener.ErrorListener;
import cn.cc1w.app.ui.widget.camera.listener.JCameraListener;
import cn.cc1w.app.ui.widget.dialog.CustomProgressDialog;
import cn.cc1w.app.ui.R;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * 拍客的视频或者图片上传
 *
 * @author kpinfo
 */
public class PaikewUploadActivity extends CustomActivity implements JCameraListener, BlankViewClickListener, EasyPermissions.PermissionCallbacks {
    @BindView(R.id.camera_upload)
    JCameraView cameraView;
    @BindView(R.id.blankView_upload)
    BlankView blankView;
    @BindView(R.id.img_cancel_upload)
    ImageView closeImg;
    private static final String SUFFIX_PIC = ".jpg";
    private static final int COMPRESS_QUALITY = 85;
    private Unbinder unbinder;
    private CustomProgressDialog processingDialog;
    private PLMediaFile mediaFile;
    private PLShortVideoTranscoder shortVideoTranscoder;
    private final DisplayMetrics metrics = new DisplayMetrics();
    private static final int CODE_REQUEST_PERMISSION_PAIKEW = 3;
    private boolean hasPermission = false;
    private static final int VIDEO_MAX_TIME = 60;
    private static final int VIDEO_MIN_TIME = 1;

    private final PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
    private final ImageEngine imageEngine = GlideEngine.createGlideEngine();
    private final String[] permissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paikew_upload);
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissionDialog();
        } else {
            init();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        cameraView.setVisibility(View.VISIBLE);
        closeImg.setVisibility(View.VISIBLE);
        initBlankView();
        initCameraViewInfo();
        initDialogInfo();
        initData();
    }

    @SuppressLint("InflateParams")
    private void showPermissionDialog() {
        if (!EasyPermissions.hasPermissions(this, permissionList)) {
            EasyPermissions.requestPermissions(this, "是否允许开屏新闻获取相机、相册权限用于拍摄、获取本地图片及视频", CODE_REQUEST_PERMISSION_PAIKEW, permissionList);
        } else {
            hasPermission = true;
            init();
        }
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.img_unlogin, "您还没有登录,请登录后查看更多!", "登录");
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化 dialog
     */
    private void initDialogInfo() {
        processingDialog = new CustomProgressDialog(this);
        processingDialog.setOnCancelListener(dialog -> {
            if (null != shortVideoTranscoder) {
                shortVideoTranscoder.cancelTranscode();
            }
        });
    }

    /**
     * 初始化 Camera信息
     */
    private void initCameraViewInfo() {
//        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constant.DIR_CACHE + File.separator + "videos";
        cameraView.setJCameraLisenter(this);
        cameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH);
        if (getExternalCacheDir() != null) {
            cameraView.setSaveVideoPath((getExternalCacheDir()).getAbsolutePath() + File.separator + "videos");
        }
        cameraView.setLeftClickListener(leftClick);
        cameraView.setRightClickListener(rightClick);
        cameraView.setErrorLisenter(errorListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            blankView.setVisibility(View.VISIBLE);
        } else {
            blankView.setVisibility(View.GONE);
        }
    }

    /**
     * 登录
     */
    private void doLogin() {
        IntentUtil.startActivity(this, LoginActivity.class, null);
    }

    /**
     * 空白页被点击
     *
     * @param view 空白页
     */
    @Override
    public void onBlankViewClickListener(View view) {
        blankView.setClickable(false);
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            doLogin();
            blankView.setClickable(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            // 更新 拍客信息
            if (TextUtils.equals("updateUserInfo", message.getLabel())) {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    blankView.setVisibility(View.VISIBLE);
                    blankView.setClickable(true);
                } else {

                    //  判断是否绑定手机号
                    blankView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 拍照
     *
     * @param bitmap 拍照
     */
    @Override
    public void captureSuccess(Bitmap bitmap) {
        //  拍照成功
        if (null != bitmap) {
            saveBitmap2File(bitmap);
        }
    }

    /**
     * 录制视频
     */
    @Override
    public void recordSuccess(String url, Bitmap firstFrame) {
//        //  进行视频的转码
        if (getExternalCacheDir() != null) {
            try {
                shortVideoTranscoder = new PLShortVideoTranscoder(this, url, getExternalCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + "transcoded.mp4");
                shortVideoTranscoder.setMaxFrameRate(30);
                mediaFile = new PLMediaFile(url);
                mediaFile.getVideoRotation();
                processingDialog.show();
                LogUtil.e("width = " + metrics.widthPixels + " height = " + metrics.heightPixels + " widthRate = " + (metrics.widthPixels / 480.0 + "  heightRate = " + (metrics.heightPixels / 854.0)));
                shortVideoTranscoder.transcode(mediaFile.getVideoWidth(), mediaFile.getVideoHeight(), mediaFile.getVideoBitrate(), 0, new PLVideoSaveListener() {
                    @Override
                    public void onSaveVideoSuccess(String videoPath) {
                        processingDialog.dismiss();
                        if (!isFinishing() && !TextUtils.isEmpty(videoPath)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("videoPath", videoPath);
                            IntentUtil.startActivity(PaikewUploadActivity.this, VideoClipActivity.class, bundle);
                            finish();
                        }
                    }

                    @Override
                    public void onSaveVideoFailed(int errorCode) {
                        processingDialog.dismiss();
                        LogUtil.e("onSaveVideoFailed");
                    }

                    @Override
                    public void onSaveVideoCanceled() {
                        LogUtil.e("onSaveVideoCanceled");
                    }

                    @Override
                    public void onProgressUpdate(float percentage) {
                        runOnUiThread(() -> processingDialog.setProgress((int) (100 * percentage)));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将 bitmap 保存为 file
     *
     * @param bitmap 目标 bitmap
     */
    private void saveBitmap2File(Bitmap bitmap) {
        // 图片地址
        try {
            if (getExternalCacheDir() != null) {
                String filePath = (getExternalCacheDir()).getAbsolutePath() + File.separator + "pic" + System.currentTimeMillis() + SUFFIX_PIC;
                File file = new File(filePath);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, bos);
                bos.flush();
                bos.close();
                uploadPhotograph2ResourceServer(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        PictureSelector.create(this).openGallery(SelectMimeType.ofImage()).setSelectorUIStyle(selectorStyle)
                .isQuickCapture(true)
                .setMaxSelectNum(9).setMinSelectNum(0)
                .setImageEngine(imageEngine)
                .setCompressEngine((CompressFileEngine) (context, source, call) -> Luban.with(context).load(source).ignoreBy(100)
                        .setCompressListener(new OnNewCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String source, File compressFile) {
                                if (call != null) {
                                    call.onCallback(source, compressFile.getAbsolutePath());
                                }
                            }

                            @Override
                            public void onError(String source, Throwable e) {
                                if (call != null) {
                                    call.onCallback(source, null);
                                }
                            }
                        }).launch())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> selectList) {
                        if (null != selectList && !selectList.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("picList", selectList);
                            IntentUtil.startActivity(PaikewUploadActivity.this, PaiKewPictureUploadActivity.class, bundle);
                            finish();
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    /**
     * 选取本地视频
     */
    private void pickLocalVideo() {
        PictureSelector.create(this).openGallery(SelectMimeType.ofVideo()).setSelectorUIStyle(selectorStyle)
                .isQuickCapture(true)
                .setRecordVideoMaxSecond(VIDEO_MAX_TIME)
                .setRecordVideoMinSecond(VIDEO_MIN_TIME)
                .setFilterVideoMaxSecond(VIDEO_MAX_TIME + 1)
                .setFilterVideoMinSecond(VIDEO_MIN_TIME)
                .setSelectMaxDurationSecond(VIDEO_MAX_TIME)
                .setSelectMinDurationSecond(VIDEO_MIN_TIME)
                .setImageEngine(imageEngine)
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> videoList) {
                        if (null != videoList && !videoList.isEmpty()) {
                            if (getExternalCacheDir() != null) {
                                try {
                                    shortVideoTranscoder = new PLShortVideoTranscoder(PaikewUploadActivity.this, videoList.get(0).getPath(), getExternalCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + "transcoded.mp4");
                                    mediaFile = new PLMediaFile(videoList.get(0).getPath());
                                    processingDialog.show();
                                    shortVideoTranscoder.setMaxFrameRate(30);
                                    shortVideoTranscoder.transcode(mediaFile.getVideoWidth(), mediaFile.getVideoHeight(), mediaFile.getVideoBitrate(), 0, new PLVideoSaveListener() {
                                        @Override
                                        public void onSaveVideoSuccess(String videoPath) {
                                            processingDialog.dismiss();
                                            if (!isFinishing() && !TextUtils.isEmpty(videoPath)) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("videoPath", videoPath);
                                                IntentUtil.startActivity(PaikewUploadActivity.this, VideoClipActivity.class, bundle);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onSaveVideoFailed(int errorCode) {
                                            LogUtil.e("onSaveVideoFailed");
                                        }

                                        @Override
                                        public void onSaveVideoCanceled() {
                                            LogUtil.e("onSaveVideoCanceled");
                                        }

                                        @Override
                                        public void onProgressUpdate(float percentage) {
                                            runOnUiThread(() -> processingDialog.setProgress((int) (100 * percentage)));
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("获取到视频内容 onCancel !!! ");
                    }
                });
    }

    /**
     * 上传拍照的图片 到资源服务器
     *
     * @param filePath 图片保存的路径
     */
    private void uploadPhotograph2ResourceServer(String filePath) {
        Bundle bundle = new Bundle();
        LocalMedia media = new LocalMedia();
        List<LocalMedia> picList = new ArrayList<>();
        media.setPath(filePath);
        picList.add(media);
        bundle.putParcelableArrayList("picList", (ArrayList<? extends Parcelable>) picList);
        IntentUtil.startActivity(this, PaiKewPictureUploadActivity.class, bundle);
        finish();
    }

    /**
     * 出错
     */
    private final ErrorListener errorListener = new ErrorListener() {
        @Override
        public void onError() {
            new Handler(Looper.getMainLooper()).post(() -> ToastUtil.showShortToast("照相机打开失败"));
        }

        @Override
        public void AudioPermissionError() {
            LogUtil.e("没有录取权限回调");
        }
    };

    // 打开图库
    /**
     * 左侧图标点击事件
     */
    private final ClickListener leftClick = this::openAlbum;

    // 选取本地视频
    /**
     * 右侧点击事件
     */
    private final ClickListener rightClick = this::pickLocalVideo;

    @OnClick({R.id.img_cancel_upload})
    public void onClick(View v) {
        if (v.getId() == R.id.img_cancel_upload) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtil.e("ParentHomeFragment  onPermissionsGranted code =  " + requestCode);
        if (requestCode == CODE_REQUEST_PERMISSION_PAIKEW && EasyPermissions.hasPermissions(this, permissionList)) {
            hasPermission = true;
            init();
            onResume();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PERMISSION_PAIKEW && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            hasPermission = false;
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限申请")
                    .setRationale("为了能够正常的使用拍客服务，请允许开屏新闻使用您的手机存储和相机权限,以及音频录制权限")
                    .setNegativeButton("取消").setPositiveButton("确定").build().show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermission && null != cameraView) {
            cameraView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasPermission && null != cameraView) {
            cameraView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}