package cn.cc1w.app.ui.ui.usercenter.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.ToastUtil;

/**
 * 地图详情
 *  @author kpinfo
 */
public class MapDetailActivity extends AppCompatActivity implements AMap.OnInfoWindowClickListener {
    private Unbinder unbinder;
    @BindView(R.id.mapView_detail)
    TextureMapView mapView;
    @BindView(R.id.txt_detail)
    TextView positionInfoTv;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    private UiSettings uiSettings;
    private double lon;
    private double lat;
    private String address;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        unbinder = ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        initNavigation();
        initData();
        setupMap();
        positionInfoTv.setText(address);
    }

    private void setupMap() {
        if (null == aMap) {
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();
        }
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        LatLng latLng = new LatLng(lat, lon);
        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15, 30, 30)));
        addMarkersToMap(latLng);
        LogUtil.e("lon = " + lon + " lat = " + lat);
    }

    /**
     * 添加 marker
     *
     */
    private void addMarkersToMap(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(latLng)
                .title("到这里去");
        Marker marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
        aMap.setOnInfoWindowClickListener(this);
    }

    private void initNavigation() {
        titleTv.setText("位置信息");
    }

    private void initData() {
        lat = Double.parseDouble(getIntent().getStringExtra("lat"));
        lon = Double.parseDouble(getIntent().getStringExtra("lon"));
        address = getIntent().getStringExtra("address");
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {
        aMap.moveCamera(update);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!TextUtils.isEmpty(Constant.ADDRESS) && !TextUtils.isEmpty(Constant.CW_LONGITUDE) && !TextUtils.isEmpty(Constant.CW_LATITUDE)){
            Bundle bundle = new Bundle();
            bundle.putString("startLon", Constant.CW_LONGITUDE);
            bundle.putString("startLat", Constant.CW_LATITUDE);
            bundle.putString("startAddress", Constant.ADDRESS);
            bundle.putString("endLon", String.valueOf(lon));
            bundle.putString("endLat", String.valueOf(lat));
            bundle.putString("endAddress", address);
            bundle.putInt("naviType", 0);
            IntentUtil.startActivity(this, NavigationActivity.class, bundle);
        }
        else{
            ToastUtil.showShortToast("无法获取当前定位信息");
        }
    }
}