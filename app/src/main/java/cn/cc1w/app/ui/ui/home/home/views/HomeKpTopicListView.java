package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.home.MoreKpTopicActivity;

/**
 * @author kpinfo
 * 首页开屏置顶
 */
public class HomeKpTopicListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.KpTopicHolder holder;
    private long lastTime;

    public HomeKpTopicListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.KpTopicHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.textView.setTypeface(null, Typeface.BOLD_ITALIC);
            List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> items = new ArrayList<>(entity.getNews());
            holder.verticalScrollLayout.setViews(items);
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    Intent intent = new Intent();
                    intent.setClass(context, MoreKpTopicActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.TAG_ID, entity.getNews().get(0).getId());
                    context.startActivity(intent);
                }
                lastTime = currentTime;
            });
        }
    }
}