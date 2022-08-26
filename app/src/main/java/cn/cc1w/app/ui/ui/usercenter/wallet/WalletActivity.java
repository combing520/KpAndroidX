package cn.cc1w.app.ui.ui.usercenter.wallet;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.bank.BankInfoAddActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.bank.BankListActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.bank.withdraw.WithdrawActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.income.IncomeActivity;
import cn.cc1w.app.ui.utils.KeybordUtil;
import cn.cc1w.app.ui.R;

/**
 * 个人钱包
 *
 * @author kpinfo
 */
public class WalletActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
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
     * 初始化导航信息
     */
    private void initNavigation() {
        title.setText(getResources().getString(R.string.wallet));
    }

    @OnClick({R.id.txt_income_wallet, R.id.txt_withdraw_wallet, R.id.ll_bankCard_wallet, R.id.img_back_header_not_title})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_income_wallet) {
            KeybordUtil.closeInputMethod(this);
            intent.setClass(this, IncomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.txt_withdraw_wallet) {
            KeybordUtil.closeInputMethod(this);
            intent.setClass(this, WithdrawActivity.class);
            startActivity(intent);
        } else if (id == R.id.ll_bankCard_wallet) {
            KeybordUtil.closeInputMethod(this);
            showBankList();
        }
    }

    /**
     * 显示 银行卡绑定
     */
    private void showWithDrawDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_withdraw_no_bank_card, null);

        if (null != view) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();

            dialog.setCancelable(false);
            dialog.show();

            Window window = dialog.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);

            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            // 解除绑定
            TextView confirmTv = view.findViewById(R.id.txt_dialog_bind_card);
            TextView cancelTv = view.findViewById(R.id.txt_dialog_cancel_card);
            confirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进行 银行卡绑定的操作
                    bindBankCard();
                    dialog.dismiss();
                }
            });
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(view);
        }
    }

    /**
     * 绑定银行卡
     */
    private void bindBankCard() {
        Intent intent = new Intent();
        intent.setClass(this, BankInfoAddActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 显示银行卡列表
     */
    private void showBankList() {
        Intent intent = new Intent();
        intent.setClass(this, BankListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}