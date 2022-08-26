package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.adapter.details_apps.NewsGroupSubAdapter;
import cn.cc1w.app.ui.entity.NewsEntity;

/**
 * 新闻组 View
 * @author kpinfo
 */
public class NewsGroupListView {
    private final Context activity;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.NewsGroupHolder holder;
    private final NewsGroupSubAdapter adapter;
    public NewsGroupListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        activity = ac;
//        int index = i;
        entity = l;
        holder = (AppsDetailNewsAdapter.NewsGroupHolder) h;
        adapter = new NewsGroupSubAdapter();
    }
    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.newsList.setLayoutManager(manager);
            adapter.setData(entity.getNews());
            holder.newsList.setAdapter(adapter);
            holder.titleTv.setText("新闻组");
            holder.container.setVisibility(View.GONE);
        }
    }
}