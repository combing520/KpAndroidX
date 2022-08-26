package cn.cc1w.app.ui.ui.share;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;


import org.greenrobot.eventbus.EventBus;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.WebShareEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;

/**
 * 给网页调用的分享
 */
public class WebCallShareActivity extends CustomActivity {
    @BindView(R.id.ll_share_friend_wx)
    LinearLayout wxFriendLayout;
    @BindView(R.id.ll_share_circle_wx)
    LinearLayout wxCircleLayout;
    @BindView(R.id.ll_share_sina)
    LinearLayout sinaLayout;
    @BindView(R.id.ll_share_friend_qq)
    LinearLayout qqLayout;
    @BindView(R.id.ll_share_friend_mini)
    LinearLayout wxMiniLayout;

    private Unbinder unbinder;
    private WebShareEntity shareEntity;
    private long lastTime;
    private ShareAction shareAction;

    //  分享的平台类型  微信
    private static final String TYPE_SHARE_PLATFORM_WECHAT = "wechat";
    //  分享的平台类型  微信朋友圈
    private static final String TYPE_SHARE_PLATFORM_WX_CIRCLE = "wxcircle";
    // 分享的平台类型  新浪微博
    private static final String TYPE_SHARE_PLATFORM_SINA = "sina";
    // 分享的平台类型  qq
    private static final String TYPE_SHARE_PLATFORM_QQ = "qq";
    private static final String TYPE_SHARE_IMG = "pic"; //pic
    private static final String TYPE_SHARE_VIDEO = "video"; // 视频
    private static final String TYPE_SHARE_URL = "web"; // 网页
    private static final String PLATFORM_SHARE_WX = "wxFriend"; // 微信好友
    private static final String PLATFORM_SHARE_CIRCLE_WX = "wxCircle";// 微信朋友圈
    private static final String PLATFORM_SHARE_QQ = "qq";// qq
    private static final String PLATFORM_SHARE_SINA = "sina";// 新浪微博
    private static final String PLATFORM_SHARE_MINI_WX = "wxMini";// 微信小程序
    private LoadingDialog loading;
    private boolean isSharing;
    private boolean isResume;
    private String sharePlatformType = TYPE_SHARE_PLATFORM_WECHAT;
    private final Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_call_share);
        init();
    }

    private void init() {
        overridePendingTransition(0, 0);
        unbinder = ButterKnife.bind(this);
        initData();
        initLoading();
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String shareContent = getIntent().getStringExtra(Constant.TAG_SHARE_CONTENT);
        shareEntity = JsonUtil.getObject(shareContent, WebShareEntity.class);
        lastTime = System.currentTimeMillis();
        if (null != shareEntity) {
            // 获取分享的平台
            String functions = shareEntity.getFunctions();
            if (!functions.isEmpty()) { // 传了平台
                if (functions.contains(",")) { // 有多个
                    String[] functionList = functions.split(",", -1);
                    for (String fun : functionList) {
                        if (TextUtils.equals(fun, PLATFORM_SHARE_WX)) { //微信好友
                            wxFriendLayout.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals(fun, PLATFORM_SHARE_CIRCLE_WX)) { // 微信朋友圈
                            wxCircleLayout.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals(fun, PLATFORM_SHARE_QQ)) { // QQ
                            qqLayout.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals(fun, PLATFORM_SHARE_SINA)) { // 新浪微博
                            sinaLayout.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals(fun, PLATFORM_SHARE_MINI_WX)) { // 微信小程序
                            wxMiniLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } else { // 单个 判断平台
                    if (TextUtils.equals(functions, PLATFORM_SHARE_WX)) { //微信好友
                        wxFriendLayout.setVisibility(View.VISIBLE);
                    } else if (TextUtils.equals(functions, PLATFORM_SHARE_CIRCLE_WX)) { // 微信朋友圈
                        wxCircleLayout.setVisibility(View.VISIBLE);
                    } else if (TextUtils.equals(functions, PLATFORM_SHARE_QQ)) { // QQ
                        qqLayout.setVisibility(View.VISIBLE);
                    } else if (TextUtils.equals(functions, PLATFORM_SHARE_SINA)) { // 新浪微博
                        sinaLayout.setVisibility(View.VISIBLE);
                    } else if (TextUtils.equals(functions, PLATFORM_SHARE_MINI_WX)) { // 微信小程序
                        wxMiniLayout.setVisibility(View.VISIBLE);
                    }
                }

            } else {
                wxFriendLayout.setVisibility(View.VISIBLE);
                wxCircleLayout.setVisibility(View.VISIBLE);
                qqLayout.setVisibility(View.VISIBLE);
                sinaLayout.setVisibility(View.VISIBLE);
            }
        } else {
            finish();
        }
    }

    @OnClick({
            R.id.ll_share_circle_wx,
            R.id.ll_share_friend_wx,
            R.id.ll_share_sina,
            R.id.ll_share_friend_qq,
            R.id.txt_share_cancel,
            R.id.ll_share_friend_mini

    })
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.ll_share_circle_wx) { // 微信朋友圈
                share2WxCircle();
            } else if (id == R.id.ll_share_friend_wx) { // 微信好友
                share2WxFriend();
            } else if (id == R.id.ll_share_sina) { // 新浪微博
                share2Sina();
            } else if (id == R.id.ll_share_friend_qq) { // QQ 好友
                share2QQ();
            } else if (id == R.id.txt_share_cancel) { // 取消
                finish();
            } else if (id == R.id.ll_share_friend_mini) { // 小程序
                shareWxMiniProgram();
            }
        }
        lastTime = currentTime;
    }

    /**
     * 分享到微信好友
     */
    private void share2WxFriend() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != shareEntity) {
                if (!AppUtil.isAvilible(this, "com.tencent.mm")) {
                    ToastUtil.showShortToast("请安装最新版微信");
                    return;
                }
                try {
                    isSharing = true;
                    sharePlatformType = TYPE_SHARE_PLATFORM_WECHAT;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
                    shareAction.setCallback(umShareListener);
                    if (!TextUtils.isEmpty(shareEntity.getType()) && !isFinishing()) {
                        if (TYPE_SHARE_IMG.equals(shareEntity.getType())) {// 分享图片
                            UMImage umImage = new UMImage(this, shareEntity.getPicPath());
                            umImage.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        } else if (TYPE_SHARE_URL.equals(shareEntity.getType())) { // 分享网页
                            UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                            umWeb.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(umWeb);
                            shareAction.share();
                        } else if (TYPE_SHARE_VIDEO.equals(shareEntity.getType())) { // 分享视频
                            UMVideo video = new UMVideo(shareEntity.getUrl());
                            video.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            video.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            video.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(video);
                            shareAction.share();
                        }
                    } else {
                        ToastUtil.showShortToast("无法获取分享类型");
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "分享异常，请安装最新版本微信", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtil.showLongToast(getString(R.string.network_error));
        }
    }

    /**
     * 分享到微信朋友圈
     */
    private void share2WxCircle() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != shareEntity) {
                if (!AppUtil.isAvilible(this, "com.tencent.mm")) {
                    ToastUtil.showShortToast("请安装最新版微信");
                    return;
                }
                try {
                    isSharing = true;
                    sharePlatformType = TYPE_SHARE_PLATFORM_WX_CIRCLE;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                    shareAction.setCallback(umShareListener);
                    if (!TextUtils.isEmpty(shareEntity.getType()) && !isFinishing()) {
                        if (TYPE_SHARE_IMG.equals(shareEntity.getType())) {// 分享图片
                            UMImage umImage = new UMImage(this, shareEntity.getPicPath());
                            umImage.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        } else if (TYPE_SHARE_URL.equals(shareEntity.getType())) { // 分享网页
                            UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                            umWeb.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(umWeb);
                            shareAction.share();
                        } else if (TYPE_SHARE_VIDEO.equals(shareEntity.getType())) { // 分享视频
                            UMVideo video = new UMVideo(shareEntity.getUrl());
                            video.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            video.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            video.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(video);
                            shareAction.share();
                        }
                    } else {
                        ToastUtil.showShortToast("无法获取分享类型");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //分享数据格式有问题
                Toast.makeText(this, "分享异常，请安装最新版本微信", Toast.LENGTH_SHORT).show();
            }
        } else {
            ToastUtil.showLongToast(getString(R.string.network_error));
        }
    }

    // 分享到小程序
    private void shareWxMiniProgram() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != shareEntity) {
                if (!AppUtil.isAvilible(this, "com.tencent.mm")) {
                    ToastUtil.showShortToast("请安装最新版微信");
                    return;
                }
                try {
                    isSharing = true;
                    sharePlatformType = TYPE_SHARE_PLATFORM_WECHAT;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
                    shareAction.setCallback(umShareListener);
                    if (!TextUtils.isEmpty(shareEntity.getType())
                            && TextUtils.equals(shareEntity.getType(), PLATFORM_SHARE_MINI_WX)
                            && !isFinishing()) {
//                    // 兼容低版本的微信的网页分享
                        if (TextUtils.isEmpty(shareEntity.getWxUserName())) {
                            ToastUtil.showShortToast("无法获取小程序 原始ID");
                        } else if (TextUtils.isEmpty(shareEntity.getWxMiniPath())) {
                            ToastUtil.showShortToast("无法获取小程序分享页面");
                        } else {
                            // 兼容低版本的网页链接
                            UMMin umMin = new UMMin(TextUtils.isEmpty(shareEntity.getWebpageUrl()) ? shareEntity.getWxMiniPath() : shareEntity.getWebpageUrl());
                            // 小程序的海报
                            if (!TextUtils.isEmpty(shareEntity.getWxMiniImg())) {
                                umMin.setThumb(new UMImage(this, shareEntity.getWxMiniImg()));
                            } else {
                                umMin.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            }
                            // 小程序 的 tile
                            umMin.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            // 小程序的 summary 描述
                            umMin.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            // 小程序的对应页面
                            umMin.setPath(shareEntity.getWxMiniPath());
                            // 小程序原始ID获取方法：登录小程序管理后台-设置-基本设置-帐号信息
                            umMin.setUserName(shareEntity.getWxUserName());

                            shareAction.withMedia(umMin);
                            shareAction.share();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("分享小程序出错  ");
                }
            }
        } else {
            ToastUtil.showLongToast(getString(R.string.network_error));
        }
    }

    /**
     * 分享到新浪微博
     */
    private void share2Sina() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != shareEntity) {
                try {
                    isSharing = true;
                    sharePlatformType = TYPE_SHARE_PLATFORM_SINA;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.SINA);
                    shareAction.setCallback(umShareListener);
                    if (!TextUtils.isEmpty(shareEntity.getType()) && !isFinishing()) {
                        if (TYPE_SHARE_IMG.equals(shareEntity.getType())) {// 分享图片
                            UMImage umImage = new UMImage(this, shareEntity.getPicPath());
                            umImage.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        } else if (TYPE_SHARE_URL.equals(shareEntity.getType())) { // 分享网页
                            UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                            umWeb.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(umWeb);
                            shareAction.share();
                        } else if (TYPE_SHARE_VIDEO.equals(shareEntity.getType())) { // 分享视频
                            UMVideo video = new UMVideo(shareEntity.getUrl());
                            video.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            video.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            video.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(video);
                            shareAction.share();
                        }
                    } else {
                        ToastUtil.showShortToast("无法获取分享类型");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtil.showLongToast(getString(R.string.network_error));
        }
    }

    /**
     * 分享到QQ
     */
    private void share2QQ() {
        if (NetUtil.isNetworkConnected(this)) {
            if (!AppUtil.isAvilible(this, "com.tencent.mobileqq")) {
                ToastUtil.showShortToast("请安装最新版 QQ");
                return;
            }
            if (null != shareEntity) {
                try {
                    isSharing = true;
                    sharePlatformType = TYPE_SHARE_PLATFORM_QQ;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.QQ);
                    shareAction.setCallback(umShareListener);
                    if (!TextUtils.isEmpty(shareEntity.getType()) && !isFinishing()) {
                        if (TYPE_SHARE_IMG.equals(shareEntity.getType())) {// 分享图片
                            UMImage umImage = new UMImage(this, shareEntity.getPicPath());
                            umImage.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        } else if (TYPE_SHARE_URL.equals(shareEntity.getType())) { // 分享网页
                            UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                            umWeb.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(umWeb);
                            shareAction.share();
                        } else if (TYPE_SHARE_VIDEO.equals(shareEntity.getType())) { // 分享视频
                            UMVideo video = new UMVideo(shareEntity.getUrl());
                            video.setThumb(new UMImage(this, R.mipmap.ic_share_app));
                            video.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                            video.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                            shareAction.withMedia(video);
                            shareAction.share();
                        }
                    } else {
                        ToastUtil.showShortToast("无法获取分享类型");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtil.showLongToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private final UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            if (null != loading) {
                loading.show();
            }
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            if (null != shareEntity && !TextUtils.isEmpty(shareEntity.getRedirect_url()) && !isFinishing()) {
                EventBus.getDefault().post(new EventMessage("refresh", shareEntity.getRedirect_url()));
            }
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            finish();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSharing = false;
        isResume = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isSharing) {
            isSharing = false;
            handler.postDelayed(() -> {
                // 如果0.2秒后没有调用onResume，则认为是分享成功并且留着微信。
                if (!isResume) {  //留在 微信 进行的操作
                    if (TextUtils.equals(sharePlatformType, TYPE_SHARE_PLATFORM_WECHAT)
                            && (null != shareEntity)
//                            && (!TextUtils.isEmpty(shareEntity.getNewsId()))
                            && (TextUtils.equals("news", shareEntity.getType()))) {
//                        LogUtil.e("分享成功，留在微信");

                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (null != shareEntity && !TextUtils.isEmpty(shareEntity.getRedirect_url()) && !isFinishing()) {
                            EventBus.getDefault().post(new EventMessage("refresh", shareEntity.getRedirect_url()));
                        }
                        finish();
                    }
                }
            }, 200);
        }
        if (null != loading && loading.isShow()) {
            loading.close();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        handler.removeCallbacksAndMessages(null);
        UMShareAPI.get(this).release();
        unbinder.unbind();
        super.onDestroy();
    }
}