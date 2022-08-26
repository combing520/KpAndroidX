package cn.cc1w.app.ui.ui.home.home.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.rxjava.rxlife.RxLife;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AutoVerticalViewDataData;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.detail.ScrollNewsDetailActivity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 首页滚动新闻
 * @author kpinfo
 */
public class HomeScrollNewsListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.ScrollNewsHolder holder;
    private long lastTime;
    private final LifecycleOwner owner;

    public HomeScrollNewsListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity,LifecycleOwner owner) {
        activity = ac;
        this.entity = entity;
        this.owner = owner;
        holder = (HomeNewsAdapter.ScrollNewsHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity) {
            if (null != entity.getNews() && !entity.getNews().isEmpty()) {
                List<AutoVerticalViewDataData> data = new ArrayList<>();
                bindData2View(data, entity.getNews());
            }
            else {
                if (NetUtil.isNetworkConnected(activity) && null != holder && (null != holder.verticalViewView)) {
                    RxHttp.postJson(Constant.EXCLUSIVE_NEWS_NEW)
                            .asResponseList(HomeNewsEntity.ItemHomeNewsEntity.class)
                            .as(RxLife.asOnMain(owner))
                            .subscribe(dataSet -> {
                                if(dataSet != null && !dataSet.isEmpty()){
                                    List<AutoVerticalViewDataData> data = new ArrayList<>();
                                    for (HomeNewsEntity.ItemHomeNewsEntity item : dataSet) {
                                        //  将数据保存到数据库中
                                        data.add(new AutoVerticalViewDataData("", item.getNews().get(0).getTitle(), ""));
                                    }
                                    if (holder.verticalViewView != null) {
                                        holder.verticalViewView.setViews(data);
                                    }
                                    if (holder.scrollNewsContainer != null && holder.scrollNewsContainer.getVisibility() == View.GONE) {
                                        holder.scrollNewsContainer.setVisibility(View.VISIBLE);
                                    }
                                    if (holder.verticalViewView != null) {
                                        holder.verticalViewView.setVisibility(View.VISIBLE);
                                    }
                                    if (holder.imageView != null) {
                                        holder.imageView.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    if (holder.scrollNewsContainer != null) {
                                        holder.scrollNewsContainer.setVisibility(View.GONE);
                                    }
                                    if (holder.verticalViewView != null) {
                                        holder.verticalViewView.setVisibility(View.GONE);
                                    }
                                    if (holder.imageView != null) {
                                        holder.imageView.setVisibility(View.GONE);
                                    }
                                    if (holder.scrollNewsContainer != null) {
                                        holder.scrollNewsContainer.setPadding(0, 0, 0, 0);
                                    }
                                }
                            }, (OnError) error -> {
                            });
                }
            }
           if(holder != null){
               holder.itemView.setOnClickListener(v -> {
                   long currentTime = System.currentTimeMillis();
                   if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                       Intent intent = new Intent();
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       intent.setClass(activity, ScrollNewsDetailActivity.class);
                       activity.startActivity(intent);
                   }
                   lastTime = currentTime;
               });
               holder.imageView.setOnClickListener(v -> {
                   long currentTime = System.currentTimeMillis();
                   if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                       Intent intent = new Intent();
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       intent.setClass(activity, ScrollNewsDetailActivity.class);
                       activity.startActivity(intent);
                   }
                   lastTime = currentTime;
               });
           }
        } else {
            holder.scrollNewsContainer.setVisibility(View.GONE);
            holder.verticalViewView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.scrollNewsContainer.setPadding(0, 0, 0, 0);
        }
    }

    private void bindData2View(List<AutoVerticalViewDataData> data, List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> entity) {
        for (HomeNewsEntity.ItemHomeNewsEntity.NewsBean item : entity) {
            data.add(new AutoVerticalViewDataData("", item.getTitle(), ""));
        }
        holder.verticalViewView.setViews(data);
    }
}