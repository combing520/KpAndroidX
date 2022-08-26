package cn.cc1w.app.ui.adapter.live;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 用户聊天
 *
 * @author kpinfo
 */
public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    private final List<LiveHostEntity.LiveHostItemEntity> dataSet = new ArrayList<>();
    private OnItemLongClickListener listener;

    /**
     * 设置数据
     */
    public void setData(List<LiveHostEntity.LiveHostItemEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     */
    public void addData(List<LiveHostEntity.LiveHostItemEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加某一天消息
     */
    public void addItem(int pos, LiveHostEntity.LiveHostItemEntity item) {
        dataSet.add(pos, item);
        notifyDataSetChanged();
    }

    /**
     * 获取对应位置条目
     */
    public LiveHostEntity.LiveHostItemEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取数据集合
     */
    public List<LiveHostEntity.LiveHostItemEntity> getList() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_live_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        LiveHostEntity.LiveHostItemEntity entity = getItem(position);
        if (null != entity) {
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.userAvatarImg);
            holder.commentContentTv.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            holder.userNameTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
            holder.commentTimeTv.setText(TextUtils.isEmpty(entity.getAdd_time()) ? "" : entity.getAdd_time());
            holder.itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onItemLongClickListener(position);
                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView userAvatarImg;
        private final TextView userNameTv;
        private final TextView commentTimeTv;
        private final TextView commentContentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            userAvatarImg = itemView.findViewById(R.id.img_item_avatar_comment_chat_live);
            userNameTv = itemView.findViewById(R.id.txt_item_username_comment_chat_live);
            commentTimeTv = itemView.findViewById(R.id.txt_item_time_comment_chat_live);
            commentContentTv = itemView.findViewById(R.id.txt_item_describe_comment_chat_live);
        }
    }

    public interface OnItemLongClickListener {

        void onItemLongClickListener(int pos);
    }
}