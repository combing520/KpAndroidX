package cn.cc1w.app.ui.ui.usercenter.wallet.bank;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import app.cloud.ccwb.cn.linlibrary.edit.PayPsdInputView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.R;

/**
 * 填写银行卡的验证码
 * @author kpinfo
 */
public class BankInfoVerificationInputActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_info_verification_input);
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
        titleTv.setText("填写验证码");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_info_verification, R.id.btn_complete_verification})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_info_verification) {
        } else if (id == R.id.btn_complete_verification) {
            showPwdInputDialog();
        }
    }

    /**
     * 显示弹出的 验证码输入的 dialog
     */
    private void showPwdInputDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pwd_setting, null);
        if (null != view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            dialog.show();
            Window window = dialog.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            PayPsdInputView edit = view.findViewById(R.id.dialog_input_dialog_pay);
            edit.setComparePassword(new PayPsdInputView.onPasswordListener() {
                @Override
                public void onDifference(String oldPsd, String newPsd) {

                }

                @Override
                public void onEqual(String psd) {

                }
                @Override
                public void inputFinished(String inputPsd) {
                    dialog.dismiss();
                    LogUtil.e("inputPsd = " + inputPsd);
                }
            });


            dialog.setContentView(view);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
