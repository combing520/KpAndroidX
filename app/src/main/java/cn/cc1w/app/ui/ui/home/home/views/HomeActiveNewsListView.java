package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.HomeVideoListSubAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.home.MoreVideoListActivity;

/**
 * @author kpinfo
 * 动新闻
 */
public class HomeActiveNewsListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.ActiveNewsHolder holder;
    private long lastTime;

    public HomeActiveNewsListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        //        int index = i;
        this.entity = entity;
        holder = (HomeNewsAdapter.ActiveNewsHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.titleTv.setText(TextUtils.isEmpty(entity.getType_name()) ? "" : entity.getType_name());
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            manager.setOrientation(RecyclerView.HORIZONTAL);
            holder.recyclerView.setLayoutManager(manager);
            manager.setAutoMeasureEnabled(true);
            HomeVideoListSubAdapter adapter = new HomeVideoListSubAdapter();
            holder.recyclerView.setAdapter(adapter);
            if (holder.recyclerView.getAdapter() != null) {
                ((HomeVideoListSubAdapter) holder.recyclerView.getAdapter()).setData(entity.getNews());
            }
            holder.btnMore.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String id = entity.getNews().get(0).getId();
                    if (!TextUtils.isEmpty(id)) {
                        Intent intent = new Intent();
                        intent.setClass(activity, MoreVideoListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.TAG_ID, id);
                        activity.startActivity(intent);
                    }
                }
                lastTime = currentTime;
            });

        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }
}