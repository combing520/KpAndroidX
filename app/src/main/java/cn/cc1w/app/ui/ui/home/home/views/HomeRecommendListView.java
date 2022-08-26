package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.entity.HomeNewsEntity;


/**
 * @author kpinfo
 * @date 2018/10/23
 * 首页普通新闻
 */
public class HomeRecommendListView {
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.RecommendHolder holder;

    public HomeRecommendListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        this.entity = entity;
        holder = (HomeNewsAdapter.RecommendHolder) h;
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.container.setVisibility(View.VISIBLE);
            holder.titleTv.setText(entity.getNews().get(0).getTitle());
        } else {
            holder.container.setVisibility(View.GONE);
        }
    }
}