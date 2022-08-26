package cn.cc1w.app.ui.ui.xg_push.view;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.push.PushNewsGroupAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.PushNewsGroupEntity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 视频的View [推送]
 * @author kpinfo
 */
public class PushVideoView {
    private final Context context;
    private final PushNewsGroupEntity.ItemPushNewsGroupEntity entity;
    private final PushNewsGroupAdapter.VideoHolder holder;

    public PushVideoView(Context context, RecyclerView.ViewHolder holder, PushNewsGroupEntity.ItemPushNewsGroupEntity entity) {
        this.context = context;
        this.entity = entity;
        this.holder = (PushNewsGroupAdapter.VideoHolder) holder;
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            PushNewsGroupEntity.ItemPushNewsGroupEntity.NewsBean item = entity.getNews().get(0);
            holder.titleTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            holder.timeTv.setText(TextUtils.isEmpty(item.getCreate_time()) ? "" : item.getCreate_time());
            holder.smallTitleTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            holder.sourceTv.setText(TextUtils.isEmpty(item.getApp_name()) ? "" : item.getApp_name());
            holder.describeTv.setText(TextUtils.isEmpty(item.getSummary()) ? "" : item.getSummary());
            AppUtil.loadBannerImg(item.getPic_path(), holder.postImg);

            holder.container.setOnClickListener(v -> {
                String newsId = entity.getNews().get(0).getNews_id();
                if (!TextUtils.isEmpty(newsId)) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, VideoDetailActivity.class);
                    intent.putExtra(Constant.TAG_ID, newsId);
                    context.startActivity(intent);
                }
            });
        }
    }

}