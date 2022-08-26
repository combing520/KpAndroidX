package cn.cc1w.app.ui.umeng;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.constants.Constant;


/**
 * @author kpinfo
 * on 2021-08-24
 */
public class UmInitConfig {

    private static final String TAG = "UmInitConfig";
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";

    public void UMinit(Context context) {
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        if (context != null) {
            UMConfigure.init(context, Constant.KEY_YOUMENG, "UMENG_APPKEY", UMConfigure.DEVICE_TYPE_PHONE, "");
            String fileProvider = BuildConfig.APPLICATION_ID + ".fileprovider";

            PlatformConfig.setWeixin(Constant.WX_APP_ID, Constant.WX_APP_SECRET);
            PlatformConfig.setWXFileProvider(fileProvider);

            PlatformConfig.setSinaWeibo(Constant.SINA_APP_ID, Constant.SINA_APP_KEY, "http://sns.whalecloud.com");
            PlatformConfig.setSinaFileProvider(fileProvider);

            PlatformConfig.setQQZone(Constant.QQ_APP_ID, Constant.QQ_APP_KEY);
            PlatformConfig.setQQFileProvider(fileProvider);

            //集成umeng-crash-vx.x.x.aar，则需要关闭原有统计SDK异常捕获功能
            MobclickAgent.setCatchUncaughtExceptions(false);
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
            //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
            UMConfigure.setProcessEvent(true);//支持多进程打点

            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        }
    }
}