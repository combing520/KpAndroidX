package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;


/**
 * 首页普通新闻
 * @author kpinfo
 */
public class HomeOtherListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.OtherHolder holder;
    private long lastTime;

    public HomeOtherListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        this.context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.OtherHolder) h;
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            final HomeNewsEntity.ItemHomeNewsEntity.NewsBean itemNews = entity.getNews().get(0);
            if (null != itemNews) {
                holder.newsTitleTv.setText(itemNews.getTitle());
                holder.createTimeTv.setText(itemNews.getCreate_time());
                holder.newsAppsNameTv.setText(itemNews.getApp_name());
                AppUtil.loadNewsImg(itemNews.getPic_path(), holder.newsPicImg);
                holder.itemView.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        IntentUtil.startActivity2DetailWithEntity(context, itemNews);
                    }
                    lastTime = currentTime;
                });
            }
        }
    }
}