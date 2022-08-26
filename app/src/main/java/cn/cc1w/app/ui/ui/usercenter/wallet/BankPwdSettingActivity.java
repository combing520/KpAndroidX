package cn.cc1w.app.ui.ui.usercenter.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;



import app.cloud.ccwb.cn.linlibrary.edit.PayPsdInputView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.R;
/**
 * 银行卡支付密码设置
 * @author kpinfo
 */
public class BankPwdSettingActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.edit_pwd_bank_setting)
    PayPsdInputView pwdEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_pwd_setting);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        pwdEdit.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }
            @Override
            public void inputFinished(String inputPsd) {

            }
        });
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("设置支付密码");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_pwd_bank_setting})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_pwd_bank_setting) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }
}
