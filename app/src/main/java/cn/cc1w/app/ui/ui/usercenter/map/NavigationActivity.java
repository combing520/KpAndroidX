package cn.cc1w.app.ui.ui.usercenter.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.ToastUtil;

/**
 * 地图导航
 *
 * @author kpinfo
 */
public class NavigationActivity extends CustomActivity {
    private Unbinder unbinder;
    private double startPosLat;
    private double startPosLon;
    private String startPosAddress;
    private double endPosLat;
    private double endPosLon;
    private String endPosAddress;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        init();
    }

    private void initStatusBar() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(false).init();
    }

    /**
     * 初始化
     */
    private void init() {
        initStatusBar();
        unbinder = ButterKnife.bind(this);
        overridePendingTransition(0, 0);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        lastTime = System.currentTimeMillis();
        startPosLon = Double.parseDouble(getIntent().getStringExtra("startLon"));
        startPosLat = Double.parseDouble(getIntent().getStringExtra("startLat"));
        startPosAddress = getIntent().getStringExtra("startAddress");
        endPosLon = Double.parseDouble(getIntent().getStringExtra("endLon"));
        endPosLat = Double.parseDouble(getIntent().getStringExtra("endLat"));
        endPosAddress = getIntent().getStringExtra("endAddress");
    }

    /**
     * 使用高德地图
     *
     * @param endPosLat  目标位置的 纬度
     * @param endPosLon  目标位置的 经度
     * @param endAddress 目标位置的 地址
     */
    private void gotoGaodeMap(double endPosLat, double endPosLon, String endAddress) {
        if (AppUtil.isAvilible(this, "com.autonavi.minimap")) {
            try {
                String url = "amapuri://route/plan/?sid=BGVIS1&slat=" + startPosLat + "&slon=" + startPosLon + "&sname=" + startPosAddress + "&did=&dlat=" + endPosLat + "&dlon=" + endPosLon + "&dname=" + endAddress + "&dev=0&t=0";
                Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShortToast("您尚未安装高德地图");
            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * 使用百度地图
     */
    private void gotoBaiduMap(double endPosLat, double endPosLon, String endAddress) {
        if (AppUtil.isAvilible(this, "com.baidu.BaiduMap")) {
            Uri uri = Uri.parse("baidumap://map/direction?destination=latlng:" + endPosLat + "," + endPosLon + "|name:" + endAddress);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            finish();
        } else {
            ToastUtil.showShortToast("您尚未安装百度地图");
            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * 使用腾讯地图
     *
     * @param endPosLat  目标位置的 纬度
     * @param endPosLon  目标位置的 经度
     * @param endAddress 目标位置的 地址
     */
    private void gotoTencntMap(double endPosLat, double endPosLon, String endAddress) {
        if (AppUtil.isAvilible(this, "com.tencent.map")) {
            // 启动路径规划页面
            Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=" + endAddress + "&tocoord=" + endPosLat + "," + endPosLon + "&policy=0&referer=appName"));
            naviIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(naviIntent);
        } else {
            ToastUtil.showShortToast("您尚未安装腾讯地图");
            Uri uri = Uri.parse("market://details?id=com.tencent.map");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @OnClick({R.id.txt_gaode_navigation, R.id.txt_tencent_navigation, R.id.txt_baidu_navigation, R.id.txt_cancel_navigation})
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.txt_gaode_navigation) {
                gotoGaodeMap(endPosLat, endPosLon, endPosAddress);
            } else if (id == R.id.txt_tencent_navigation) {
                gotoTencntMap(endPosLat, endPosLon, endPosAddress);
            } else if (id == R.id.txt_baidu_navigation) {
                gotoBaiduMap(endPosLat, endPosLon, endPosAddress);
            } else if (id == R.id.txt_cancel_navigation) {
                finish();
            }
        }
        lastTime = currentTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}