package cn.cc1w.app.ui.ui.detail.live;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.adapter.live.HostRoomMessageAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.detail.ShowWebViewGalleryDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 主持人单图类型消息
 * @author kpinfo
 */
public class HostSingleView {
    private final Context context;
    private final LiveHostEntity.LiveHostItemEntity entity;
    private final HostRoomMessageAdapter.HostSingleImgHolder holder;
    private long lastTime;

    public HostSingleView(Context context, int position, RecyclerView.ViewHolder holder, LiveHostEntity.LiveHostItemEntity entity) {
        this.context = context;
//        int index = position;
        this.entity = entity;
        this.holder = (HostRoomMessageAdapter.HostSingleImgHolder) holder;
        lastTime = System.currentTimeMillis();
    }

    public void initView() {
        if (null != entity) {
            holder.timeTv.setText(TextUtils.isEmpty(entity.getCreate_time()) ? "" : entity.getCreate_time());
            if (TextUtils.isEmpty(entity.getTitle())) {
                holder.titleTv.setVisibility(View.GONE);
            } else {
                holder.titleTv.setVisibility(View.VISIBLE);
                holder.titleTv.setText(entity.getTitle());
            }
            holder.userNameTv.setText(TextUtils.isEmpty(entity.getAnchor_name()) ? "" : entity.getAnchor_name());
            if (TextUtils.isEmpty(entity.getContent())) {
                holder.describeTv.setVisibility(View.GONE);
            } else {
                holder.describeTv.setText(entity.getContent());
            }
            AppUtil.loadAvatarImg(entity.getAnchor_headpic(), holder.avatarImg);
            List<LiveHostEntity.LiveHostItemEntity.JsonBean> list = entity.getJson();
            if (null != list && !list.isEmpty()) {
                AppUtil.loadBannerImg(list.get(0).getPic_path(), holder.postImg);
                holder.postImg.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        ArrayList<String> picList = new ArrayList<>();
                        if (null != list.get(0)) {
                            picList.add(list.get(0).getPic_path());
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, ShowWebViewGalleryDetailActivity.class);
                            intent.putExtra("picList", picList);
                            intent.putExtra("selectPos", 0);
                            context.startActivity(intent);
                        }
                    }
                    lastTime = currentTime;
                });
            }
        }
    }
}