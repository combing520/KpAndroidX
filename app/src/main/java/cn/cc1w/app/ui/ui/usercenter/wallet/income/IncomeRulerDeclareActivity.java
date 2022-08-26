package cn.cc1w.app.ui.ui.usercenter.wallet.income;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.widget.MWebView;

/**
 * 收入规则说明
 * @author kpinfo
 */
public class IncomeRulerDeclareActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.webView_income_ruler)
    MWebView webView;
    private static final String URL = "https://www.baidu.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_ruler_declare);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        webView.loadUrl(URL);
    }

    /**
     * 初始化导航头信息
     */
    private void initNavigation() {
        titleTv.setText("收入规则说明");
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != webView) {
            webView.removeAllViews();
            webView.clearHistory();
            webView.destroy();
        }
        unbinder.unbind();
    }
}
