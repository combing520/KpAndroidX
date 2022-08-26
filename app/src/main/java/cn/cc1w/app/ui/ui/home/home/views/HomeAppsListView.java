package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.SubHomeAppsAdapter;
import cn.cc1w.app.ui.entity.HomeNewsEntity;

/**
 *
 * @author kpinfo
 * 首页应用号
 */
public class HomeAppsListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.AppsHolder holder;
    private final SubHomeAppsAdapter adapter;

    public HomeAppsListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity,LifecycleOwner owner) {
        activity = ac;
//        int index = i;
        this.entity = entity;
        holder = (HomeNewsAdapter.AppsHolder) h;
        adapter = new SubHomeAppsAdapter(owner);
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            holder.appsRecycleView.setLayoutManager(manager);
            adapter.setData(entity.getNews());
            holder.appsRecycleView.setAdapter(adapter);
        }
    }
}