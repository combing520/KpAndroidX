package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import com.google.gson.Gson;
import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 视频 View
 * @author kpinfo
 */
public class VideoListView {
    private final Context activity;
    private final NewsEntity.DataBean list;
    private final AppsDetailNewsAdapter.VideoHolder holder;
    private long lastTime;

    public VideoListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        activity = ac;
//        int index = i;
        list = l;
        holder = (AppsDetailNewsAdapter.VideoHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != list && null != list.getNews() && !list.getNews().isEmpty()) {
            final NewsEntity.DataBean.ItemNewsEntity item = list.getNews().get(0);
            holder.shareCntTv.setText(TextUtils.isEmpty(item.getComment_num()) ? "" : item.getComment_num());
            holder.nameTv.setText(TextUtils.isEmpty(item.getApp_name()) ? "" : item.getApp_name());
            holder.describeTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            holder.watchTv.setText(TextUtils.isEmpty(item.getClick_num()) ? "" : item.getClick_num());
            holder.likeTv.setText(TextUtils.isEmpty(item.getGood_num()) ? "" : item.getGood_num());
            AppUtil.loadBigImg(item.getPic_path(), holder.postImg);
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
                IntentUtil.startActivity(activity, ShareActivity.class, bundle);

            });
            holder.container.setOnClickListener(rippleView -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String id = list.getNews().get(0).getNews_id();
                    if (!TextUtils.isEmpty(id)) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(activity, VideoDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, id);
                        activity.startActivity(intent);
                    }
                }
                lastTime = currentTime;
            });
        }
    }
}