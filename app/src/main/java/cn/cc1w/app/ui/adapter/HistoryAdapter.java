package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HistoryEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 用户 浏览记录 Adapter
 * @author kpinfo
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<HistoryEntity.ItemHistoryEntity> dataSet = new ArrayList<>();
    private long lastTime;
    private final Context context;

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public HistoryAdapter(Context context) {
        this.context = context;
    }

    /**
     * 射者数据
     *
     * @param list 数据源
     */
    public void setData(List<HistoryEntity.ItemHistoryEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param list 数据源
     */
    public void addData(List<HistoryEntity.ItemHistoryEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取列表的 size
     *
     * @return 列表 size
     */
    public int getListSize() {
        return dataSet.size();
    }

    /**
     * 获取对应位置的 条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public HistoryEntity.ItemHistoryEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_focus_news_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        final HistoryEntity.ItemHistoryEntity item = getItem(position);
        if (null != item) {
            holder.titleTv.setText(item.getTitle());
            AppUtil.loadMenuImg(item.getApp_logo_pic_path(), holder.thumbnailImg);
            AppUtil.loadNewsGroupImg(item.getPic_path(), holder.newsImg);
            holder.sourceTv.setText(item.getApp_name());
            holder.timeTv.setText(item.getCreate_time());
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String newsId = item.getNews_id();
                    String newsType = item.getIn_type();
                    if (!TextUtils.isEmpty(newsId) && !TextUtils.isEmpty(newsType)) {
                        Intent intent = new Intent();
                        if (Constant.TYPE_NEWS_NORMAL.equals(newsType)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, NewsDetailNewActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_VIDEO_NORMAL.equals(newsType)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, VideoDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_VIDEO_GROUP.equals(newsType)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, VideoGroupDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_PHOTO.equals(newsType) || Constant.TYPE_PHOTO_GROUP.equals(newsType)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, AlbumDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_LIVE.equals(newsType)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, LiveDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_TOPIC.equals(newsType)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, SpecialDetailActivity.class);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_NEWS_NORMAL_GROUP.equals(newsType)) {
                            intent.setClass(context, SpecialDetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            context.startActivity(intent);
                        }
                    }
                }
                lastTime = currentTime;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * 删除指定位置的条目
     *
     * @param pos 指定的位置
     */
    public void deleteHistoryItem(int pos) {
        dataSet.remove(pos);
        notifyDataSetChanged();
    }

    public void clearHistory() {
        dataSet.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final ImageView thumbnailImg;
        private final TextView sourceTv;
        private final TextView timeTv;
        private final ImageView newsImg;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_news_title_focus);
            thumbnailImg = itemView.findViewById(R.id.img_item_news_source_focus);
            sourceTv = itemView.findViewById(R.id.txt_item_news_source_focus);
            timeTv = itemView.findViewById(R.id.txt_item_news_time_focus);
            newsImg = itemView.findViewById(R.id.img_item_news_focus);
        }
    }
}