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
     * ????????????Toast????????????
     */
    @JavascriptInterface
    public void getToast(String message) {
        ToastUtil.showLongToast(message);
    }

    /**
     * ??????????????????
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
     * ??????????????????
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
            alert.setPositiveButton("??????", (dialog, which) -> ((Activity) mContext).closeOptionsMenu());
            alert.setCancelable(false);
            alert.create();
            alert.show();
        }
    }

    /**
     * ??????
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
     * ??????webView?????? ??????????????????????????????
     */
    @JavascriptInterface
    public void openGallery(String[] picPathList, String currentUrl) {
        ArrayList<String> list = new ArrayList<>();
        int currentIndex = 0;
        for (String picPath : picPathList) {
            // ????????????????????? ????????????
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
     * ??????????????????????????????????????????
     *
     * @param b b="true"???????????? b="false"?????????
     */
    @JavascriptInterface
    public void setRefreshViewEnable(String b) {
        javaScriptCallBack.setCanRefresh("true".equals(b));
    }

    /**
     * ????????????????????????????????????
     */
    @JavascriptInterface
    public void autoRefresh() {
        javaScriptCallBack.autoRefresh();
    }

    /**
     * ???????????????????????????(????????????)
     */
    @JavascriptInterface
    public void setHeadBarVisible(String visible) {
        javaScriptCallBack.setHeadBarVisible("true".equals(visible));
    }

    /**
     * ????????????
     */
    @JavascriptInterface
    public void startAppRecord() {
        javaScriptCallBack.startAppRecordCallBack();
    }

    /**
     * ????????????
     */
    @JavascriptInterface
    public void endAppRecord() {
        javaScriptCallBack.endAppRecordCallBack();
    }

    /**
     * ?????????????????????
     */
    @JavascriptInterface
    public void cwImageQRCode(String pic) {
        javaScriptCallBack.imageQRCode(pic);
    }

    /**
     * ????????????????????????
     */
    @JavascriptInterface
    public void setWebviewWindows(String type) {
        javaScriptCallBack.setWebviewWindows(type);
    }

    /**
     * ?????????????????????
     */
    @JavascriptInterface
    public void cwHeadBarConfig(String json) {
        javaScriptCallBack.cwHeadBarConfig(json);
    }

    /**
     * ????????????
     */
    @JavascriptInterface
    public void userLogout() {
        AppUtil.doUserLogOut();
    }

    /**
     * @return ?????????????????????token
     */
    @JavascriptInterface
    public String getUserToken() {
        return TextUtils.isEmpty(Constant.CW_AUTHORIZATION) ? "" : Constant.CW_AUTHORIZATION;
    }

    //======================== 6.0 ?????????????????? begin ===================================== //

    /**
     * ???????????????????????????
     * ???????????????????????????????????? ???????????????????????????????????? [??????????????????]
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
        LogUtil.e("???????????? = " + gson.toJson(userLocationEntity));
        return gson.toJson(userLocationEntity);
    }

    /**
     * ???????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????????????????app ????????? [??????????????????]
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
     * ??????????????????
     * ???????????????????????????????????????????????? token??? ??????????????? [??????????????????]
     */
    @JavascriptInterface
    public String getCWUserInfo() {
        Gson gson = new Gson();
        LogUtil.e("????????????????????????????????? getCWUserInfo");
        UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
        if (null == userInfo) {
            LogUtil.e("?????? " + gson.toJson(new UserInfoResultEntity.UserInfo()));
            return gson.toJson(new UserInfoResultEntity.UserInfo());
        } else {
            LogUtil.e("????????? " + gson.toJson(userInfo));
            return gson.toJson(userInfo);
        }
    }

    /**
     * ????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????? [??????????????????]
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
                    LogUtil.e("????????? " + " mobile == null ? " + TextUtils.isEmpty(userInfo.getMobile()));
                    if (TextUtils.isEmpty(userInfo.getMobile())) { // ???????????????????????????
                        Bundle bundle = new Bundle();
                        bundle.putString("from", Constant.LOGIN_WEB);
                        IntentUtil.startActivity(mContext, UpdateMobileActivity.class, bundle);
                    }
                } else {
                    LogUtil.e("??????");
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.LOGIN_WEB);
                    IntentUtil.startActivity(mContext, LoginActivity.class, bundle);
                }
            }
        }
    }

    /**
     * ????????????
     */
    @JavascriptInterface
    public void cwLoginWX() {
        if (null != mContext) {
            IntentUtil.startActivity(mContext, LoginActivity.class, null);
        }
    }

    /**
     * ????????????????????????
     */
    @JavascriptInterface
    public void openBizShare(String shareContent) {
        try {
            if (TextUtils.isEmpty(shareContent)) {
                ToastUtil.showShortToast("??????????????????????????????");
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
     * ???????????????
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
     * ????????????(???????????????)
     */
    @JavascriptInterface
    public void toCWShare(String shareContent) {
        if (null != mContext) {
            if (TextUtils.isEmpty(shareContent)) {
                ToastUtil.showShortToast("??????????????????????????????");
            } else {
                if (TextUtils.isEmpty(shareContent)) {
                    ToastUtil.showShortToast("????????????????????????");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                    IntentUtil.startActivity(mContext, ShareActivity.class, bundle);
                }
            }
        }
    }

    /**
     * ????????????(???????????????)
     */
    @JavascriptInterface
    public void toCustomCWShare(String shareContent) {
        //        LogUtil.e("???????????? = " + shareContent);
        if (null != mContext) {
            if (TextUtils.isEmpty(shareContent)) {
                ToastUtil.showShortToast("????????????????????????");
            } else {
                if (!shareContent.contains("type")) {
                    ToastUtil.showShortToast("????????????????????????");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                    IntentUtil.startActivity(mContext, WebCallShareActivity.class, bundle);
                }
            }
        }
    }

    /**
     * ??????????????????
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
     * ??????????????????
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
     * ??????token ????????????   [??????????????????]
     */
    @JavascriptInterface
    public void insertCWUser(String userToken) {
        if (null != mContext && !TextUtils.isEmpty(userToken)) {
            // ??????????????? ???????????? ??????????????????
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
                            // ??????????????????
                            EventBus.getDefault().post(new EventMessage("updateUserInfo", "updateUserInfo"));
                        }
                    }, (OnError) error -> {
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    /**
     * ??????????????????
     *
     * @param navigationJsonStr ??????????????????json ??????
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
                    ToastUtil.showShortToast("????????????????????????,??????????????????");
                }
            } else {
                ToastUtil.showShortToast("????????????????????????");
            }
        }
    }

    /**
     * ??????????????????
     */
    @JavascriptInterface
    public void payWX(String wxPayJson) {
        LogUtil.e("payWX " + wxPayJson);
        if (null != mContext) {
            if (TextUtils.isEmpty(wxPayJson)) {
                ToastUtil.showShortToast("????????????????????????");
            } else {
                if (!AppUtil.isAvilible(mContext, "com.tencent.mm")) { // ??????????????????
                    ToastUtil.showShortToast("???????????????????????????");
                } else {
                    LogUtil.e("????????????  = " + wxPayJson);
                    JsPayInfoEntity entity = JsonUtil.getObject(wxPayJson, JsPayInfoEntity.class);
                    if (null != entity && (null != entity.getPay())) {
                        Gson gson = new Gson();
                        Bundle bundle = new Bundle();
                        bundle.putString("wxPayJson", gson.toJson(entity.getPay()));
                        LogUtil.e("url = " + entity.getUrl());
                        Constant.URL_RELOAD_WEBVIEW = TextUtils.isEmpty(entity.getUrl()) ? "" : entity.getUrl();
                        IntentUtil.startActivity(mContext, WeChatPayActivity.class, bundle);
                    } else {
                        ToastUtil.showShortToast("??????????????????");
                    }
                }
            }
        }
    }

    /**
     * ??????????????????????????? ??????????????????????????????
     */
    @JavascriptInterface
    public void uploadVideo(String videoInfoParamsJson) {
        if (!TextUtils.isEmpty(videoInfoParamsJson)) {
            LogUtil.e("uploadVideo " + videoInfoParamsJson);
            // ???????????????????????????????????????
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
     * ??????App??????
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