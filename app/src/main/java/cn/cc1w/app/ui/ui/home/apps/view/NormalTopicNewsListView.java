package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 普通新闻 View
 * @author kpinfo
 */
public class NormalTopicNewsListView {
    private final Context context;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.NormalTopicHolder holder;
    private long lastTime;

    public NormalTopicNewsListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean entity) {
        this.context = ac;
//        int index = i;
        this.entity = entity;
        holder = (AppsDetailNewsAdapter.NormalTopicHolder) h;
        lastTime = System.currentTimeMillis();
    }
    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != holder) {
            final NewsEntity.DataBean.ItemNewsEntity itemNews = entity.getNews().get(0);
            if (null != itemNews) {
                holder.newsTitleTv.setText(itemNews.getTitle());
                AppUtil.loadNewsImg(itemNews.getPic_path(), holder.newsPicImg);
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