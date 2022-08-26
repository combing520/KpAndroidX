package cn.cc1w.app.ui.ui.usercenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 用户协议
 *
 * @author kpinfo
 */
public class UserAgreementActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView navigationTitleTv;
    @BindView(R.id.web_agreenment)
    WebView webView;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
        setUpWebView();
    }

    private void initNavigation() {
        navigationTitleTv.setText("用户协议");
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    private void setUpWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        String ua = AppUtil.setWebViewUserAgent(webView.getSettings().getUserAgentString());
        webView.getSettings().setUserAgentString(ua);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(Constant.USER_AGREEMENT);
    }

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                goToUrlDetail(url);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (null != loading) {
                loading.show();
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError sslError) {
            handler.proceed();
        }
    };

    private void goToUrlDetail(String url) {
        if (isUrl(url)) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.TAG_URL, url);
            IntentUtil.startActivity(this, UrlDetailActivity.class, bundle);
        }
    }

    private boolean isUrl(String url) {
        boolean result = false;
        if (!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))) {
            result = true;
        }
        return result;
    }

    @OnClick(R.id.img_back_header_not_title)
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != webView) {
                ViewParent parent = webView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(webView);
                }
                webView.stopLoading();
                webView.removeAllViewsInLayout();
                webView.removeAllViews();
                webView.setWebViewClient(null);
                CookieSyncManager.getInstance().stopSync();
                webView.pauseTimers();
                webView.destroy();
                webView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unbinder.unbind();
    }
}