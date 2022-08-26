package cn.cc1w.app.ui.ui.usercenter.usercenter.forgetpwd;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;

import java.lang.ref.WeakReference;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.KeybordUtil;
import cn.cc1w.app.ui.utils.MyCountDownTimer;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 忘记密码
 *
 * @author kpinfo
 */
public class ForgetPwdActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_verification_obtain_forgetPwd)
    TextView obtainVerificationCodeBtn;
    @BindView(R.id.edit_phone_forgetPwd)
    EditText mobileEdit;
    @BindView(R.id.edit_verification_forgetPwd)
    EditText verificationCodeEdit;
    @BindView(R.id.edit_password_forgetPwd)
    EditText pwdEdit;
    @BindView(R.id.txt_submit_forgetPwd)
    TextView submitBtn;
    private boolean isPhoneNumberEmpty = true;
    private boolean isVerificationCodeEmpty = true;
    private boolean isPasswordEmpty = true;
    private CountTimer timeCountDown;
    private LoadingDialog loading;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd2);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
        renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty, isPasswordEmpty);
        addRegister();
    }

    private void initNavigation() {
        titleTv.setText("忘记密码");
        lastTime = System.currentTimeMillis();
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    private void addRegister() {
        mobileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPhoneNumberEmpty = TextUtils.isEmpty(s);
                renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty, isPasswordEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        verificationCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isVerificationCodeEmpty = TextUtils.isEmpty(s);
                renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty, isPasswordEmpty);
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
                isPasswordEmpty = !(!TextUtils.isEmpty(s) && s.length() >= Constant.MIN_LENGTH_PASSWORD);
                renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty, isPasswordEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * render 登录按钮状态
     */
    private void renderLoginBtn(boolean isPhoneNumberEmpty, boolean isVerificationCodeEmpty, boolean isPasswordEmpty) {
        if (null != submitBtn) {
            Drawable drawable;
            if (!isPhoneNumberEmpty && !isVerificationCodeEmpty && !isPasswordEmpty) {
                drawable = ContextCompat.getDrawable(this, R.drawable.bg_container_btn_able_login);
                submitBtn.setClickable(true);
                submitBtn.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            } else {
                drawable = ContextCompat.getDrawable(this, R.drawable.bg_container_disable_login);
                submitBtn.setClickable(false);
                submitBtn.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
            }
            submitBtn.setBackground(drawable);
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
                        if(data != null){
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
                    if (!isFinishing()) {
                        if (null != submitBtn) {
                            submitBtn.setClickable(true);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    }
                });
    }

    // 关闭 输入法
    private void hideInputMethod() {
        if (KeybordUtil.isInputMethodActive()) {
            KeybordUtil.closeInputMethod(this);
        }
    }

    /**
     * render 带有颜色的 spannableStringBuilder
     */
    private SpannableStringBuilder renderColorfulStr(String targetStr, int endPos) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(targetStr);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 修改用户的密码
     */
    private void updateUserPwd() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            String mobileStr = mobileEdit.getText().toString();
            String verificationCodeStr = verificationCodeEdit.getText().toString();
            String pwdStr = pwdEdit.getText().toString();
            if (!NetUtil.isNetworkConnected(this)) {
                ToastUtil.showShortToast("当前网络不可用");
                return;
            }
            if (TextUtils.isEmpty(mobileStr)) {
                ToastUtil.showShortToast("手机号不能为空");
                return;
            }
            if (!AppUtil.isChinaPhoneLegal(mobileStr)) {
                ToastUtil.showShortToast("手机格式错误");
                return;
            }
            if (TextUtils.isEmpty(verificationCodeStr)) {
                ToastUtil.showShortToast("验证码不能为空");
                return;
            }
            if (TextUtils.isEmpty(pwdStr)) {
                ToastUtil.showShortToast("密码不能为空");
                return;
            }
            hideInputMethod();
            if (NetUtil.isNetworkConnected(this)) {
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.PASSWORD_FIND).add("cw_mobile", mobileStr).add("cw_smscode", verificationCodeStr).add("cw_password", pwdStr)
                        .asResponse(JsonObject.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (!isFinishing()) {
                                ToastUtil.showShortToast("找回成功");
                                finish();
                            }
                        }, (OnError) error -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            ToastUtil.showShortToast(error.getErrorMsg());
                        });
            } else {
                ToastUtil.showShortToast(getString(R.string.network_error));
            }
        }
        lastTime = currentTime;
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
                obtainVerificationCodeBtn.setTextColor(ContextCompat.getColor(ForgetPwdActivity.this, R.color.colorTxtLabel));
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
                obtainVerificationCodeBtn.setTextColor(ContextCompat.getColor(ForgetPwdActivity.this, R.color.colorTxtContent));
                obtainVerificationCodeBtn.setText(Constant.SEND_VERIFICATION);
                obtainVerificationCodeBtn.setClickable(true);
            }
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_verification_obtain_forgetPwd, R.id.txt_submit_forgetPwd})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_verification_obtain_forgetPwd) {
            obtainVerificationCode();
            hideInputMethod();
        } else if (id == R.id.txt_submit_forgetPwd) {
            updateUserPwd();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != timeCountDown) {
            timeCountDown.cancel();
            timeCountDown = null;
        }
        unbinder.unbind();
    }
}