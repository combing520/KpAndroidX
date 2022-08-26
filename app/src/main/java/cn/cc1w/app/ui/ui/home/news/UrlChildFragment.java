package cn.cc1w.app.ui.ui.home.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.jetbrains.annotations.NotNull;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.BaseFragment;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;

/**
 * 视频
 *
 * @author kpinfo
 */
public class UrlChildFragment extends BaseFragment {
    WebView webView;
    private String onPagePauseCallback;
    private String onPageResumeCallback;
    private String mUrl;
    private View decorView;

    public UrlChildFragment() {
    }

    public static UrlChildFragment newInstance(String url) {
        UrlChildFragment fragment = new UrlChildFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            return;
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_active_url_child, container, false);
        }
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        webView = decorView.findViewById(R.id.webView_detail_url);
        if (getArguments() != null) {
            mUrl = getArguments().getString("url");
        }
        setUpWebView();
    }

    /**
     * 设置WebView
     */
    private void setUpWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUserAgentString(AppUtil.setWebViewUserAgent(webView.getSettings().getUserAgentString()));
        webView.addJavascriptInterface(new AppMethodsInterface(), "APP_METHODS");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    if (TextUtils.equals("open_new=_blank_", url)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", url);
                        IntentUtil.startActivity(getActivity(), UrlDetailActivity.class, bundle);
                    } else {
                        webView.loadUrl(url);
                    }
                }
                return true;
            }
        });
        if (!TextUtils.isEmpty(mUrl)) {
            webView.loadUrl(mUrl);
        }
    }

    private class AppMethodsInterface extends Object {
        @JavascriptInterface
        public void onPagePause(String json) {
            LogUtil.print("tongchao", "onPagePauseCallback ------- " + json);
            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            onPagePauseCallback = obj.get("callBack").getAsString();
        }

        @JavascriptInterface
        public void onPageResume(String json) {
            LogUtil.print("tongchao", "onPageResumeCallback ------- " + json);
            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            onPageResumeCallback = obj.get("callBack").getAsString();
        }
    }

    @Override
    public void frResume() {
        if (webView != null && !TextUtils.isEmpty(onPageResumeCallback)) {
            webView.loadUrl("javascript:APP_METHODS." + onPageResumeCallback + "();");
            LogUtil.print("tongchao", "javascript:APP_METHODS." + onPageResumeCallback + "();");
        }
    }

    @Override
    public void frPause() {
        if (webView != null && !TextUtils.isEmpty(onPagePauseCallback)) {
            webView.loadUrl("javascript:APP_METHODS." + onPagePauseCallback + "();");
            LogUtil.print("tongchao", "javascript:APP_METHODS." + onPagePauseCallback + "();");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != webView) {
            webView.destroy();
            webView = null;
        }
    }
}