package cn.cc1w.app.ui.ui.usercenter.wallet.bank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.R;
/**
 * 添加银行卡
 * @author kpinfo
 */
public class BankListAddActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list_add);
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
     * 初始化 导航头
     */
    private void initNavigation() {
        titleTv.setText(getResources().getString(R.string.bank_add));
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_next_bank_add})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_next_bank_add) {
            Intent intent = new Intent();
            intent.setClass(this, BankInfoAddActivity.class);
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
