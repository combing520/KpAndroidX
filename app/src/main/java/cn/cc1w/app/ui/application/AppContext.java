package cn.cc1w.app.ui.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEnv;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppModeEntity;
import cn.cc1w.app.ui.entity.PublicParamsEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.kpush.MyLog;
import cn.cc1w.app.ui.kpush.MyMixPushReceiver;
import cn.cc1w.app.ui.manager.CrashCollectHandler;
import cn.cc1w.app.ui.manager.LocationManager;
import cn.cc1w.app.ui.umeng.UmInitConfig;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.MyDnsUtils;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ThreadUtil;
import cn.cc1w.app.ui.utils.converter.AndroidAudioConverter;
import cn.cc1w.app.ui.utils.converter.callback.ILoadCallback;
import cn.cc1w.app.ui.utils.sm4.SM4Utils;
import cn.ccwb.cloud.httplibrary.rxhttp.BaseAppContext;
import cn.ccwb.push.KpPush;
import cn.ccwb.push.mixpush.MixPushLogger;
import cn.ccwb.push.mpush.MPushTool;
import cn.kpinfo.log.KpLog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import loghttp.RxHttp;
import okhttp3.OkHttpClient;
import rxhttp.wrapper.ssl.HttpsUtils;

/**
 * APP 的全局管理类
 *
 * @author kpinfo
 */
public class AppContext extends BaseAppContext {

    @Override
    public void onCreate() {
        super.onCreate();
        initRxHttp();
        doConverterInit();
        getAppModeInfo();
        intUmengNew();
        initUtil();
        getPolicyStatus();
        addFileDownInfo();
        initKpPushInfo();
        CrashCollectHandler.Companion.getInstance().init(this);
    }

    private void getAppModeInfo() {
        AppModeEntity.DataBean appModeInfo = SharedPreferenceUtil.getAppModeInfo();
        if (null != appModeInfo) {
            Constant.IS_SHOW_RED_MODE = appModeInfo.isRed();
            Constant.IS_SHOW_GARY_MODE = appModeInfo.isMourning();
        }
    }

    public static void initShortVide() {
        if (getAppContext() != null) {
            PLShortVideoEnv.init(getAppContext());
        }
    }

    public static void initLocationInfo(Application application) {
        if (application != null) {
            LocationManager.init(application);
            Constant.IS_LOCATION_INIT_SUCCESS = true;
        }
    }

    /**
     * 初始化 xUtil框架
     */
    private void initUtil() {
        x.Ext.init(this);
        x.Ext.setDebug(false);
    }

    private void getPolicyStatus() {
        Constant.IS_POLICY_NEED_AUTHORIZATION = SharedPreferenceUtil.getPolicyState();
        if (!Constant.IS_POLICY_NEED_AUTHORIZATION) {
            initLocationInfo(this);
            initX5WebView();
            initShortVide();
            LogUtil.e("已经授权 +++ ");
        } else {
            LogUtil.e("没有授权 ！！！");
        }
    }

