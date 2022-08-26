package cn.cc1w.app.ui.ui.xg_push.view;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.push.PushNewsGroupAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.PushNewsGroupEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 *
 * @author kpinfo
 * @date 2018/11/16
 * <p>
 * 图集View  推送
 */
public class PushAlbumView {
    private final Context context;
    private final PushNewsGroupEntity.ItemPushNewsGroupEntity entity;
    private final PushNewsGroupAdapter.AlbumHolder holder;

    public PushAlbumView(Context context, RecyclerView.ViewHolder holder, PushNewsGroupEntity.ItemPushNewsGroupEntity entity) {
        this.context = context;
        this.entity = entity;
        this.holder = (PushNewsGroupAdapter.AlbumHolder) holder;
    }

    /**
     * 初始化
     */
    public void initView() {

        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            final PushNewsGroupEntity.ItemPushNewsGroupEntity.NewsBean item = entity.getNews().get(0);
            holder.titleTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            holder.timeTv.setText(TextUtils.isEmpty(item.getCreate_time()) ? "" : item.getCreate_time());
            holder.smallTitleTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            holder.sourceTv.setText(TextUtils.isEmpty(item.getApp_name()) ? "" : item.getApp_name());
            holder.describeTv.setText(TextUtils.isEmpty(item.getSummary()) ? "" : item.getSummary());
            AppUtil.loadBannerImg(item.getPic_path(), holder.postImg);

            holder.container.setOnClickListener(v -> {
                String type = item.getIn_type();
                String newsId = item.getNews_id();
                if (!TextUtils.isEmpty(newsId) && !TextUtils.isEmpty(type)) {
                    Intent intent = new Intent();
                    // 图集
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
                        intent.putExtra(Constant.TAG_URL, item.getUrl());
                        intent.putExtra(Constant.TAG_TITLE, item.getTitle());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(item.getSummary()) ? "" : item.getSummary());
                        context.startActivity(intent);
                    } else if (Constant.TYPE_TOPIC.equals(type)) { // 专题
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}