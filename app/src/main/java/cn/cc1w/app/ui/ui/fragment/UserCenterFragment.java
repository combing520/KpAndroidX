package cn.cc1w.app.ui.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.SignListAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppUpdateEntity;
import cn.cc1w.app.ui.utils.DownloadUtil;
import cn.cc1w.app.ui.widget.dialog.CustomProgressDialog;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.SignInfoEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.entity.WeatherEntity;
import cn.cc1w.app.ui.ui.home.apps.FavoriteActivity;
import cn.cc1w.app.ui.ui.usercenter.PrivacyPolicyActivity;
import cn.cc1w.app.ui.ui.usercenter.PromotionCodeActivity;
import cn.cc1w.app.ui.ui.usercenter.UserAgreementActivity;
import cn.cc1w.app.ui.ui.usercenter.activity.ActiveActivity;
import cn.cc1w.app.ui.ui.usercenter.collection.CollectionActivity;
import cn.cc1w.app.ui.ui.usercenter.history.HistoryActivity;
import cn.cc1w.app.ui.ui.usercenter.integral.IntegralActivity;
import cn.cc1w.app.ui.ui.usercenter.message.MessageAndCommentActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.AboutUsActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.AccountManagerActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.SettingActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.WalletActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.kpinfo.log.KpLog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.RxHttp;

/**
 * 我的
 *
 * @author kpinfo
 */
public class UserCenterFragment extends Fragment {
    private Unbinder mBinder;
    @BindView(R.id.img_avatar_userCenter)
    RoundAngleImageView avatarImg;
    @BindView(R.id.img_avatar_big_usercenter)
    ImageView userBigAvatarImg;
    @BindView(R.id.list_center_userCenter)
    RecyclerView signList;
    @BindView(R.id.txt_integral_userCenter)
    TextView integralTv;
    @BindView(R.id.nickname_userCenter)
    TextView nickNameTv;
    @BindView(R.id.temperature_userCenter)
    TextView temperatureTv;
    @BindView(R.id.weather_userCenter)
    TextView weatherTv;
    @BindView(R.id.location_userCenter)
    TextView locationTv;
    @BindView(R.id.btn_sign_userCenter)
    TextView signBtn;
    @BindView(R.id.ll_sign_userCenter)
    LinearLayout signLayout;
    private LoadingDialog loading;
    private SignListAdapter signListAdapter;
    private Drawable signedBtnDrawable;
    private Drawable unSignBtnDrawable;
    private View mDecorView;
    private Context mContext;
    private CustomProgressDialog downloadDialog;

    public UserCenterFragment() {
    }

