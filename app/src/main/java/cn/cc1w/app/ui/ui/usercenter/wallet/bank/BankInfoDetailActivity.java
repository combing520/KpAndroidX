package cn.cc1w.app.ui.ui.usercenter.wallet.bank;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;

/**
 * 银行卡详情
 *
 * @author kpinfo
 */
public class BankInfoDetailActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.detail_img_bankCard)
    ImageView smallImg;
    @BindView(R.id.detail_img_big_bank)
    ImageView bigImg;
    @BindView(R.id.txt_bank_detail_phone)
    TextView phoneTv;
    private static final String URL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535783082989&di=b0fbae40d96fdab0fb79658d4286aaf4&imgtype=0&src=http%3A%2F%2Fimg.weixinyidu.com%2F160320%2F699ce00b.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_info_detail);
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
        titleTv.setText(getResources().getString(R.string.detail_bank));
        phoneTv.setText("✽✽✽✽ 1234");
        AppUtil.loadNetworkImg(URL, smallImg);
        AppUtil.loadNetworkImg(URL, bigImg);
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_bank_detail_unbind})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_bank_detail_unbind) {
            showUnbindDialog();
        }
    }

    /**
     * show 解除绑定的 dialog
     */
    private void showUnbindDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_detail_unbind, null);
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
            TextView cancelTv = view.findViewById(R.id.txt_dialog_unbind_cancel);
            TextView confirmTv = view.findViewById(R.id.txt_dialog_unbind_confirm);
            cancelTv.setOnClickListener(v -> {
                ToastUtil.showShortToast("取消");
                dialog.dismiss();
            });
            confirmTv.setOnClickListener(v -> {
                ToastUtil.showShortToast("解除绑定");
                dialog.dismiss();
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
