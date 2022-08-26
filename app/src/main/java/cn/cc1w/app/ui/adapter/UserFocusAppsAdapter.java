package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.UserFocusAppsEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.apps.AppDetailActivity;
import cn.cc1w.app.ui.ui.home.apps.FavoriteAppsAddActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 用户关注的应用号 Adapter
 * @author kpinfo
 */
public class UserFocusAppsAdapter extends RecyclerView.Adapter<UserFocusAppsAdapter.ViewHolder> {
    private final List<UserFocusAppsEntity.ItemUserFocusAppsEntity> dataSet = new ArrayList<>();
    private boolean isDeleteIconShow;
    private OnItemClickListener listener;

    /**
     * 设置数据
     *
     * @param list 数据集合
     */
    public void setData(List<UserFocusAppsEntity.ItemUserFocusAppsEntity> list) {
        if (null != list) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public UserFocusAppsEntity.ItemUserFocusAppsEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 对应位置插入条目
     *
     * @param pos  插入的位置
     * @param item 插入的条目
     */
    public void addItem(int pos, UserFocusAppsEntity.ItemUserFocusAppsEntity item) {
        dataSet.add(pos, item);
        notifyDataSetChanged();
    }

    /**
     * 删除对应位置的条目
     *
     * @param pos 对应的位置
     */
    public void deleteItem(int pos) {
        dataSet.remove(pos);
        notifyDataSetChanged();
    }

    /**
     * 显示 删除icon
     */
    public void showDeleteIcon() {
        isDeleteIconShow = true;
        notifyDataSetChanged();
    }

    /**
     * 隐藏删除icon
     */
    public void hideDeleteIcon() {
        isDeleteIconShow = false;
        notifyDataSetChanged();
    }

    /**
     * 设置条目删除
     */
    public void setOnItemDeleteClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_focus_apps_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {
        final UserFocusAppsEntity.ItemUserFocusAppsEntity entity = getItem(position);
        if (!TextUtils.equals(entity.getName(), "添加+")) {
            if (isDeleteIconShow) {
                holder.deleteImg.setVisibility(View.VISIBLE);
            } else {
                holder.deleteImg.setVisibility(View.GONE);
            }
        } else {
            holder.deleteImg.setVisibility(View.GONE);
        }
        holder.appsThumbnail.setOnClickListener(v -> {
            // 跳转到对应的 应用号详情
        });
        holder.deleteImg.setOnClickListener(v -> {
            if (null != listener) {
                listener.onItemClick(v, position);
            }
        });
        holder.appsNameTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
        if (entity.getRes() != 0) {
            AppUtil.loadRes(entity.getRes(), holder.appsThumbnail);
        } else {
            AppUtil.loadAppsImg(entity.getLogo_pic_path(), holder.appsThumbnail);
        }

        holder.appsThumbnail.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                if (TextUtils.equals("添加+", entity.getName())) {
                    intent.setClass(context, FavoriteAppsAddActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    intent.setClass(context, AppDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.TAG_ID, entity.getId());
                    intent.putExtra(Constant.TAG_TITLE, entity.getName());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView appsThumbnail;
        private final ImageView deleteImg;
        private final TextView appsNameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            appsThumbnail = itemView.findViewById(R.id.img_item_apps_focus_user);
            deleteImg = itemView.findViewById(R.id.btn_item_apps_delete_focus_user);
            appsNameTv = itemView.findViewById(R.id.txt_item_apps_focus_user);
        }
    }
}