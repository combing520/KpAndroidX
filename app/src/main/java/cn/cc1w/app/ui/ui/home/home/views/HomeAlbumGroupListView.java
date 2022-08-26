package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.HomePhotoGroupSubAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.home.MoreAlbumListActivity;

/**
 * @author kpinfo
 * 首页 图集组
 */
public class HomeAlbumGroupListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.AlbumGroupHolder holder;
    private long lastTime;

    public HomeAlbumGroupListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.AlbumGroupHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.titleTv.setText(entity.getType_name());
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            manager.setOrientation(RecyclerView.HORIZONTAL);
            holder.recyclerView.setLayoutManager(manager);
            manager.setAutoMeasureEnabled(true);
            HomePhotoGroupSubAdapter adapter = new HomePhotoGroupSubAdapter();
            holder.recyclerView.setAdapter(adapter);
            if (holder.recyclerView.getAdapter() != null) {
                ((HomePhotoGroupSubAdapter) holder.recyclerView.getAdapter()).setData(entity.getNews());
            }
            holder.btnMore.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String id = entity.getNews().get(0).getId();
                    if (!TextUtils.isEmpty(id)) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(activity, MoreAlbumListActivity.class);
                        intent.putExtra(Constant.TAG_ID, id);
                        activity.startActivity(intent);
                    }
                }
                lastTime = currentTime;
            });
        }
    }
}