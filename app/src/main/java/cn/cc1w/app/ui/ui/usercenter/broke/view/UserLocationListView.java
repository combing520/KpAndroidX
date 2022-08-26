package cn.cc1w.app.ui.ui.usercenter.broke.view;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.List;

import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.ui.usercenter.map.MapDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.R;

/**
 *
 * @author kpinfo
 * @date 2018/11/12
 */
public class UserLocationListView {
    private final Context context;
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.UserLocationHolder holder;
    private long lastTime;

    public UserLocationListView(Context context, RecyclerView.ViewHolder h, ItemBrokeEntity item, List<ItemBrokeEntity> dataSet) {
        holder = (BrokeAdapter.UserLocationHolder) h;
        entity = item;
        this.context = context;
        lastTime = System.currentTimeMillis();
    }

    public void initView() {
        if (null != entity) {
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.userAvatarImg);
            holder.address.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            AppUtil.loadMapImg(entity.getPic_path(), holder.mapImg);
            holder.mapImg.setOnClickListener(v -> {
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
        Bundle bundle = new Bundle();
        bundle.putString("lon", longitude);
        bundle.putString("lat", latitude);
        bundle.putString("address", address);
        IntentUtil.startActivity(context, MapDetailActivity.class, bundle);
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
