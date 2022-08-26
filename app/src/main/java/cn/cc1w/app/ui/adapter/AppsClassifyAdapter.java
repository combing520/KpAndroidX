package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.home.apps.AppDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 应用分类 Adapter
 *
 * @author kpinfo
 */
public class AppsClassifyAdapter extends RecyclerView.Adapter<AppsClassifyAdapter.ViewHolder> {
    private final List<AppsListEntity.ItemAppsListEntity> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * 设置数据源
     *
     * @param list 数据源
     */
    public void setData(List<AppsListEntity.ItemAppsListEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 清空数据
     */
    public void clearData() {
        dataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取当前的 list 的数据
     */
    public List<AppsListEntity.ItemAppsListEntity> getData() {
        return dataSet;
    }

    /**
     * 设置 添加应用号和打开应用号的监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public AppsListEntity.ItemAppsListEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classify_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AppsListEntity.ItemAppsListEntity item = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        holder.describeTv.setText(TextUtils.isEmpty(item.getSummary()) ? "" : item.getSummary());
        AppUtil.loadNewsGroupImg(item.getLogo_pic_path(), holder.icon);
        if (TextUtils.isEmpty(item.getNews_id())) {
            holder.recommendLayout.setVisibility(View.GONE);
        } else {
            holder.recommendLayout.setVisibility(View.VISIBLE);
            holder.recommendNewsTitleTv.setText(TextUtils.isEmpty(item.getNews_title()) ? "" : item.getNews_title());
            holder.recommendNewsDescribeTv.setText(TextUtils.isEmpty(item.getNews_summary()) ? "" : item.getNews_summary());
        }
        if (item.isAttention()) {
            AppUtil.loadRes(R.mipmap.ic_choose, holder.addImg);
        } else {
            AppUtil.loadRes(R.mipmap.ic_add, holder.addImg);
        }
        holder.icon.setOnClickListener(v -> {
            if (holder.recommendLayout.getVisibility() == View.VISIBLE) {
                holder.recommendLayout.setVisibility(View.GONE);
            } else {
                holder.recommendLayout.setVisibility(View.VISIBLE);
            }
        });
        holder.addImg.setOnClickListener(v -> {
            if (null != listener) {
                listener.onItemClick(v, position);
            }
        });
        holder.topContainer.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setClass(context, AppDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.TAG_ID, item.getId());
                intent.putExtra(Constant.TAG_TITLE, item.getName());
                intent.putExtra("group_id", item.getGroup_id());
                context.startActivity(intent);
            }
        });
        holder.recommendLayout.setOnClickListener(rippleView -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                String type = item.getIn_type();
                String newsId = item.getNews_id();
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
                        intent.putExtra(Constant.TAG_URL, dataSet.get(position).getNews_url());
                        intent.putExtra(Constant.TAG_TITLE, dataSet.get(position).getNews_title());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(dataSet.get(position).getId()) ? "" : dataSet.get(position).getId());
                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(dataSet.get(position).getSummary()) ? "" : dataSet.get(position).getSummary());
                        context.startActivity(intent);
                    } else if (Constant.TYPE_TOPIC.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    }
                }
            }
        });
    }

    /**
     * 返回数据集合
     *
     * @return 数据集合
     */
    public List<AppsListEntity.ItemAppsListEntity> getDataSet() {
        return dataSet;
    }

    /**
     * 关注或者打开 对应的位置的应用号 （如果未曾关注则关注，如果关注就打开）
     *
     * @param pos 对应的 位置
     */
    public void addApps(int pos) {
        AppsListEntity.ItemAppsListEntity item = getItem(pos);
        if (!item.isAttention()) {
            item.setAttention(true);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消关注
     *
     * @param pos 对应的 位置
     */
    public void cancleApps(int pos) {
        AppsListEntity.ItemAppsListEntity item = getItem(pos);
        if (item.isAttention()) {
            item.setAttention(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final TextView describeTv;
        private final ImageView addImg;
        private final RoundAngleImageView icon;
        private final TextView recommendNewsTitleTv;
        private final TextView recommendNewsDescribeTv;
        private final RelativeLayout recommendLayout;
        private final RelativeLayout topContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_title_item_app_list);
            describeTv = itemView.findViewById(R.id.txt_describe_item_app_list);
            addImg = itemView.findViewById(R.id.img_btn_add_item_app_list);
            icon = itemView.findViewById(R.id.img_item_app_list);
            recommendNewsTitleTv = itemView.findViewById(R.id.txt_title_recommend_item_app_list);
            recommendNewsDescribeTv = itemView.findViewById(R.id.txt_describe_recommend_item_app_list);
            recommendLayout = itemView.findViewById(R.id.ll_recommend_item_app_list);
            topContainer = itemView.findViewById(R.id.ll_top_item_app);
        }
    }
}