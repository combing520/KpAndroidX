package cn.cc1w.app.ui.ui.qr;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import app.cloud.ccwb.cn.linlibrary.qrscan.CaptureActivity;
import app.cloud.ccwb.cn.linlibrary.qrscan.Intents;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.LogUtil;

/**
 * 二维码扫描
 *
 * @author kpinfo
 */
public class QrCodeScanActivity extends CaptureActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_SCAN = 0X01;
    private static final int REQUEST_CODE_PHOTO = 0X02;
    private static final int REQUEST_CODE_SCAN2 = 0X03;
    private boolean isFromFunScan = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_qr_code_scan;
    }

    @Override
    public void onCreate(Bundle icicle) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && this.isTranslucentOrFloating()) {
            this.fixOrientation();
        }
        super.onCreate(icicle);
        if (getCaptureHelper() != null) {
            getCaptureHelper().playBeep(true).vibrate(true);
        }
        isFromFunScan = getIntent().getBooleanExtra("isFromFunScan", false);
        findViewById(R.id.ivLeft).setOnClickListener(this);
        findViewById(R.id.ivRight).setOnClickListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 打开图集
     */
    private void openAlbumCode() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_PHOTO) {
                LogUtil.e("QrCodeScanActivity 本地图片识别结果 = " + data);
                if (isFromFunScan) {
                    EventBus.getDefault().post(new EventMessage("functionScan", data));
                } else {
                    EventBus.getDefault().post(new EventMessage(Constant.TAG_RESULT_SCAN, data));
                }
                onBackPressed();
            } else if (requestCode == REQUEST_CODE_SCAN || requestCode == REQUEST_CODE_SCAN2) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                LogUtil.e("QrCodeScanActivity 扫描结果 = " + result);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivLeft) {
            onBackPressed();
        } else if (id == R.id.ivRight) {
            openAlbumCode();
        }
    }
}