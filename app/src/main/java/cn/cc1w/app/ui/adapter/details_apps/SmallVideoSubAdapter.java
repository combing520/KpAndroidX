package cn.cc1w.app.ui.adapter.details_apps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;

/**
 * @author kpinfo
 * 小视频--更多
 */
public class SmallVideoSubAdapter extends RecyclerView.Adapter<SmallVideoSubAdapter.ViewHolder> {
    private final List<NewsEntity.DataBean> dataSet = new ArrayList<>();
    private final Context context;
    private long lastTime;

    public SmallVideoSubAdapter(Context context) {
        this.context = context;
        lastTime = System.currentTimeMillis();
    }

    public void setData(List<NewsEntity.DataBean> list) {
        if (list != null && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addData(List<NewsEntity.DataBean> list) {
        if (list != null && !list.isEmpty()) {
            int startPos = dataSet.size();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    private NewsEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_video_details_apps_recycle2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int pos) {
        NewsEntity.DataBean newsBean = getItem(pos);
        if (newsBean != null && newsBean.getNews() != null && !newsBean.getNews().isEmpty()) {
            final NewsEntity.DataBean.ItemNewsEntity item = newsBean.getNews().get(0);
            holder.shareCntTv.setText(TextUtils.isEmpty(item.getComment_num()) ? "" : item.getComment_num());
            holder.nameTv.setText(TextUtils.isEmpty(item.getApp_name()) ? "" : item.getApp_name());
            holder.describeTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            holder.watchTv.setText(TextUtils.isEmpty(item.getClick_num()) ? "" : item.getClick_num());
            holder.likeTv.setText(TextUtils.isEmpty(item.getGood_num()) ? "" : item.getGood_num());
            AppUtil.loadNewsImg(item.getPic_path(), holder.postImg);
            holder.shareImg.setOnClickListener(v -> {
                Gson gson = new Gson();
                Bundle bundle = new Bundle();
                ShareEntity shareEntity = new ShareEntity();
                shareEntity.setNewsId(TextUtils.isEmpty(item.getNews_id()) ? "" : item.getNews_id());
                shareEntity.setRedirect_url("");
                shareEntity.setSummary(TextUtils.isEmpty(item.getSummary()) ? Constant.SUMMARY_SHARE : item.getSummary());
                shareEntity.setTitle(TextUtils.isEmpty(item.getTitle()) ? Constant.TILE_SHARE : item.getTitle());
                shareEntity.setUrl(TextUtils.isEmpty(item.getUrl()) ? "" : item.getUrl());
                shareEntity.setType(Constant.TYPE_SHARE_NEWS);
                String shareContent = gson.toJson(shareEntity);
                bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                IntentUtil.startActivity(context, ShareActivity.class, bundle);
            });
            holder.container.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String id = item.getNews_id();
                    if (!TextUtils.isEmpty(id)) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, id);
                        context.startActivity(intent);
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
        private final TextView nameTv;
        private final TextView shareCntTv;
        private final TextView describeTv;
        private final RoundAngleImageView postImg;
        private final ConstraintLayout container;
        private final ImageView shareImg;
        private final TextView watchTv;
        private final TextView likeTv;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.txt_item_name_video_home);
            shareCntTv = itemView.findViewById(R.id.txt_item_cnt_video_home);
            describeTv = itemView.findViewById(R.id.txt_item_describe_video_home);
            postImg = itemView.findViewById(R.id.img_item_video_home);
            container = itemView.findViewById(R.id.container_item_active_news);
            shareImg = itemView.findViewById(R.id.img_share_video_home);
            watchTv = itemView.findViewById(R.id.txt_item_watch_video_home);
            likeTv = itemView.findViewById(R.id.txt_item_like_video_home);
        }
    }
}