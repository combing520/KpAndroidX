package cn.cc1w.app.ui.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.amap.api.location.AMapLocation;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.JsonObject;
import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.rxjava.rxlife.RxLife;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.broadcast.DownloadCompleteReceiver;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AdvertisementEntity;
import cn.cc1w.app.ui.entity.AppUpdateEntity;
import cn.cc1w.app.ui.entity.BottomTabEntity;
import cn.cc1w.app.ui.manager.LocationManager;
import cn.cc1w.app.ui.utils.DownloadUtil;
import cn.cc1w.app.ui.utils.Task;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.DialogAdEntity;
import cn.cc1w.app.ui.manager.AppManager;
import cn.cc1w.app.ui.ui.fragment.FunctionFragment;
import cn.cc1w.app.ui.ui.fragment.ParentHomeFragment;
import cn.cc1w.app.ui.ui.fragment.RecordFragment;
import cn.cc1w.app.ui.ui.fragment.UserAppsFragment;
import cn.cc1w.app.ui.ui.fragment.UserCenterFragment;
import cn.cc1w.app.ui.ui.home.upload.PaikewUploadActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.dialog.CustomProgressDialog;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;

/**
 * @author kpinfo
 * 首页 ---开屏 7。0+
 */
public class MainNewActivity extends CustomActivity implements OnTabSelectListener, EasyPermissions.PermissionCallbacks {
    private CommonTabLayout mTabLayout;
    private ParentHomeFragment mParentHomeFragment;
    private RecordFragment mPaiKeFragment;
    private UserAppsFragment mKaiPingCodeFragment;
    private FunctionFragment mFunctionFragment;
    private UserCenterFragment mUserCenterFragment;