    /**
     * 初始化 qqx5 WebView
     */
    public static void initX5WebView() {
        try {
            QbSdk.PreInitCallback callback = new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {
                }

                @Override
                public void onViewInitFinished(boolean b) {
                }
            };
            QbSdk.initX5Environment(getAppContext(), callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

    /**
     * 初始化开屏推送
     */
    public static void initKpPushInfo() {
        if (!Constant.isPushMessageOpened) {
            Constant.isPushMessageOpened = SharedPreferenceUtil.getPushState();
        }
        KpLog.getInstance().setDebug(BuildConfig.DEBUG);
        if (Constant.isPushMessageOpened && getAppContext() != null) {
            KpPush.getInstance().init(getAppContext());
            KpPush.getInstance().setDebug(BuildConfig.DEBUG);
            MPushTool.getInstance().setAllocServer(Constant.SERVER_PUSH);
            MPushTool.getInstance().setApiHost(Constant.HOST_API_PUSH);
            MPushTool.getInstance().setClientId("cn.cc1w.app");
            MPushTool.getInstance().setClientSecret("cc1w@0201");
            MPushTool.getInstance().setPublicKey(Constant.KEY_PUBLIC_PUSH);
            KpPush.getInstance().setMpushLogger(new MyLog(getAppContext()));
            KpPush.getInstance().setMixPushLogger(new MixPushLogger() {
                @Override
                public void log(String tag, String content, Throwable throwable) {
                    LogUtil.print(tag, content + "\n" + throwable.toString());
                }

                @Override
                public void log(String tag, String content) {
                    LogUtil.print(tag, content);
                }
            });
            KpPush.getInstance().setMixPushReceiver(new MyMixPushReceiver());
            KpPush.getInstance().registerPush(new KpPush.RegisterCallback() {
                @Override
                public void onRegister(String regId) {
                    LogUtil.print("tongchao", "onRegister Success --- " + regId);
                    if (!TextUtils.isEmpty(regId)) {
                        Constant.IS_PUSH_INIT_SUCCESS = true;
                        Constant.CW_ID_KPUSH = regId;
                        boolean isRegistered = SharedPreferenceUtil.getKPushRegisterState();
                        LogUtil.d("isRegistered = " + isRegistered);
                        if (!isRegistered) {
                            SharedPreferenceUtil.setKPushRegisterState(true);
                            addKpPushTag(getAppContext());
                        }
                    }
                }

                @Override
                public void onFail(String error) {
                    LogUtil.print("tongchao", "onRegister Fail --- " + error);
                }
            });
        }
    }

    /**
     * 添加开屏推送
     */
    private static void addKpPushTag(Context context) {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.TAG_PUSH_KAI_PIN)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponse(JsonObject.class).observeOn(AndroidSchedulers.mainThread()).subscribe();
        }
    }

    private void intUmengNew() {
        UMConfigure.preInit(getAppContext(), Constant.KEY_YOUMENG, "UMENG_APPKEY");
        boolean isNeedShowPolicyDialog = SharedPreferenceUtil.getPolicyState();
        if (!isNeedShowPolicyDialog) {
            UmInitConfig umInitConfig = new UmInitConfig();
            umInitConfig.UMinit(getApplicationContext());
        }
    }

    private void addFileDownInfo() {
        Constant.LIST_FILE_DOWNLOAD.add("jpg");
        Constant.LIST_FILE_DOWNLOAD.add("jpeg");
        Constant.LIST_FILE_DOWNLOAD.add("png");
        Constant.LIST_FILE_DOWNLOAD.add("bmp");
        Constant.LIST_FILE_DOWNLOAD.add("gif");
        Constant.LIST_FILE_DOWNLOAD.add("webp");
        Constant.LIST_FILE_DOWNLOAD.add("mp4");
        Constant.LIST_FILE_DOWNLOAD.add("avi");
        Constant.LIST_FILE_DOWNLOAD.add("wmv");
        Constant.LIST_FILE_DOWNLOAD.add("3gp");
        Constant.LIST_FILE_DOWNLOAD.add("mkv");
        Constant.LIST_FILE_DOWNLOAD.add("flv");
        Constant.LIST_FILE_DOWNLOAD.add("mov");
        Constant.LIST_FILE_DOWNLOAD.add("mpg");
        Constant.LIST_FILE_DOWNLOAD.add("ts");
        Constant.LIST_FILE_DOWNLOAD.add("asf");
        Constant.LIST_FILE_DOWNLOAD.add("h264");
        Constant.LIST_FILE_DOWNLOAD.add("mts");
        Constant.LIST_FILE_DOWNLOAD.add("mp3");
        Constant.LIST_FILE_DOWNLOAD.add("wma");
        Constant.LIST_FILE_DOWNLOAD.add("wav");
        Constant.LIST_FILE_DOWNLOAD.add("ogg");
        Constant.LIST_FILE_DOWNLOAD.add("mid");
        Constant.LIST_FILE_DOWNLOAD.add("m4a");
        Constant.LIST_FILE_DOWNLOAD.add("amr");
        Constant.LIST_FILE_DOWNLOAD.add("ac3");
        Constant.LIST_FILE_DOWNLOAD.add("zip");
        Constant.LIST_FILE_DOWNLOAD.add("rar");
        Constant.LIST_FILE_DOWNLOAD.add("doc");
        Constant.LIST_FILE_DOWNLOAD.add("docx");
        Constant.LIST_FILE_DOWNLOAD.add("xls");
        Constant.LIST_FILE_DOWNLOAD.add("xlsx");
        Constant.LIST_FILE_DOWNLOAD.add("pdf");
        Constant.LIST_FILE_DOWNLOAD.add("txt");
        Constant.LIST_FILE_DOWNLOAD.add("ppt");
        Constant.LIST_FILE_DOWNLOAD.add("pptx");
        Constant.LIST_FILE_DOWNLOAD.add("apk");
        Constant.LIST_FILE_DOWNLOAD.add("css");
        Constant.LIST_FILE_DOWNLOAD.add("js");
    }

