package cn.cc1w.app.ui.ui.usercenter.usercenter.register;

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
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.KeybordUtil;
import cn.cc1w.app.ui.utils.MyCountDownTimer;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 注册
 *
 * @author kpinfo
 */
public class RegisterActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.edit_phone_register)
    EditText phoneNumberEdit;
    @BindView(R.id.edit_verification_register)
    EditText verificationCodeEdit;
    @BindView(R.id.txt_verification_obtain_register)
    TextView obtainVerificationCodeBtn;
    @BindView(R.id.edit_password_register)
    EditText passwordEdit;
    @BindView(R.id.txt_submit_register)
    TextView submitBtn;
    private boolean isPhoneNumberEmpty = true;
    private boolean isVerificationCodeEmpty = true;
    private boolean isPasswordEmpty = true;
    private LoadingDialog loading;
    private CountTimer timeCountDown;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
        renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty, isPasswordEmpty);
        addRegister();
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    private void addRegister() {
        phoneNumberEdit.addTextChangedListener(new TextWatcher() {
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

        passwordEdit.addTextChangedListener(new TextWatcher() {
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
     * 初始化 导航头
     */
    private void initNavigation() {
        titleTv.setText("手机号注册");
        lastTime = System.currentTimeMillis();
        renderLoginBtn(isPhoneNumberEmpty, isVerificationCodeEmpty, isPasswordEmpty);
    }

    /**
     * render 登录按钮状态
     *
     * @param isPhoneNumberEmpty      手机号码是否为空
     * @param isVerificationCodeEmpty 验证码是否为空
     * @param isPasswordEmpty         密码是否为空
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

    private void obtainVerificationCode() {
        // 手机号
        String phoneNumberStr = phoneNumberEdit.getText().toString().trim();
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
                        timeCountDown = new CountTimer(Constant.TOTAL_TIME, Constant.TIME_INTERVAL);
                        WeakReference<CountTimer> weakTimerCountDowner = new WeakReference<>(timeCountDown);
                        if (null != weakTimerCountDowner.get()) {
                            weakTimerCountDowner.get().start();
                        }
                        if (submitBtn != null) {
                            submitBtn.setClickable(true);
                        }
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (!isFinishing() && null != submitBtn) {
                        submitBtn.setClickable(true);
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                });
    }

    /**
     * 注册
     */
    private void doRegister() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            //手机号
            String mobileStr = phoneNumberEdit.getText().toString();
            // 验证码
            String verificationCodeStr = verificationCodeEdit.getText().toString();
            // 密码
            String pwdStr = passwordEdit.getText().toString();
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
                RxHttp.postJson(Constant.USER_ACCOUNT_REGISTER).add("cw_mobile", mobileStr).add("cw_smscode", verificationCodeStr).add("cw_password", pwdStr)
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
                obtainVerificationCodeBtn.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorTxtLabel));
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
                obtainVerificationCodeBtn.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorTxtContent));
                obtainVerificationCodeBtn.setText(Constant.SEND_VERIFICATION);
                obtainVerificationCodeBtn.setClickable(true);
            }
        }
    }

    // 关闭 输入法
    private void hideInputMethod() {
        if (KeybordUtil.isInputMethodActive()) {
            KeybordUtil.closeInputMethod(this);
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

    @OnClick({R.id.img_back_header_not_title, R.id.txt_verification_obtain_register, R.id.txt_submit_register})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_verification_obtain_register) {
            obtainVerificationCode();
        } else if (id == R.id.txt_submit_register) {
            doRegister();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}