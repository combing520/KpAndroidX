package cn.cc1w.app.ui.ui.home.record;

import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.gson.Gson;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.MWebView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 推广详情
 *
 * @author kpinfo
 */
public class PromotionDetailActivity extends CustomActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.webView_promotion_detail)
    MWebView webView;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    private Unbinder unbinder;
    private String title;
    private String url;
    private String newsId;
    private String summary;
    private int minTimeLength = 1, maxTimeLength = 60;
    private static final String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recoverData(savedInstanceState);
        setContentView(R.layout.activity_promotion_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initNavigation();
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
        summary =
                TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_SUMMARY)) ? "" : getIntent().getStringExtra(Constant.TAG_SUMMARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (webView != null) {
            webView.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.img_share_header_not_title})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            if (null != webView && webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        } else if (id == R.id.img_share_header_not_title) {
            share();
        }
    }

    private void share() {
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setNewsId(TextUtils.isEmpty(newsId) ? "" : newsId);
        shareEntity.setRedirect_url("");
        shareEntity.setSummary(TextUtils.isEmpty(summary) ? (TextUtils.isEmpty(title) ? Constant.SUMMARY_SHARE : title) : summary);
        shareEntity.setTitle(TextUtils.isEmpty(title) ? Constant.TILE_SHARE : title);
//        shareEntity.setUrl(TextUtils.isEmpty(url) ? "" : url);
        if (webView != null && webView.getUrl() != null) {
            shareEntity.setUrl(webView.getUrl());
        } else {
            shareEntity.setUrl(url);
        }
        shareEntity.setType(Constant.TYPE_SHARE_NEWS);
        String shareContent = gson.toJson(shareEntity);
        bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
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
                    webView.parsePhoto(intent);
                }
                //                LogUtil.e("本地图片扫描信息 " + message.getContent());
            } else if (TextUtils.equals("videoPicture", message.getLabel())) {
                if (!EasyPermissions.hasPermissions(PromotionDetailActivity.this, permissionList)) {
                    EasyPermissions.requestPermissions(PromotionDetailActivity.this, "开屏新闻申请以下权限\n\n" +
                            "手机存储和相机\n" +
                            "拍照或上传图片，以及视频录制\n" +
                            "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", MWebView.CODE_VIDEO_2_REQUEST, permissionList);
                } else {
                    webView.setCallback(TextUtils.isEmpty(message.getCallBackMethod()) ? "" : message.getCallBackMethod());
                    minTimeLength = message.getMinTimeLength();
                    maxTimeLength = message.getMaxTimeLength();
                    webView.openInputFileVideo2(minTimeLength, maxTimeLength);
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
        }
    }

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
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != webView && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != webView) {
                ((ViewGroup) webView.getParent()).removeView(webView);
                webView.destroy();
                webView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("出错 = " + e.getMessage());
        }
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}