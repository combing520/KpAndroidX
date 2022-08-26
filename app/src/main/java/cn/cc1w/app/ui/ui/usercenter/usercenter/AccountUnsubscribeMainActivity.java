package cn.cc1w.app.ui.ui.usercenter.usercenter;

import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;

/**
 * @author kpinfo
 */
public class AccountUnsubscribeMainActivity extends CustomActivity implements CompoundButton.OnCheckedChangeListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView navigationTitleTv;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.check)
    CheckBox checkBox;
    @BindView(R.id.btn_unsubscribe)
    TextView btn;
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    private long lastClickTime = System.currentTimeMillis();
    private Drawable normalDrawable;
    private Drawable selectDrawable;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_unsubscribe_main);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initDrawable();
        initLoading();
        webView.loadUrl(Constant.PROTOCOL_CANCEL_USER);
        checkBox.setOnCheckedChangeListener(this);
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (i >= 95) {
                    if (btn != null) {
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (checkBox != null) {
                        checkBox.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void initNavigation() {
        navigationTitleTv.setText("注销账号");
    }

    private void initDrawable() {
        selectDrawable = ContextCompat.getDrawable(this, R.drawable.bg_container_btn_able_login);
        normalDrawable = ContextCompat.getDrawable(this, R.drawable.bg_container_disable_login);
        btn.setBackground(normalDrawable);
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_unsubscribe})
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.img_back_header_not_title) {
                finish();
            } else if (id == R.id.btn_unsubscribe) {
                doSubmit();
            }
        }
        lastClickTime = currentTime;
    }

    private void doSubmit() {
        if (checkBox.isChecked()) {
            showRiskDialog();
        }
    }

    private void showRiskDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_risk, null);
        if (null != view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            dialog.show();
            Window window = dialog.getWindow();
            if (null != window) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                int statusBarHeight = AppUtil.getStatusBarHeight(AccountUnsubscribeMainActivity.this);
                if (statusBarHeight >= 0) {
                    lp.height = displayMetrics.heightPixels - statusBarHeight;
                } else {
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                }
                window.setAttributes(lp);
            }
            view.findViewById(R.id.quit).setOnClickListener(v -> dialog.dismiss());
            view.findViewById(R.id.confirm).setOnClickListener(v -> {
                doUnsubscribeInfo();
                dialog.dismiss();
            });
            dialog.setCancelable(false);
            dialog.setContentView(view);
        }
    }

    private void doUnsubscribeInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            loading.show();
            RxHttp.postJson(Constant.CANCEL_USER)
                    .asResponse(JsonObject.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (loading != null) {
                            loading.close();
                        }
                        if(!isFinishing()){
                            gotoUnsubscribeSuccess();
                        }
                    }, (OnError) error -> {
                        if (loading != null) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(getResources().getString(R.string.network_error));
        }
    }

    private void gotoUnsubscribeSuccess() {
        IntentUtil.startActivity(this, AccountUnsubscribeSuccessActivity.class, null);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtil.d("onCheckedChanged isChecked = " + isChecked);
        if (isChecked) {
            btn.setBackground(selectDrawable);
        } else {
            btn.setBackground(normalDrawable);
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