package cn.cc1w.app.ui.ui.detail.live;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import cn.cc1w.app.ui.adapter.live.HostRoomMessageAdapter;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 主持人 文本
 * @author kpinfo
 */
public class HostTxtView {
    private final LiveHostEntity.LiveHostItemEntity entity;
    private final HostRoomMessageAdapter.HostTxtHolder holder;

    public HostTxtView(Context context, int position, RecyclerView.ViewHolder holder, LiveHostEntity.LiveHostItemEntity entity) {
//        Context context1 = context;
//        int index = position;
        this.entity = entity;
        this.holder = (HostRoomMessageAdapter.HostTxtHolder) holder;
    }
    public void initView() {
        if (null != entity) {
            holder.timeTv.setText(TextUtils.isEmpty(entity.getCreate_time()) ? "" : entity.getCreate_time());
            holder.userNameTv.setText(TextUtils.isEmpty(entity.getAnchor_name()) ? "" : entity.getAnchor_name());
            AppUtil.loadAvatarImg(entity.getAnchor_headpic(), holder.avatarImg);
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
        }
    }
}