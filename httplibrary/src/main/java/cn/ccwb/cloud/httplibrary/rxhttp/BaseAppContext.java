package cn.ccwb.cloud.httplibrary.rxhttp;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import java.lang.ref.WeakReference;

/**
 * @author kpinfo
 */
public class BaseAppContext extends MultiDexApplication {
    private static WeakReference<Context> context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = new WeakReference<>(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return context.get();
    }
}
