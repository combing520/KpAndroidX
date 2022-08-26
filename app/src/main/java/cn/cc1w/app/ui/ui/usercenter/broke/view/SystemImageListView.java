package cn.cc1w.app.ui.ui.usercenter.broke.view;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeAlbumDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;


/**
 * @author kpinfo
 * @date 2018/11/12
 */
public class SystemImageListView {
    private final Context activity;
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.SystemImgHolder holder;

    public SystemImageListView(Context context, RecyclerView.ViewHolder h, ItemBrokeEntity item) {
        holder = (BrokeAdapter.SystemImgHolder) h;
        activity = context;
        entity = item;
    }

    public void initView() {
        if (null != entity) {
            AppUtil.loadBrokeImg(entity.getPic_path(), holder.pic);
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.avatarImg);
            holder.pic.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(activity, BrokeAlbumDetailActivity.class);
                intent.putExtra("pic", entity.getPic_path());
                activity.startActivity(intent);
            });
        }
    }
}