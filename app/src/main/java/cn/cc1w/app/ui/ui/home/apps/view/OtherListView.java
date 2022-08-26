package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 活动 View [详情中的]
 *
 * @author kpinfo
 */
public class OtherListView {
    private final Context context;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.OtherHolder holder;
    private long lastTime;

    public OtherListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        this.context = ac;
//        int index = i;
        entity = l;
        holder = (AppsDetailNewsAdapter.OtherHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            // 设置数据
            final NewsEntity.DataBean.ItemNewsEntity itemNews = entity.getNews().get(0);
            if (null != itemNews) {
                holder.newsTitleTv.setText(itemNews.getTitle());
                holder.createTimeTv.setText(itemNews.getCreate_time());
                holder.newsAppsNameTv.setText(itemNews.getApp_name());
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