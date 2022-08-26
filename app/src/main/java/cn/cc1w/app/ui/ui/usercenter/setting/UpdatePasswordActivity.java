package cn.cc1w.app.ui.ui.usercenter.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 修改密码
 *
 * @author kpinfo
 */
public class UpdatePasswordActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.edit_mobile_pwd)
    EditText oldPwdEdit;
    @BindView(R.id.edit_newPwd_pwd)
    EditText newPwdEdit;
    @BindView(R.id.edit_confirmPwd_pwd)
    EditText confirmPwdEdit;
    @BindView(R.id.txt_submit_pwd)
    TextView submitBtn;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("修改密码");
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_submit_pwd})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_submit_pwd) {
            updatePwd();
        }
    }

    /**
     * 更新 密码
     */
    private void updatePwd() {
        String oldPwdStr = oldPwdEdit.getText().toString();
        String pwdStr = newPwdEdit.getText().toString();
        String confirmPwdStr = confirmPwdEdit.getText().toString();
        if (TextUtils.isEmpty(oldPwdStr)) {
            ToastUtil.showShortToast("旧密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwdStr)) {
            ToastUtil.showShortToast("新密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(confirmPwdStr)) {
            ToastUtil.showShortToast("确认密码不能为空");
            return;
        }
        if (!TextUtils.equals(pwdStr, confirmPwdStr)) {
            ToastUtil.showShortToast("两次输入密码不正确");
            return;
        }
        if (NetUtil.isNetworkConnected(this)) {
            loading.show();
            submitBtn.setClickable(false);
            RxHttp.postJson(Constant.PASSWORD_UPDATE).add("cw_password", pwdStr)
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
                            finish();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (null != submitBtn) {
                                submitBtn.setClickable(true);
                            }
                        }
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(getResources().getString(R.string.network_error));
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