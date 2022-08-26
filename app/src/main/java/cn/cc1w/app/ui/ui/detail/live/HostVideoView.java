package cn.cc1w.app.ui.ui.detail.live;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import cn.cc1w.app.ui.adapter.live.HostRoomMessageAdapter;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;
import cn.jzvd.JzvdStd;

/**
 * 主持人--视频 消息类型
 *
 * @author kpinfo
 */
public class HostVideoView {
    private final LiveHostEntity.LiveHostItemEntity entity;
    private final HostRoomMessageAdapter.HostVideoHolder holder;

    public HostVideoView(Context context, int position, RecyclerView.ViewHolder holder, LiveHostEntity.LiveHostItemEntity entity) {
//        Context context1 = context;
//        int index = position;
        this.entity = entity;
        this.holder = (HostRoomMessageAdapter.HostVideoHolder) holder;
    }

    public void initView() {
        if (null != entity) {
            holder.timeTv.setText(TextUtils.isEmpty(entity.getCreate_time()) ? "" : entity.getCreate_time());
            holder.userNameTv.setText(TextUtils.isEmpty(entity.getAnchor_name()) ? "" : entity.getAnchor_name());
            if (TextUtils.isEmpty(entity.getTitle())) {
                holder.titleTv.setVisibility(View.GONE);
            } else {
                holder.titleTv.setVisibility(View.VISIBLE);
                holder.titleTv.setText(entity.getTitle());
            }
            if (TextUtils.isEmpty(entity.getContent())) {
                holder.describeTv.setVisibility(View.GONE);
            } else {
                holder.describeTv.setText(entity.getContent());
            }
            AppUtil.loadAvatarImg(entity.getAnchor_headpic(), holder.avatarImg);
            List<LiveHostEntity.LiveHostItemEntity.JsonBean> list = entity.getJson();
            if (null != list && !list.isEmpty()) {
                AppUtil.loadBannerImg(list.get(0).getPic_path(), holder.videoPlayer.posterImageView);
                holder.videoPlayer.setUp(list.get(0).getUrl(), "", JzvdStd.SCREEN_NORMAL);
                holder.videoPlayer.widthRatio = 16;
                holder.videoPlayer.heightRatio = 9;
            }
        }
    }
}