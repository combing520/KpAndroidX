package cn.cc1w.app.ui.ui.usercenter.usercenter.login.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import org.jetbrains.annotations.NotNull;
import java.util.Map;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.ui.home.upload.PaikewUploadActivity;
import cn.cc1w.app.ui.ui.usercenter.PrivacyPolicyActivity;
import cn.cc1w.app.ui.ui.usercenter.UserAgreementActivity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.forgetpwd.ForgetPwdActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.register.RegisterActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.KeybordUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;
import rxhttp.RxHttpJsonParam;

/**
 * ??????
 *
 * @author kpinfo
 */
public class LoginFragment extends Fragment {

    private Unbinder unbinder;
    private View decorView;
    private Context context;
    @BindView(R.id.edit_phone_fast)
    EditText mobileEdit; //?????????
    @BindView(R.id.edit_verification_login)
    EditText pwdEdit; //??????
    @BindView(R.id.txt_login_login)
    TextView loginBtn; //????????????
    @BindView(R.id.txt_register_login)
    TextView registerBtn; //????????????
    @BindView(R.id.txt_forget_pwd_login)
    TextView forgetPwdBtn; //???????????? ??????
    @BindView(R.id.ll_login_wechat_login)
    LinearLayout weChatLoginBtn; // ????????????
    @BindView(R.id.ll_login_weibo_login)
    LinearLayout sinaLoginBtn; // ??????????????????
    @BindView(R.id.ll_login_qq_login)
    LinearLayout qqLoginBtn; // QQ??????
    @BindView(R.id.check)
    CheckBox checkBox;
    private LoadingDialog loading;
    private boolean isPhoneNumberEmpty = true;// ????????????????????????
    private boolean isVerificationCodeEmpty = true; // ?????????????????????
    private String from;
    private long lastClickTime = System.currentTimeMillis();

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_login, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /**
     * ?????????
     */
    private void init() {
        initData();
        renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty);
        addListener();
        initLoading();
    }

    /**
     * ???????????????
     */
    private void initData() {
        if (getActivity() != null) {
            from = getActivity().getIntent().getStringExtra("from");
        }
    }

    /**
     * ?????????loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(context);
        if (null != loading) {
            loading.setLoadingText(null);
        }
    }

    // ????????????
    private void addListener() {
        mobileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPhoneNumberEmpty = TextUtils.isEmpty(s);
                renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pwdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isVerificationCodeEmpty = (TextUtils.isEmpty(s) || s.length() < 6);
                renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * render ??????????????????
     *
     * @param isPhoneNumberEmpty      ????????????????????????
     * @param isVerificationCodeEmpty ?????????????????????
     */
    private void renderLoginBtn(boolean isPhoneNumberEmpty, boolean isVerificationCodeEmpty) {
        if (!isPhoneNumberEmpty && !isVerificationCodeEmpty) {
            if (null != context) {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.bg_container_btn_able_login);
                loginBtn.setClickable(true);
                loginBtn.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                loginBtn.setBackground(drawable);
            }
        } else {
            if (null != context) {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.bg_container_disable_login);
                loginBtn.setClickable(false);
                loginBtn.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                loginBtn.setBackground(drawable);
            }
        }
    }

    @OnClick({
            R.id.txt_login_login,
            R.id.txt_forget_pwd_login,
            R.id.txt_register_login,
            R.id.ll_login_wechat_login,
            R.id.ll_login_weibo_login,
            R.id.ll_login_qq_login,
            R.id.tv_agreement_login,
            R.id.tv_policy_login
    })
    protected void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            hideInputMethod();
            SHARE_MEDIA shareMedia;
            int id = view.getId();
            if (id == R.id.txt_login_login) {
                attemptLogin();
            } else if (id == R.id.txt_forget_pwd_login) {
                IntentUtil.startActivity(context, ForgetPwdActivity.class, null);
            } else if (id == R.id.txt_register_login) {
                IntentUtil.startActivity(context, RegisterActivity.class, null);
            } else if (id == R.id.ll_login_wechat_login) {
                if (!AppUtil.isAvilible(context, "com.tencent.mm")) {
                    ToastUtil.showShortToast("????????????????????????");
                } else {
                    shareMedia = SHARE_MEDIA.WEIXIN;
                    threePartyAuthorization(shareMedia);
                }
            } else if (id == R.id.ll_login_weibo_login) {
                shareMedia = SHARE_MEDIA.SINA;
                threePartyAuthorization(shareMedia);
            } else if (id == R.id.ll_login_qq_login) { //qq??????
                if (AppUtil.isAvilible(context, "com.tencent.mobileqq")) {
                    shareMedia = SHARE_MEDIA.QQ;
                    threePartyAuthorization(shareMedia);
                } else {
                    ToastUtil.showShortToast("?????????????????? QQ");
                }
            } else if (id == R.id.tv_agreement_login) {
                IntentUtil.startActivity(context, UserAgreementActivity.class, null);
            } else if (id == R.id.tv_policy_login) {
                IntentUtil.startActivity(context, PrivacyPolicyActivity.class, null);
            }
        }
        lastClickTime = currentTime;
    }

    /**
     * ???????????????
     *
     * @param share_media ??????
     */
    private void threePartyAuthorization(SHARE_MEDIA share_media) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.showShortToast(context.getResources().getString(R.string.network_error));
            return;
        }
        if (checkBox.isChecked()) {
            UMShareAPI.get(context).getPlatformInfo(getActivity(), share_media, umAuthListener);
        } else {
            ToastUtil.showShortToast("????????????????????????????????????");
        }
    }

    /**
     * ????????????
     */
    private final UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (!map.isEmpty()) {
                threePartyLogin(share_media, map);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            ToastUtil.showShortToast("????????????");
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }
    };

    /**
     * ?????????????????????
     *
     * @param share_media ???????????????
     * @param map         ????????????
     */
    private void threePartyLogin(SHARE_MEDIA share_media, Map<String, String> map) {
        for (String key : map.keySet()) {
            LogUtil.e("key = " + key + " value = " + map.get(key) + "\n");
        }
        //?????????????????????
        String openid = TextUtils.isEmpty(map.get("openid")) ? "" : map.get("openid");
        // ????????????
        String nickname = TextUtils.isEmpty(map.get("name")) ? "" : map.get("name");
        // ????????????
        String sex = TextUtils.isEmpty(map.get("gender")) ? "" : map.get("gender");
        // ????????????
        String headimgurl = TextUtils.isEmpty(map.get("iconurl")) ? map.get("profile_image_url") : map.get("iconurl");
        String unionid = "";
        if (share_media == SHARE_MEDIA.WEIXIN) {
            unionid = TextUtils.isEmpty(map.get("unionid")) ? "" : map.get("unionid");
        } else if (share_media == SHARE_MEDIA.QQ) {
            unionid = TextUtils.isEmpty(map.get(Constant.STR_CW_UID_SYSTEM)) ? "" : map.get(Constant.STR_CW_UID_SYSTEM);
        } else if (share_media == SHARE_MEDIA.SINA) {
            unionid = TextUtils.isEmpty(map.get(Constant.STR_CW_UID_SYSTEM)) ? "" : map.get(Constant.STR_CW_UID_SYSTEM);
        }
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.showShortToast(getResources().getString(R.string.network_error));
        } else {
            if (null != loading && !loading.isShow()) {
                loading.show();
            }
            loginBtn.setClickable(false);
            weChatLoginBtn.setClickable(false);
            sinaLoginBtn.setClickable(false);
            qqLoginBtn.setClickable(false);
            RxHttpJsonParam http = RxHttp.postJson(Constant.LOGIN_THREE_PARTY);
            http.add("cw_nickname", nickname);
            String male = "???";
            // ??? ????????????????????????
            String man = "1";
            // ??? ????????????????????????
            String woman = "0";
            // ?????????????????????
            if (share_media == SHARE_MEDIA.WEIXIN) {
                http.add("cw_type", "WX");
                http.add("cw_externalappid", Constant.WX_APP_ID);
                http.add("cw_sex", TextUtils.equals(male, sex) ? man : woman);
                http.add("cw_headimgurl", headimgurl);
                http.add("cw_openid", openid);
                http.add("cw_unionid", unionid);
            }
            // QQ???????????????
            else if (share_media == SHARE_MEDIA.QQ) {
                http.add("cw_type", "QQ");
                http.add("cw_externalappid", Constant.QQ_APP_ID);
                http.add("cw_gender", TextUtils.equals(male, sex) ? man : woman);
                http.add("cw_figureurl", headimgurl);
                http.add("cw_openid", openid);
            }
            // ???????????????????????????
            else if (share_media == SHARE_MEDIA.SINA) {
                http.add("cw_type", "SINA");
                http.add("cw_externalappid", Constant.SINA_APP_ID);
                http.add("cw_gender", TextUtils.equals(male, sex) ? man : woman);
                http.add("cw_headimage", headimgurl);
                http.add("cw_wid", unionid);
            }
            http.asResponse(UserInfoResultEntity.UserInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(userInfo -> {
                        if (loginBtn != null) {
                            loginBtn.setClickable(true);
                        }
                        if (weChatLoginBtn != null) {
                            weChatLoginBtn.setClickable(true);
                        }
                        if (sinaLoginBtn != null) {
                            sinaLoginBtn.setClickable(true);
                        }
                        if (qqLoginBtn != null) {
                            qqLoginBtn.setClickable(true);
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (userInfo != null) {
                            ToastUtil.showShortToast("?????????????????????");
                            Constant.CW_AUTHORIZATION = TextUtils.isEmpty(userInfo.getToken()) ? "" : userInfo.getToken();
                            Constant.CW_USERNAME = TextUtils.isEmpty(userInfo.getNickname()) ? "" : userInfo.getNickname();
                            Constant.CW_AVATAR = TextUtils.isEmpty(userInfo.getHeadpic()) ? "" : userInfo.getHeadpic();
                            AppUtil.initLogInfo();
                            SharedPreferenceUtil.saveUserInfo(userInfo);
                            EventBus.getDefault().post(new EventMessage("updateUserInfo", "updateUserInfo"));
                            if (!TextUtils.isEmpty(from)) {
                                if (!TextUtils.isEmpty(userInfo.getMobile())) { // ????????????
                                    if (TextUtils.equals(from, Constant.RECORD_PAIKEW)) { // ??????????????????
                                        // ?????????????????? ??????
                                        IntentUtil.startActivity(context, PaikewUploadActivity.class, null);
                                    } else if (TextUtils.equals(from, Constant.RECORD_BROKE)) {
                                        IntentUtil.startActivity(context, BrokeActivity.class, null);
                                    }
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("from", from);
                                    IntentUtil.startActivity(context, UpdateMobileActivity.class, bundle);
                                }
                            }
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    }, (OnError) error -> {
                        if (loginBtn != null) {
                            loginBtn.setClickable(true);
                        }
                        if (weChatLoginBtn != null) {
                            weChatLoginBtn.setClickable(true);
                        }
                        if (sinaLoginBtn != null) {
                            sinaLoginBtn.setClickable(true);
                        }
                        if (qqLoginBtn != null) {
                            qqLoginBtn.setClickable(true);
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }

    }


    // ?????? ?????????
    private void hideInputMethod() {
        if (KeybordUtil.isInputMethodActive()) {
            KeybordUtil.hideSoftInput(this);
        }
    }

    /**
     * ??????
     */
    private void attemptLogin() {
        String mobileStr = mobileEdit.getText().toString();
        String passwordStr = pwdEdit.getText().toString();
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.showShortToast("?????????????????????");
            return;
        }
        if (TextUtils.isEmpty(mobileStr)) {
            ToastUtil.showShortToast("?????????????????????");
            return;
        }
        if (!AppUtil.isChinaPhoneLegal(mobileStr)) {
            ToastUtil.showShortToast("??????????????????");
            return;
        }
        if (TextUtils.isEmpty(mobileStr)) {
            ToastUtil.showShortToast("?????????????????????");
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            ToastUtil.showShortToast("??????????????????");
            return;
        }
        if (passwordStr.length() < 6) {
            ToastUtil.showShortToast("??????????????????");
            return;
        }
        if (!checkBox.isChecked()) {
            ToastUtil.showShortToast("????????????????????????????????????");
            return;
        }
        if (null != loading) {
            loading.show();
        }
        RxHttp.postJson(Constant.LOGIN).add("cw_type", "PASSWORD").add("cw_mobile", mobileStr).add("cw_password", passwordStr)
                .asResponse(UserInfoResultEntity.UserInfo.class)
                .as(RxLife.asOnMain(this))
                .subscribe(userInfo -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (userInfo != null) {
                        ToastUtil.showShortToast("????????????");
                        Constant.CW_AUTHORIZATION = TextUtils.isEmpty(userInfo.getToken()) ? "" : userInfo.getToken();
                        Constant.CW_USERNAME = TextUtils.isEmpty(userInfo.getNickname()) ? "" : userInfo.getNickname();
                        Constant.CW_AVATAR = TextUtils.isEmpty(userInfo.getHeadpic()) ? "" : userInfo.getHeadpic();
                        AppUtil.initLogInfo();
                        SharedPreferenceUtil.saveUserInfo(userInfo);
                        EventBus.getDefault().post(new EventMessage("updateUserInfo", "updateUserInfo"));
                        if (!TextUtils.isEmpty(from)) {
                            if (!TextUtils.isEmpty(userInfo.getMobile())) { // ????????????
                                if (TextUtils.equals(from, Constant.RECORD_PAIKEW)) { // ??????????????????
                                    //  ?????????????????? ??????
                                    IntentUtil.startActivity(context, PaikewUploadActivity.class, null);
                                } else if (TextUtils.equals(from, Constant.RECORD_BROKE)) {
                                    IntentUtil.startActivity(context, BrokeActivity.class, null);
                                }
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString("from", from);
                                IntentUtil.startActivity(context, UpdateMobileActivity.class, bundle);
                            }
                        }
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (loginBtn != null) {
                        loginBtn.setClickable(true);
                    }
                    if (weChatLoginBtn != null) {
                        weChatLoginBtn.setClickable(true);
                    }
                    if (sinaLoginBtn != null) {
                        sinaLoginBtn.setClickable(true);
                    }
                    if (qqLoginBtn != null) {
                        qqLoginBtn.setClickable(true);
                    }
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(context, getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        KpLog.getInstance().onAppViewScreenOut(context, getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != loading) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}
