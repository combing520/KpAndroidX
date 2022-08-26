package cn.cc1w.app.ui.ui.usercenter.wallet.bank.forgetpwd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.BankPwdSettingActivity;
import cn.cc1w.app.ui.R;
/**
 * 忘记密码 验证码输入
 */
public class ForgetPayPwdVerificationActivity extends CustomActivity {

    private Unbinder unbinder;

    @BindView(R.id.txt_header_not_title)
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pay_pwd_verification);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("忘记支付密码");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_complete_pwd_verification})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_complete_pwd_verification) {
            Intent intent = new Intent();
            intent.setClass(this, BankPwdSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
