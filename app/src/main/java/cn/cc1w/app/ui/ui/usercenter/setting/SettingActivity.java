package cn.cc1w.app.ui.ui.usercenter.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.permission.AuthorityActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.FileUtils;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;

/**
 * APP 设置
 *
 * @author kpinfo
 */
public class SettingActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_cnt_cache_setting)
    TextView cacheCntTv;
    @BindView(R.id.switch_setting)
    SwitchCompat switchCompat;
    @BindView(R.id.txt_version_setting)
    TextView versionInfoTv;
    @BindView(R.id.btn_logout_setting)
    TextView logoutBtn;
    private long lastTime;
    private boolean isCheck;
    private static final String NO_CACHE = "0KB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initData();
        getVersionInfo();
        getCacheInfo();
    }

    /**
     * 获取版本信息
     */
    private void getVersionInfo() {
        String versionName = AppUtil.getVersionName(this);
        if (!TextUtils.isEmpty(versionName)) {
            versionInfoTv.setText("V".concat(versionName));
        }
    }

    private void initData() {
        isCheck = Constant.isPushMessageOpened;
        switchCompat.setChecked(isCheck);
    }

    /**
     * 获取 缓存信息
     */
    private void getCacheInfo() {
        try {
            if (!FileUtils.iSSDCardExist()) {
                cacheCntTv.setText(NO_CACHE);
            } else {
                if (getExternalCacheDir() != null) {
                    File file = new File(getExternalCacheDir().getAbsolutePath());
                    long size = FileUtils.getFolderSize(file);
                    if (size == 0) {
                        cacheCntTv.setText(NO_CACHE);
                    } else {
                        String sizeInfo = FileUtils.getFormatSize(size);
                        cacheCntTv.setText(TextUtils.isEmpty(sizeInfo) ? NO_CACHE : sizeInfo);
                    }
                } else {
                    cacheCntTv.setText(NO_CACHE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText(getResources().getString(R.string.setting));
        lastTime = System.currentTimeMillis();
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            logoutBtn.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_logout_setting, R.id.img_back_header_not_title,
            R.id.relate_cache_clear_setting, R.id.relate_account_manager_setting,
            R.id.relate_feedback_setting, R.id.relate_authority_setting})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int id = view.getId();
        if (id == R.id.btn_logout_setting) {
            logoOut();
        } else if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.relate_cache_clear_setting) {
            clearCache();
        } else if (id == R.id.relate_account_manager_setting) {
            manageAccount();
        } else if (id == R.id.relate_feedback_setting) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                intent.setClass(this, FeedBackActivity.class);
                startActivity(intent);
            } else {
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.relate_authority_setting) {
            IntentUtil.startActivity(this, AuthorityActivity.class, null);
        }
    }

    @OnCheckedChanged(R.id.switch_setting)
    public void onSwitchCheck(CompoundButton button, boolean checked) {
        Constant.isPushMessageOpened = checked;
        if (!Constant.IS_PUSH_INIT_SUCCESS && Constant.isPushMessageOpened) {
            AppContext.initKpPushInfo();
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        if (getExternalCacheDir() != null) {
            boolean isSuccess = FileUtils.deleteFolderFile(getExternalCacheDir().getAbsolutePath(), false);
            if (isSuccess) {
                cacheCntTv.setText(NO_CACHE);
                ToastUtil.showShortToast("清除缓存成功");
            }
        } else {
            cacheCntTv.setText(NO_CACHE);
        }
    }

    /**
     * 账号管理
     */
    private void manageAccount() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, AccountManagerActivity.class, null);
        } else {
            login();
        }
    }

    /**
     * 进行登录
     */
    private void login() {
        IntentUtil.startActivity(this, LoginActivity.class, null);
    }

    /**
     * 登出
     */
    private void logoOut() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                AppUtil.doUserLogOut();
                AppUtil.initLogInfo();
                ToastUtil.showShortToast("退出成功");
                finish();
            }
        }
        lastTime = currentTime;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            SharedPreferenceUtil.setPushState(Constant.isPushMessageOpened);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