    private FragmentManager mFragmentManager;
    private Fragment mCurrentSelectFragment;
    private static final int POS_HOME_TAB = 0;
    private static final int POS_PAI_KE_TAB = 1;
    private static final int POS_CODE_KAI_PING_TAB = 2;
    private static final int POS_FUN_TAB = 3;
    private static final int POS_USER_CENTER_TAB = 4;
    private static final int[] ICON_NORMAL = new int[]{R.mipmap.home_normal, R.mipmap.paike_normal, R.mipmap.kaipinghao_normal, R.mipmap.function_normal, R.mipmap.my_normal};
    private static final int[] ICON_SELECT = new int[]{R.mipmap.home_selected, R.mipmap.paker_selected, R.mipmap.kaipinghao_selected, R.mipmap.function_selected, R.mipmap.my_selected};
    private static final int[] ICON_NORMAL_RED = new int[]{R.mipmap.home_normal_red, R.mipmap.paike_normal_red, R.mipmap.kaipinghao_normal_red, R.mipmap.function_normal_red, R.mipmap.my_normal_red};
    private static final int[] ICON_SELECT_RED = new int[]{R.mipmap.home_selected_red, R.mipmap.paker_selected_red, R.mipmap.kaipinghao_selected_red, R.mipmap.function_selected_red, R.mipmap.my_selected_red};
    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private static final int CODE_REQUEST_PERMISSION_PAIKE = 1;
    public static boolean isForeground = false;
    private boolean isActivityFront = false;
    private int mLastSelectPos = -1;
    private CustomProgressDialog downloadDialog;
    private DownloadCompleteReceiver receiver;
    private boolean isExit = false;
    private Timer timer = new Timer();
    private Task task;
    private boolean isUploading = false;
    private boolean isObtainLocation = false;
    private XPopup.Builder builder = null;
    private BasePopupView popView = null;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        init();
    }

    private void init() {
        SharedPreferenceUtil.setUserGuide(false);
        initView();
        initTimer();
        Constant.isMainActivityCreated = true;
        EventBus.getDefault().register(this);
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        initFragment();
        initBottomNaviInfo();
        initStatusBar(POS_HOME_TAB);
        initDialog();
        initData();
        initDownLoadCompleteReceiver();
        setRedThemeInfo();
        getLocation();
    }

    private void doRequest() {
        if (!Constant.isHasFetchVersion) {
            addMachine();
            getAppVersionInfo();
            getAdvertisementInfo();
        }
    }

    private void initView() {
        mTabLayout = findViewById(R.id.main_new_nav_bottom);
    }

    private void initTimer() {
        task = new Task() {
            @Override
            public void onRun() {
                mHandler.post(MainNewActivity.this::updateLocationInfo);
            }
        };
        timer.schedule(task, 0, Constant.FREQUENCY_LOCATION_UPDATE);
    }

    private void updateLocationInfo() {
        if (!TextUtils.isEmpty(Constant.CW_LATITUDE) && !TextUtils.isEmpty(Constant.CW_LONGITUDE) && !TextUtils.isEmpty(Constant.CW_ID_KPUSH) && NetUtil.isNetworkConnected(this) && !isUploading) {
            isUploading = true;
            RxHttp.postJson(Constant.TAG_PUSH_KAI_PIN)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponse(JsonObject.class).as(RxLife.asOnMain(this))
                    .subscribe(data -> isUploading = false, (OnError) error -> isUploading = false);
        }
    }

    private void addMachine() {
        RxHttp.postJson(Constant.ADD_MACHINE)
                .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                .asResponse(JsonObject.class).as(RxLife.asOnMain(this)).subscribe();
    }

    private void setRedThemeInfo() {
        if (Constant.IS_SHOW_RED_MODE) {
            ImmersionBar.with(this).statusBarColor(R.color.color_red_theme).statusBarDarkFont(false).init();
        }
    }

    /**
     * 初始化 dialog
     */
    private void initDialog() {
        downloadDialog = new CustomProgressDialog(this, "软件下载中...");
        downloadDialog.setCancelable(false);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        AdvertisementEntity.AdvertisementBean item = (AdvertisementEntity.AdvertisementBean) getIntent().getSerializableExtra("item");
        if (null != item) {
            AppUtil.splashJump(this, item);
        }
    }

    /**
     * 系统下载管理
     */
    private void initDownLoadCompleteReceiver() {
        receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 初始化 状态栏
     */
    private void initStatusBar(int pos) {
        if (!Constant.IS_SHOW_RED_MODE) {
            if (pos == POS_USER_CENTER_TAB) {
                ImmersionBar.with(this).statusBarColor(R.color.colorPrimary).statusBarDarkFont(false).init();
            } else {
                ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).init();
            }
        }
    }

    /**
     * 初始化 Fragment信息
     */
    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mParentHomeFragment = ParentHomeFragment.newInstance();
        mPaiKeFragment = RecordFragment.newInstance();
        mKaiPingCodeFragment = UserAppsFragment.newInstance();
        mFunctionFragment = FunctionFragment.newInstance();
        mUserCenterFragment = UserCenterFragment.newInstance();
        switchFragment(mParentHomeFragment);
    }

    /**
     * 初始化底部导航
     */
    private void initBottomNaviInfo() {
        ArrayList<CustomTabEntity> dataSet = new ArrayList<>();
        String[] tabNameArr = getResources().getStringArray(R.array.bottom_navigation_txt);
        if (Constant.IS_SHOW_RED_MODE) {
            for (int i = 0; i < tabNameArr.length; i++) {
                dataSet.add(new BottomTabEntity(tabNameArr[i], ICON_SELECT_RED[i], ICON_NORMAL_RED[i]));
            }
            mTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_red_theme));
            mTabLayout.setTextSelectColor(ContextCompat.getColor(this, R.color.color_golden_theme));
            mTabLayout.setTextUnselectColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else {
            for (int i = 0; i < tabNameArr.length; i++) {
                dataSet.add(new BottomTabEntity(tabNameArr[i], ICON_SELECT[i], ICON_NORMAL[i]));
            }
        }
        mTabLayout.setTabData(dataSet);
        mTabLayout.setOnTabSelectListener(this);
    }

    /**
     * 切换 fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (targetFragment != null) {
            if (mCurrentSelectFragment != targetFragment) {
                if (!targetFragment.isAdded()) {
                    if (null != mCurrentSelectFragment) {
                        mFragmentManager.beginTransaction().hide(mCurrentSelectFragment).commit();
                    }
                    mFragmentManager.beginTransaction().add(R.id.main_new_container, targetFragment).commit();
                } else {
                    mFragmentManager.beginTransaction().hide(mCurrentSelectFragment).show(targetFragment).commit();
                }
                EventBus.getDefault().post(new EventMessage("closeSmallWindow", "closeSmallWindow"));
                mCurrentSelectFragment = targetFragment;
            }
        }
    }

    @Override
    public void onTabSelect(int position) {
        if (mLastSelectPos != position) {
            mLastSelectPos = position;
            initStatusBar(position);
            if (position == POS_HOME_TAB) {
                switchFragment(mParentHomeFragment);
            } else if (position == POS_PAI_KE_TAB) {
                LogUtil.e(" pos = " + POS_PAI_KE_TAB + " fragment = " + mPaiKeFragment);
                switchFragment(mPaiKeFragment);
            } else if (position == POS_CODE_KAI_PING_TAB) {
                LogUtil.e(" pos = " + POS_CODE_KAI_PING_TAB + " fragment = " + mKaiPingCodeFragment);
                switchFragment(mKaiPingCodeFragment);
            } else if (position == POS_FUN_TAB) {
                LogUtil.e(" pos = " + POS_FUN_TAB + " fragment = " + mPaiKeFragment);
                switchFragment(mFunctionFragment);
            } else if (position == POS_USER_CENTER_TAB) {
                LogUtil.e(" pos = " + POS_PAI_KE_TAB + " fragment = " + mPaiKeFragment);
                switchFragment(mUserCenterFragment);
            }
        }
    }

    @Override
    public void onTabReselect(int position) {

    }

    @SuppressLint("InflateParams")
    private void showNotificationAuthorizationDialog() {
        boolean isPushOpen = AppUtil.isNotificationEnabled(this);
        if (!isPushOpen) {
            boolean isNeverAlterNotification = SharedPreferenceUtil.isNeverAlterPhoneNotification();
            if (!isNeverAlterNotification) {
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_alter_notification, null);
                if (null != view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    Window window = dialog.getWindow();
                    if (null != window) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                        window.getDecorView().setPadding(0, 0, 0, 0);
                        WindowManager.LayoutParams lp = window.getAttributes();
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        int statusBarHeight = AppUtil.getStatusBarHeight(MainNewActivity.this);
                        if (statusBarHeight >= 0) {
                            lp.height = mDisplayMetrics.heightPixels - statusBarHeight;
                        } else {
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                        }
                        window.setAttributes(lp);
                    }
                    view.findViewById(R.id.img_close_dialog_notification).setOnClickListener(v -> dialog.dismiss());
                    view.findViewById(R.id.btn_open_dialog_notification).setOnClickListener(v -> {
                        AppUtil.goToAppSetting(MainNewActivity.this);
                        dialog.dismiss();
                    });
                    view.findViewById(R.id.btn_never_dialog_notification).setOnClickListener(v -> {
                        dialog.dismiss();
                        SharedPreferenceUtil.setNeverAlterPhoneNotification(true);
                    });
                    dialog.setContentView(view);
                }
            }
        }
    }

    private void getLocation() {
        LocationManager.getInstance().startLocation();
        if (LocationManager.getInstance().getLocation() != null) {
            updateUserLocation();
            AMapLocation aMapLocation = LocationManager.getInstance().getLocation();
            setLocationInfo(aMapLocation);
            LocationManager.getInstance().stopLocation();
        } else {
            LocationManager.getInstance().setLocationCallback(aMapLocation -> {
                setLocationInfo(aMapLocation);
                if (null != aMapLocation) {
                    setLocationInfo(aMapLocation);
                    updateUserLocation();
                }
            });
        }
    }

    private void setLocationInfo(AMapLocation aMapLocation) {
        if (aMapLocation != null && !isFinishing()) {
            Constant.CW_COUNTRY = aMapLocation.getCountry();
            Constant.ADDRESS = aMapLocation.getAddress();
            Constant.CW_LATITUDE = String.valueOf(aMapLocation.getLatitude());
            Constant.CW_LONGITUDE = String.valueOf(aMapLocation.getLongitude());
            Constant.CW_COUNTRY = aMapLocation.getCountry();
            Constant.CW_PROVINCE = aMapLocation.getProvince();
            Constant.CW_CITY = aMapLocation.getCity();
            Constant.CW_AREA = aMapLocation.getDistrict();
            EventBus.getDefault().post(new EventMessage("updateAddress", "updateAddress"));
        }
    }

    private void updateUserLocation() {
        if (!isFinishing()) {
            mHandler.postDelayed(this::getLocationInfo, 30 * 60 * 1000);
        }
    }

    private void getLocationInfo() {
        if (!isObtainLocation && NetUtil.isNetworkConnected(this)) {
            isObtainLocation = true;
            LocationManager.getInstance().startLocation();
            if (LocationManager.getInstance().getLocation() != null) {
                isObtainLocation = false;
                AMapLocation aMapLocation = LocationManager.getInstance().getLocation();
                setLocationInfo(aMapLocation);
                LocationManager.getInstance().stopLocation();
            } else {
                LocationManager.getInstance().setLocationCallback(aMapLocation -> {
                    isObtainLocation = false;
                    if (null != aMapLocation) {
                        setLocationInfo(aMapLocation);
                    }
                });
            }
        }
    }

    /**
     * @param grade app 的下载级别
     */
    private void showUpdateDialog(int grade, final String appDownloadUrl, String remark) {
        if (grade == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("更新提示");
            builder.setMessage("检测到新版本，是否下载更新?\n" + "新版本更新描述:\n" + remark);
            builder.setPositiveButton("确定", (dialog, which) -> {
                LogUtil.e("进行下载");
                if (!isFinishing()) {
                    doAppDownload(appDownloadUrl);
                }
                dialog.dismiss();
            });
            builder.create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("更新提示");
            builder.setMessage("检测到新版本，是否下载更新?\n" + "新版本更新描述:\n" + remark);
            builder.setPositiveButton("确定", (dialog, which) -> {
                LogUtil.e("进行下载");
                if (!isFinishing()) {
                    doAppDownload(appDownloadUrl);
                }
                dialog.dismiss();
            });
            builder.setNegativeButton("取消", (dialog, which) -> {
                LogUtil.e("取消");
                dialog.dismiss();
            });
            builder.create().show();
        }
    }

    /**
     * 安装 APK
     */
    private void installApk(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri data;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                data = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                data = Uri.fromFile(file);
            }
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载新版本的apk
     */
    private void doAppDownload(String appUrl) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != downloadDialog) {
                downloadDialog.show();
            }
            try {
                String dir = DownloadUtil.getDownloadDir(this);
                String destPath = dir + "/" + System.currentTimeMillis() + ".apk";
                RxHttp.get(appUrl)
                        .asAppendDownload(destPath, AndroidSchedulers.mainThread(), progress -> {
                            //下载进度回调,0-100，仅在进度有更新时才会回调
                            int currentProgress = progress.getProgress(); //当前进度 0-100
                            long currentSize = progress.getCurrentSize(); //当前已下载的字节大小
                            long totalSize = progress.getTotalSize();     //要下载的总字节大小
                            LogUtil.e("currentProgress = " + currentProgress + " currentSize = " + currentSize + " totalSize=" + totalSize);
                            if (null != downloadDialog) {
                                downloadDialog.setProgress(currentProgress);
                            }
                        })
                        .doFinally(() -> {
                            if (null != downloadDialog) {
                                downloadDialog.dismiss();
                            }
                        }).as(RxLife.as(this))
                        .subscribe(s -> {
                            LogUtil.e("下载成功 s = " + s);
                            if (!TextUtils.isEmpty(s)) {
                                installApk(new File(s));
                            }
                            if (null != downloadDialog) {
                                downloadDialog.dismiss();
                            }
                        }, throwable -> {
                            if (null != downloadDialog) {
                                downloadDialog.dismiss();
                            }
                        });
            } catch (Exception e) {
                if (null != downloadDialog) {
                    downloadDialog.dismiss();
                }
            }
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 获取线上APP的版本信息
     */
    private void getAppVersionInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            Constant.isHasFetchVersion = true;
            RxHttp.postJson(Constant.UPDATE_APP)
                    .add("type", Constant.CW_OS)
                    .asResponse(AppUpdateEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(appUpdate -> {
                        if (!isFinishing() && appUpdate != null) {
                            LogUtil.e("getAppVersionInfo onObjectSuccess !!!!  ++++ " + appUpdate);
                            try {
                                double currentAppVersion = TextUtils.isEmpty(AppUtil.getVersionName(MainNewActivity.this)) ? 1.0 : Double.parseDouble(AppUtil.getVersionName(MainNewActivity.this));
                                double onlineAppVersion = TextUtils.isEmpty(appUpdate.getVersion()) ? 1.0 : Double.parseDouble(appUpdate.getVersion());
                                LogUtil.e("  当前使用版本 = " + currentAppVersion + "  线上版本 = " + onlineAppVersion);
                                if (onlineAppVersion > currentAppVersion) {
                                    showUpdateDialog(appUpdate.getGrade(), appUpdate.getUrl(), appUpdate.getRemark());
                                } else {
                                    showNotificationAuthorizationDialog();
                                }
                            } catch (Exception e) {
                                showNotificationAuthorizationDialog();
                                Constant.isHasFetchVersion = false;
                            }
                        }
                    }, (OnError) error -> showNotificationAuthorizationDialog());
        } else {
            Constant.isHasFetchVersion = true;
            showNotificationAuthorizationDialog();
        }
    }

    /**
     * 获取广告弹窗
     */
    private void getAdvertisementInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.LIST_DIALOG_AD_HOME)
                    .asResponseList(DialogAdEntity.ItemDialogAdEntity.class).as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (!isFinishing() && dataSet != null && !dataSet.isEmpty()) {
                            showAdDialog(dataSet);
                        }
                    }, (OnError) error -> LogUtil.e("弹窗广告获取失败 --->>> " + error.getErrorMsg()));
        }
    }

    private void showAdDialog(List<DialogAdEntity.ItemDialogAdEntity> dataSet) {
        AdvertisementDialog adDialog = new AdvertisementDialog(this);
        adDialog.setDataSet(dataSet);
        if (builder == null) {
            builder = new XPopup.Builder(this).autoOpenSoftInput(false);
        }
        popView = builder.asCustom(adDialog);
        popView.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 权限通过
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtil.e("MainActivity onPermissionsGranted code = " + requestCode);
        if (requestCode == CODE_REQUEST_PERMISSION_PAIKE) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.RECORD_PAIKEW);
                IntentUtil.startActivity(this, LoginActivity.class, bundle);
            } else {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo && !TextUtils.isEmpty(userInfo.getMobile())) {
                    IntentUtil.startActivity(this, PaikewUploadActivity.class, null);
                } else {
                    IntentUtil.startActivity(this, UpdateMobileActivity.class, null);
                }
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityFront = true;
        isForeground = true;
        if (mLastSelectPos > -1 && mTabLayout != null) {
            onTabReselect(mLastSelectPos);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        isActivityFront = false;
        isForeground = false;
    }

    /**
     * @param message 接受到的信息 对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel()) && !isFinishing()) {
            if (TextUtils.equals("changeBottomTab", message.getLabel())) {
                switchFragment(mKaiPingCodeFragment);
            } else if (TextUtils.equals("enterFullScreen", message.getLabel())) {
                if (mTabLayout.getVisibility() == View.VISIBLE) {
                    mTabLayout.setVisibility(View.GONE);
                }
            } else if (TextUtils.equals("quitFullScreen", message.getLabel())) {
                if (mTabLayout.getVisibility() == View.GONE) {
                    mTabLayout.setVisibility(View.VISIBLE);
                }
            } else if (TextUtils.equals("showPaikewTab", message.getLabel())) {
                switchFragment(mPaiKeFragment);
            } else if (TextUtils.equals(Constant.TAG_DOWNLOAD_COMPLETE, message.getLabel())) {
                if (isActivityFront) {
                    ToastUtil.showShortToast("下载完成");
                }
            }
        }
    }

    /**
     * 退出APP
     */
    private void quitTheApp() {
        if (isExit) {
            this.onDestroy();
            System.exit(0);
        } else {
            isExit = true;
            ToastUtil.showShortToast(getResources().getString(R.string.doubleClickQuit));
            mHandler.sendEmptyMessageDelayed(0, Constant.TIME_QUIT);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentSelectPos", mLastSelectPos);
        LogUtil.e("onSaveInstanceState  pos = " + mLastSelectPos + "  " + System.currentTimeMillis());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            mLastSelectPos = savedInstanceState.getInt("currentSelectPos", 0);
        }
        LogUtil.e("onRestoreInstanceState  pos = " + mLastSelectPos + "  " + System.currentTimeMillis());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quitTheApp();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        doRequest();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        LocationManager.getInstance().destroy();
        GSYVideoManager.releaseAllVideos();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (popView != null) {
            popView.destroy();
        }
        mHandler.removeCallbacksAndMessages(null);
        Constant.isMainActivityCreated = false;
        if (null != receiver) {
            unregisterReceiver(receiver);
        }
        AppManager.appExit(this);
        super.onDestroy();
    }
}