    public static UserCenterFragment newInstance() {
        UserCenterFragment fragment = new UserCenterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mDecorView == null) {
            mDecorView = inflater.inflate(R.layout.fragment_user_center, container, false);
        }
        mBinder = ButterKnife.bind(this, mDecorView);
        return mDecorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        initDialog();
        initLocationInfo();
        initSignBtnInfo();
        setSignBtnInfo();
        initList();
        initLoading();
        initUserInfo();
        getWeather();
    }

    /**
     * 初始化 dialog
     */
    private void initDialog() {
        downloadDialog = new CustomProgressDialog(mContext, "软件下载中...");
        downloadDialog.setCancelable(false);
    }

    /**
     * 定义 定位信息
     */
    private void initLocationInfo() {
        if (!TextUtils.isEmpty(Constant.CW_AREA) && locationTv != null) {
            locationTv.setVisibility(View.VISIBLE);
            locationTv.setText(Constant.CW_AREA);
        }
    }

    /**
     * 初始化签到按钮的状态
     */
    private void initSignBtnInfo() {
        signedBtnDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_container_red_big);
        unSignBtnDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_container_gray_big);
    }

    /**
     * 初始化list
     */
    private void initList() {
        signListAdapter = new SignListAdapter(mContext);
        GridLayoutManager manager = new GridLayoutManager(mContext, 6);
        signList.setLayoutManager(manager);
        signList.setAdapter(signListAdapter);
    }

    /**
     * 设置 签到按钮 样式
     */
    private void setSignBtnInfo() {
        setSignBtnClickable(true);
    }

    /**
     * 设置签到按钮是否可点击
     *
     * @param clickable 是否可点击
     */
    private void setSignBtnClickable(boolean clickable) {
        if (clickable) {
            signBtn.setBackground(signedBtnDrawable);
            signBtn.setTextColor(Color.WHITE);
        } else {
            signBtn.setBackground(unSignBtnDrawable);
            signBtn.setTextColor(Color.GRAY);
        }
        signBtn.setClickable(clickable);
    }

    /**
     * 显示 浏览记录
     */
    private void showUserBrowseRecord() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(mContext, HistoryActivity.class);
        } else {
            IntentUtil.startActivity(mContext, LoginActivity.class);
        }
    }

    /**
     * 查看 用户收藏
     */
    private void showUserCollection() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(mContext, CollectionActivity.class);
        } else {
            IntentUtil.startActivity(mContext, LoginActivity.class);
        }
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(mContext);
        if (null != loading) {
            loading.setLoadingText(null);
        }
    }

    /**
     * 获取城市的天气情况
     */
    private void getWeather() {
        if (NetUtil.isNetworkConnected(mContext)) {
            RxHttp.get(Constant.WEATHER)
                    .add("cw_city", TextUtils.isEmpty(Constant.CW_CITY) ? "昆明市" : Constant.CW_CITY)
                    .asResponse(WeatherEntity.WeatherInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(weather -> {
                        if (null != weather && temperatureTv != null && weatherTv != null) {
                            temperatureTv.setText(TextUtils.isEmpty(weather.getTemperature()) ? "" : weather.getTemperature());
                            weatherTv.setText(TextUtils.isEmpty(weather.getWeather()) ? "" : weather.getWeather());
                        }
                    }, (OnError) error -> LogUtil.e("天气信息获取失败 " + error.getErrorMsg()));
        }
    }

    /**
     * 获取签到信息
     */
    private void getSignInfo() {
        RxHttp.postJson(Constant.INFO_SIGN_INTEGRAL)
                .asResponse(SignInfoEntity.SignInfo.class)
                .as(RxLife.asOnMain(this))
                .subscribe(signInfo -> {
                    if (null != signInfo) {
                        signLayout.setVisibility(View.VISIBLE);
                        if (signInfo.isDay_signin()) {
                            setSignBtnClickable(false);
                            signBtn.setText("已签到");
                        }
                        // 设置签到信息
                        List<SignInfoEntity.SignInfo.SigninBean> signList = signInfo.getSignin();
                        signListAdapter.setData(signList);
                        // 设置到第几天签到
                        if (!TextUtils.isEmpty(signInfo.getSignin_number()) && Integer.parseInt(signInfo.getSignin_number()) > 0) {
                            int daySign = Integer.parseInt(signInfo.getSignin_number());
                            signListAdapter.setSignPos(daySign);
                        }
                    }
                }, (OnError) error -> {
                    if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOutWithoutMessage();
                        IntentUtil.startActivity(mContext, LoginActivity.class);
                    }
                });
    }

    /**
     * 检查更新信息
     */
    private void checkUpdateInfo() {
        if (NetUtil.isNetworkConnected(mContext)) {
            if (loading != null && !loading.isShow()) {
                loading.show();
            }
            RxHttp.postJson(Constant.UPDATE_APP)
                    .add("type", Constant.CW_OS)
                    .asResponse(AppUpdateEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(appUpdate -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (appUpdate != null) {
                            try {
                                double currentAppVersion = TextUtils.isEmpty(AppUtil.getVersionName(mContext)) ? 0 : Double.parseDouble(AppUtil.getVersionName(mContext));
                                double onlineAppVersion = TextUtils.isEmpty(appUpdate.getVersion()) ? 0 : Double.parseDouble(appUpdate.getVersion());
                                if (onlineAppVersion > currentAppVersion) {
                                    showUpdateDialog(appUpdate.getUrl(), appUpdate.getRemark());
                                } else {
                                    ToastUtil.showShortToast(getString(R.string.str_version_lastest));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            LogUtil.e("checkUpdateInfo onObjectSuccess !!!!  ++++ " + appUpdate);
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    private void showUpdateDialog(final String appDownloadUrl, String remark) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("更新提示");
        builder.setMessage("检测到新版本，是否下载更新?\n" + "新版本更新描述:\n" + remark);
        builder.setPositiveButton("立即更新", (dialog, which) -> {
            LogUtil.e("进行下载");
            doAppDownload(appDownloadUrl);
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        });
        builder.setNegativeButton("取消更新", (dialog, which) -> {
            LogUtil.e("取消");
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        });
        builder.create().show();
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
                data = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileprovider", file);
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
        if (NetUtil.isNetworkConnected(mContext)) {
            if (null != downloadDialog) {
                downloadDialog.show();
            }
            String dir = DownloadUtil.getDownloadDir(mContext);
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
                    }) //指定主线程回调
                    .as(RxLife.as(this))
                    .subscribe(s -> { //s为String类型
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
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 进行签到
     */
    private void doSign() {
        if (NetUtil.isNetworkConnected(mContext)) {
            signBtn.setClickable(false);
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.SIGN)
                    .asResponse(JsonObject.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        getUserInfo();
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (signBtn != null) {
                            signBtn.setClickable(true);
                        }
                        int primaryPos = signListAdapter.getSignPos();
                        signListAdapter.setSignPos(primaryPos + 1);
                        setSignBtnClickable(false);
                        SignInfoEntity.SignInfo.SigninBean integralEntity = signListAdapter.getItem(primaryPos + 1);
                        if (null != integralEntity && signBtn != null) {
                            signBtn.setText("已签到");
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (signBtn != null) {
                            signBtn.setClickable(true);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(mContext, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    private void initUserInfo() {
        getUserInfo();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION) && NetUtil.isNetworkConnected(mContext)) {
            RxHttp.postJson(Constant.USER_INFO)
                    .asResponse(UserInfoResultEntity.UserInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(userInfo -> {
                        getSignInfo();
                        if (null != userInfo && nickNameTv != null && avatarImg != null) {
                            SharedPreferenceUtil.saveUserInfo(userInfo);
                            AppUtil.loadAvatarImg(userInfo.getHeadpic(), avatarImg);
                            AppUtil.loadImgWithoutPlaceholder(userInfo.getHeadpic(), userBigAvatarImg);
                            nickNameTv.setText(userInfo.getNickname());
                            if (!TextUtils.isEmpty(userInfo.getCredits())) {
                                integralTv.setVisibility(View.VISIBLE);
                                integralTv.setText(userInfo.getCredits());
                            } else {
                                integralTv.setVisibility(View.GONE);
                            }
                        }
                    }, (OnError) error -> {
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOutWithoutMessage();
                            IntentUtil.startActivity(mContext, LoginActivity.class);
                            if (avatarImg != null) {
                                AppUtil.loadRes(R.mipmap.ic_avatar_default, avatarImg);
                            }
                            if (nickNameTv != null) {
                                nickNameTv.setText(R.string.str_login_hint);
                            }
                            if (integralTv.getVisibility() == View.VISIBLE) {
                                integralTv.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void getUserInfoWithOutSignInfo() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION) && NetUtil.isNetworkConnected(mContext)) {
            RxHttp.postJson(Constant.USER_INFO)
                    .asResponse(UserInfoResultEntity.UserInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(userInfo -> {
                        if (null != userInfo && nickNameTv != null && avatarImg != null) {
                            SharedPreferenceUtil.saveUserInfo(userInfo);
                            AppUtil.loadAvatarImg(userInfo.getHeadpic(), avatarImg);
                            AppUtil.loadImgWithoutPlaceholder(userInfo.getHeadpic(), userBigAvatarImg);
                            nickNameTv.setText(userInfo.getNickname());
                            if (!TextUtils.isEmpty(userInfo.getCredits())) {
                                integralTv.setVisibility(View.VISIBLE);
                                integralTv.setText(userInfo.getCredits());
                            } else {
                                integralTv.setVisibility(View.GONE);
                            }
                        }
                    }, (OnError) error -> {
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(mContext, LoginActivity.class);
                            AppUtil.doUserLogOutWithoutMessage();
                            if (avatarImg != null) {
                                AppUtil.loadRes(R.mipmap.ic_avatar_default, avatarImg);
                            }
                            if (nickNameTv != null) {
                                nickNameTv.setText(R.string.str_login_hint);
                            }
                            if (integralTv.getVisibility() == View.VISIBLE) {
                                integralTv.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    @OnClick({
            R.id.ll_setting_userCenter, R.id.ll_message_userCenter,
            R.id.ll_collection_userCenter, R.id.ll_activity_userCenter,
            R.id.ll_history_userCenter, R.id.ll_favorite_userCenter,
            R.id.txt_money_userCenter, R.id.txt_integral_userCenter,
            R.id.container_avatar_center_userCenter,
            R.id.ll_about_userCenter, R.id.ll_integral_usercenter,
            R.id.ll_about_usercenter, R.id.ll_update_usercenter,
            R.id.tv_agreement_fast, R.id.tv_policy_fast,
            R.id.btn_sign_userCenter, R.id.ll_promotion_userCenter
    })
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_setting_userCenter) {
            IntentUtil.startActivity(mContext, SettingActivity.class, null);
        } else if (id == R.id.txt_money_userCenter) {
            IntentUtil.startActivity(mContext, WalletActivity.class, null);
        } else if (id == R.id.txt_integral_userCenter) {
            IntentUtil.startActivity(mContext, IntegralActivity.class, null);
        } else if (id == R.id.ll_message_userCenter) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(mContext, MessageAndCommentActivity.class, null);
            } else {
                IntentUtil.startActivity(mContext, LoginActivity.class, null);
            }
        } else if (id == R.id.ll_collection_userCenter) {
            showUserCollection();
        } else if (id == R.id.ll_activity_userCenter) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(mContext, ActiveActivity.class);
            } else {
                IntentUtil.startActivity(mContext, LoginActivity.class);
            }
        } else if (id == R.id.ll_history_userCenter) {
            showUserBrowseRecord();
        } else if (id == R.id.ll_favorite_userCenter) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(mContext, FavoriteActivity.class);
            } else {
                IntentUtil.startActivity(mContext, LoginActivity.class);
            }
        } else if (id == R.id.container_avatar_center_userCenter) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(mContext, LoginActivity.class);
            } else {
                IntentUtil.startActivity(mContext, AccountManagerActivity.class);
            }
        } else if (id == R.id.ll_about_userCenter) {
            IntentUtil.startActivity(mContext, AboutUsActivity.class);
        } else if (id == R.id.ll_integral_usercenter) {
            IntentUtil.startActivity(mContext, IntegralActivity.class);
        } else if (id == R.id.ll_update_usercenter) {
            checkUpdateInfo();
        } else if (id == R.id.tv_agreement_fast) {
            IntentUtil.startActivity(mContext, UserAgreementActivity.class, null);
        } else if (id == R.id.tv_policy_fast) {
            IntentUtil.startActivity(mContext, PrivacyPolicyActivity.class, null);
        } else if (id == R.id.btn_sign_userCenter) {
            doSign();
        } else if (id == R.id.ll_promotion_userCenter) {
            showUserPromotionCode();
        }
    }

    private void showUserPromotionCode() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(mContext, LoginActivity.class);
        } else {
            UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
            if (null != userInfo) {
                LogUtil.e("不为空 " + " mobile == null ? " + TextUtils.isEmpty(userInfo.getMobile()));
                if (TextUtils.isEmpty(userInfo.getMobile())) { // 没有绑定过手机号码
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.LOGIN_WEB);
                    IntentUtil.startActivity(mContext, UpdateMobileActivity.class, bundle);
                } else {
                    IntentUtil.startActivity(mContext, PromotionCodeActivity.class);
                }
            } else {
                LogUtil.e("为空");
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.LOGIN_WEB);
                IntentUtil.startActivity(mContext, LoginActivity.class, bundle);
            }
        }
    }

    /**
     * 接收EventBus
     *
     * @param message EventBus 接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                if (TextUtils.equals(Constant.USERINFO_UPDATE, message.getLabel())) {
                    // 拉去用户信息
                    getUserInfo();
                } else if (TextUtils.equals("updateMobile", message.getLabel())) {
                    getUserInfo();
                }
                // 更换名称
                else if (TextUtils.equals(Constant.USERNAME_UPDATE, message.getLabel())) {
                    LogUtil.e("content = " + message.getContent());
                    getUserInfoWithOutSignInfo();
                }
                //更新头像
                else if (TextUtils.equals("updateAvatar", message.getLabel())) {
                    getUserInfoWithOutSignInfo();
                } else if (TextUtils.equals("integral", message.getLabel())) {
                    LogUtil.e("积分  = " + message.getContent());
                    getUserInfoWithOutSignInfo();
                } else if (TextUtils.equals("updateUserIntegral", message.getLabel())) {
                    LogUtil.e("积分  = " + message.getContent());
                    getUserInfoWithOutSignInfo();
                }
                // 关闭当前页码
                else if (TextUtils.equals("close", message.getLabel()) || TextUtils.equals(Constant.TAG_LOGOUT, message.getLabel())) {
                    AppUtil.loadRes(R.mipmap.ic_avatar_default, avatarImg);
                    nickNameTv.setText(R.string.str_login_hint);
                    if (integralTv.getVisibility() == View.VISIBLE) {
                        integralTv.setVisibility(View.GONE);
                    }
                    if (signLayout != null) {
                        signLayout.setVisibility(View.GONE);
                    }
                } else if (TextUtils.equals("updateAddress", message.getLabel())) {
                    initLocationInfo();
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(mContext, getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        KpLog.getInstance().onAppViewScreenOut(mContext, getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (null != loading) {
            loading.close();
            loading = null;
        }
        mBinder.unbind();
    }
}