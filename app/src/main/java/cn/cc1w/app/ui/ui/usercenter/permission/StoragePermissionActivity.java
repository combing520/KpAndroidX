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
public class StoragePermissionActivity extends CustomActivity {

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
        setContentView(R.layout.activity_storage_permission);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
    }

    private void initNavigation() {
        titleTv.setText("存储权限");
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void checkPermission() {
        String[] storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean isStorageAuthored = EasyPermissions.hasPermissions(this, storagePermission);

        if (topImg != null && labelTv != null) {
            if (isStorageAuthored) {
                AppUtil.loadRes(R.mipmap.storage2, topImg);
                labelTv.setText("允许访问存储权限");
            } else {
                AppUtil.loadRes(R.mipmap.storage1, topImg);
                labelTv.setText("未允许访问存储权限");
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