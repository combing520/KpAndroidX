package cn.cc1w.app.ui.ui.usercenter.setting;

import android.graphics.Color;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.ui.home.upload.PaikewUploadActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.MyCountDownTimer;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 修改手机号
 *
 * @author kpinfo
 */
public class UpdateMobileActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.edit_mobile_update)
    EditText mobileEdit;
    @BindView(R.id.btn_verification_update)
    TextView obtainVerificationCodeBtn;
    @BindView(R.id.edit_verification_update)
    EditText verificationCodeEdit;
    @BindView(R.id.btn_mobile_submit_update)
    TextView submitBtn;
    private LoadingDialog loading;
    private CountTimer timeCountDown;
    private String number;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mobile);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化导航信息
     */
    private void initNavigation() {
        number = TextUtils.isEmpty(getIntent().getStringExtra("number")) ? "" : getIntent().getStringExtra("number");
        from = getIntent().getStringExtra("from");
        if (!TextUtils.isEmpty(number)) {
            titleTv.setText("更新手机号");
            submitBtn.setText("确认更新");
        } else {
            titleTv.setText("绑定手机号");
            submitBtn.setText("确认绑定");
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_verification_update, R.id.btn_mobile_submit_update})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_verification_update) {
            obtainVerificationCode();
        } else if (id == R.id.btn_mobile_submit_update) {
            updateMobile();
        }
    }

    /**
     * 获取验证码
     */
    private void obtainVerificationCode() {
        String phoneNumberStr = mobileEdit.getText().toString().trim();
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast("当前网络不可用");
            return;
        }
        if (TextUtils.isEmpty(phoneNumberStr)) {
            ToastUtil.showShortToast("手机号不能为空");
            return;
        }
        if (!AppUtil.isChinaPhoneLegal(phoneNumberStr)) {
            ToastUtil.showShortToast("手机格式错误");
            return;
        }
        if (null != loading) {
            loading.show();
        }
        submitBtn.setClickable(false);
        RxHttp.postJson(Constant.MESSAGE_SEND).add("cw_mobile", phoneNumberStr)
                .asMsgResponse(MsgResonse.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (!isFinishing()) {
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                        if (null != submitBtn) {
                            submitBtn.setClickable(true);
                        }
                        timeCountDown = new CountTimer(Constant.TOTAL_TIME, Constant.TIME_INTERVAL);
                        WeakReference<CountTimer> weakTimerCountDowner = new WeakReference<>(timeCountDown);
                        if (null != weakTimerCountDowner.get()) {
                            weakTimerCountDowner.get().start();
                        }
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (null != submitBtn) {
                        submitBtn.setClickable(true);
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                });
    }

    /**
     * 更新手机号码
     * 如果是 第三方登录的就可以进行绑定手机号
     * 如果是 手机号登录就直接进行 更新
     */
    private void updateMobile() {
        final String phoneNumberStr = mobileEdit.getText().toString();
        String verificationCodeStr = verificationCodeEdit.getText().toString();
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast("当前网络不可用");
            return;
        }
        if (TextUtils.isEmpty(phoneNumberStr)) {
            ToastUtil.showShortToast("手机号不能为空");
            return;
        }
        if (!AppUtil.isChinaPhoneLegal(phoneNumberStr)) {
            ToastUtil.showShortToast("手机格式错误");
            return;
        }
        if (TextUtils.isEmpty(verificationCodeStr)) {
            ToastUtil.showShortToast("验证码不能为空");
            return;
        }
        if (null != loading) {
            loading.show();
        }
        if (!TextUtils.isEmpty(number)) {
            submitBtn.setClickable(false);
            RxHttp.postJson(Constant.MOBILE_UPDATE).add("cw_mobile", phoneNumberStr).add("cw_smscode", verificationCodeStr)
                    .asResponse(UserInfoResultEntity.UserInfo.class).as(RxLife.asOnMain(this))
                    .subscribe(userInfo -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && userInfo != null) {
                            if (submitBtn != null) {
                                submitBtn.setClickable(true);
                            }
                            ToastUtil.showShortToast("更新成功");
                            Constant.CW_UID_SYSTEM = TextUtils.isEmpty(userInfo.getUid()) ? "" : userInfo.getUid();
                            if (!TextUtils.isEmpty(userInfo.getToken())) {
                                UserInfoResultEntity.UserInfo userInfoNew = SharedPreferenceUtil.getUserInfo();
                                if (userInfoNew != null) {
                                    userInfoNew.setToken(userInfo.getToken());
                                } else {
                                    userInfoNew = userInfo;
                                }
                                SharedPreferenceUtil.saveUserInfo(userInfoNew);
                                Constant.CW_AUTHORIZATION = userInfo.getToken();
                            }
                            if (!TextUtils.isEmpty(from)) {
                                if (TextUtils.equals(from, Constant.RECORD_PAIKEW)) { // 绑定完手机后回到 拍客录制
                                    IntentUtil.startActivity(UpdateMobileActivity.this, PaikewUploadActivity.class, null);
                                }
                            }
                            EventBus.getDefault().post(new EventMessage("updateMobile", userInfo.getMobile()));
                            finish();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (null != submitBtn) {
                            submitBtn.setClickable(true);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {   // 绑定手机号
            submitBtn.setClickable(false);
            RxHttp.postJson(Constant.MOBILE_BIND).add("cw_mobile", phoneNumberStr).add("cw_smscode", verificationCodeStr)
                    .asResponse(UserInfoResultEntity.UserInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(userInfo -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && userInfo != null) {
                            Constant.CW_UID_SYSTEM = TextUtils.isEmpty(userInfo.getUid()) ? "" : userInfo.getUid();
                            if (!TextUtils.isEmpty(userInfo.getToken())) {
                                UserInfoResultEntity.UserInfo userInfoNew = SharedPreferenceUtil.getUserInfo();
                                if (userInfoNew != null) {
                                    userInfoNew.setToken(userInfo.getToken());
                                } else {
                                    userInfoNew = userInfo;
                                }
                                SharedPreferenceUtil.saveUserInfo(userInfoNew);
                                Constant.CW_AUTHORIZATION = userInfo.getToken();
                            }
                            if (submitBtn != null) {
                                submitBtn.setClickable(true);
                            }
                            ToastUtil.showShortToast("绑定成功");
                            if (!TextUtils.isEmpty(from)) {
                                if (TextUtils.equals(from, Constant.RECORD_PAIKEW)) { // 绑定完手机后回到 拍客录制
                                    IntentUtil.startActivity(UpdateMobileActivity.this, PaikewUploadActivity.class, null);
                                }
                            }
                            EventBus.getDefault().post(new EventMessage("updateMobile", userInfo.getMobile()));
                            finish();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (null != submitBtn) {
                            submitBtn.setClickable(true);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    class CountTimer extends MyCountDownTimer {
        CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (null != obtainVerificationCodeBtn) {
                int time = (int) (millisUntilFinished / 1000) + 1;
                obtainVerificationCodeBtn.setClickable(false);
                obtainVerificationCodeBtn.setTextColor(ContextCompat.getColor(UpdateMobileActivity.this, R.color.colorTxtLabel));
                if (time >= 10) {
                    obtainVerificationCodeBtn.setText(renderColorfulStr(String.valueOf(time).concat(Constant.SEND_VERIFICATION_TRY), 3));
                } else {
                    obtainVerificationCodeBtn.setText(renderColorfulStr(String.valueOf(time).concat(Constant.SEND_VERIFICATION_TRY), 2));
                }
            }
        }

        @Override
        public void onFinish() {
            if (null != obtainVerificationCodeBtn) {
                obtainVerificationCodeBtn.setTextColor(ContextCompat.getColor(UpdateMobileActivity.this, R.color.colorTxtContent));
                obtainVerificationCodeBtn.setText(Constant.SEND_VERIFICATION);
                obtainVerificationCodeBtn.setClickable(true);
            }
        }
    }

    /**
     * render 带有颜色的 spannableStringBuilder
     *
     * @param targetStr 对应的文字
     * @param endPos    结束位置
     */
    private SpannableStringBuilder renderColorfulStr(String targetStr, int endPos) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(targetStr);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != timeCountDown) {
            timeCountDown.cancel();
            timeCountDown = null;
        }
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}