    private void initRxHttp() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier((hostname, session) -> true).dns(new MyDnsUtils()).build();
        RxHttp.init(client);
        RxHttp.setDebug(BuildConfig.DEBUG);
        RxHttp.setOnParamAssembly(params -> {
            params.addHeader("source-id", Constant.ID_APP);
            params.addHeader("User-Agent", AppUtil.getHttpHeader(this));
            params.addHeader("X-Access-Token", getSm4Params());
            String token = TextUtils.isEmpty(Constant.CW_AUTHORIZATION) ? "" : Constant.CW_AUTHORIZATION;
            if (token.isEmpty()) {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    Constant.CW_AUTHORIZATION = TextUtils.isEmpty(userInfo.getToken()) ? "" : (userInfo.getToken());
                }
            }
            params.addHeader(Constant.STR_CW_AUTHORIZATION, token);
            return params;
        });
    }

    private String getSm4Params() {
        String resultData;
        PublicParamsEntity paramsEntity = new PublicParamsEntity();
        paramsEntity.setCw_client(Constant.CW_CLIENT);
        paramsEntity.setCw_device(Constant.CW_DEVICE);
        paramsEntity.setCw_os(Constant.CW_OS);
        paramsEntity.setClient(Constant.CW_OS);
        paramsEntity.setCw_machine_type(Constant.CW_MACHINE_TYPE);
        paramsEntity.setCw_machine_id(Constant.CW_MACHINE_ID);

        paramsEntity.setCw_country(TextUtils.isEmpty(Constant.CW_COUNTRY) ? "" : Constant.CW_COUNTRY);
        paramsEntity.setCw_province(TextUtils.isEmpty(Constant.CW_PROVINCE) ? "" : Constant.CW_PROVINCE);
        paramsEntity.setCw_city(TextUtils.isEmpty(Constant.CW_CITY) ? "" : Constant.CW_CITY);
        paramsEntity.setCw_area(TextUtils.isEmpty(Constant.CW_AREA) ? "" : Constant.CW_AREA);
        paramsEntity.setCw_longitude(TextUtils.isEmpty(Constant.CW_LONGITUDE) ? "" : Constant.CW_LONGITUDE);
        paramsEntity.setCw_latitude(TextUtils.isEmpty(Constant.CW_LATITUDE) ? "" : Constant.CW_LATITUDE);

        paramsEntity.setCw_ip(TextUtils.isEmpty(Constant.CW_IP) ? "" : Constant.CW_IP);
        paramsEntity.setCw_version(TextUtils.isEmpty(Constant.CW_VERSION) ? AppUtil.getVersionName(this) : Constant.CW_VERSION);
        paramsEntity.setCw_networktype(TextUtils.isEmpty(Constant.CW_NETWORKTYPE) ? "" : Constant.CW_NETWORKTYPE);
        paramsEntity.setCw_devicemodel(Constant.TYPE_MOBILE);

        if (TextUtils.isEmpty(Constant.CW_ID_KPUSH)) {
            String id = SharedPreferenceUtil.getKPushRegisterId();
            if (!TextUtils.isEmpty(id)) {
                Constant.CW_ID_KPUSH = id;
            }
        }
        paramsEntity.setKpush_regId(TextUtils.isEmpty(Constant.CW_ID_KPUSH) ? "" : Constant.CW_ID_KPUSH);
        Gson gson = new Gson();
        String content = gson.toJson(paramsEntity);
        resultData = TextUtils.isEmpty(SM4Utils.encryptECB(Constant.KEY_SM4, content)) ? "" : SM4Utils.encryptECB(Constant.KEY_SM4, content);
        return resultData;
    }

    public boolean isDebuggable() {
        return 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
    }

    private void doConverterInit() {
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                Constant.IS_AUDIO_CONVERT_FINISHED = true;
            }

            @Override
            public void onFailure(Exception error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ThreadUtil.destroy();
    }
}