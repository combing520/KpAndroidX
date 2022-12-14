package cn.cc1w.app.ui.ui.usercenter.setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;

import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.rxjava.rxlife.RxLife;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AccountManageUserInfoEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.pictureSelector.GlideEngine;
import cn.cc1w.app.ui.utils.pictureSelector.MeSandboxFileEngine;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.SignFileUploadResponEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.AccountUnsubscribeMainActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import cn.cc1w.app.ui.R;
import rxhttp.RxHttp;
import rxhttp.RxHttpFormParam;
import rxhttp.RxHttpJsonParam;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * ????????????
 */
public class AccountManagerActivity extends CustomActivity implements EasyPermissions.PermissionCallbacks {
    private Unbinder unbinder;
    @BindView(R.id.ll_avatar_accountManage)
    LinearLayout ll_avatar_accountManage;
    @BindView(R.id.relate_qq_accountManage)
    RelativeLayout qqLayout;
    @BindView(R.id.relate_wx_accountManage)
    RelativeLayout wxLayout;
    @BindView(R.id.relate_sina_accountManage)
    RelativeLayout sinaLayout;
    @BindView(R.id.img_avatar_accountManage)
    RoundAngleImageView avatarImg;
    @BindView(R.id.txt_nickname_accountManage)
    TextView nicknameTv;
    @BindView(R.id.txt_phone_accountManage)
    TextView phoneNumberTv;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_wx_accountManage)
    TextView wxBindStateTv;
    @BindView(R.id.txt_qq_accountManage)
    TextView qqBindStateTv;
    @BindView(R.id.txt_sina_accountManage)
    TextView sinaBindStateTv;
    @BindView(R.id.relate_pwd_accountManage)
    RelativeLayout passwordModifyLayout;
    private PopupWindow popWindow;
    private String phoneNumberStr = "";
    private static final int CODE_REQUEST_PIC = 100;
    private static final int CODE_REQUEST_CAMERA = 200;
    private static final int CODE_REQUEST_PHONE = 300;
    private LoadingDialog loading;
    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;
    private static final String SEX_MALE = "???";
    private static final String TYPE_SEX_MALE = "1";
    private static final String TYPE_SEX_FEMALE = "0";
    private long lastTime;

    private final PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
    private final ImageEngine imageEngine = GlideEngine.createGlideEngine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        init();
    }

    /**
     * ?????????
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initNavigation();
        initLoading();
        initPopupWindow();
        getUserInfo();
        lastTime = System.currentTimeMillis();
    }

    /**
     * ????????? ???????????????
     */
    private void initNavigation() {
        titleTv.setText("????????????");
    }

    /**
     * ????????? loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * ??????????????????
     */
    private void getUserInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.USER_INFO_ACCONT_MANAGE)
                    .asResponse(AccountManageUserInfoEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(item -> {
                        if (!isFinishing()) {
                            if (null != item) {
                                AppUtil.loadAvatarImg(item.getHeadpic(), avatarImg);
                                nicknameTv.setText(TextUtils.isEmpty(item.getNickname()) ? "" : item.getNickname());
                                if (item.getMobile().length() > 4) {
                                    phoneNumberTv.setText((item.getMobile().substring(0, 3) + "****" + item.getMobile().substring(7)));
                                    LogUtil.e(" ???????????? = " + item.getMobile() + " ----> " + (item.getMobile().substring(0, 3) + "****" + item.getMobile().substring(7)));
                                }
                                phoneNumberStr = TextUtils.equals(item.getMobile(), "****") ? "" : item.getMobile();
                                if (TextUtils.equals("1", item.getStatus())) {
                                    List<AccountManageUserInfoEntity.UserRight> list = item.getList();
                                    if (null != list && !list.isEmpty()) {
                                        for (AccountManageUserInfoEntity.UserRight userRight : list) {
                                            if (TextUtils.equals("qq", userRight.getSource())) {
                                                qqLayout.setClickable(false);
                                                qqBindStateTv.setText("?????????");
                                                qqLayout.setVisibility(View.VISIBLE);
                                            } else if (TextUtils.equals("mp", userRight.getSource())) {
                                                wxLayout.setClickable(false);
                                                wxBindStateTv.setText("?????????");
                                                wxLayout.setVisibility(View.VISIBLE);
                                            } else if (TextUtils.equals("weibo", userRight.getSource())) {
                                                sinaLayout.setClickable(false);
                                                sinaBindStateTv.setText("?????????");
                                                sinaLayout.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                } else {
                                    passwordModifyLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    }, (OnError) error -> {
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            finish();
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        }
    }

    /**
     * ????????? PopupWindow
     */
    @SuppressLint("InflateParams")
    private void initPopupWindow() {
        View popView = LayoutInflater.from(this).inflate(R.layout.dialog_avatar_picker, null);
        if (null != popView) {
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            popWindow = new PopupWindow(popView, width, height);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);
            popView.findViewById(R.id.txt_photo_dialog).setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String[] permissionList = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    };
                    if (EasyPermissions.hasPermissions(this, permissionList)) {
                        getPictureFromCamera();
                    } else {
                        EasyPermissions.requestPermissions(this, "??????????????????????????????\n\n" +
                                "?????????????????????\n" +
                                "?????????????????????\n" +
                                "????????????\n" +
                                "???????????????????????????????????????????????????????????????????????????????????????????????????????????????", CODE_REQUEST_CAMERA, permissionList);
                    }

                } else {
                    getPictureFromCamera();
                }
                popWindow.dismiss();
            });

            popView.findViewById(R.id.txt_album_dialog).setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String[] permissionList = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    };
                    // ??????????????????
                    if (EasyPermissions.hasPermissions(this, permissionList)) {
                        getPictureFromAlbum();
                    } else {
                        EasyPermissions.requestPermissions(this, "??????????????????????????????\n\n" +
                                "?????????????????????\n" +
                                "?????????????????????\n" +
                                "????????????\n" +
                                "???????????????????????????????????????????????????????????????????????????????????????????????????????????????", CODE_REQUEST_PIC, permissionList);
                    }
                } else {
                    getPictureFromAlbum();
                }
                popWindow.dismiss();
            });
            popView.findViewById(R.id.txt_cancel_dialog).setOnClickListener(v -> popWindow.dismiss());
        }
    }

    /**
     * ??? ?????????????????????
     */
    private void getPictureFromCamera() {
        PictureSelector.create(this).openCamera(SelectMimeType.ofImage())
                .isQuickCapture(true)
                .setCropEngine((fragment, srcUri, destinationUri, dataSource, requestCode) -> {
                    UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
                    uCrop.setImageEngine(new UCropImageEngine() {
                        @Override
                        public void loadImage(Context context, String url, ImageView imageView) {
                            Glide.with(context).load(url).override(100, 100).into(imageView);
                        }

                        @Override
                        public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                            Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (call != null) {
                                        call.onCall(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    if (call != null) {
                                        call.onCall(null);
                                    }
                                }
                            });
                        }
                    });
                    uCrop.start(fragment.requireActivity(), fragment, requestCode);
                })
                .setCompressEngine((CompressFileEngine) (context, source, call) -> Luban.with(context).load(source).ignoreBy(100)
                        .setCompressListener(new OnNewCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String source, File compressFile) {
                                if (call != null) {
                                    call.onCallback(source, compressFile.getAbsolutePath());
                                }
                            }

                            @Override
                            public void onError(String source, Throwable e) {
                                if (call != null) {
                                    call.onCallback(source, null);
                                }
                            }
                        }).launch())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .setLanguage(0)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(ArrayList<LocalMedia> selectList) {
                if (null != selectList && !selectList.isEmpty()) {
                    String picPath;
                    LocalMedia item = selectList.get(0);
                    if (!TextUtils.isEmpty(item.getCompressPath())) {
                        picPath = item.getCompressPath();
                    } else {
                        picPath = item.getPath();
                    }
                    uploadPic2QukanServer(picPath);
                }
                LogUtil.d("onResult !!! ");
            }

            @Override
            public void onCancel() {
                LogUtil.d("PictureSelector onCancel !!! ");
            }
        });
    }

    /**
     * ??? ?????????????????????
     */
    private void getPictureFromAlbum() {
        PictureSelector.create(this).openGallery(SelectMimeType.ofImage()).setSelectorUIStyle(selectorStyle)
                .isQuickCapture(true)
                .setImageEngine(imageEngine)
                .setCropEngine((fragment, srcUri, destinationUri, dataSource, requestCode) -> {
                    UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
                    uCrop.setImageEngine(new UCropImageEngine() {
                        @Override
                        public void loadImage(Context context, String url, ImageView imageView) {
                            Glide.with(context).load(url).override(100, 100).into(imageView);
                        }

                        @Override
                        public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                            Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (call != null) {
                                        call.onCall(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    if (call != null) {
                                        call.onCall(null);
                                    }
                                }
                            });
                        }
                    });
                    uCrop.start(fragment.requireActivity(), fragment, requestCode);
                })
                .setCompressEngine((CompressFileEngine) (context, source, call) -> Luban.with(context).load(source).ignoreBy(100)
                        .setCompressListener(new OnNewCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String source, File compressFile) {
                                if (call != null) {
                                    call.onCallback(source, compressFile.getAbsolutePath());
                                }
                            }

                            @Override
                            public void onError(String source, Throwable e) {
                                if (call != null) {
                                    call.onCallback(source, null);
                                }
                            }
                        }).launch())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .setMaxSelectNum(1).forResult(new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(ArrayList<LocalMedia> selectList) {
                if (null != selectList && !selectList.isEmpty()) {
                    String picPath;
                    LocalMedia item = selectList.get(0);
                    if (!TextUtils.isEmpty(item.getCompressPath())) {
                        picPath = item.getCompressPath();
                    } else {
                        picPath = item.getPath();
                    }
                    uploadPic2QukanServer(picPath);
                }
                LogUtil.d("onResult !!! ");
            }

            @Override
            public void onCancel() {
                LogUtil.d("PictureSelector onCancel !!! ");
            }
        });
    }

    /**
     * ?????? ???????????????
     */
    private void showAvatarPicker() {
        showPopWindow();
    }

    /**
     * ?????? popWindow
     */
    private void showPopWindow() {
        if (null == popWindow) {
            initPopupWindow();
        }
        popWindow.showAtLocation(ll_avatar_accountManage, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * ?????????????????????????????? ?????????
     *
     * @param picPath ???????????????
     */
    private void uploadPic2QukanServer(String picPath) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            LogUtil.d("uploadPic2QukanServer  picPath = " + picPath);
            RxHttpFormParam http = RxHttp.postForm(Constant.FILE_SINGLE_UPLOAD_QUKAN);
            http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString());
            http.addFile("file", new File(picPath));
            http.asSingleUpload(SignFileUploadResponEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            if (null != data && !TextUtils.isEmpty(data.getPic_path_s())) {
                                updateUserAvatar(data.getPic_path_s());
                            } else {
                                if (null != loading && loading.isShow()) {
                                    loading.close();
                                }
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * ????????????
     *
     * @param avatarImgPath ????????????
     */
    private void updateUserAvatar(String avatarImgPath) {
        RxHttp.postJson(Constant.AVATAR_UPDATE).add("cw_headpic", avatarImgPath)
                .asResponse(UserInfoResultEntity.UserInfo.class)
                .as(RxLife.asOnMain(this))
                .subscribe(userInfo -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (!isFinishing()) {
                        if (null != userInfo) {
//                            SharedPreferenceUtil.saveUserInfo(userInfo);
                            AppUtil.loadAvatarImg(userInfo.getHeadpic(), avatarImg);
                            Constant.CW_AVATAR = userInfo.getHeadpic();
                            EventBus.getDefault().post(new EventMessage("updateAvatar", userInfo.getHeadpic()));
                        }
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(this, LoginActivity.class);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // ??????
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PIC) { // ??????????????????
            getPictureFromAlbum();
        } else if (requestCode == CODE_REQUEST_CAMERA) { // ??????
            getPictureFromCamera();
        } else if (requestCode == CODE_REQUEST_PHONE) { // QQ ??????
            UMShareAPI.get(this).getPlatformInfo(this, shareMedia, umAuthListener);
        }
    }

    // ????????????
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PIC) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("????????????")
                    .setRationale("???????????????????????????????????????????????????????????????????????????????????????????????????")
                    .setNegativeButton("??????")
                    .setPositiveButton("??????")
                    .build().show();
        } else if (requestCode == CODE_REQUEST_CAMERA) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("????????????")
                    .setRationale("????????????????????????????????????????????????????????????????????????????????????????????????????????????")
                    .setNegativeButton("??????")
                    .setPositiveButton("??????")
                    .build().show();
        } else if (requestCode == CODE_REQUEST_PHONE) {// QQ ??????
            new AppSettingsDialog.Builder(this)
                    .setTitle("????????????")
                    .setRationale("???????????????????????????QQ??????????????????????????????????????????????????????????????????????????????")
                    .setNegativeButton("??????")
                    .setPositiveButton("??????")
                    .build().show();
        }
    }

    @OnClick({R.id.ll_avatar_accountManage,
            R.id.img_back_header_not_title,
            R.id.relate_nickname_accountManage,
            R.id.relate_mobile_accountManage,
            R.id.relate_pwd_accountManage,
            R.id.relate_qq_accountManage,
            R.id.relate_wx_accountManage,
            R.id.relate_sina_accountManage,
            R.id.relate_account_cancelled_accountManage
    })

    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            Bundle bundle = new Bundle();
            int id = view.getId();
            if (id == R.id.ll_avatar_accountManage) {
                showAvatarPicker();
            } else if (id == R.id.img_back_header_not_title) { // ??????
                finish();
            } else if (id == R.id.relate_nickname_accountManage) { // ????????????
                bundle.putString("name", nicknameTv.getText().toString());
                IntentUtil.startActivity(this, UpdateUserNameActivity.class, bundle);
            } else if (id == R.id.relate_mobile_accountManage) {// ???????????????
                bundle.putString("number", phoneNumberStr);
                IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
            } else if (id == R.id.relate_pwd_accountManage) {// ????????????
                IntentUtil.startActivity(this, UpdatePasswordActivity.class, null);
            } else if (id == R.id.relate_qq_accountManage) { // QQ ??????
                if (AppUtil.isAvilible(this, "com.tencent.mobileqq")) {
                    shareMedia = SHARE_MEDIA.QQ;
                    threePartyAuthorization(shareMedia);
                } else {
                    ToastUtil.showShortToast("?????????QQ");
                }
            } else if (id == R.id.relate_wx_accountManage) { // ????????????
                if (!AppUtil.isAvilible(this, "com.tencent.mm")) {
                    ToastUtil.showShortToast("???????????????");
                } else {
                    shareMedia = SHARE_MEDIA.WEIXIN;
                    threePartyAuthorization(shareMedia);
                }
            } else if (id == R.id.relate_sina_accountManage) { // ????????????????????????
                shareMedia = SHARE_MEDIA.SINA;
                threePartyAuthorization(shareMedia);
            } else if (id == R.id.relate_account_cancelled_accountManage) {
                IntentUtil.startActivity(AccountManagerActivity.this, AccountUnsubscribeMainActivity.class);
            }
        }
        lastTime = currentTime;
    }

    /**
     * ???????????????
     *
     * @param share_media ??????
     */
    private void threePartyAuthorization(SHARE_MEDIA share_media) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(this.getResources().getString(R.string.network_error));
            return;
        }
        UMShareAPI.get(this).getPlatformInfo(this, share_media, umAuthListener);
    }

    /**
     * ????????????
     */
    private final UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            loading.show();
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (!map.isEmpty()) {
                threePartyLogin(share_media, map);
            } else {
                if (null != loading && loading.isShow()) {
                    loading.close();
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            ToastUtil.showLongToast("????????????");
            if (null != loading && loading.isShow()) {
                loading.close();
            }

        }
    };


    /**
     * ?????????????????????
     *
     * @param share_media ???????????????
     * @param map         ????????????
     */
    private void threePartyLogin(SHARE_MEDIA share_media, Map<String, String> map) {
        for (String key : map.keySet()) {
            LogUtil.e("key = " + key + " value = " + map.get(key) + "\n");
        }
        //?????????????????????
        String openid = TextUtils.isEmpty(map.get("openid")) ? "" : map.get("openid");
        // ????????????
        String nickname = TextUtils.isEmpty(map.get("name")) ? "" : map.get("name");
        // ????????????
        String sex = TextUtils.isEmpty(map.get("gender")) ? "" : map.get("gender");
        // ????????????
        String headimgurl = TextUtils.isEmpty(map.get("iconurl")) ? "" : map.get("iconurl");
        String unionid = "";

        if (share_media == SHARE_MEDIA.WEIXIN) {
            unionid = TextUtils.isEmpty(map.get("unionid")) ? "" : map.get("unionid");
        } else if (share_media == SHARE_MEDIA.QQ) {
            unionid = TextUtils.isEmpty(map.get(Constant.STR_CW_UID_SYSTEM)) ? "" : map.get(Constant.STR_CW_UID_SYSTEM);
        } else if (share_media == SHARE_MEDIA.SINA) {
            unionid = TextUtils.isEmpty(map.get(Constant.STR_CW_UID_SYSTEM)) ? "" : map.get(Constant.STR_CW_UID_SYSTEM);
        }
        if (!NetUtil.isNetworkConnected(this)) {
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        } else {
            // ??????????????????
            RxHttpJsonParam http = RxHttp.postJson(Constant.THREE_PARTY_BIND);
            http.add("cw_nickname", nickname);
            if (share_media == SHARE_MEDIA.WEIXIN) { // ?????????????????????
                http.add("cw_type", "WX");
                http.add("cw_externalappid", Constant.WX_APP_ID);
                http.add("cw_sex", TextUtils.equals(SEX_MALE, sex) ? TYPE_SEX_MALE : TYPE_SEX_FEMALE);
                http.add("cw_headimgurl", headimgurl);
                http.add("cw_openid", openid);
                http.add("cw_unionid", unionid);
            } else if (share_media == SHARE_MEDIA.QQ) { // QQ???????????????
                http.add("cw_type", "QQ");
                http.add("cw_externalappid", Constant.QQ_APP_ID);
                http.add("cw_gender", TextUtils.equals(SEX_MALE, sex) ? TYPE_SEX_MALE : TYPE_SEX_FEMALE);
                http.add("cw_figureurl", headimgurl);
                http.add("cw_openid", openid);
            } else if (share_media == SHARE_MEDIA.SINA) { // ???????????????????????????
                http.add("cw_type", "SINA");
                http.add("cw_externalappid", Constant.SINA_APP_ID);
                http.add("cw_gender", TextUtils.equals(SEX_MALE, sex) ? TYPE_SEX_MALE : TYPE_SEX_FEMALE);
                http.add("cw_headimage", headimgurl);
                http.add("cw_wid", unionid);
            }
            http.asResponse(JsonObject.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (share_media == SHARE_MEDIA.SINA) {  // ????????????
                                sinaLayout.setClickable(false);
                                sinaBindStateTv.setText("?????????");
                            } else if (share_media == SHARE_MEDIA.WEIXIN) { // ??????
                                wxLayout.setClickable(false);
                                wxBindStateTv.setText("?????????");
                            } else if (share_media == SHARE_MEDIA.QQ) { // qq
                                qqLayout.setClickable(false);
                                qqBindStateTv.setText("?????????");
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    /**
     * ??????EventBus
     *
     * @param message ???????????????
     */
    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                if (TextUtils.equals("updateName", message.getLabel())) {
                    nicknameTv.setText(message.getContent());
                } else if (TextUtils.equals("updateMobile", message.getLabel())) {
                    getUserInfo();
                } else if (TextUtils.equals(Constant.TAG_LOGOUT, message.getLabel())) {
                    finish();
                } else if (TextUtils.equals("updateUserInfo", message.getLabel())) {
                    getUserInfo();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}