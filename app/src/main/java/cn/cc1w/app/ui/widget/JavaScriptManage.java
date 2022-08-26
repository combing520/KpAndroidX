package cn.cc1w.app.ui.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.JsPayInfoEntity;
import cn.cc1w.app.ui.entity.NavigationEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.entity.Web2AppVideoUpdateEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.DeviceInfoEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.UserLocationEntity;
import cn.cc1w.app.ui.javascript.JavaScriptCallBackBase;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.ShowWebViewGalleryDetailActivity;
import cn.cc1w.app.ui.ui.pay.WeChatPayActivity;
import cn.cc1w.app.ui.ui.qr.QrCodeScanActivity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.ui.share.WebCallShareActivity;
import cn.cc1w.app.ui.ui.usercenter.UserCenterActivity;
import cn.cc1w.app.ui.ui.usercenter.map.NavigationActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.RxHttp;

/**
 * @author kpinfo
 */
public class JavaScriptManage {
    private static final int REQUEST_CODE_SCAN = 0X01;
    private final Context mContext;

    public JavaScriptManage(Context context) {
        this.mContext = context;
    }

    private final JavaScriptCallBackBase javaScriptCallBack = new JavaScriptCallBackBase() {
        @Override
        public void setCanRefresh(boolean tag) {
        }

        @Override
        public void autoRefresh() {
        }

        @Override
        public void setHeadBarVisible(boolean visible) {

        }

        @Override
        public void fileUploadFinish(String json, String type) {

        }

        @Override
        public void fileUploadError(String json, String type) {

        }

        @Override
        public void startAppRecordCallBack() {

        }

        @Override
        public void endAppRecordCallBack() {

        }

        @Override
        public void imageQRCode(String pic) {
        }

        @Override
        public void setWebviewWindows(String type) {

        }

        @Override
        public void cwHeadBarConfig(String json) {

        }

        @Override
        public void commonBack(Activity activity) {
        }

        @Override
        public void commonShare(String json) {

        }

    };

    @JavascriptInterface
    public String getSysVerSion() {
        String version = "";
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 调用系统Toast消息提示
     */
    @JavascriptInterface
    public void getToast(String message) {
        ToastUtil.showLongToast(message);
    }

    /**
     * 获取屏幕尺寸
     */
    @JavascriptInterface
    public String getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        if (mContext instanceof Activity) {
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        return String.valueOf(dm.heightPixels);
    }

    /**
     * 获取屏幕尺寸
     */
    @JavascriptInterface
    public String getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        if (mContext instanceof Activity) {
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        return String.valueOf(dm.widthPixels);
    }

    /**
     * Alert
     */
    @JavascriptInterface
    public void commonAlert(String title, String message) {
        if (mContext instanceof Activity) {
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setCancelable(true);
            alert.setPositiveButton("确定", (dialog, which) -> ((Activity) mContext).closeOptionsMenu());
            alert.setCancelable(false);
            alert.create();
            alert.show();
        }
    }

    /**
     * 返回
     */
    @JavascriptInterface
    public void commonBack() {
        javaScriptCallBack.commonBack((Activity) mContext);
    }

    @JavascriptInterface
    public void showAlert(String title, String message, String ok, String result, int tag) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setCancelable(true);
        alert.setPositiveButton(ok, (dialog, which) -> dialog.dismiss());
        alert.setNegativeButton(result, (dialog, which) -> {
            dialog.dismiss();
            ((Activity) mContext).closeOptionsMenu();
        });
        alert.setCancelable(false);
        alert.create();
        alert.show();
    }

