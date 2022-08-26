package cn.cc1w.app.ui.ui.usercenter.integral;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.ToastUtil;

/**
 * 积分兑换详情
 * @author kpinfo
 */
public class IntegralExchangeDetailActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.img_integralExchangeDetail)
    ImageView detailImg;
    private static final String URL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533815049291&di=d43b8723ad7e507ee6b0ce77cef78202&imgtype=0&src=http%3A%2F%2Fpic33.nipic.com%2F20130920%2F12413197_102353020000_2.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_exchange_detail);
        
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        AppUtil.loadNetworkImg(URL, detailImg);
    }

    @OnClick({R.id.img_back_integralExchangeDetail, R.id.txt_integralExchangeDetail})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_integralExchangeDetail) {
            finish();
        } else if (id == R.id.txt_integralExchangeDetail) {
            ToastUtil.showShortToast("开始兑换");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}