package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.util.List;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.bannerAdapter.HomeSuddenItemAdapter;
import cn.cc1w.app.ui.entity.HomeNewsEntity;

/**
 * @author kpinfo
 * @date 2018/10/23
 * 首页普通新闻
 */
public class HomeSuddenListView {
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.SuddenHolder holder;
    private final Context context;

    public HomeSuddenListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        this.entity = entity;
        this.context = ac;
        holder = (HomeNewsAdapter.SuddenHolder) h;
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            initBanner(holder.banner, entity.getNews());
        } else {
            holder.banner.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化 Banner相关信息
     */
    private void initBanner(Banner banner, List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> list) {
        HomeSuddenItemAdapter mBannerAdapter = new HomeSuddenItemAdapter(list);
        banner.setAdapter(mBannerAdapter).setIndicator(new CircleIndicator(context));
    }
}