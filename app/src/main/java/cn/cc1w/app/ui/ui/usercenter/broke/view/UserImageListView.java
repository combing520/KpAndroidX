package cn.cc1w.app.ui.ui.usercenter.broke.view;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeAlbumDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;


/**
 *
 * @author kpinfo
 * @date 2018/11/12
 */

public class UserImageListView {
    private final Context activity;
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.UserImgHolder holder;

    public UserImageListView(Context context, RecyclerView.ViewHolder h, ItemBrokeEntity item) {
        holder = (BrokeAdapter.UserImgHolder) h;
        entity = item;

        activity = context;
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
