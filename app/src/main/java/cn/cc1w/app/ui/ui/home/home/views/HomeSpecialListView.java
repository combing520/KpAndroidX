package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.home.MoreSpecialListActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 首页普通新闻
 *
 * @author kpinfo
 */
public class HomeSpecialListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.SpecialHolder holder;
    private long lastTime;

    public HomeSpecialListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.SpecialHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            HomeNewsEntity.ItemHomeNewsEntity.NewsBean itemNews = entity.getNews().get(0);
            if (null != itemNews) {
                holder.titleTv.setText(itemNews.getTitle());
                AppUtil.loadNewsImg(itemNews.getPic_path(), holder.coverIv);
                holder.coverIv.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        IntentUtil.startActivity2DetailWithEntity(holder.coverIv.getContext(), itemNews);
                    }
                    lastTime = currentTime;
                });
                holder.titleTv.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        IntentUtil.startActivity2DetailWithEntity(holder.titleTv.getContext(), itemNews);
                    }
                    lastTime = currentTime;
                });
                holder.moreSpecialLayout.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        String id = itemNews.getId();
                        if (!TextUtils.isEmpty(id)) {
                            Intent intent = new Intent();
                            intent.setClass(activity, MoreSpecialListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constant.TAG_ID, id);
                            activity.startActivity(intent);
                        }
                    }
                    lastTime = currentTime;
                });
            }
        }
    }
}