package cn.cc1w.app.ui.ui.usercenter.broke.view;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeVideoPlayDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;


/**
 *
 * @author kpinfo
 * @date 2018/11/12
 * 系统发送的 视频信息
 */
public class SystemVideoListView {
    private final Context activity;
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.SystemVideoHolder holder;

    public SystemVideoListView(Context context,  RecyclerView.ViewHolder h, ItemBrokeEntity item) {
        holder = (BrokeAdapter.SystemVideoHolder) h;
        activity = context;
        entity = item;
    }

    public void initView() {
        if (null != entity) {
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.avatarImg);
            AppUtil.loadBrokeImg(entity.getPic_path(), holder.videoPostImg);
            holder.videoPostImg.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(activity, BrokeVideoPlayDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("videoUrl", entity.getVideo_path());
                intent.putExtra("videoPostUrl", entity.getPic_path());
                activity.startActivity(intent);
            });
        }
    }
}