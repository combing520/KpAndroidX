package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 *
 * @author kpinfo
 * @date 2018/10/23
 * 首页小视频
 */
public class HomeObsListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.ObsHolder holder;
    private long lastTime;

    public HomeObsListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.ObsHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            HomeNewsEntity.ItemHomeNewsEntity.NewsBean itemNews = entity.getNews().get(0);
            holder.titleTv.setText(itemNews.getTitle());
            AppUtil.loadNewsImg(itemNews.getPic_path(), holder.coverIv);
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String newsId = itemNews.getNews_id();
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, SpecialDetailActivity.class);
                    intent.putExtra(Constant.TAG_ID, newsId);
                    context.startActivity(intent);
                }
                lastTime = currentTime;
            });
        }
    }
}