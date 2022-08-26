package cn.cc1w.app.ui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.luck.lib.camerax.CameraImageEngine;
import com.luck.lib.camerax.SimpleCameraX;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.xutils.http.RequestParams;

import java.io.File;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AdvertisementEntity;
import cn.cc1w.app.ui.glide.GlideLoader;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PaikewTopicActivity;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PhotoDetailActivity;
import cn.cc1w.app.ui.ui.home.record.UserPaikewActivity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeActivity;
import cn.cc1w.app.ui.utils.pictuerSelect.MeOnSimpleXPermissionDeniedListener;
import cn.cc1w.app.ui.utils.pictuerSelect.MeOnSimpleXPermissionDescriptionListener;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.kpinfo.log.KpLog;
import cn.kpinfo.log.callback.InitCallback;

/**
 * APP 工具类
 */
public class AppUtil {

    /**
     * 给指定的 ImgView 加载本地图片
     */
    public static void loadRes(int res, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            Glide.with(context).load(res).into(targetView);
        }
    }

    public static void loadHome(int res, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            Glide.with(context).load(res).placeholder(R.mipmap.home_normal).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(targetView);
        }
    }

    /**
     * 加载图片 。没有占位图
     */
    public static void loadImgWithoutPlaceholder(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path);
        }
    }

    public static void loadVideo(int res, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            Glide.with(context).load(res).placeholder(R.mipmap.ic_video_normal).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(targetView);
        }
    }

    public static void loadPaikew(int res, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            Glide.with(context).load(res).placeholder(R.mipmap.ic_paikew_normal).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(targetView);
        }
    }

    public static void loadApp(int res, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            Glide.with(context).load(res).placeholder(R.mipmap.ic_apps_normal).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(targetView);
        }
    }

    /**
     * 加载 县长邮箱
     */
    public static void loadEmailImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_msg_banner);
        }
    }

    public static void loadPaikewSmallPic(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_paike);
        }
    }

    public static void loadPaikewBigPic(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_paike_news);
        }
    }

    /**
     * 加载post 广告图片
     */
    public static void loadNewsTopAdvertisement(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_144);
        }
    }

    /**
     * 加载post 底部 广告图片
     */
    public static void loadNewsBottomAdvertisement(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_405);
        }
    }

    /**
     * 加载网络图 设置圆角
     */
    public static void loadNetworkImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_avatar_default);
        }
    }

    /**
     * 加载 广告图
     */
    public static void loadAdvertisementImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.bg_splash_def);
        }
    }

    /**
     * 加载  爆料中的 图片
     */
    public static void loadBrokeImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.bg_splash_def);
        }
    }

    /**
     * 加载 banner图片
     */
    public static void loadBannerImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && null != targetView) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_banner);
        }
    }

    /**
     * 加载 banner图片
     */
    public static void loadBannerImgNoHolder(Context context, String path, ImageView targetView) {
        if (null != context && null != targetView) {
            Glide.with(context).load(path).into(targetView);
        }
    }

    /**
     * 加载 新闻 图片
     */
    public static void loadNewsImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_news);
        }
    }

    /**
     * 加载 新闻 图片
     */
    public static void loadMapImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path);
        }
    }

    /**
     * 加载 应用号 图片
     */
    public static void loadAppsImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_menu);
        }
    }

    /**
     * 加载 视频列表的 图片
     */
    public static void loadVideoListImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_list_video);
        }
    }

    /**
     * 加载 图集 图片
     */
    public static void loadAlbumListImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_album);
        }
    }

    /**
     * 加载 首页的功能区 图片
     */
    public static void loadMenuImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_menu);
        }
    }

    /**
     * 加载新闻组 图片
     */
    public static void loadNewsGroupImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_group_news);
        }
    }

    /**
     * 加载大图
     */
    public static void loadBigImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_default_banner);
        }
    }

    /**
     * 加载 用户的 图片
     */
    public static void loadAvatarImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.img_avatar_default);
        }
    }

    /**
     * 加载图片 。没有占位图
     */
    public static void loadImgWithPlaceholder(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context && targetView != null) {
            GlideLoader.loadOriginal(targetView, path);
        }
    }

    /**
     * 加载 启动图 图片
     */
    public static void loadSplashImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.bg_splash_default);
        }
    }

    public static void loadPaiKeVideoCoverImg(String path, ImageView targetView) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            GlideLoader.loadOriginal(targetView, path, R.mipmap.bg_video_paike);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sd卡是否存在
     */
    public boolean isSdCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 判断输入是否为手机号
     */
    public static boolean isChinaPhoneLegal(String str) {
        return ((!TextUtils.isEmpty(str)) && (str.length() == 11));
    }

    // 设置代理
    public static String setWebViewUserAgent(String ua) {
        String nowUa = ua + ";" + "ccwb_app/android" + ";" + "kaiping_android";
        return nowUa;
    }

    /**
     * 获取包含公共请求参数的 http请求体
     */
    private static RequestParams getRequestParamsWithToken(String url) {
        RequestParams params = new RequestParams(url);
        params.addHeader("source-id", Constant.ID_APP);
        params.setMaxRetryCount(Constant.TIMES_RETRY_NETWORK);
        params.setReadTimeout(Constant.TIME_OUT_REQUEST);
        params.setConnectTimeout(Constant.TIME_OUT_REQUEST);
        params.addHeader(Constant.CONNECTION, Constant.STR_CONNECTION);
        params.addBodyParameter(Constant.STR_CW_CLIENT, Constant.CW_CLIENT);
        params.addBodyParameter(Constant.STR_CW_DEVICE, Constant.CW_DEVICE);
        params.addBodyParameter(Constant.STR_CW_OS, Constant.CW_OS);
        params.addBodyParameter(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE);
        params.addBodyParameter("client", Constant.CW_OS);
        params.addBodyParameter(Constant.STR_CW_MACHINE_ID, Constant.CW_MACHINE_ID);
        params.addBodyParameter(Constant.STR_CW_COUNTRY, TextUtils.isEmpty(Constant.CW_COUNTRY) ? "" : Constant.CW_COUNTRY);
        params.addBodyParameter(Constant.STR_CW_PROVINCE, TextUtils.isEmpty(Constant.CW_PROVINCE) ? "" : Constant.CW_PROVINCE);
        params.addBodyParameter(Constant.STR_CW_CITY, TextUtils.isEmpty(Constant.CW_CITY) ? "" : Constant.CW_CITY);
        params.addBodyParameter(Constant.STR_CW_AREA, TextUtils.isEmpty(Constant.CW_AREA) ? "" : Constant.CW_AREA);
        params.addBodyParameter(Constant.STR_CW_LATITUDE, TextUtils.isEmpty(Constant.CW_LATITUDE) ? "" : Constant.CW_LATITUDE);
        params.addBodyParameter(Constant.STR_CW_LONGITUDE, TextUtils.isEmpty(Constant.CW_LONGITUDE) ? "" : Constant.CW_LONGITUDE);
        params.addBodyParameter(Constant.STR_CW_IP, TextUtils.isEmpty(Constant.CW_IP) ? "" : Constant.CW_IP);
        params.addBodyParameter(Constant.STR_CW_VERSION, Constant.CW_VERSION);
        params.addBodyParameter(Constant.STR_CW_NETWORKTYPE, TextUtils.isEmpty(Constant.CW_NETWORKTYPE) ? "" : Constant.CW_NETWORKTYPE);
        params.addBodyParameter(Constant.STR_CW_DEVICEMODEL, Constant.TYPE_MOBILE);
        if (TextUtils.isEmpty(Constant.CW_ID_KPUSH)) {
            String id = SharedPreferenceUtil.getKPushRegisterId();
            if (!TextUtils.isEmpty(id)) {
                Constant.CW_ID_KPUSH = id;
            }
        }
        params.addBodyParameter(Constant.STR_CW_ID_KPUSH, Constant.CW_ID_KPUSH);
        params.addHeader(Constant.STR_CW_AUTHORIZATION, TextUtils.isEmpty(Constant.CW_AUTHORIZATION) ? "" : Constant.CW_AUTHORIZATION);


//        LogUtil.e(Constant.STR_CW_CLIENT + " = " + Constant.CW_CLIENT + "  " + Constant.STR_CW_DEVICE + " = "
//                + Constant.CW_DEVICE + "   " + Constant.STR_CW_OS + "  = " + Constant.CW_OS + "  " + Constant.STR_CW_MACHINE_TYPE + " = "
//                + Constant.CW_MACHINE_TYPE + "  " + Constant.STR_CW_MACHINE_ID + " = " + Constant.CW_MACHINE_ID + "  " + Constant.STR_CW_COUNTRY + " = " + Constant.CW_COUNTRY + "" +
//                " " + Constant.STR_CW_PROVINCE + " = " + Constant.CW_PROVINCE + "  " + " " + Constant.STR_CW_CITY + " = " + Constant.CW_CITY + "  " + Constant.STR_CW_AREA + "  = " + Constant.CW_AREA + "  " +
//                Constant.STR_CW_LATITUDE + "  = " + Constant.CW_LATITUDE + "  " + Constant.STR_CW_LONGITUDE + " = " + Constant.CW_LONGITUDE
//
//                + "  " + Constant.STR_CW_IP + "  = " + Constant.CW_IP + "   " + Constant.STR_CW_VERSION + "  = " + Constant.CW_VERSION + "  "
//
//                + Constant.STR_CW_NETWORKTYPE + "  = " + Constant.CW_NETWORKTYPE + "  " + Constant.STR_CW_DEVICEMODEL + "   = " + Constant.TYPE_MOBILE + "  "
//
//                + Constant.STR_CW_AUTHORIZATION + "  = " + Constant.CW_AUTHORIZATION
//                + "  "+ Constant.STR_CW_ID_KPUSH + "  = " + Constant.CW_ID_KPUSH
//
//        );
        return params;
    }

    /**
     * 配置公共的 接口请求参数
     */
    public static RequestParams configRequestParamsWithToken(String url, HashMap<String, Object> map) {
        RequestParams params = getRequestParamsWithToken(url);
        if (null != map) {
            for (String key : map.keySet()) {
                Object obj = map.get(key);
                if (obj instanceof File) {
                    params.setMultipart(true);
                    File file = (File) obj;
                    params.addBodyParameter(key, file);
                } else {
                    params.addBodyParameter(key, (null != obj) ? obj.toString() : "");
                }
            }

        }

        return params;
    }

    /**
     * 获取当前APP的 versionName
     */
    public static String getVersionName(Context context) {
        String versionName = BuildConfig.VERSION_NAME;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return versionName;
        }
        return versionName;
    }

    /**
     * 设置 UserAgent
     */
    public static String setUserAgent(String userAgent) {
        return userAgent.concat(";").concat("ccwb_app/android");
    }

    /**
     * 获取IP地址
     *
     * @return ip 地址
     */
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getNotEmtpyString(String content) {
        return TextUtils.isEmpty(content) ? "" : content;
    }

    public static boolean isAppOnForeground(Context context) {
        if (null == context) {
            return false;
        }

        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查手机上是否安装了指定的软件
     */
    public static boolean isAvilible(Context context, String packageName) {
        if (context == null) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<>();
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        return packageNames.contains(packageName);
    }

    /**
     * @param decript 要加密的字符串
     */
    private static String MD5Encryption(String decript) {
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'};
        try {
            byte[] strTemp = decript.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte tmp[] = mdTemp.digest();
            char strs[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                strs[k++] = hexDigits[byte0 >>> 4 & 0xf];
                strs[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(strs).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    /**
     * 创建签名的字符串参数
     */
    public static String generateSignString() {
        String codes = "appid".concat(Constant.ID_APP).concat("appkey").concat(Constant.KEY_APP).concat("appsecret")
                .concat(Constant.SECRET_APP);
        String result = TextUtils.isEmpty(MD5Encryption(codes)) ? "" : MD5Encryption(codes).toLowerCase();
        return result;
    }

    public static LoadingDialog getLoading(Context context) {
        if (null != context) {
            LoadingDialog loading = new LoadingDialog(context);
            loading.setLoadStyle(1);
            loading.setLoadingText(null);
            loading.setInterceptBack(false);
            return loading;
        } else {
            return null;
        }
    }

    /**
     * 通知是否开启
     */
    public static boolean isNotificationEnabled(Context context) {
        boolean isOpend;
        try {
            isOpend = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            isOpend = false;
        }
        return isOpend;
    }

    /**
     * 跳转到设置界面
     */
    public static void goToAppSetting(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取当前手机屏幕高度
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * @param decript 要加密的字符串（给资源服务器）
     */
    public static String MD5EncryptionForResourceServer(String decript) {
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'};
        try {
            byte[] strTemp = decript.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte tmp[] = mdTemp.digest();
            char strs[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                strs[k++] = hexDigits[byte0 >>> 4 & 0xf];
                strs[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(strs).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    public static String getSignString() {
        return MD5EncryptionForResourceServer("appid".concat(Constant.ID_APP).concat("appkey").concat(Constant.KEY_APP).concat("appsecret").concat(Constant.SECRET_APP));
    }

    public static String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%;  height:auto;}"
                + "</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    private static Application getApplicationInner() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            Object current = currentActivityThread.invoke((Object) null);
            Object app = currentApplication.invoke(current);
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static void initLogInfo() {
        Application application = getApplicationInner();
        if (application != null) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    Constant.CW_AUTHORIZATION = TextUtils.isEmpty(userInfo.getToken()) ? "" : userInfo.getToken();
                }
            }
            KpLog.getInstance()
                    .init(application, Constant.ID_LOG, Constant.SECRET_LOG).setAppName(getAppName(application))
                    .setDebug(BuildConfig.DEBUG).setToken(Constant.CW_AUTHORIZATION).setAppVersion(BuildConfig.VERSION_NAME)
                    .setCallback(new InitCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailed(String msg) {

                        }
                    });
        } else {
            LogUtil.d("application 为空 XXXX   ");
        }
    }

    private static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized static void doUserLogOut() {
        Constant.CW_AUTHORIZATION = "";
        Constant.CW_USERNAME = "";
        Constant.CW_AVATAR = "";
        SharedPreferenceUtil.saveUserInfo(new UserInfoResultEntity.UserInfo());
        EventBus.getDefault().post(new EventMessage(Constant.TAG_LOGOUT, Constant.TAG_LOGOUT));
    }

    public synchronized static void doUserLogOutWithoutMessage() {
        Constant.CW_AUTHORIZATION = "";
        Constant.CW_USERNAME = "";
        Constant.CW_AVATAR = "";
        SharedPreferenceUtil.saveUserInfo(new UserInfoResultEntity.UserInfo());
    }

    /**
     * 初始化 公共参数
     */
    @SuppressLint("HardwareIds")
    public static void initPublicParams(Context context) {
        UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
        if (null != userInfo) {
            Constant.CW_AUTHORIZATION = TextUtils.isEmpty(userInfo.getToken()) ? "" : userInfo.getToken();
            Constant.CW_USERNAME = TextUtils.isEmpty(userInfo.getNickname()) ? "" : userInfo.getNickname();
            Constant.CW_AVATAR = TextUtils.isEmpty(userInfo.getHeadpic()) ? "" : userInfo.getHeadpic();
            Constant.CW_UID_SYSTEM = TextUtils.isEmpty(userInfo.getUid()) ? "" : userInfo.getUid();
        }
        String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        Constant.CW_MACHINE_ID = TextUtils.isEmpty(androidId) ? Build.SERIAL : androidId;
        Constant.CW_IP = AppUtil.getIpAddressString();
        Constant.CW_VERSION = AppUtil.getVersionName(context);
        String STATE_NETWORK_MOBILE = "Mobile";
        String STATE_NETWORK_WIFI = "WIFI";
        Constant.CW_NETWORKTYPE = NetUtil.isMobileConnected(context) ? STATE_NETWORK_MOBILE : NetUtil.isWifiConnected(context) ? STATE_NETWORK_WIFI : STATE_NETWORK_WIFI;
        Constant.HEIGHT_INPUT_METHOD_CURRENT = SharedPreferenceUtil.getInputMethodHeight();
        Constant.isPushMessageOpened = SharedPreferenceUtil.getPushState();
        Constant.TYPE_MOBILE = Build.MODEL;
        Constant.STATE_USE_BROKE = SharedPreferenceUtil.getBrokeAudioUseState();
    }

    public static void splashJump(Context context, AdvertisementEntity.AdvertisementBean item) {
        String action = item.getAction();
        Bundle bundle = new Bundle();
        if (TextUtils.equals(item.getIn_type(), Constant.TAG_URL) && !TextUtils.isEmpty(item.getUrl())) {
            bundle.putString(Constant.TAG_URL, item.getUrl());
            bundle.putString(Constant.TAG_TITLE, TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
            bundle.putString(Constant.TAG_SUMMARY, "");
            IntentUtil.startActivity(context, UrlDetailActivity.class, bundle);
        } else if (TextUtils.equals(item.getIn_type(), "proto")) {
            if (!TextUtils.isEmpty(action)) {
                if (Constant.TYPE_NEWS_NORMAL.equals(action) || Constant.TYPE_NEWS_NEWSLIST.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, NewsDetailNewActivity.class, bundle);
                    }
                } else if (Constant.TYPE_NEWS_VIDEO.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, VideoDetailActivity.class, bundle);
                    }
                    // 视频组
                } else if (Constant.TYPE_NEWS_VIDEOGROUP.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, VideoGroupDetailActivity.class, bundle);
                    }
                    // 图集
                } else if (Constant.TYPE_NEWS_PHOTO.equals(action) || Constant.TYPE_NEWS_PHOTOGROUP.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, AlbumDetailActivity.class, bundle);
                    }
                    // 直播
                } else if (Constant.TYPE_NEWS_LIVE.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, LiveDetailActivity.class, bundle);
                    }
                    // 活动
                } else if (Constant.TYPE_NEWS_ACTIVITY.equals(action)) {
                    if (!TextUtils.isEmpty(item.getUrl())) {
                        bundle.putString(Constant.TAG_URL, item.getUrl());
                        bundle.putString(Constant.TAG_TITLE, TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
                        bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                        bundle.putString(Constant.TAG_SUMMARY, "");
                        IntentUtil.startActivity(context, UrlDetailActivity.class, bundle);
                    }
                    // 专题
                } else if (Constant.TYPE_NEWS_SPECIAL.equals(action) || Constant.TYPE_NEWS_SPEICAL_NORMAL.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, SpecialDetailActivity.class, bundle);
                    }
                    // 拍客视频
                } else if (Constant.TYPE_NEWS_VIDEO_PAIKEW.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, PaikewVideoDetailActivity.class, bundle);
                    }
                    // 拍客图片
                } else if (Constant.TYPE_NEWS_PHOTO_PAIKEW.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, PhotoDetailActivity.class, bundle);
                    }
                    // 拍客话题
                } else if (Constant.TYPE_NEWS_TOPIC_PAIKEW.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId()) && TextUtils.isDigitsOnly(item.getId())) {
                        bundle.putInt(Constant.TAG_TOPIC, Integer.parseInt(item.getId()));
                        bundle.putString(Constant.TAG_TITLE, item.getTitle());
                        IntentUtil.startActivity(context, PaikewTopicActivity.class, bundle);
                    }
                    // 拍客用户中心
                } else if (Constant.TYPE_NEWS_USERCETER_PAIKEW.equals(action)) {
                    if (!TextUtils.isEmpty(item.getId())) {
                        bundle.putString(Constant.TAG_ID, item.getId());
                        IntentUtil.startActivity(context, UserPaikewActivity.class, bundle);
                    }
                    // 问吧
                } else if (Constant.TYPE_NEWS_BROKE.equals(action)) {
                    IntentUtil.startActivity(context, BrokeActivity.class, bundle);
                    // 拍客
                } else if (Constant.TYPE_NEWS_PAIKEW.equals(action)) {
                    EventBus.getDefault().post(new EventMessage("showPaikewTab", "showPaikewTab"));
                }
            }
        }
    }

    public static void gotoSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    public static String getHttpHeader(Context context) {
        return "Mozilla/5.0 (Macintosh;Android " + Build.VERSION.RELEASE + ") WebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.61 Safari/537.36; ccwb_app/android;kaiping_android_" + getVersionName(context);
    }

    public static void makeLongSnackMsg(@NotNull View view, String msg) {
        if (msg != null && !TextUtils.isEmpty(msg)) {
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void doShoot(AppCompatActivity activity, int cameraMode) {
        SimpleCameraX camera = SimpleCameraX.of();
        camera.isAutoRotation(true);
        camera.setCameraMode(cameraMode);
        camera.setVideoFrameRate(25);
        camera.setVideoBitRate(3 * 1024 * 1024);
        camera.isDisplayRecordChangeTime(true);
        camera.isManualFocusCameraPreview(true);
        camera.isZoomCameraPreview(true);
        camera.setOutputPathDir(getSandboxCameraOutputPath(activity));
        camera.setPermissionDeniedListener(new MeOnSimpleXPermissionDeniedListener());
        camera.setPermissionDescriptionListener(new MeOnSimpleXPermissionDescriptionListener());
        camera.setImageEngine(new CameraImageEngine() {
            @Override
            public void loadImage(Context context, String url, ImageView imageView) {
                Glide.with(context).load(url).into(imageView);
            }
        });
        camera.start(activity, 909);
    }

    private static String getSandboxCameraOutputPath(Context context) {
        File externalFilesDir = context.getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }
    /**
     * 将View从父控件中移除
     */
    public static void removeViewFormParent(View v) {
        if (v == null) {
            return;
        }
        ViewParent parent = v.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(v);
        }
    }
}
