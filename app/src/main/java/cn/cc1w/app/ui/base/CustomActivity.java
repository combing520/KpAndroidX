package cn.cc1w.app.ui.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.manager.AppManager;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.kpinfo.log.KpLog;

/**
 * Activity 的基类
 *
 * @author kpinfo
 */
public class CustomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && this.isTranslucentOrFloating()) {
            this.fixOrientation();
        }
        super.onCreate(savedInstanceState);
        initData();
        ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).init();
        overridePendingTransition(R.anim.ccwb_left_in, R.anim.ccwb_left_out);
        AppManager.addActivity(this);
        setPageGrayModeWithHardWareSpeed();
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

    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            Object obj = m.invoke(null, ta);
            if (obj != null) {
                isTranslucentOrFloating = (boolean) obj;
            }
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo activityInfo = (ActivityInfo) field.get(this);
            if (activityInfo != null) {
                activityInfo.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            }
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置黑白化 --- 针对有WebView的页面 -- 必须要 硬件加速才可以
     */
    public void setPageGrayModeWithHardWareSpeed() {
        if (Constant.IS_SHOW_GARY_MODE) {
            View decorView = getWindow().getDecorView();
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            decorView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        }
    }

    private void initData(){
        if (TextUtils.isEmpty(Constant.CW_MACHINE_ID)) {
            if (!Constant.IS_POLICY_NEED_AUTHORIZATION) {
                AppUtil.initPublicParams(this);
            } else {
                Constant.IS_POLICY_NEED_AUTHORIZATION = SharedPreferenceUtil.getPolicyState();
                if (!Constant.IS_POLICY_NEED_AUTHORIZATION) {
                    AppUtil.initPublicParams(this);
                }
            }
        }
        if (!Constant.IS_LOCATION_INIT_SUCCESS) {
            AppContext.initLocationInfo(getApplication());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!BuildConfig.DEBUG){
            MobclickAgent.onPageStart(getClass().getSimpleName());
            KpLog.getInstance().onAppStart(this);
            KpLog.getInstance().onAppViewScreenIn(this, getClass().getSimpleName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!BuildConfig.DEBUG){
            MobclickAgent.onPageEnd(getClass().getSimpleName());
            KpLog.getInstance().onAppViewScreenOut(this, getClass().getSimpleName());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        AppManager.finishActivity(this);
        super.onDestroy();
    }
}