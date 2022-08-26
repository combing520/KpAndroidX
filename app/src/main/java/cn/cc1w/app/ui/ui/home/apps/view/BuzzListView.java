package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 *
 * @author kpinfo
 * 首页小视频
 */
public class BuzzListView {
    private final Context context;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.BuzzHolder holder;
    private long lastTime;

    public BuzzListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean entity) {
        context = ac;
        this.entity = entity;
        holder = (AppsDetailNewsAdapter.BuzzHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            NewsEntity.DataBean.ItemNewsEntity itemNews = entity.getNews().get(0);
            holder.titleTv.setText(itemNews.getTitle());
            holder.sumaryTv.setText(itemNews.getSummary());
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    IntentUtil.startActivity2DetailWithEntity2(context, itemNews);
                }
                lastTime = currentTime;
            });

            holder.moreBtn.setVisibility(View.GONE);
        }
    }
}