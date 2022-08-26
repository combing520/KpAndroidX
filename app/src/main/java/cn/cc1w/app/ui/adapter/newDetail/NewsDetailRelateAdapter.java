package cn.cc1w.app.ui.adapter.newDetail;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemNewsDetailRelateEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 新闻详情中 的相关新闻
 *
 * @author kpinfo
 */
public class NewsDetailRelateAdapter extends RecyclerView.Adapter<NewsDetailRelateAdapter.ViewHolder> {
    private final List<ItemNewsDetailRelateEntity> dataSet = new ArrayList<>();
    private long lastTime;
    private final Context context;

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public NewsDetailRelateAdapter(Context context) {
        lastTime = System.currentTimeMillis();
        this.context = context;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemNewsDetailRelateEntity> list) {
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
    public void addData(List<ItemNewsDetailRelateEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public ItemNewsDetailRelateEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_recommend_and_relate_detail_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        ItemNewsDetailRelateEntity entity = getItem(position);
        if (null != entity) {
            holder.newsTitleTv.setText(entity.getTitle());
            holder.createTimeTv.setText(entity.getCreate_time());
            holder.newsAppsNameTv.setText(entity.getApp_name());
            AppUtil.loadNewsImg(entity.getPic_path(), holder.newsPicImg);
            holder.itemView.setOnClickListener(rippleView -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String newsId = entity.getNews_id();
                    if (!TextUtils.isEmpty(newsId)) {
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
                            } else if (Constant.TYPE_NEWS_NORMAL_GROUP.equals(type)) {
                                intent.setClass(context, SpecialDetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Constant.TAG_ID, newsId);
                                context.startActivity(intent);
                            }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView newsTitleTv;
        private final TextView newsAppsNameTv;
        private final TextView createTimeTv;
        private final RoundAngleImageView newsPicImg;

        public ViewHolder(View itemView) {
            super(itemView);
            newsTitleTv = itemView.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = itemView.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = itemView.findViewById(R.id.img_item_time_news_normal_home);
            newsPicImg = itemView.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }
}
