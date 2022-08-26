package cn.cc1w.app.ui.ui.share;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.qrscan.util.CodeUtils;
import butterknife.BindView;
import cn.cc1w.app.ui.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.utils.videocut.ThreadPool2;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;

/**
 * 分享
 */
public class ShareActivity extends CustomActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.ll_qr_share)
    LinearLayout qrLinearLayout;
    @BindView(R.id.ll_cover_share)
    LinearLayout shareContainer;
    @BindView(R.id.ll_album_share)
    LinearLayout albumLayout;
    @BindView(R.id.img_qr_share)
    ImageView qrCodeImg;
    @BindView(R.id.img_top_share)
    ImageView topImg;
    @BindView(R.id.img_bottom_share)
    ImageView bottomImg;
    @BindView(R.id.img_post_share)
    ImageView coverImg;
    @BindView(R.id.txt_username_share)
    TextView paikewUsernameTv;
    @BindView(R.id.txt_describe_share)
    TextView paikewWorksDescribeTv;
    @BindView(R.id.img_play_share)
    ImageView playImg;
    private Unbinder unbinder;
    private ShareAction shareAction;
    private boolean isSharing;
    private boolean isResume;
    private ShareEntity shareEntity;
    private LoadingDialog loading;
    private static final String TYPE_SHARE_PLATFORM_WECHAT = "wechat";
    private static final String TYPE_SHARE_PLATFORM_WX_CIRCLE = "wxcircle";
    private static final String TYPE_SHARE_PLATFORM_SINA = "sina";
    private static final String TYPE_SHARE_PLATFORM_QQ = "qq";
    private static final String TYPE_SHARE_PLATFORM_QZONE = "qzone";
    private String sharePlatformType = TYPE_SHARE_PLATFORM_WECHAT;
    private boolean isCancle = false;
    private static final String SUFFIX_PIC = ".jpg";
    private static final int COMPRESS_QUALITY = 100;
    private final DisplayMetrics metrics = new DisplayMetrics();
    private long lastTime;
    private Bitmap shareBitmap = null;
    private String filePath;
    private boolean isQrCodeClick = false;
    private boolean isFirstCreateBitmap = true;
    private boolean isBitmapSaveSuccess = false;
    private boolean isPictureLoadFinish;
    private static final int PIC_SAVE_START = 1;
    private static final int PIC_SAVE_RESULT = 2;
    private static final int CODE_REQUEST_PERMISSION = 1;
    private final String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,};
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (null != msg) {
                if (msg.what == PIC_SAVE_START) {
                    if (null != loading) {
                        loading.show();
                    }
                } else if (msg.what == PIC_SAVE_RESULT) {
                    boolean isFinished = (boolean) msg.obj;
                    if (isFinished && !isFinishing() && null != loading) {
                        loading.close();
                        ToastUtil.showShortToast("保存成功");
                        finish();
                    } else {
                        ToastUtil.showShortToast("保存失败，请稍后重试");
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        ImmersionBar.with(this).transparentStatusBar().init();
        unbinder = ButterKnife.bind(this);
        initData();
        initLoading();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Gson gson = new Gson();
        lastTime = System.currentTimeMillis();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        String shareContent = getIntent().getStringExtra(Constant.TAG_SHARE_CONTENT);
        if (!TextUtils.isEmpty(shareContent)) {
            shareEntity = gson.fromJson(shareContent, ShareEntity.class);
            // 是否为paike 分享
            boolean isPaikewShare = shareEntity.isPaikewShare();
            if (isPaikewShare) {
                qrLinearLayout.setVisibility(View.VISIBLE);
                // 创建二维码；然后生成二维码
                ThreadPool2.getInstance().execute(() -> {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share_app);
                    Bitmap bitmap = CodeUtils.createQRCode(shareEntity.getUrl(), AppUtil.dip2px(this, 70), logo);
                    runOnUiThread(() -> {
                        //显示二维码
                        if (!isFinishing()) {
                            Glide.with(this).load(bitmap).into(qrCodeImg);
                        }
                    });
                });
                Glide.with(this).load(R.mipmap.ic_top_share).into(topImg);
                Glide.with(this).load(R.mipmap.ic_bottom_share).into(bottomImg);
                Glide.with(this).load(shareEntity.getPicUrl())
                        .into(new SimpleTarget<Drawable>(AppUtil.dip2px(ShareActivity.this, 110), AppUtil.dip2px(ShareActivity.this, 150)) {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (!isFinishing()) {
                                    coverImg.setImageDrawable(resource);
                                    isPictureLoadFinish = true;
                                }
                            }
                        });
                paikewUsernameTv.setText(TextUtils.isEmpty(shareEntity.getPaikewerUserName()) ? "" : ("@".concat(shareEntity.getPaikewerUserName())));
                if (!TextUtils.isEmpty(shareEntity.getPaikewType())) {
                    if (TextUtils.equals("pic", shareEntity.getPaikewType())) {
                        paikewWorksDescribeTv.setText(getString(R.string.paikew_pic_release_notification));
                    } else if (TextUtils.equals("video", shareEntity.getPaikewType())) {
                        paikewWorksDescribeTv.setText(getString(R.string.paikew_video_release_notification));
                        playImg.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
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
            if (!isCancle && !TextUtils.isEmpty(shareEntity.getNewsId()) && (TextUtils.equals("news", shareEntity.getType()))) {
                addShareSuccessRecord();
            }
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            isCancle = false;
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            isCancle = true;
            finish();
        }
    };

    @OnClick({
            R.id.relate_share_friend_share,
            R.id.ll_share_friend_wx,
            R.id.ll_share_circle_wx,
            R.id.ll_share_sina,
            R.id.ll_share_friend_qq,
            R.id.ll_share_copy,
            R.id.txt_share_cancel,
            R.id.ll_qr_share,
            R.id.ll_album_share
    })
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.relate_share_friend_share) {
                finish();
            } else if (id == R.id.ll_share_friend_wx) {
                share2WxFriend();
            } else if (id == R.id.ll_share_circle_wx) {
                share2WxCircle();
            } else if (id == R.id.ll_share_sina) {
                share2Sina();
            } else if (id == R.id.ll_share_friend_qq) {
                share2QQ();
            } else if (id == R.id.ll_share_circle_qq) {
                share2QQZone();
            } else if (id == R.id.ll_share_copy) {
                copyUrl();
            } else if (id == R.id.ll_qr_share) {
                showShareCover();
            } else if (id == R.id.ll_album_share) {
                savePic2SdCard();
            } else if (id == R.id.txt_share_cancel) {
                finish();
            }
        }
        lastTime = currentTime;
    }

    /**
     * 图片保存到本地
     */
    private void savePic2SdCard() {
        if (isPictureLoadFinish) {
            handler.postDelayed(() -> {
                shareContainer.setDrawingCacheEnabled(true);
                shareContainer.buildDrawingCache();
                shareBitmap = shareContainer.getDrawingCache();
                Message message = Message.obtain();
                message.what = PIC_SAVE_START;
                handler.sendMessage(message);
                if (null != shareBitmap) {
                    if (EasyPermissions.hasPermissions(this, permissionList)) {
                        saveBitmap2File(shareBitmap);
                    } else {
                        EasyPermissions.requestPermissions(this, "开屏新闻需要获取SD卡读写权限用于读写缓存", CODE_REQUEST_PERMISSION, permissionList);
                    }
                }
            }, 200);
        } else {
            ToastUtil.showShortToast("拍客生成中，请稍后");
        }
    }

    // 显示 分享的 cover
    private void showShareCover() {
        if (isFirstCreateBitmap) {
            isQrCodeClick = true;
            isFirstCreateBitmap = false;
            shareContainer.setVisibility(View.VISIBLE);
            qrLinearLayout.setVisibility(View.GONE);
            albumLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 将 bitmap 保存为 file
     *
     * @param bitmap 目标 bitmap
     */
    private void saveBitmap2File(Bitmap bitmap) {
        // 图片地址
        if (getExternalCacheDir() != null) {
            filePath = getExternalCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + SUFFIX_PIC;
            File file = new File(filePath);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, bos);
                bos.flush();
                bos.close();
                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            file.getAbsolutePath(), file.getName(), null);
                    isBitmapSaveSuccess = true;
                    Message message = Message.obtain();
                    message.obj = true;
                    message.what = PIC_SAVE_RESULT;
                    handler.sendMessage(message);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.showShortToast("图片保存失败,请稍后重试");
                    Message message = Message.obtain();
                    message.obj = false;
                    message.what = PIC_SAVE_RESULT;
                    handler.sendMessage(message);
                }
                // 最后通知图库更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            } catch (IOException e) {
                e.printStackTrace();
                isBitmapSaveSuccess = false;
                Message message = Message.obtain();
                message.obj = false;
                message.what = PIC_SAVE_RESULT;
                handler.sendMessage(message);
            }
        }
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
                isSharing = true;
                try {
                    sharePlatformType = TYPE_SHARE_PLATFORM_WECHAT;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
                    shareAction.setCallback(umShareListener);
                    if (isQrCodeClick) { // 点击过 显示二维码
                        if (null != shareBitmap && isBitmapSaveSuccess) {
                            File file = new File(filePath);
                            UMImage umImage = new UMImage(this, file);
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        }
                    } else {
                        LogUtil.d("------>>> " + shareEntity.toString());
                        UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                        umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                        umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());

                        UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share_app));
                        umWeb.setThumb(umImage);
                        shareAction.withMedia(umWeb);
                        shareAction.share();
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
                isSharing = true;
                try {
                    sharePlatformType = TYPE_SHARE_PLATFORM_WX_CIRCLE;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                    shareAction.setCallback(umShareListener);
                    if (isQrCodeClick) {
                        if (null != shareBitmap && isBitmapSaveSuccess) {
                            File file = new File(filePath);
                            UMImage umImage = new UMImage(this, file);
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        }
                    } else {
                        UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                        umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                        umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());
                        UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share_app));
                        umWeb.setThumb(umImage);
                        shareAction.withMedia(umWeb);
                        shareAction.share();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 分享数据格式有问题
                Toast.makeText(this, "分享异常，请安装最新版本微信", Toast.LENGTH_SHORT).show();
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
                isSharing = true;
                try {
                    sharePlatformType = TYPE_SHARE_PLATFORM_SINA;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.SINA);
                    shareAction.setCallback(umShareListener);

                    if (isQrCodeClick) {
                        if (null != shareBitmap && isBitmapSaveSuccess) {
                            File file = new File(filePath);
                            UMImage umImage = new UMImage(this, file);
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        }
                    } else {
                        UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                        umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                        umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());

                        UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share_app));
                        umWeb.setThumb(umImage);
                        shareAction.withMedia(umWeb);
                        shareAction.share();
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
            //  判断是否安装QQ
            if (!AppUtil.isAvilible(this, "com.tencent.mobileqq")) {
                ToastUtil.showShortToast("请安装最新版 QQ");
                return;
            }
            if (null != shareEntity) {
                isSharing = true;
                try {
                    sharePlatformType = TYPE_SHARE_PLATFORM_QQ;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.QQ);
                    shareAction.setCallback(umShareListener);
                    if (isQrCodeClick) { // 点击过 显示二维码
                        if (null != shareBitmap && isBitmapSaveSuccess) {
                            File file = new File(filePath);
                            UMImage umImage = new UMImage(this, file);
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        }
                    } else {
                        UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                        umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                        umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());

                        UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share_app));
                        umWeb.setThumb(umImage);
                        shareAction.withMedia(umWeb);
                        shareAction.share();
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
     * 分享到QQ空间
     */
    private void share2QQZone() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != shareEntity) {
                isSharing = true;
                try {
                    sharePlatformType = TYPE_SHARE_PLATFORM_QZONE;
                    shareAction = new ShareAction(this);
                    shareAction.setPlatform(SHARE_MEDIA.QZONE);
                    shareAction.setCallback(umShareListener);

                    if (isQrCodeClick) { // 点击过 显示二维码
                        if (null != shareBitmap && isBitmapSaveSuccess) {

                            File file = new File(filePath);
                            UMImage umImage = new UMImage(this, file);
                            shareAction.withMedia(umImage);
                            shareAction.share();
                        }
                    } else {
                        UMWeb umWeb = new UMWeb(shareEntity.getUrl());
                        umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
                        umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());

                        UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share_app));
                        umWeb.setThumb(umImage);
                        shareAction.withMedia(umWeb);
                        shareAction.share();
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
     * 复制当前的 url
     */
    private void copyUrl() {
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (null != clipboardManager && null != shareEntity) {
            // 监听 剪切板，可以去除敏感词
            clipboardManager.addPrimaryClipChangedListener(() -> {
                ClipData.Item itemAt = clipboardManager.getPrimaryClip().getItemAt(0);
                LogUtil.e(itemAt.toString());
            });
            ClipData clipData = ClipData.newPlainText("save", shareEntity.getUrl());
            clipboardManager.setPrimaryClip(clipData);
            ToastUtil.showShortToast("链接复制成功");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isSharing) {
            isSharing = false;
            handler.postDelayed(() -> {
                if (!isResume) {
                    if (TextUtils.equals(sharePlatformType, TYPE_SHARE_PLATFORM_WECHAT)
                            || TextUtils.equals(sharePlatformType, TYPE_SHARE_PLATFORM_SINA)
                            && (null != shareEntity)
                            && (!TextUtils.isEmpty(shareEntity.getNewsId()))
                            && (TextUtils.equals("news", shareEntity.getType()))) {
                        addShareSuccessRecord();
                        if (null != loading && loading.isShow()) {
                            loading.close();
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

    /**
     * 添加分享成功的回调记录
     */
    private void addShareSuccessRecord() {
        if (NetUtil.isNetworkConnected(this)) {
            if (TextUtils.isEmpty(shareEntity.getNewsId())) {
                return;
            }
            RxHttp.postJson(Constant.SUCCESS_SHARE)
                    .add("cw_news_id", shareEntity.getNewsId()).add("cw_type", shareEntity.getType()).add("cw_share_type", "share")
                    .add("cw_platform", sharePlatformType).add("cw_share_url", shareEntity.getUrl()).add("cw_title", shareEntity.getTitle()).add("cw_summary", shareEntity.getSummary())
                    .asResponse(JsonObject.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        EventBus.getDefault().post(new EventMessage("reload", shareEntity.getRedirect_url()));
                        finish();
                    }, (OnError) error -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSharing = false;
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PERMISSION && EasyPermissions.hasPermissions(this, permissionList)) {
            if (shareBitmap != null) {
                saveBitmap2File(shareBitmap);
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PERMISSION && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限请求")
                    .setRationale("开屏新闻需要获取SD卡读写权限用于读写缓存")
                    .build().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0, 0);
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        handler.removeCallbacksAndMessages(null);
        UMShareAPI.get(this).release();
        unbinder.unbind();
    }
}