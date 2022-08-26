package cn.cc1w.app.ui.ui.usercenter.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.Projection;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.LogUtil;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 获取定位
 *  @author kpinfo
 */
public class LocationActivity extends AppCompatActivity implements LocationSource, AMap.OnMapTouchListener, AMapLocationListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapScreenShotListener, EasyPermissions.PermissionCallbacks {
    @BindView(R.id.map_location)
    TextureMapView mapView;
    @BindView(R.id.txt_address_detail_location)
    TextView detailAddressTv;
    @BindView(R.id.txt_address_location)
    TextView simpleAddressTv;
    private AMap aMap;
    private Unbinder unbinder;
    private AMapLocationClient aMapLocationClient;
    private OnLocationChangedListener mListener;
    private Marker locationMarker;
    private Projection projection;
    private final MyCancelCallback myCancelCallback = new MyCancelCallback();
    private UiSettings uiSettings;
    private double lat;
    private double lon;
    private String address;
    private boolean isFirstShowMarker = true;
    private GeocodeSearch geoCoderSearch;
    private static final int CODE_REQUEST_PERMISSION_LOCATION = 300;
    private static final int CODE_REQUEST_PERMISSION_PIC_SEND = 100;
    private boolean isPermissionDialogShow = false;
    private boolean isLocationRequestSuccess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        unbinder = ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissionDialog();
        } else {
            init();
        }
    }

    @SuppressLint("InflateParams")
    private void showPermissionDialog() {
        final String[] permissionList = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (!EasyPermissions.hasPermissions(this, permissionList)) {
            LogUtil.e("权限被拒绝 ");
            EasyPermissions.requestPermissions(this, "权限申请\n\n" +
                    "为了能够正常的使用导航服务，请允许开屏新闻使用您的定位权限。", CODE_REQUEST_PERMISSION_LOCATION, permissionList);
        } else {
            LogUtil.e("已授权");
            init();
        }
    }

    private void init() {
        initMap();
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        if (null == aMap) {
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();
        }
        geoCoderSearch = new GeocodeSearch(this);
        geoCoderSearch.setOnGeocodeSearchListener(this);
        setUpMap();
    }

    private void setUpMap() {
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMapTouchListener(this);
        aMap.setOnCameraChangeListener(this);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        LogUtil.e("onResume --->>> ");
        if (!isLocationRequestSuccess) {
            initMap();
            isLocationRequestSuccess = true;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
        LogUtil.e("onPause --- >>> ");
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (aMapLocationClient == null) {
            aMapLocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            aMapLocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(Constant.TIME_INTERVAL_LOCATION);
            aMapLocationClient.setLocationOption(mLocationOption);
            aMapLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        aMapLocationClient = null;
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        LogUtil.e("--->>> onLocationChanged ");
        if (mListener != null && aMapLocation != null && null != aMapLocationClient) {
            if (aMapLocation.getErrorCode() == 0) {
                isLocationRequestSuccess = true;
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (isFirstShowMarker) {
                    lat = aMapLocation.getLatitude();
                    lon = aMapLocation.getLongitude();
                    address = aMapLocation.getAddress();
                }
                if (locationMarker == null) {
                    locationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                            .anchor(0.5f, 0.5f));
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } else {
                    if (isFirstShowMarker) {
                        isFirstShowMarker = false;
                        startMoveLocationAndMap(latLng);
                    }
                }
            } else {
                isLocationRequestSuccess = false;
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                LogUtil.e("AmapErr = " + errText);
                //  定位权限缺失，让用户去打开
                if (aMapLocation.getErrorCode() == 12 && !isPermissionDialogShow) { // 缺少定位权限。
                    isPermissionDialogShow = true;
                    new AppSettingsDialog.Builder(this)
                            .setTitle("权限申请")
                            .setRationale("为了能够正常的使用地图定位服务，请允许开屏新闻使用您的定位权限")
                            .setNegativeButton("取消")
                            .setPositiveButton("确定")
                            .build().show();
                }
            }
        }
    }

    /**
     * 同时修改自定义定位小蓝点和地图的位置
     *
     * @param latLng 经纬度
     */
    private void startMoveLocationAndMap(LatLng latLng) {
        if (projection == null) {
            projection = aMap.getProjection();
        }
        if (locationMarker != null && projection != null) {
            LatLng markerLocation = locationMarker.getPosition();
            Point screenPosition = aMap.getProjection().toScreenLocation(markerLocation);
            locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        }
        myCancelCallback.setTargetLatlng(latLng);
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng), 1000, myCancelCallback);
        doGeocode(latLng);
    }

    /**
     * Poi 搜索
     *
     * @param latLng 定位信息
     */
    private void doGeocode(LatLng latLng) {
        LatLonPoint lonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        lat = latLng.latitude;
        lon = latLng.longitude;
        RegeocodeQuery query = new RegeocodeQuery(lonPoint, 500,
                GeocodeSearch.AMAP);
        geoCoderSearch.getFromLocationAsyn(query);

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (null != cameraPosition) {
            LatLng latLng = cameraPosition.target;
            doGeocode(latLng);
            startJumpAnimation(latLng);
        }
    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation(LatLng latLng) {
        if (locationMarker != null) {
            Point point = aMap.getProjection().toScreenLocation(latLng);
            locationMarker.setPositionByPixels(point.x, point.y);
            point.y -= AppUtil.dip2px(this, 125);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(input -> {
                if (input <= 0.5) {
                    return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                } else {
                    return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                }
            });
            animation.setDuration(600);
            locationMarker.setAnimation(animation);
            locationMarker.startAnimation();
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                String detailAddress = "";
                String areaAddress = "";
                if (addressName.contains("区")) {
                    String[] ss = addressName.split("区", -1);
                    detailAddress = ss[ss.length - 1];
                    areaAddress = ss[0];
                } else if (addressName.contains("县")) {
                    String[] ss = addressName.split("县", -1);
                    detailAddress = ss[ss.length - 1];
                    areaAddress = ss[0];
                }
                //  进行 poi 搜索
                detailAddressTv.setText(TextUtils.isEmpty(areaAddress) ? "" : areaAddress);
                simpleAddressTv.setText(TextUtils.isEmpty(detailAddress) ? "" : detailAddress);
                address = addressName;
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int rCode) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if (null == bitmap) {
            return;
        }
        try {
            if (getExternalCacheDir() != null) {
                String path = getExternalCacheDir().getAbsolutePath() + File.separator + Constant.DIR_CACHE + File.separator + "map_";
                String picName = sdf.format(new Date());
                String suffix_pic = ".jpg";

                File fileParent = new File(path.concat(picName).concat(suffix_pic));
                File fileParent2 = fileParent.getParentFile();
                if (!fileParent2.exists()) {
                    fileParent2.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + picName + suffix_pic);
                boolean isSaveSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 32, fos);
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isSaveSuccess) {
                    EventBus.getDefault().post(new EventMessage("location", "location", String.valueOf(lon), String.valueOf(lat), address, (path.concat(picName).concat(suffix_pic))));
                    finish();
                } else {
                    LogUtil.e("截屏失败 ");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监控地图动画移动情况，如果结束或者被打断，都需要执行响应的操作
     */
    class MyCancelCallback implements AMap.CancelableCallback {
        LatLng targetLatlng;

        public void setTargetLatlng(LatLng latlng) {
            this.targetLatlng = latlng;
        }

        @Override
        public void onFinish() {
            if (locationMarker != null && targetLatlng != null) {
                locationMarker.setPosition(targetLatlng);
            }
        }

        @Override
        public void onCancel() {
            if (locationMarker != null && targetLatlng != null) {
                locationMarker.setPosition(targetLatlng);
            }
        }
    }

    @OnClick({R.id.tv_back_location, R.id.tv_send_location})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_back_location) {
            finish();
        } else if (id == R.id.tv_send_location) {
            sendLocation();
        }
    }

    /**
     * 发送位置
     */
    private void sendLocation() {
        final String[] permissionList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, permissionList)) {
            LogUtil.e("权限被拒绝 ");
            EasyPermissions.requestPermissions(this, "权限申请\n" +
                    "为了能够正常的使用地图截图保存功能，请允许开屏新闻使用您的手机存储功能。", CODE_REQUEST_PERMISSION_PIC_SEND, permissionList);
        } else {
            getMapScreenShot();
        }
    }

    /**
     * 获取地图截屏
     */
    private void getMapScreenShot() {
        aMap.getMapScreenShot(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtil.e("ParentHomeFragment  onPermissionsGranted code =  " + requestCode);
        if (requestCode == CODE_REQUEST_PERMISSION_LOCATION) {
            init();
        } else if (requestCode == CODE_REQUEST_PERMISSION_PIC_SEND) {
            getMapScreenShot();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PERMISSION_LOCATION) {
            LogUtil.e("地理定位权限被拒绝  requestCode " + requestCode);
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限申请")
                    .setRationale("为了能够正常的使用地图定位服务，请允许开屏新闻使用您的定位权限")
                    .setNegativeButton("取消")
                    .setPositiveButton("确定")
                    .build().show();
        } else if (requestCode == CODE_REQUEST_PERMISSION_PIC_SEND) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限申请")
                    .setRationale("为了能够正常的使用地图截图功能，请允许开屏新闻使用您的手机存储功能")
                    .setNegativeButton("取消")
                    .setPositiveButton("确定")
                    .build().show();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != aMapLocationClient) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        unbinder.unbind();
    }
}
