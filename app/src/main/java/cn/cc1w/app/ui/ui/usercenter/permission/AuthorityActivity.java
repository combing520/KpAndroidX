package cn.cc1w.app.ui.ui.usercenter.permission;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.IntentUtil;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author kpinfo
 */
public class AuthorityActivity extends CustomActivity {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.locationTv)
    TextView locationTv;
    @BindView(R.id.storageTv)
    TextView storageTv;
    @BindView(R.id.cameraTv)
    TextView cameraTv;
    @BindView(R.id.microphoneTv)
    TextView microphoneTv;
    private long lastClickTime = System.currentTimeMillis();
    private Unbinder unbinder;
    private static final String PERMISSION_AUTHORED = "允许访问";
    private static final String PERMISSION_AUTHORED_NOT = "未允许访问";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority);
        unbinder = ButterKnife.bind(this);
        init();
    }

    private void init() {
        initNavigation();
    }

    private void initNavigation() {
        titleTv.setText("权限管理");
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissionInfo();
    }

    private void checkPermissionInfo() {
        String[] storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean isStorageAuthored = EasyPermissions.hasPermissions(this, storagePermission);

        if (storageTv != null) {
            if (isStorageAuthored) {
                storageTv.setText(PERMISSION_AUTHORED);
            } else {
                storageTv.setText(PERMISSION_AUTHORED_NOT);
            }
        }
        String[] locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        boolean isLocationAuthored = EasyPermissions.hasPermissions(this, locationPermission);

        if (locationTv != null) {
            if (isLocationAuthored) {
                locationTv.setText(PERMISSION_AUTHORED);
            } else {
                locationTv.setText(PERMISSION_AUTHORED_NOT);
            }
        }

        String[] cameraPermission = new String[]{Manifest.permission.CAMERA};
        boolean isCameraAuthored = EasyPermissions.hasPermissions(this, cameraPermission);
        if (cameraTv != null) {
            if (isCameraAuthored) {
                cameraTv.setText(PERMISSION_AUTHORED);
            } else {
                cameraTv.setText(PERMISSION_AUTHORED_NOT);
            }
        }
        String[] microphonePermission = new String[]{Manifest.permission.RECORD_AUDIO};
        boolean isMicroPhoneAuthored = EasyPermissions.hasPermissions(this, microphonePermission);

        if (microphoneTv != null) {
            if (isMicroPhoneAuthored) {
                microphoneTv.setText(PERMISSION_AUTHORED);
            } else {
                microphoneTv.setText(PERMISSION_AUTHORED_NOT);
            }
        }
    }

    @OnClick({R.id.location, R.id.img_back_header_not_title, R.id.store, R.id.camera, R.id.microphone})
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.TIME_INTERVAL) {
            int id = v.getId();
            if (id == R.id.img_back_header_not_title) {
                finish();
            } else if (id == R.id.location) {
                IntentUtil.startActivity(this, LocationPermissionActivity.class);
            } else if (id == R.id.store) {
                IntentUtil.startActivity(this, StoragePermissionActivity.class);
            } else if (id == R.id.camera) {
                IntentUtil.startActivity(this, CameraPermissionActivity.class);
            } else if (id == R.id.microphone) {
                IntentUtil.startActivity(this, MicrophoneActivity.class);
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