package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.home.HotNewsActivity;

/**
 *
 * @author kpinfo
 * 首页普通新闻
 */
public class HomeAppsLoadMoreListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.AppsLoadMoreHolder holder;
    private long lastTime;

    public HomeAppsLoadMoreListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
//        int index = i;
        this.entity = entity;
        holder = (HomeNewsAdapter.AppsLoadMoreHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.loadMoreLayout.setVisibility(View.VISIBLE);
            final HomeNewsEntity.ItemHomeNewsEntity.NewsBean item = entity.getNews().get(0);
            if (null != item) {
                holder.titleTv.setText(entity.getNews().get(0).getTitle());
                final String showType = entity.getShow_type();
                if (!TextUtils.isEmpty(showType)) {
                    holder.loadMoreLayout.setOnClickListener(rippleView -> {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                            if (TextUtils.equals("MoreHotNews", showType)) {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(activity, HotNewsActivity.class);
                                intent.putExtra(Constant.TAG_ID, item.getId());
                                intent.putExtra("module", item.getModule());
                                activity.startActivity(intent);
                            } else if (TextUtils.equals("MoreChannel", showType)) {
                                EventBus.getDefault().post(new EventMessage("changeBottomTab", ""));
                            }
                        }
                        lastTime = currentTime;
                    });
                }
            }
        } else {
            holder.loadMoreLayout.setVisibility(View.GONE);
        }
    }
}