    /**
     * 点击webView中的 富文本标签，打开图库
     */
    @JavascriptInterface
    public void openGallery(String[] picPathList, String currentUrl) {
        ArrayList<String> list = new ArrayList<>();
        int currentIndex = 0;
        for (String picPath : picPathList) {
            // 不是默认图片的 话就加入
            if (!TextUtils.isEmpty(picPath)) {
                list.add(picPath);
            }
        }
        for (int j = 0; j < list.size(); j++) {
            if (TextUtils.equals(currentUrl, list.get(j))) {
                currentIndex = j;
            }
        }
        if (null != mContext && !list.isEmpty()) {
            Intent intent = new Intent();
            intent.setClass(mContext, ShowWebViewGalleryDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("picList", list);
            intent.putExtra("selectPos", currentIndex);
            mContext.startActivity(intent);
        }
    }

    /**
     * 是否支持页面原生下拉刷新效果
     *
     * @param b b="true"设置支持 b="false"不支持
     */
    @JavascriptInterface
    public void setRefreshViewEnable(String b) {
        javaScriptCallBack.setCanRefresh("true".equals(b));
    }

    /**
     * 调用原生刷新控件刷新页面
     */
    @JavascriptInterface
    public void autoRefresh() {
        javaScriptCallBack.autoRefresh();
    }

    /**
     * 原生导航栏是否可见(默认可见)
     */
    @JavascriptInterface
    public void setHeadBarVisible(String visible) {
        javaScriptCallBack.setHeadBarVisible("true".equals(visible));
    }

    /**
     * 开始录音
     */
    @JavascriptInterface
    public void startAppRecord() {
        javaScriptCallBack.startAppRecordCallBack();
    }

    /**
     * 关闭录音
     */
    @JavascriptInterface
    public void endAppRecord() {
        javaScriptCallBack.endAppRecordCallBack();
    }

    /**
     * 图片识别二维码
     */
    @JavascriptInterface
    public void cwImageQRCode(String pic) {
        javaScriptCallBack.imageQRCode(pic);
    }

    /**
     * 设置是否是多窗口
     */
    @JavascriptInterface
    public void setWebviewWindows(String type) {
        javaScriptCallBack.setWebviewWindows(type);
    }

    /**
     * 设置导航栏样式
     */
    @JavascriptInterface
    public void cwHeadBarConfig(String json) {
        javaScriptCallBack.cwHeadBarConfig(json);
    }

    /**
     * 用户登出
     */
    @JavascriptInterface
    public void userLogout() {
        AppUtil.doUserLogOut();
    }

    /**
     * @return 获取登录用户的token
     */
    @JavascriptInterface
    public String getUserToken() {
        return TextUtils.isEmpty(Constant.CW_AUTHORIZATION) ? "" : Constant.CW_AUTHORIZATION;
    }

    //======================== 6.0 新的公共方法 begin ===================================== //

    /**
     * 获取用户经纬度信息
     * 获取地理位置，里面包含： 经度，纬度，地理位置信息 [网页端的要求]
     */
    @JavascriptInterface
    public String getCWLocationInfo() {
        Gson gson = new Gson();
        UserLocationEntity userLocationEntity = new UserLocationEntity();
        userLocationEntity.setCw_latitude(Constant.CW_LATITUDE);
        userLocationEntity.setCw_longitude(Constant.CW_LONGITUDE);
        userLocationEntity.setCw_address(Constant.ADDRESS);
        userLocationEntity.setCw_province(Constant.CW_PROVINCE);
        userLocationEntity.setCw_city(Constant.CW_CITY);
        userLocationEntity.setCw_district(Constant.CW_AREA);
        LogUtil.e("定位信息 = " + gson.toJson(userLocationEntity));
        return gson.toJson(userLocationEntity);
    }

    /**
     * 获取设备的基本信息
     * 获取设备信息，里面包含：设备高度，设备宽度，设备名称，设备机器码，app 版本号 [网页端的要求]
     */
    @JavascriptInterface
    public String getCWDeviceInfo() {
        Gson gson = new Gson();
        DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
        deviceInfoEntity.setCw_device_width(getScreenWidth());
        deviceInfoEntity.setCw_device_height(getScreenHeight());
        deviceInfoEntity.setCw_machine_id(Constant.CW_MACHINE_ID);
        deviceInfoEntity.setCw_verstion(getSysVerSion());
        deviceInfoEntity.setCw_device(Constant.TYPE_MOBILE);
        return gson.toJson(deviceInfoEntity);
    }

    /**
     * 获取用户信息
     * 获取用户信息，里面包含：用户用户 token， 用户详细信 [网页端的要求]
     */
    @JavascriptInterface
    public String getCWUserInfo() {
        Gson gson = new Gson();
        LogUtil.e("网页获取客户端用户信息 getCWUserInfo");
        UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
        if (null == userInfo) {
            LogUtil.e("为空 " + gson.toJson(new UserInfoResultEntity.UserInfo()));
            return gson.toJson(new UserInfoResultEntity.UserInfo());
        } else {
            LogUtil.e("不为空 " + gson.toJson(userInfo));
            return gson.toJson(userInfo);
        }
    }

    /**
     * 用户登录
     * 跳转到登录界面接口，并且需要支持，成功登陆以后，跳回跳入页面 [网页端的要求]
     */
    @JavascriptInterface
    public void toCWLogin() {
        if (null != mContext) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.LOGIN_WEB);
                IntentUtil.startActivity(mContext, LoginActivity.class, bundle);
            } else {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    LogUtil.e("不为空 " + " mobile == null ? " + TextUtils.isEmpty(userInfo.getMobile()));
                    if (TextUtils.isEmpty(userInfo.getMobile())) { // 没有绑定过手机号码
                        Bundle bundle = new Bundle();
                        bundle.putString("from", Constant.LOGIN_WEB);
                        IntentUtil.startActivity(mContext, UpdateMobileActivity.class, bundle);
                    }
                } else {
                    LogUtil.e("为空");
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.LOGIN_WEB);
                    IntentUtil.startActivity(mContext, LoginActivity.class, bundle);
                }
            }
        }
    }

    /**
     * 调用登录
     */
    @JavascriptInterface
    public void cwLoginWX() {
        if (null != mContext) {
            IntentUtil.startActivity(mContext, LoginActivity.class, null);
        }
    }

    /**
     * 打开商城分享页面
     */
    @JavascriptInterface
    public void openBizShare(String shareContent) {
        try {
            if (TextUtils.isEmpty(shareContent)) {
                ToastUtil.showShortToast("分享异常，请稍候再试");
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                IntentUtil.startActivity(mContext, ShareActivity.class, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开扫一扫
     */
    @JavascriptInterface
    public void toCWScan() {
        if (null != mContext && mContext instanceof Activity) {
            Intent qrIntent = new Intent();
            qrIntent.setClass(mContext, QrCodeScanActivity.class);
            ((Activity) mContext).startActivityForResult(qrIntent, REQUEST_CODE_SCAN);
        }
    }

    /**
     * 打开分享(带分享面板)
     */
    @JavascriptInterface
    public void toCWShare(String shareContent) {
        if (null != mContext) {
            if (TextUtils.isEmpty(shareContent)) {
                ToastUtil.showShortToast("分享异常，请稍候再试");
            } else {
                if (TextUtils.isEmpty(shareContent)) {
                    ToastUtil.showShortToast("无法获取分享内容");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                    IntentUtil.startActivity(mContext, ShareActivity.class, bundle);
                }
            }
        }
    }

    /**
     * 打开分享(自定义面板)
     */
    @JavascriptInterface
    public void toCustomCWShare(String shareContent) {
        //        LogUtil.e("分享情况 = " + shareContent);
        if (null != mContext) {
            if (TextUtils.isEmpty(shareContent)) {
                ToastUtil.showShortToast("无法获取分享内容");
            } else {
                if (!shareContent.contains("type")) {
                    ToastUtil.showShortToast("无法获取分享类型");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                    IntentUtil.startActivity(mContext, WebCallShareActivity.class, bundle);
                }
            }
        }
    }

    /**
     * 打开新闻详情
     */
    @JavascriptInterface
    public void toCWNewsDetail(String newsId) {
        if (null != mContext) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.TAG_ID, newsId);
            IntentUtil.startActivity(mContext, NewsDetailNewActivity.class, bundle);
        }
    }

    /**
     * 打开用户中心
     */
    @JavascriptInterface
    public void toCWUserCenter() {
        if (null != mContext) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(mContext, LoginActivity.class, null);
            } else {
                IntentUtil.startActivity(mContext, UserCenterActivity.class, null);
            }
        }
    }

    /**
     * 注入token 进行登录   [网页端的要求]
     */
    @JavascriptInterface
    public void insertCWUser(String userToken) {
        if (null != mContext && !TextUtils.isEmpty(userToken)) {
            // 进行登录； 登录成功 进行状态更新
            RxHttp.postJson(Constant.USER_INFO).add("cw-authorization", userToken)
                    .asResponse(UserInfoResultEntity.UserInfo.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userInfo -> {
                        if (userInfo != null) {
                            Constant.CW_AUTHORIZATION = TextUtils.isEmpty(userInfo.getToken()) ? userToken : userInfo.getToken();
                            Constant.CW_USERNAME = TextUtils.isEmpty(userInfo.getNickname()) ? "" : userInfo.getNickname();
                            Constant.CW_AVATAR = TextUtils.isEmpty(userInfo.getHeadpic()) ? "" : userInfo.getHeadpic();
                            Constant.CW_UID_SYSTEM = TextUtils.isEmpty(userInfo.getUid()) ? "" : userInfo.getUid();
                            SharedPreferenceUtil.saveUserInfo(userInfo);
                            // 同步刷新数据
                            EventBus.getDefault().post(new EventMessage("updateUserInfo", "updateUserInfo"));
                        }
                    }, (OnError) error -> {
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    /**
     * 打开地图导航
     *
     * @param navigationJsonStr 网页端传来的json 信息
     */
    @JavascriptInterface
    public void toCWNavigation(String navigationJsonStr) {
        if (null != mContext) {
            if (!TextUtils.isEmpty(navigationJsonStr)) {
                Gson gson = new Gson();
                NavigationEntity navigationEntity = gson.fromJson(navigationJsonStr, NavigationEntity.class);
                if (null != navigationEntity
                        && !TextUtils.isEmpty(navigationEntity.getStartLon())
                        && !TextUtils.isEmpty(navigationEntity.getStartLat())
                        && !TextUtils.isEmpty(navigationEntity.getStartAddress())
                        && !TextUtils.isEmpty(navigationEntity.getEndLon())
                        && !TextUtils.isEmpty(navigationEntity.getEndLat())
                        && !TextUtils.isEmpty(navigationEntity.getEndAddress())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("startLon", navigationEntity.getStartLon());
                    bundle.putString("startLat", navigationEntity.getStartLat());
                    bundle.putString("startAddress", navigationEntity.getStartAddress());
                    bundle.putString("endLon", navigationEntity.getEndLon());
                    bundle.putString("endLat", navigationEntity.getEndLat());
                    bundle.putString("endAddress", navigationEntity.getEndAddress());
                    IntentUtil.startActivity(mContext, NavigationActivity.class, bundle);
                } else {
                    ToastUtil.showShortToast("导航参数不能不全,无法进行定位");
                }
            } else {
                ToastUtil.showShortToast("导航参数不能为空");
            }
        }
    }

    /**
     * 进行微信支付
     */
    @JavascriptInterface
    public void payWX(String wxPayJson) {
        LogUtil.e("payWX " + wxPayJson);
        if (null != mContext) {
            if (TextUtils.isEmpty(wxPayJson)) {
                ToastUtil.showShortToast("支付参数不能为空");
            } else {
                if (!AppUtil.isAvilible(mContext, "com.tencent.mm")) { // 没有安装微信
                    ToastUtil.showShortToast("请安装最新版的微信");
                } else {
                    LogUtil.e("支付信息  = " + wxPayJson);
                    JsPayInfoEntity entity = JsonUtil.getObject(wxPayJson, JsPayInfoEntity.class);
                    if (null != entity && (null != entity.getPay())) {
                        Gson gson = new Gson();
                        Bundle bundle = new Bundle();
                        bundle.putString("wxPayJson", gson.toJson(entity.getPay()));
                        LogUtil.e("url = " + entity.getUrl());
                        Constant.URL_RELOAD_WEBVIEW = TextUtils.isEmpty(entity.getUrl()) ? "" : entity.getUrl();
                        IntentUtil.startActivity(mContext, WeChatPayActivity.class, bundle);
                    } else {
                        ToastUtil.showShortToast("支付参数不全");
                    }
                }
            }
        }
    }

    /**
     * 网页端调用上传视频 ；进行视频的时长限定
     */
    @JavascriptInterface
    public void uploadVideo(String videoInfoParamsJson) {
        if (!TextUtils.isEmpty(videoInfoParamsJson)) {
            LogUtil.e("uploadVideo " + videoInfoParamsJson);
            // 必须包含最大时长和最小时长
            if (videoInfoParamsJson.contains("minLength") || videoInfoParamsJson.contains("maxLength")) {
                Gson gson = new Gson();
                Web2AppVideoUpdateEntity entity = gson.fromJson(videoInfoParamsJson, Web2AppVideoUpdateEntity.class);
                if (null != entity && !TextUtils.isEmpty(entity.getMinLength()) && !TextUtils.isEmpty(entity.getMaxLength())) {
                    EventBus.getDefault().post(new EventMessage("videoPicture", Integer.parseInt(entity.getMinLength()),
                            Integer.parseInt(entity.getMaxLength()), entity.getCallBackMethod()));
                }
            }
        }
    }

    @JavascriptInterface
    public String getAppName() {
        return "cn.ccwb.cloud.kaiping.app";
    }

    /**
     * 获取App版本
     */
    @JavascriptInterface
    public String getAppVersionName() {
        String version = "";
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    @JavascriptInterface
    public void CWLoginOut() {
        AppUtil.doUserLogOut();
    }
}