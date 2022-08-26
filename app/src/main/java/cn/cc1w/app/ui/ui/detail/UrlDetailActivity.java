package cn.cc1w.app.ui.ui.detail;

import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.MWebView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;

/**
 * 活动详情类
 */
public class UrlDetailActivity extends CustomActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.webView_detail_url)
    ViewGroup viewParent;
    @BindView(R.id.txt_title_detail_url)
    TextView titleTv;
    private MWebView webView;
    private Unbinder unbinder;
    private String title;
    private String url = "";
    private String newsId;
    private String summary;
    private int minTimeLength = 1, maxTimeLength = 60;
    private boolean isActivityFront = false;
    private final String[] permissionList2 = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,};
    private static final String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    // 列表传过来有 title
    private boolean isHasListTitle = false;
    // 网页通过JS方法传过来 Title
    private boolean isHasJsTitle = false;
    // 列表传过来有 Summary
    private boolean isHasListSummary = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recoverData(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_url_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initNavigation();
        setUpWebView();
    }

    /**
     * 设置WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView() {
        webView = new MWebView(this, null);
        viewParent.addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webView.setOnPageFinishListener(new MWebView.OnPageFinishListener() {
            @Override
            public void onFinish(String title) {
                if (!TextUtils.isEmpty(title) && !isHasListTitle && !isHasJsTitle) {
                    titleTv.setText(title);
                    UrlDetailActivity.this.title = title;
                }
            }

            @Override
            public void onProgressChanged(int newProgress) {

            }
        });
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    /**
     * 初始化导航
     */
    private void initNavigation() {
        title = TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_TITLE)) ? "" : getIntent().getStringExtra(Constant.TAG_TITLE);
        url = TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_URL)) ? "" : getIntent().getStringExtra(Constant.TAG_URL);
        titleTv.setText(TextUtils.isEmpty(title) ? "" : title);
        newsId = TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_ID)) ? "" : getIntent().getStringExtra(Constant.TAG_ID);
        summary = TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_SUMMARY)) ? "" : getIntent().getStringExtra(Constant.TAG_SUMMARY);
        if (!TextUtils.isEmpty(title)) {
            isHasListTitle = true;
        }
        if (!TextUtils.isEmpty(summary)) {
            isHasListSummary = true;
        }
        if (!TextUtils.isEmpty(newsId)) {
            addNewsDetailRecord(newsId);
        }
    }

    /**
     * 添加新闻详情 记录
     *
     * @param newsId 新闻 ID
     */
    private void addNewsDetailRecord(String newsId) {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.DETAIL_NEWS_NORMAL)
                    .add("cw_news_id", newsId)
                    .asResponse(JsonObject.class).subscribe();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (webView != null) {
            webView.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick({R.id.img_back_detail_url, R.id.img_share_detail_url})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_detail_url) {
            if (null != webView && webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        } else if (id == R.id.img_share_detail_url) {
            share();
        }
    }

    // 分享
    private void share() {
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setNewsId(TextUtils.isEmpty(newsId) ? "" : newsId);
        shareEntity.setRedirect_url("");
        shareEntity.setSummary(TextUtils.isEmpty(summary) ? (TextUtils.isEmpty(title) ? Constant.SUMMARY_SHARE : title) : summary);
        shareEntity.setTitle(TextUtils.isEmpty(title) ? Constant.TILE_SHARE : title);
        if (webView != null && webView.getUrl() != null) {
            shareEntity.setUrl(webView.getUrl());
        } else {
            shareEntity.setUrl(url);
        }
        shareEntity.setType(Constant.TYPE_SHARE_NEWS);
        String shareContent = gson.toJson(shareEntity);
        bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);

        LogUtil.d("shareContent = " + shareContent);
        IntentUtil.startActivity(this, ShareActivity.class, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            // webview 中的 刷新数据的
            if (TextUtils.equals("updateUserInfo", message.getLabel())) {  // reload
                if (null != webView) {
                    LogUtil.e("ua = " + webView.getSettings().getUserAgentString());
                    webView.reload();
                }
            } else if (TextUtils.equals("paySuccess", message.getLabel())) { // 支付成功
                LogUtil.e("发送来的 url = " + Constant.URL_RELOAD_WEBVIEW);
                if (!TextUtils.isEmpty(Constant.URL_RELOAD_WEBVIEW)) {
                    LogUtil.e("执行 。。。");
                    LogUtil.e("ua = " + webView.getSettings().getUserAgentString());
                    webView.loadUrl(Constant.URL_RELOAD_WEBVIEW);
                }
                //                webView.reload();
            } else if (TextUtils.equals("payFailure", message.getLabel())) { // 支付失败

                //  处理支付失败的逻辑
            } else if (TextUtils.equals("updateMobile", message.getLabel())) { // 手机号绑定成功
                if (null != webView) {
                    LogUtil.e("ua = " + webView.getSettings().getUserAgentString());
                    webView.reload();
                }
            }
            // 刷新页面
            else if (TextUtils.equals("refresh", message.getLabel())) {
                if (null != webView) {
                    webView.loadUrl(message.getContent());
                }
            } else if (TextUtils.equals(Constant.TAG_RESULT_SCAN, message.getLabel())) {
                Intent intent = message.getIntentData();
                if (null == intent) {
                    ToastUtil.showShortToast("无法识别的图片");
                } else {
                    if (webView != null) {
                        webView.parsePhoto(intent);
                    }
                }
                //                LogUtil.e("本地图片扫描信息 " + message.getContent());
            } else if (TextUtils.equals("videoPicture", message.getLabel())) {
                if (!EasyPermissions.hasPermissions(UrlDetailActivity.this, permissionList)) {
                    EasyPermissions.requestPermissions(UrlDetailActivity.this, "开屏新闻申请以下权限\n\n" +
                            "手机存储和相机\n" +
                            "拍照或上传图片，以及视频录制\n" +
                            "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", MWebView.CODE_VIDEO_2_REQUEST, permissionList);
                } else if (TextUtils.equals(Constant.TAG_DOWNLOAD_COMPLETE, message.getLabel())) {
                    if (isActivityFront) {
                        ToastUtil.showShortToast("下载完成");
                    }
                } else {
                    if (webView == null) {
                        return;
                    }
                    webView.setCallback(TextUtils.isEmpty(message.getCallBackMethod()) ? "" : message.getCallBackMethod());
                    minTimeLength = message.getMinTimeLength();
                    maxTimeLength = message.getMaxTimeLength();
                    webView.openInputFileVideo2(minTimeLength, maxTimeLength);
                }
            } else if (TextUtils.equals(Constant.TAG_DOWNLOAD_COMPLETE, message.getLabel())) {
                if (isActivityFront) {
                    ToastUtil.showShortToast("下载完成");
                }
            } else if (TextUtils.equals("html2App", message.getLabel())) {
                if (message.getHtml2AppEntity().getTitle() != null && !TextUtils.isEmpty(message.getHtml2AppEntity().getTitle()) && !isHasListTitle) {
                    this.title = message.getHtml2AppEntity().getTitle();
                    titleTv.setText(title);
                    isHasJsTitle = true;
                }
                if (message.getHtml2AppEntity().getSummary() != null && !TextUtils.isEmpty(message.getHtml2AppEntity().getSummary()) && !isHasListSummary) {
                    this.summary = message.getHtml2AppEntity().getSummary();
                }
            }
        }
    }

    // 恢复数据
    private void recoverData(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            LogUtil.e("恢复数据");
            if (!TextUtils.isEmpty(savedInstanceState.getString("avatar"))) {
                Constant.CW_AVATAR = savedInstanceState.getString("avatar");
            }
            if (!TextUtils.isEmpty(savedInstanceState.getString("token"))) {
                Constant.CW_AUTHORIZATION = savedInstanceState.getString("token");
                EventBus.getDefault().post(new EventMessage("updateUserInfo", "updateUserInfo"));
            }
            AppUtil.initPublicParams(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // 权限通过
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == MWebView.CODE_PIC_SINGLE_REQUEST) { // 单图
            if (webView != null) {
                webView.openInputFileAppImage();
            }
        } else if (requestCode == MWebView.CODE_PIC_MULTIPLE_REQUEST) { // 多图
            if (webView != null) {
                webView.openMultiplePic();
            }
        } else if (requestCode == MWebView.CODE_VIDEO_REQUEST) {// 视频
            if (webView != null) {
                webView.openInputFileVideo();
            }
        } else if (requestCode == MWebView.CODE_ALL_REQUEST) { // 所有图片
            if (webView != null) {
                webView.pickAllTypeFile();
            }
        } else if (requestCode == MWebView.CODE_VIDEO_2_REQUEST) { // 诡异的视频上传
            if (webView != null) {
                webView.openInputFileVideo2(minTimeLength, maxTimeLength);
            }
        } else if (requestCode == MWebView.CODE_REQUEST_PERMISSION) {
            if (EasyPermissions.hasPermissions(this, permissionList2) && webView != null) {
                if (!TextUtils.isEmpty(webView.getFilePath()) && webView.getFilePath().startsWith("http")) {
                    ToastUtil.showShortToast("下载开始");
                    webView.downLoadFile();
                }
            }
        }
    }

    // 权限拒绝
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == MWebView.CODE_PIC_SINGLE_REQUEST) { // 单图
            if (webView != null && null != webView.getFilePathCallback()) {
                webView.setFilePathCallback(null);
            }
        } else if (requestCode == MWebView.CODE_PIC_MULTIPLE_REQUEST) { // 多图
            if (webView != null && null != webView.getFilePathCallback()) {
                webView.setFilePathCallback(null);
            }
        } else if (requestCode == MWebView.CODE_VIDEO_REQUEST) {// 视频
            if (webView != null && null != webView.getFilePathCallback()) {
                webView.setFilePathCallback(null);
            }
        } else if (requestCode == MWebView.CODE_ALL_REQUEST) { // 所有图片
            if (webView != null) {
                webView.setFilePathCallback(null);
            }
        } else if (requestCode == MWebView.CODE_VIDEO_2_REQUEST) { // 诡异的视频上传
            if (webView != null && null != webView.getFilePathCallback()) {
                webView.setFilePathCallback(null);
            }
        } else if (requestCode == MWebView.CODE_REQUEST_PERMISSION) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                ToastUtil.showShortToast("权限拒绝，下载失败");
                new AppSettingsDialog.Builder(this)
                        .setTitle("权限请求")
                        .setRationale("开屏新闻需要获取SD卡读写权限用于读写缓存")
                        .build().show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("avatar", TextUtils.isEmpty(Constant.CW_AVATAR) ? "" : Constant.CW_AVATAR);
        outState.putString("token", TextUtils.isEmpty(Constant.CW_AUTHORIZATION) ? "" : Constant.CW_AUTHORIZATION);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LogUtil.e("可以回退  = " + webView.canGoBack());
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityFront = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityFront = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("onNewsIntent --->>>>");
    }

    @Override
    protected void onDestroy() {
        try {
            if (null != webView) {
                webView.destroy();
                webView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("出错 = " + e.getMessage());
        }
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        super.onDestroy();
    }
}
