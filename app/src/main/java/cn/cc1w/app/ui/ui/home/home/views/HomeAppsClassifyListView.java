package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.home.HomeAppsClassifySubAdapter;
import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.entity.HomeNewsEntity;

/**
 *
 * @author kpinfo
 * 首页普通新闻
 */
public class HomeAppsClassifyListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.AppsClassifyHolder holder;
    private final HomeAppsClassifySubAdapter adapter;

    public HomeAppsClassifyListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.AppsClassifyHolder) h;
        adapter = new HomeAppsClassifySubAdapter(ac);
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            LinearLayoutManager manager = new LinearLayoutManager(activity) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.recyclerView.setLayoutManager(manager);
            adapter.setData(entity.getNews());
            holder.recyclerView.setAdapter(adapter);
        }
    }
}