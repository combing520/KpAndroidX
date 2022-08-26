package cn.cc1w.app.ui.ui.usercenter.wallet.bank;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.widget.wheel.BottomDialog;
import cn.cc1w.app.ui.widget.wheel.WheelView;
import cn.cc1w.app.ui.R;
/**
 * 银行卡信息添加
 * @author kpinfo
 */
public class BankInfoAddActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.edit_bank_type)
    EditText typeEdit;
    private BottomDialog bottomDialog;
    private static final String[] title = new String[]{
            "工商银行",
            "建设银行",
            "中国银行",
            "农业银行",
            "光大银行",
            "中信银行",
            "浦发银行",
    };

    // 卡类型
    private static final String[] label = new String[]{
            "储蓄卡",
            "信用卡"

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_info_add);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        titleTv.setText(getResources().getString(R.string.bank_info_input));
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_bank_add_verification,R.id.edit_bank_type})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_bank_add_verification) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(this, BankInfoVerificationInputActivity.class);
            startActivity(intent);
        } else if (id == R.id.edit_bank_type) {
            showBankCardSelectDialog();
        }
    }

    private void showBankCardSelectDialog() {
        View outerView1 = LayoutInflater.from(this).inflate(R.layout.dialog_select_bank_card, null);
        final WheelView wv1 = outerView1.findViewById(R.id.wv1);
        final WheelView wv2 = outerView1.findViewById(R.id.wv2);
        wv1.setItems(Arrays.asList(title), 0);
        wv2.setItems(Arrays.asList(label), 0);
        wv1.setOnItemSelectedListener((index, item) -> {
        });
        wv2.setOnItemSelectedListener((index, item) -> {
        });

        TextView tv_ok = outerView1.findViewById(R.id.tv_ok);
        TextView tv_cancel = outerView1.findViewById(R.id.tv_cancel);
        //点击确定
        tv_ok.setOnClickListener(view -> {
            bottomDialog.dismiss();

            typeEdit.setText(wv1.getSelectedItem() +" "+ wv2.getSelectedItem());
        });
        tv_cancel.setOnClickListener(arg0 -> bottomDialog.dismiss());
        if (bottomDialog != null && bottomDialog.isShowing()) {
            return;
        }
        bottomDialog = new BottomDialog(this, R.style.ActionSheetDialogStyle);
        bottomDialog.setContentView(outerView1);
        bottomDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
