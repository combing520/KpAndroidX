package cn.cc1w.app.ui.ui.usercenter.broke;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.camera.JCameraView;
import cn.cc1w.app.ui.widget.camera.listener.ClickListener;
import cn.cc1w.app.ui.widget.camera.listener.ErrorListener;
import cn.cc1w.app.ui.widget.camera.listener.JCameraListener;
import cn.cc1w.app.ui.R;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 拍摄
 *
 * @author kpinfo
 */
public class ShootActivity extends CustomActivity implements ClickListener, EasyPermissions.PermissionCallbacks {
    @BindView(R.id.cameraView_shoot)
    JCameraView cameraView;
    private Unbinder unbinder;
    private String PATH_VIDEO_SAVE = "";
    private String PATH_PICTURE_SAVE = "";
    private static final String SUFFIX_PIC = ".jpg";
    private static final int COMPRESS_QUALITY = 70;
    private static final String LABEL_EVENT = "sendMessage";
    private static final String TYPE_VIDEO_EVENT = "video";
    private static final String TYPE_PIC_EVENT = "pic";
    private static final int CODE_REQUEST_PERMISSION_PAIKEW = 3;
    private boolean hasPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoot);
        if (getExternalCacheDir() != null) {
            PATH_VIDEO_SAVE = getExternalCacheDir().getAbsolutePath() + File.separator;
            PATH_PICTURE_SAVE = getExternalCacheDir().getAbsolutePath() + File.separator;
            LogUtil.d("PATH_VIDEO_SAVE = " + PATH_VIDEO_SAVE + "   PATH_PICTURE_SAVE = " + PATH_PICTURE_SAVE);
        }
        unbinder = ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissionDialog();
        } else {
            init();
        }
    }

    @SuppressLint("InflateParams")
    private void showPermissionDialog() {
        final String[] permissionList = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        if (!EasyPermissions.hasPermissions(this, permissionList)) {
            EasyPermissions.requestPermissions(this, "开屏新闻申请以下权限\n" +
                    "手机存储和相机\n" +
                    "拍照或录像\n" +
                    "权限申请\n" +
                    "为了能够正常的使用拍客服务，请允许开屏新闻使用您的手机存储和相机权限。", CODE_REQUEST_PERMISSION_PAIKEW, permissionList);
        } else {
            hasPermission = true;
            init();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        initCameraViewInfo();
    }

    /**
     * 初始化 拍摄的信息
     */
    private void initCameraViewInfo() {
        cameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH);
        cameraView.setSaveVideoPath(PATH_VIDEO_SAVE);
        cameraView.setErrorLisenter(errorListener);
        cameraView.setJCameraLisenter(cameraListener);
        cameraView.setLeftClickListener(this);
    }

    /**
     * 出错
     */
    private final ErrorListener errorListener = new ErrorListener() {
        @Override
        public void onError() {
        }

        @Override
        public void AudioPermissionError() {
        }
    };

    /**
     * 获取视频或者是图片的回调
     */
    private final JCameraListener cameraListener = new JCameraListener() {
        @Override
        public void captureSuccess(Bitmap bitmap) {
            if (null != bitmap) {
                saveBitmap2File(bitmap);
            }
        }

        @Override
        public void recordSuccess(String url, Bitmap firstFrame) {
//            LogUtil.e("recordSuccess 获取视频路径 = " + url);
            EventBus.getDefault().post(new EventMessage(LABEL_EVENT, url, TYPE_VIDEO_EVENT));
            finish();
        }
    };

    /**
     * 将 bitmap 保存为 file
     *
     * @param bitmap 目标 bitmap
     */
    private void saveBitmap2File(Bitmap bitmap) {
        // 图片地址
        String filePath = PATH_PICTURE_SAVE + System.currentTimeMillis() + SUFFIX_PIC;
        File file = new File(filePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, bos);
            bos.flush();
            bos.close();
            EventBus.getDefault().post(new EventMessage(LABEL_EVENT, filePath, TYPE_PIC_EVENT));
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermission) {
            cameraView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasPermission) {
            cameraView.onPause();
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
        if (requestCode == CODE_REQUEST_PERMISSION_PAIKEW) {
            hasPermission = true;
            init();
            onResume();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PERMISSION_PAIKEW) {
            hasPermission = false;
            ToastUtil.showShortToast("摄像头权限被拒绝，无法使用使用拍客功能");
        }
    }

    @Override
    public void onClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}