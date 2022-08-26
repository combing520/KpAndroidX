package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 活动 View
 *
 * @author kpinfo
 */
public class SpecialListView {
    private final Context activity;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.SpecialHolder holder;
    private long lastTime;

    public SpecialListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        activity = ac;
//        int index = i;
        entity = l;
        holder = (AppsDetailNewsAdapter.SpecialHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            NewsEntity.DataBean.ItemNewsEntity itemNews = entity.getNews().get(0);
            if (null != itemNews) {
                holder.titleTv.setText(itemNews.getTitle());
                AppUtil.loadNewsImg(itemNews.getPic_path(), holder.coverIv);
                holder.coverIv.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        IntentUtil.startActivity2DetailWithEntity2(holder.coverIv.getContext(), itemNews);
                    }
                    lastTime = currentTime;
                });
                holder.titleTv.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        IntentUtil.startActivity2DetailWithEntity2(holder.titleTv.getContext(), itemNews);
                    }
                    lastTime = currentTime;
                });
            }
            holder.moreSpecialLayout.setVisibility(View.GONE);
        }
    }
}