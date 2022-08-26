package cn.cc1w.app.ui.ui.detail.live;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import cn.cc1w.app.ui.adapter.live.HostRoomMessageAdapter;
import cn.cc1w.app.ui.adapter.live.LiveSudokuAdapter;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 主持人三张图片类型消息
 * @author kpinfo
 */
public class HostTripleView {
    private final Context context;
    private final LiveHostEntity.LiveHostItemEntity entity;
    private final HostRoomMessageAdapter.HostTripleGirdHolder holder;

    public HostTripleView(Context context, int position, RecyclerView.ViewHolder holder, LiveHostEntity.LiveHostItemEntity entity) {
        this.context = context;
//        int index = position;
        this.entity = entity;
        this.holder = (HostRoomMessageAdapter.HostTripleGirdHolder) holder;
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
            holder.describeTv.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            holder.userNameTv.setText(TextUtils.isEmpty(entity.getAnchor_name()) ? "" : entity.getAnchor_name());
            AppUtil.loadAvatarImg(entity.getAnchor_headpic(), holder.avatarImg);
            List<LiveHostEntity.LiveHostItemEntity.JsonBean> list = entity.getJson();
            if (null != list && !list.isEmpty()) {
                GridLayoutManager manager = new GridLayoutManager(context, 3);
                holder.recyclerView.setLayoutManager(manager);
                LiveSudokuAdapter adapter = new LiveSudokuAdapter();
                adapter.setData(list);
                holder.recyclerView.setAdapter(adapter);
                holder.recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context, 2), 3));
            }
        }
    }
}