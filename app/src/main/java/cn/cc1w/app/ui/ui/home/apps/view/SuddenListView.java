package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;

import java.util.List;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 突发 View
 *
 * @author kpinfo
 */
public class SuddenListView {
    private final NewsEntity.DataBean list;
    private final AppsDetailNewsAdapter.SuddenHolder holder;
    private final Context context;
    private long lastTime;

    public SuddenListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        this.context = ac;
        list = l;
        holder = (AppsDetailNewsAdapter.SuddenHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != list && null != list.getNews() && !list.getNews().isEmpty()) {
            initBanner(holder.banner, list.getNews());
        } else {
            holder.banner.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化 Banner相关信息
     */
    private void initBanner(Banner banner, List<NewsEntity.DataBean.ItemNewsEntity> list) {
        banner.setAdapter(new BannerImageAdapter<NewsEntity.DataBean.ItemNewsEntity>(list) {
            @Override
            public void onBindView(BannerImageHolder holder, NewsEntity.DataBean.ItemNewsEntity data, int position, int size) {
                AppUtil.loadBannerImg(data.getPic_path(), holder.imageView);
            }
        });
    }
}