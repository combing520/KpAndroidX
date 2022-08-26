package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.adapter.details_apps.PhotoGroupSubAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.ui.home.home.MoreAlbumListActivity;

/**
 * 动新闻 [图集组]
 *
 * @author kpinfo
 */
public class AlbumGroupNewsListView {
    private final Context activity;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.AlbumGroupHolder holder;
    private long lastTime;

    public AlbumGroupNewsListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        activity = ac;
//        int index = i;
        entity = l;
        holder = (AppsDetailNewsAdapter.AlbumGroupHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.titleTv.setText(entity.getType_name());
            holder.btnMore.setVisibility(View.GONE);
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            manager.setOrientation(RecyclerView.HORIZONTAL);
            holder.recyclerView.setLayoutManager(manager);
            manager.setAutoMeasureEnabled(true);
            PhotoGroupSubAdapter adapter = new PhotoGroupSubAdapter();
            holder.recyclerView.setAdapter(adapter);
            if (holder.recyclerView.getAdapter() != null) {
                ((PhotoGroupSubAdapter) holder.recyclerView.getAdapter()).setData(entity.getNews());
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