package cn.cc1w.app.ui.ui.splash;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.rxjava.rxlife.RxLife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.UserGuiderAdapter;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AdvertisementEntity;
import cn.cc1w.app.ui.entity.AppConfigEntity;
import cn.cc1w.app.ui.entity.AppModeEntity;
import cn.cc1w.app.ui.utils.SignCheckUtils;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.manager.AppManager;
import cn.cc1w.app.ui.ui.home.MainNewActivity;
import cn.cc1w.app.ui.ui.usercenter.PrivacyPolicyActivity;
import cn.cc1w.app.ui.ui.usercenter.UserAgreementActivity;
import cn.cc1w.app.ui.umeng.UmInitConfig;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.FileUtils;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.MyCountDownTimer;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;

/**
 * 开机页
 *
 * @author kpinfo
 */
public class SplashActivity extends CustomActivity implements EasyPermissions.PermissionCallbacks, ViewPager.OnPageChangeListener {
    private Unbinder unbinder;
    @BindView(R.id.relate_default_splash)
    RelativeLayout defaultLayout;
    @BindView(R.id.img_ad_splash)
    ImageView adImgSplash;
    @BindView(R.id.relate_guider_splash)
    RelativeLayout guiderLayout;
    @BindView(R.id.pager_bottom_splash)
    ViewPager guiderViewPager;
    @BindView(R.id.btn_start_splash)
    Button startBtn;
    @BindView(R.id.txt_timer_splash)
    TextView timerCountDownTxt;
    private boolean isSkipAble = true;
    private static final int CODE_REQUEST_PERMISSION = 1;
    private CountTimer countTimer;
    private UserGuiderAdapter adapter;
    private static final int INTERVAL = 1000;
    private AdvertisementEntity.AdvertisementBean advertisementBean = null;
    private boolean isFirstUserApp = true;
    private boolean isUseHasClickPermission = false;
    private AlertDialog policyDialog;
    private AlertDialog permissionDialog;
    private DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_splash);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        displayMetrics = getResources().getDisplayMetrics();
        EventBus.getDefault().register(this);
        SignCheckUtils signCheck = new SignCheckUtils(this, "32:4F:FF:90:C6:4F:78:EE:3B:2A:F3:38:FB:FA:63:50:AB:E9:81:65");
        SignCheckUtils signCheck2 = new SignCheckUtils(this, "EB:23:B5:8A:96:25:48:16:CC:78:F3:EB:57:D2:44:CF:4A:4D:77:8A");
        if (signCheck.check() || signCheck2.check()) {
            showPolicyDialog();
        } else {
            ToastUtil.showLongToast("签名异常，请前往应用市场下载最新版APP");
            finish();
        }
    }

    private void showPolicyDialog() {
        Constant.IS_POLICY_NEED_AUTHORIZATION = SharedPreferenceUtil.getPolicyState();
        if (Constant.IS_POLICY_NEED_AUTHORIZATION) {
            String content = "欢迎使用“开屏新闻”！我们非常重视您的个人信息和隐私保护。在您使用“开屏新闻”之前，请仔细阅读\n《隐私政策》和《用户协议》，我们将严格按照您同意的各项条款使用您的个人信息，以便为您提供更好的服务。";
            View view = LayoutInflater.from(SplashActivity.this).inflate(R.layout.dialog_policy, null);
            if (null != view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                policyDialog = builder.create();
                Window window = policyDialog.getWindow();
                policyDialog.show();

                if (null != window) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                    window.getDecorView().setPadding(0, 0, 0, 0);
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    int statusBarHeight = AppUtil.getStatusBarHeight(SplashActivity.this);
                    if (statusBarHeight >= 0) {
                        lp.height = displayMetrics.heightPixels - statusBarHeight;
                    } else {
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    }
                    window.setAttributes(lp);
                }
                TextView tv = view.findViewById(R.id.tv_content_policy_dialog);
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(content);
                int start = content.indexOf("《");
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ((TextView) widget).setHighlightColor(getResources().getColor(android.R.color.transparent, getTheme()));
                        } else {
                            ((TextView) widget).setHighlightColor(ContextCompat.getColor(SplashActivity.this, R.color.colorTransport));
                        }
                        IntentUtil.startActivity(SplashActivity.this, PrivacyPolicyActivity.class, null);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.colorPrimary));
                        ds.setUnderlineText(false);
                    }
                }, start, start + 6, 0);
                int end = content.lastIndexOf("《");
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ((TextView) widget).setHighlightColor(getResources().getColor(android.R.color.transparent, getTheme()));
                        } else {
                            ((TextView) widget).setHighlightColor(ContextCompat.getColor(SplashActivity.this, R.color.colorTransport));
                        }
                        IntentUtil.startActivity(SplashActivity.this, UserAgreementActivity.class, null);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.colorPrimary));
                        ds.setUnderlineText(false);
                    }
                }, end, end + 6, 0);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(ssb, TextView.BufferType.SPANNABLE);
                view.findViewById(R.id.btn_open_policy_dialog).setOnClickListener(v -> {
                    AppUtil.initPublicParams(this);
                    Constant.IS_POLICY_NEED_AUTHORIZATION = false;
                    policyDialog.dismiss();
                    SharedPreferenceUtil.setPolicyState(false);
                    AppContext.initShortVide();
                    if (getApplication() != null) {
                        AppContext.initLocationInfo(getApplication());
                    }
                    initUmengInfo();
                    AppContext.initX5WebView();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        showPermissionDialog();
                    } else {
                        initView();
                    }
                });
                view.findViewById(R.id.btn_dismiss_policy_dialog).setOnClickListener(v -> {
                    policyDialog.dismiss();
                    SharedPreferenceUtil.setPolicyState(true);
                    finish();
                    AppManager.appExit(this);
                });
                policyDialog.setCancelable(false);
                policyDialog.setContentView(view);
            }
        } else {
            AppUtil.initPublicParams(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showPermissionDialog();
            } else {
                initView();
            }
        }
    }

    private void initUmengInfo() {
        UmInitConfig umInitConfig = new UmInitConfig();
        umInitConfig.UMinit(getApplicationContext());
    }

    /**
     * 显示授权框
     */
    @SuppressLint("InflateParams")
    private void showPermissionDialog() {
        // 用户是否点击过 权限相关的按钮
        isUseHasClickPermission = SharedPreferenceUtil.getSplashPermissionStatus();
        LogUtil.d("isUseHasClickPermission = " + isUseHasClickPermission);
        if (isUseHasClickPermission) {
            initView();
        } else {
            String[] permissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
            if (!EasyPermissions.hasPermissions(this, permissionList)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_permission_apply, null);
                if (null != dialogView) {
                    builder.setView(dialogView);
                    builder.setCancelable(false);
                    permissionDialog = builder.create();
                    TextView confirmTv = dialogView.findViewById(R.id.txt_dialog_confirm);
                    TextView rejectTv = dialogView.findViewById(R.id.txt_dialog_reject);
                    // 进行授权
                    confirmTv.setOnClickListener(v -> {
                        EasyPermissions.requestPermissions(this, "开屏新闻需要获取SD卡读写权限用于读写缓存、获取地理位置权限用于首页的新闻推荐、发表信息时展示地理位置等", CODE_REQUEST_PERMISSION,
                                permissionList);
                        permissionDialog.dismiss();
                    });
                    // 拒绝权限
                    rejectTv.setOnClickListener(v -> {
                        initView();
                        permissionDialog.dismiss();
                    });
                    permissionDialog.show();
                }
            } else {
                initView();
            }
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        if (!isUseHasClickPermission) {
            SharedPreferenceUtil.setSplashPermissionStatus(true);
        }
        isFirstUserApp = SharedPreferenceUtil.getUserGuide();
        if (isFirstUserApp) {
            requestHomeTitleInfo();
            showIntroducePage();
            getGrayModeState(false);
        } else {
            requestHomeTitleInfo();
            getGrayModeState(true);
        }
    }

    /**
     * 显示介绍图
     */
    @SuppressLint("InflateParams")
    private void showIntroducePage() {
        defaultLayout.setVisibility(View.GONE);
        guiderLayout.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.guider_one, null);
        View view2 = inflater.inflate(R.layout.guider_two, null);
        View view3 = inflater.inflate(R.layout.guider_three, null);
        View view4 = inflater.inflate(R.layout.guider_four, null);
        View view5 = inflater.inflate(R.layout.guider_five, null);
        List<View> list = new ArrayList<>();
        list.add(view1);
        list.add(view2);
        list.add(view3);
        list.add(view4);
        list.add(view5);
        adapter = new UserGuiderAdapter(list);
        guiderViewPager.setAdapter(adapter);
        guiderViewPager.addOnPageChangeListener(this);
    }

    /**
     * 点击广告页的逻辑
     *
     * @param item 广告实体类
     */
    private void doAdvertisementAction(AdvertisementEntity.AdvertisementBean item) {
        if (null != adImgSplash) {
            adImgSplash.setClickable(false);
        }
        if (null != countTimer) {
            countTimer.cancel();
            countTimer = null;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        IntentUtil.gotoActivity(this, MainNewActivity.class, bundle);
        finish();
    }

    /**
     * 跳过倒计时
     */
    private void skipTimeCountDown() {
        if (isSkipAble) {
            isSkipAble = false;
            if (null != timerCountDownTxt) {
                timerCountDownTxt.setClickable(false);
            }
            if (null != countTimer) {
                countTimer.cancel();
                countTimer = null;
            }
            gotoMain();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        initView();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        initView();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == adapter.getCount() - 1) {
            startBtn.setVisibility(View.VISIBLE);
        } else {
            if (startBtn.getVisibility() == View.VISIBLE) {
                startBtn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 跳转到首页
     */
    private void gotoMain() {
        LogUtil.e("gotoMain " + System.currentTimeMillis());
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.CONFIG_APP)
                    .asResponse(AppConfigEntity.AppConfigDetail.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        SharedPreferenceUtil.setAppConfigInfo(data);
                        justGoMain();
                        initParams();
                    }, (OnError) error -> {
                        initParams();
                        justGoMain();
                    });
        } else {
            initParams();
            justGoMain();
        }
    }

    private void initParams() {
        AppConfigEntity.AppConfigDetail it = SharedPreferenceUtil.getAppConfigInfo();
        if (it != null) {
            if (it.getCloud_socket_url() != null && !TextUtils.isEmpty(it.getCloud_socket_url())) {
                Constant.HOST_SOCKET = it.getCloud_socket_url();
            }
            if (it.getCloud_resource_url() != null && !TextUtils.isEmpty(it.getCloud_resource_url())) {
                Constant.DOMAIN_FILE_UPLOAD_QUKAN = it.getCloud_resource_url();
            }
        }
    }

    private void justGoMain() {
        LogUtil.e("justGoMain ---- >>> " + System.currentTimeMillis());
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainNewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        AppManager.finishActivity(this);
        finish();
    }

    /**
     * 保存数据到缓存
     *
     * @param result Json数据
     */
    private void saveTitle2Cache(String result) {
        try {
            if (getExternalCacheDir() != null) {
                String title = "homeTitle";
                File file = new File(getExternalCacheDir(), File.separator.concat(title).concat(Constant.SUFFIX_CACHE));
                FileUtils.saveContent(result, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取首页的title信息
     */
    private void requestHomeTitleInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.CHANNEL_INDEX)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(HomeChannelEntity.ItemHomeChannelEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (data != null && !data.isEmpty()) {
                            saveTitle2Cache(new Gson().toJson(data));
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 获取黑白模式信息
     */
    private void getGrayModeState(boolean isNeedLoadAd) {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.MOURN)
                    .asResponse(AppModeEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        LogUtil.d("APP 模式 = " + data.toString());
                        if (!isFinishing()) {
                            Constant.IS_SHOW_GARY_MODE = data.isMourning();
                            Constant.IS_SHOW_RED_MODE = data.isRed();
                            SharedPreferenceUtil.setAppModeInfo(data);
                            if (data.isMourning()) {
                                setPageGrayModeWithHardWareSpeed();
                            }
                            if (isNeedLoadAd) {
                                requestAdvertise();
                            }
                        }
                    }, (OnError) error -> {
                        if (!isFinishing() && !isFirstUserApp) {
                            Constant.IS_SHOW_GARY_MODE = false;
                            Constant.IS_SHOW_RED_MODE = false;
                            gotoMain();
                        }
                    });
        } else {
            gotoMain();
        }
    }

    /**
     * 获取 广告图片
     */
    private void requestAdvertise() {
        RxHttp.get(Constant.ADVERTISEMENT_START_UP)
                .asResponse(AdvertisementEntity.AdvertisementBean.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    advertisementBean = data;
                    int advertisementTime = data.getTime() * INTERVAL;
                    // 有倒计时 而且 有 Banner图片
                    if (advertisementTime > 0 && !TextUtils.isEmpty(data.getPic_path())) {
                        Glide.with(SplashActivity.this)
                                .load(advertisementBean.getPic_path())
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .error(R.mipmap.bg_splash)
                                .placeholder(R.mipmap.bg_splash)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        if (!isFinishing()) {
                                            adImgSplash.setImageDrawable(resource);
                                            LogUtil.d("w1 = " + resource.getIntrinsicWidth() + "  h1 = " + resource.getIntrinsicHeight() + "  w2 = " + displayMetrics.widthPixels + "  h2 = " + displayMetrics.heightPixels);
                                            timerCountDownTxt.setVisibility(View.VISIBLE);
                                            defaultLayout.setVisibility(View.GONE);
                                            adImgSplash.setVisibility(View.VISIBLE);
                                            countTimer = new CountTimer(advertisementTime, INTERVAL);
                                            WeakReference<CountTimer> weakTimerCountDowner = new WeakReference<>(countTimer);
                                            if (null != weakTimerCountDowner.get()) {
                                                weakTimerCountDowner.get().start();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        if (!isFinishing()) {
                                            gotoMain();
                                        }
                                    }
                                });
                    } else {
                        gotoMain();
                    }
                }, (OnError) error -> {
                    if (!isFinishing()) {
                        gotoMain();
                    }
                });
    }

    /**
     * 倒计时
     */
    class CountTimer extends MyCountDownTimer {
        CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int time = (int) (millisUntilFinished / INTERVAL) + 1;
            if (null != timerCountDownTxt) {
                String str = getResources().getString(R.string.time_down);
                timerCountDownTxt.setText(String.format(str, time));
            }
        }

        @Override
        public void onFinish() {
            gotoMain();
        }
    }

    @OnClick({R.id.btn_start_splash, R.id.txt_timer_splash, R.id.img_ad_splash})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_start_splash) {
            gotoMain();
        } else if (id == R.id.txt_timer_splash) {
            skipTimeCountDown();
        } else if (id == R.id.img_ad_splash) {
            if (null != advertisementBean && !TextUtils.isEmpty(advertisementBean.getIn_type())) {
                doAdvertisementAction(advertisementBean);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals(Constant.TAG_SPLASH_FINISH, message.getLabel())) {
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        if (null != countTimer) {
            countTimer.cancel();
            countTimer = null;
        }
        if (policyDialog != null) {
            policyDialog.dismiss();
        }
        if (permissionDialog != null) {
            permissionDialog.dismiss();
        }
        EventBus.getDefault().unregister(this);
        if (null != unbinder) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}