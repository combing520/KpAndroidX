package cn.cc1w.app.ui.ui.usercenter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.xutils.x;

import java.io.File;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.SignListAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppUpdateEntity;
import cn.cc1w.app.ui.utils.DownloadUtil;
import cn.cc1w.app.ui.widget.dialog.CustomProgressDialog;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.SignInfoEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.entity.WeatherEntity;
import cn.cc1w.app.ui.ui.home.apps.FavoriteActivity;
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
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.RxHttp;

/**
 * ????????????
 *
 * @author kpinfo
 */
public class UserCenterActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.img_avatar_userCenter)
    RoundAngleImageView avatarImg; // ????????????
    @BindView(R.id.img_avatar_big_usercenter)
    ImageView userBigAvatarImg;
    @BindView(R.id.list_center_userCenter)
    RecyclerView signList; // ????????????
    @BindView(R.id.txt_money_userCenter)
    TextView moneyTv; //??????
    @BindView(R.id.txt_integral_userCenter)
    TextView integralTv; // ??????
    @BindView(R.id.nickname_userCenter)
    TextView nickNameTv;// ??????
    @BindView(R.id.temperature_userCenter)
    TextView temperatureTv;// ??????
    @BindView(R.id.weather_userCenter)
    TextView weatherTv;// ??????
    @BindView(R.id.location_userCenter)
    TextView locationTv; // ??????
    @BindView(R.id.btn_sign_userCenter)
    TextView signBtn; // ????????????
    @BindView(R.id.ll_sign_userCenter)
    LinearLayout signLayout;
    private LoadingDialog loading;
    private SignListAdapter signListAdapter;
    private Drawable signedBtnDrawable; // ????????? Btn drawable
    private Drawable unSignBtnDrawable; // ??????????????? Btn drawable
    private CustomProgressDialog downloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        init();
    }

    /**
     * ?????????
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initDialog();
        initLocationInfo();
        initSignBtnInfo();
        setSignBtnInfo();
        initList();
        initLoading();
        getUserInfo();
        getWeather();
        getSignInfo();
    }

    /**
     * ????????? dialog
     */
    private void initDialog() {
        downloadDialog = new CustomProgressDialog(this, "???????????????...");
        downloadDialog.setCancelable(false);
    }

    /**
     * ?????? ????????????
     */
    private void initLocationInfo() {
        locationTv.setText(TextUtils.isEmpty(Constant.CW_AREA) ? "" : Constant.CW_AREA);
    }

    /**
     * ??????????????????????????????
     */
    private void initSignBtnInfo() {
        signedBtnDrawable = ContextCompat.getDrawable(this, R.drawable.bg_container_red_big);
        unSignBtnDrawable = ContextCompat.getDrawable(this, R.drawable.bg_container_gray_big);
    }

    /**
     * ?????????list
     */
    private void initList() {
        signListAdapter = new SignListAdapter(this);
        GridLayoutManager manager = new GridLayoutManager(this, 6);
        signList.setLayoutManager(manager);
        signList.setAdapter(signListAdapter);
    }

    /**
     * ?????? ???????????? ??????
     */
    private void setSignBtnInfo() {
        setSignBtnClickable(true);
    }

    /**
     * ?????????????????????????????????
     *
     * @param clickable ???????????????
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
     * ?????? ????????????
     */
    private void showUserBrowseRecord() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, HistoryActivity.class);
        } else {
            IntentUtil.startActivity(this, LoginActivity.class);
        }
    }

    /**
     * ?????? ????????????
     */
    private void showUserCollection() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, CollectionActivity.class);
        } else {
            IntentUtil.startActivity(this, LoginActivity.class);
        }
    }

    /**
     * ????????? loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
        if (null != loading) {
            loading.setLoadingText(null);
        }
    }

    /**
     * ???????????????????????????
     */
    private void getWeather() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.WEATHER).add("cw_city", TextUtils.isEmpty(Constant.CW_CITY) ? "?????????" : Constant.CW_CITY)
                    .asResponse(WeatherEntity.WeatherInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(weather -> {
                        if (!isFinishing() && weather != null) {
                            temperatureTv.setText(TextUtils.isEmpty(weather.getTemperature()) ? "" : weather.getTemperature());
                            weatherTv.setText(TextUtils.isEmpty(weather.getWeather()) ? "" : weather.getWeather());
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * ??????????????????
     */
    private void getSignInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.INFO_SIGN_INTEGRAL)
                    .asResponse(SignInfoEntity.SignInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(signInfo -> {
                        if (!isFinishing() && signInfo != null) {
                            // ??????????????????
                            if (signInfo.isDay_signin()) { // ????????????????????????
                                setSignBtnClickable(false);
                                signBtn.setText("?????????");
                            }

                            // ??????????????????
                            List<SignInfoEntity.SignInfo.SigninBean> signList = signInfo.getSignin();
                            signListAdapter.setData(signList);

                            // ????????????????????????
                            if (!TextUtils.isEmpty(signInfo.getSignin_number()) && Integer.parseInt(signInfo.getSignin_number()) > 0) {
                                int day_sign = Integer.parseInt(signInfo.getSignin_number());
                                signListAdapter.setSignPos(day_sign);
                            }
                        }
                    }, (OnError) error -> {
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        }
    }

    /**
     * ??????????????????
     */
    private void checkUpdateInfo() {
        if (NetUtil.isNetworkConnected(this)) {
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
                        if (!isFinishing() && appUpdate != null) {
                            try {
                                double currentAppVersion = TextUtils.isEmpty(AppUtil.getVersionName(this)) ? 0 : Double.parseDouble(AppUtil.getVersionName(this));
                                double onlineAppVersion = TextUtils.isEmpty(appUpdate.getVersion()) ? 0 : Double.parseDouble(appUpdate.getVersion());
                                if (onlineAppVersion > currentAppVersion) {  // ???????????? ?????? ????????????
                                    //  ?????????????????????
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????");
        builder.setMessage("????????????????????????????????????????\n" + "?????????????????????:\n" + remark);
        builder.setPositiveButton("??????", (dialog, which) -> {
            LogUtil.e("????????????");
            if (!isFinishing()) {
                doAppDownload(appDownloadUrl);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("??????", (dialog, which) -> {
            LogUtil.e("??????");
            dialog.dismiss();
        });
        builder.create().show();
    }

    /**
     * ?????? APK
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
     * ??????????????????apk
     */
    private void doAppDownload(String appUrl) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != downloadDialog) {
                downloadDialog.show();
            }
            String dir = DownloadUtil.getDownloadDir(this);
            String destPath = dir + "/" + System.currentTimeMillis() + ".apk";
            RxHttp.get(appUrl)
                    .asAppendDownload(destPath, AndroidSchedulers.mainThread(), progress -> {
                        //??????????????????,0-100???????????????????????????????????????
                        int currentProgress = progress.getProgress(); //???????????? 0-100
                        long currentSize = progress.getCurrentSize(); //??????????????????????????????
                        long totalSize = progress.getTotalSize();     //???????????????????????????
                        LogUtil.e("currentProgress = " + currentProgress + " currentSize = " + currentSize + " totalSize=" + totalSize);
                        if (null != downloadDialog) {
                            downloadDialog.setProgress(currentProgress);
                        }
                    }) //?????????????????????
                    .as(RxLife.as(this))
                    .subscribe(s -> { //s???String??????
                        LogUtil.e("???????????? s = " + s);
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
     * ????????????
     */
    private void doSign() {
        if (NetUtil.isNetworkConnected(this)) {
            signBtn.setClickable(false);
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.SIGN)
                    .asResponse(JsonObject.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (signBtn != null) {
                                signBtn.setClickable(true);
                            }
                            int primaryPos = signListAdapter.getSignPos();
                            signListAdapter.setSignPos(primaryPos + 1);
                            setSignBtnClickable(false);
                            SignInfoEntity.SignInfo.SigninBean integralEntity = signListAdapter.getItem(primaryPos + 1);
                            if (null != integralEntity && signBtn != null) {
                                signBtn.setText("?????????");
                            }
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
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * ??????????????????
     */
    private void getUserInfo() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION) && NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.USER_INFO)
                    .asResponse(UserInfoResultEntity.UserInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(userInfo -> {
                        getSignInfo();
                        if (null != userInfo && nickNameTv != null && avatarImg != null) {
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
                    }, (OnError) error -> ToastUtil.showShortToast(error.getErrorMsg()));
        }
    }

    @OnClick({R.id.img_back_userCenter,
            R.id.ll_setting_userCenter,
            R.id.ll_message_userCenter,
            R.id.ll_collection_userCenter,
            R.id.ll_activity_userCenter,
            R.id.ll_history_userCenter,
            R.id.ll_favorite_userCenter,
            R.id.txt_money_userCenter,
            R.id.txt_integral_userCenter,
            R.id.container_avatar_center_userCenter,
            R.id.ll_about_userCenter,
            R.id.ll_integral_usercenter,
            R.id.ll_about_usercenter,
            R.id.ll_update_usercenter,
            R.id.tv_agreement_fast,
            R.id.tv_policy_fast,
            R.id.btn_sign_userCenter,
            R.id.ll_promotion_userCenter
    })
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int id = view.getId();
        if (id == R.id.img_back_userCenter) {
            finish();
        } else if (id == R.id.ll_setting_userCenter) {
            intent.setClass(this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.txt_money_userCenter) {
            intent.setClass(this, WalletActivity.class);
            startActivity(intent);
        } else if (id == R.id.txt_integral_userCenter) {
            intent.setClass(this, IntegralActivity.class);
            startActivity(intent);
        } else if (id == R.id.ll_message_userCenter) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, MessageAndCommentActivity.class, null);
            } else {
                IntentUtil.startActivity(this, LoginActivity.class, null);
            }
        } else if (id == R.id.ll_collection_userCenter) {
            showUserCollection();
        } else if (id == R.id.ll_activity_userCenter) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, ActiveActivity.class);
            } else {
                IntentUtil.startActivity(this, LoginActivity.class);
            }
        } else if (id == R.id.ll_history_userCenter) {
            showUserBrowseRecord();
        } else if (id == R.id.ll_favorite_userCenter) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, FavoriteActivity.class);
            } else {
                IntentUtil.startActivity(this, LoginActivity.class);
            }
        } else if (id == R.id.container_avatar_center_userCenter) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, LoginActivity.class);
            } else {
                IntentUtil.startActivity(this, AccountManagerActivity.class);
            }
        } else if (id == R.id.ll_about_userCenter) {
            IntentUtil.startActivity(this, AboutUsActivity.class);
        } else if (id == R.id.ll_integral_usercenter) {
            IntentUtil.startActivity(this, IntegralActivity.class);
        } else if (id == R.id.ll_update_usercenter) {
            checkUpdateInfo();
        } else if (id == R.id.tv_agreement_fast) {
            IntentUtil.startActivity(this, UserAgreementActivity.class, null);
        } else if (id == R.id.tv_policy_fast) {
            IntentUtil.startActivity(this, PrivacyPolicyActivity.class, null);
        } else if (id == R.id.btn_sign_userCenter) {
            doSign();
        } else if (id == R.id.ll_promotion_userCenter) {
            showUserPromotionCode();
        }
    }

    private void showUserPromotionCode() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, LoginActivity.class);
        } else {
            UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
            if (null != userInfo) {
                LogUtil.e("????????? " + " mobile == null ? " + TextUtils.isEmpty(userInfo.getMobile()));
                if (TextUtils.isEmpty(userInfo.getMobile())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.LOGIN_WEB);
                    IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
                } else {
                    IntentUtil.startActivity(this, PromotionCodeActivity.class);
                }
            } else {
                LogUtil.e("??????");
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.LOGIN_WEB);
                IntentUtil.startActivity(this, LoginActivity.class, bundle);
            }
        }
    }

    /**
     * ??????EventBus
     *
     * @param message EventBus ???????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                //?????????????????????
                if (TextUtils.equals("updateName", message.getLabel())) {
                    nickNameTv.setText(message.getContent());
                }
                //????????????
                else if (TextUtils.equals("updateAvatar", message.getLabel())) {
                    x.image().bind(avatarImg, message.getContent());
                    x.image().bind(userBigAvatarImg, message.getContent());
                } else if (TextUtils.equals("updateUserInfo", message.getLabel())) {
                    getUserInfo();
                } else if (TextUtils.equals("close", message.getLabel()) || TextUtils.equals(Constant.TAG_LOGOUT, message.getLabel())) {
                    finish();
                } else if (TextUtils.equals("updateUserIntegral", message.getLabel())) {
                    integralTv.setVisibility(View.VISIBLE);
                    integralTv.setText(message.getContent());
                    getSignInfo();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != loading) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}