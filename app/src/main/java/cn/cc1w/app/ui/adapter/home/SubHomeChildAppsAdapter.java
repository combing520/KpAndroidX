package cn.cc1w.app.ui.adapter.home;

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

import cn.cc1w.app.ui.R;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;

/**
 * 首页中的 应用号的adapter
 *
 * @author kpinfo
 */
public class SubHomeChildAppsAdapter extends RecyclerView.Adapter<SubHomeChildAppsAdapter.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean.ItemNewsBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setData(List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean.ItemNewsBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     */
    public HomeNewsEntity.ItemHomeNewsEntity.NewsBean.ItemNewsBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_apps_child_home_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final HomeNewsEntity.ItemHomeNewsEntity.NewsBean.ItemNewsBean entity = getItem(position);
        holder.titleTv.setText(entity.getNews_title());
        holder.describeTv.setText(entity.getNews_summary());
        holder.container.setOnClickListener(rippleView -> {
            Context context = AppContext.getAppContext();
            String newsId = entity.getNews_id();
            if (null != context && !TextUtils.isEmpty(newsId)) {
                String type = dataSet.get(position).getIn_type();
                if (!TextUtils.isEmpty(newsId) && !TextUtils.isEmpty(type)) {
                    Intent intent = new Intent();
                    if (Constant.TYPE_NEWS_NORMAL.equals(type)) { // 普通新闻
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, NewsDetailNewActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_VIDEO_NORMAL.equals(type)) { // 视频
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_VIDEO_GROUP.equals(type)) { // 视频组
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoGroupDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_PHOTO.equals(type) || Constant.TYPE_PHOTO_GROUP.equals(type)) { // 图集组
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, AlbumDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_LIVE.equals(type)) { // 直播
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, LiveDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_URL.equals(type)) { // 外链
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, UrlDetailActivity.class);
                        intent.putExtra(Constant.TAG_URL, dataSet.get(position).getNews_url());
                        intent.putExtra(Constant.TAG_TITLE, dataSet.get(position).getNews_title());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(dataSet.get(position).getNews_id()) ? "" : dataSet.get(position).getNews_id());
                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(dataSet.get(position).getNews_summary()) ? "" : dataSet.get(position).getNews_summary());
                        context.startActivity(intent);
                    } else if (Constant.TYPE_TOPIC.equals(type)) { // 专题
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout container;
        private final TextView titleTv;
        private final TextView describeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.ll_item_recommend_child_apps_home);
            titleTv = itemView.findViewById(R.id.txt_item_title_recommend_child_apps_home);
            describeTv = itemView.findViewById(R.id.txt_item_describe_recommend_child_apps_home);
        }
    }
}