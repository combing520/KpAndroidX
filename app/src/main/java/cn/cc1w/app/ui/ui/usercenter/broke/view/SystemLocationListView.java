package cn.cc1w.app.ui.ui.usercenter.broke.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.net.URISyntaxException;
import java.util.List;

import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.R;

/**
 *
 * @author kpinfo
 * @date 2018/11/12
 * 爆料中系统发来的定位
 */
public class SystemLocationListView {
    private final Context context;
    private int index;
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.SystemLocationHolder holder;
    private long lastTime;
    public SystemLocationListView(Context context, int position, RecyclerView.ViewHolder h, ItemBrokeEntity item, List<ItemBrokeEntity> dataSet) {
        holder = (BrokeAdapter.SystemLocationHolder) h;
        entity = item;
        this.context = context;
        lastTime = System.currentTimeMillis();

    }
    public void initView() {
        if (null != entity) {
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.userAvatarImg);
            holder.address.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            // 将位置信息加入
            holder.mapImg.setOnClickListener(v -> {
                //  将地图信息分享出去
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    shareUserLocation(entity.getLongitude(), entity.getLatitude(), entity.getContent());
                }
                lastTime = currentTime;
            });
        }
    }

    /**
     * 分享用户的地址
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param address   内容
     */
    private void shareUserLocation(String longitude, String latitude, String address) {
        gaoDe(context, latitude, longitude, address);
    }
    private void openMap(String longitude, String latitude, String address) {
        try {
            Intent intent = Intent.getIntent("androidamap://route?sourceApplication=softname&slat=" + (25.035056 + 0.02) + "&slon=" + (102.693424 + 0.01) + "&sname=" + "我的位置" + "&dlat=" + Double.parseDouble(latitude) + "&dlon=" +
                    Double.parseDouble(longitude) + "&dname=" + address + "&dev=0&m=0&t=1");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 高德地图
     *
     * @param lat      纬度
     * @param lng      经度
     */
    private static void gaoDe(Context mContext, String lat, String lng, String adr) {
        if (AppUtil.isAvilible(mContext, "com.autonavi.minimap")) {
            LogUtil.e("安装了");
            try {
                String url = "amapuri://route/plan/?sid=BGVIS1&slat=&slon=&sname=&did=&dlat=" + lat + "&dlon=" + lng + "&dname=" + adr + "&dev=0&t=0";
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            T.TshowShort(mContext,"您尚未安装高德地图");
            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    /**
     * 给地图添加标记物
     *
     * @param map    地图
     * @param latLng 对应的位置
     */
    private void addMarker2Map(AMap map, LatLng latLng) {
        Marker marker1 = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        marker1.setVisible(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }
}