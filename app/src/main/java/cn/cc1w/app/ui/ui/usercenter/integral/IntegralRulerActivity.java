package cn.cc1w.app.ui.ui.usercenter.integral;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.rxjava.rxlife.RxLife;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.HtmlFormatUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.MWebView;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 积分规则
 * @author kpinfo
 */
public class IntegralRulerActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.web_integral_ruler)
    MWebView webView;
    private static final String mimeType = "text/html";
    private static final String encoding = "utf-8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_ruler);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        webView.setOnLongClickListener(v -> true);
        requestData();
    }

    @OnClick({R.id.img_back_integral_ruler})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_integral_ruler) {
            finish();
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.RULE_CREDIT)
                    .asResponse(String.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if(!isFinishing() && !TextUtils.isEmpty(data)){
                            webView.loadDataWithBaseURL(null, HtmlFormatUtil.getHtmlData(data), mimeType, encoding, null);
                        }
                    }, (OnError) error -> {
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
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