package cn.cc1w.app.ui.adapter.details_apps;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 新闻组 sub Adapter
 *
 * @author kpinfo
 */
public class NewsGroupSubAdapter extends RecyclerView.Adapter<NewsGroupSubAdapter.ViewHolder> {
    private final List<NewsEntity.DataBean.ItemNewsEntity> dataSet = new ArrayList<>();
    private long lastTime;

    public NewsGroupSubAdapter() {
        lastTime = System.currentTimeMillis();
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<NewsEntity.DataBean.ItemNewsEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应位置
     * @return 对应条目
     */
    public NewsEntity.DataBean.ItemNewsEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_group_sub_details_apps_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsEntity.DataBean.ItemNewsEntity entity = getItem(position);
        AppUtil.loadAlbumListImg(entity.getPic_path(), holder.img);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getTitle()) ? "" : entity.getTitle());
        holder.sourceTv.setText(TextUtils.isEmpty(entity.getApp_name()) ? "" : entity.getApp_name());
        holder.timeTv.setText(TextUtils.isEmpty(entity.getCreate_time()) ? "" : entity.getCreate_time());
        holder.container.setOnClickListener(rippleView -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                Context context = AppContext.getAppContext();
                if (null != context) {
                    String newsId = entity.getNews_id();
                    String type = entity.getIn_type();
                    if (!TextUtils.isEmpty(newsId) && !TextUtils.isEmpty(type)) {
                        Intent intent = new Intent();
                        if (Constant.TYPE_NEWS_NORMAL.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, NewsDetailNewActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_VIDEO_NORMAL.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, VideoDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_VIDEO_GROUP.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, VideoGroupDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_PHOTO.equals(type) || Constant.TYPE_PHOTO_GROUP.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, AlbumDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_LIVE.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, LiveDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_URL.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, UrlDetailActivity.class);
                            intent.putExtra(Constant.TAG_URL, entity.getUrl());
                            intent.putExtra(Constant.TAG_TITLE, entity.getTitle());
                            intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(entity.getId()) ? "" : entity.getId());
                            intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(entity.getSummary()) ? "" : entity.getSummary());
                            context.startActivity(intent);
                        } else if (Constant.TYPE_TOPIC.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, SpecialDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        }
                    }
                }
            }
            lastTime = currentTime;
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView img;
        private final TextView titleTv;
        private final TextView sourceTv;
        private final TextView timeTv;
        private final RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_news_group_sub_detail_apps);
            titleTv = itemView.findViewById(R.id.txt_item_news_group_title_sub_detail_apps);
            sourceTv = itemView.findViewById(R.id.txt_item_news_group_source_sub_detail_apps);
            timeTv = itemView.findViewById(R.id.txt_item_news_group_time_sub_detail_apps);
            container = itemView.findViewById(R.id.ripple_item_news_group);
        }
    }
}
