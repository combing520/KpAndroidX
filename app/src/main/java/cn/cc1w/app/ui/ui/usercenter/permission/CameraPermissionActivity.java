package cn.cc1w.app.ui.ui.usercenter.permission;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.AppUtil;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author kpinfo
 */
public class CameraPermissionActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.topImg)
    ImageView topImg;
    @BindView(R.id.labelTv)
    TextView labelTv;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    private long lastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_permission);
        init();
    }
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
    }

    private void initNavigation() {
        titleTv.setText("相机权限");
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void checkPermission(){
        String[] cameraPermission = new String[]{Manifest.permission.CAMERA};
        boolean cameraAuthored = EasyPermissions.hasPermissions(this, cameraPermission);
        if(topImg !=null && labelTv != null){
            if (cameraAuthored) {
                AppUtil.loadRes(R.mipmap.camera2, topImg);
                labelTv.setText("允许访问相机权限");
            } else {
                AppUtil.loadRes(R.mipmap.camera1, topImg);
                labelTv.setText("未允许访问相机权限");
            }
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.container})
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.TIME_INTERVAL) {
            int id = v.getId();
            if (id == R.id.img_back_header_not_title) {
                finish();
            } else if (id == R.id.container) {
                AppUtil.gotoSetting(this);
            }
        }
        lastClickTime = currentTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}