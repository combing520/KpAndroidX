package cn.cc1w.app.ui.manager;

import android.app.Application;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.LogUtil;

/**
 * @author kpinfo
 */
public class LocationManager {
    private static volatile LocationManager sLocationManager;
    private static Application mApplication;
    private AMapLocationClient mLocationClient;
    private AMapLocation mCurrentLocation;

    public static LocationManager getInstance() {
        if (sLocationManager == null) {
            synchronized (LocationManager.class) {
                if (sLocationManager == null) {
                    sLocationManager = new LocationManager();
                }
            }
        }
        return sLocationManager;
    }

    public static void init(Application application) {
        mApplication = application;
    }

    private LocationManager() {
        mLocationClient = new AMapLocationClient(mApplication);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setInterval(Constant.TIME_DURATION_LOCATION);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
    }

    public void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    public void setLocationCallback(final LocationCallback callback) {
        if (mLocationClient != null) {
            mLocationClient.setLocationListener(aMapLocation -> {
                if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                    mCurrentLocation = aMapLocation;
                    callback.onLocation(aMapLocation);
                } else {
                    LogUtil.e("setLocationCallback  失败！！！ +++  错误码 = " + aMapLocation.getErrorCode());
                    callback.onLocation(null);
                }
            });
        }
    }

    public interface LocationCallback {
        void onLocation(AMapLocation aMapLocation);
    }

    public AMapLocation getLocation() {
        if (mCurrentLocation != null) {
            return mCurrentLocation;
        }
        return null;
    }

    public void destroy() {
        stopLocation();
        mLocationClient = null;
        mCurrentLocation = null;
    }
}