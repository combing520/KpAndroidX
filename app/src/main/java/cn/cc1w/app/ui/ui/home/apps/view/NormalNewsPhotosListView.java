package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.adapter.home.HomeNormalNewsPhotosSubAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * @author kpinfo
 */
public class NormalNewsPhotosListView {
    private final Context context;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.NormalNewsPhotosHolder holder;
    private long lastTime;

    public NormalNewsPhotosListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean entity) {
        this.context = ac;
        this.entity = entity;
        holder = (AppsDetailNewsAdapter.NormalNewsPhotosHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            final NewsEntity.DataBean.ItemNewsEntity itemNews = entity.getNews().get(0);
            if (null != itemNews) {
                holder.newsTitleTv.setText(itemNews.getTitle());
                holder.createTimeTv.setText(itemNews.getCreate_time());
                holder.newsAppsNameTv.setText(itemNews.getApp_name());
                GridLayoutManager manager = new GridLayoutManager(context, 3);
                holder.recyclerView.setLayoutManager(manager);
                manager.setAutoMeasureEnabled(true);
                HomeNormalNewsPhotosSubAdapter adapter = new HomeNormalNewsPhotosSubAdapter();
                holder.recyclerView.setAdapter(adapter);
                if (holder.recyclerView.getAdapter() != null) {
                    ((HomeNormalNewsPhotosSubAdapter) holder.recyclerView.getAdapter()).setData(itemNews.getPhotos());
                }
                holder.itemView.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        IntentUtil.startActivity2DetailWithEntity2(context, itemNews);
                    }
                    lastTime = currentTime;
                });
            }
        }
    }
}