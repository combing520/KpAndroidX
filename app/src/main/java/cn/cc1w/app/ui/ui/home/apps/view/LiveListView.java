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
 * 直播 View
 *
 * @author kpinfo
 */
public class LiveListView {
    private final Context activity;
    private final NewsEntity.DataBean list;
    private final AppsDetailNewsAdapter.LiveHolder holder;
    private long lastTime;

    public LiveListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        activity = ac;
//        int index = i;
        list = l;
        holder = (AppsDetailNewsAdapter.LiveHolder) h;
        lastTime = System.currentTimeMillis();

    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != list && null != list.getNews() && !list.getNews().isEmpty()) {
            holder.groupTitleTv.setText(list.getType_name());
            final NewsEntity.DataBean.ItemNewsEntity itemNews = list.getNews().get(0);
            holder.titleTv.setText(itemNews.getTitle());
            AppUtil.loadNewsImg(itemNews.getPic_path(), holder.coverIv);
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    IntentUtil.startActivity2DetailWithEntity2(holder.itemView.getContext(), itemNews);
                }
                lastTime = currentTime;
            });
            holder.btnMore.setVisibility(View.GONE);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }
}