package cn.cc1w.app.ui.ui.usercenter.wallet.bank.withdraw;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;



import app.cloud.ccwb.cn.linlibrary.edit.PayPsdInputView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.ui.usercenter.usercenter.forgetpwd.ForgetPwdActivity;
import cn.cc1w.app.ui.R;
/**
 * 提现
 */
public class WithdrawActivity extends CustomActivity {

    private Unbinder unbinder;

    @BindView(R.id.txt_header_not_title)
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initNavigation();
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("提现");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_next_withDraw})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_next_withDraw) {
            showWithDrawDialog();
        }
    }

    /**
     * show 提现的dialog
     */
    private void showWithDrawDialog() {
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

            TextView titleTv = view.findViewById(R.id.txt_title_dialog_pay);
            TextView describeTv = view.findViewById(R.id.txt_content_dialog_Pay);
            TextView forgetTv = view.findViewById(R.id.txt_dialog_forget_pay);

            titleTv.setText("输入支付密码");
            describeTv.setTextColor(getResources().getColor(R.color.colorTxtTitle));

            describeTv.setTextSize(30);
            describeTv.setText("¥10.00");

            forgetTv.setVisibility(View.VISIBLE);
            forgetTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 忘记密码的操作
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(WithdrawActivity.this, ForgetPwdActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

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
                    doWithDraw();
                }
            });

            dialog.setContentView(view);
        }
    }

    /**
     * 接收EventBus
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (TextUtils.equals("closeWithDraw", message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                finish();
            }
        }
    }


    /**
     * 进行提现操作
     */
    private void doWithDraw() {
        Intent intent = new Intent();
        intent.setClass(this, WithDrawDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        unbinder.unbind();

    }
}
