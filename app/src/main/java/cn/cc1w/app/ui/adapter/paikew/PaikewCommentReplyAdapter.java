package cn.cc1w.app.ui.adapter.paikew;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PaikewCommentEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 拍客中回复 adapter
 *
 * @author kpinfo
 */
public class PaikewCommentReplyAdapter extends RecyclerView.Adapter<PaikewCommentReplyAdapter.ViewHolder> {
    private final List<PaikewCommentEntity.ItemPaikewCommentEntity.ItemPaikewCommentReplyEntity> dataSet = new ArrayList<>();
    private long lastTime;

    public PaikewCommentReplyAdapter() {
        lastTime = System.currentTimeMillis();
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<PaikewCommentEntity.ItemPaikewCommentEntity.ItemPaikewCommentReplyEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param list 数据源
     */
    public void addData(List<PaikewCommentEntity.ItemPaikewCommentEntity.ItemPaikewCommentReplyEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应位置
     * @return 对应位置条目
     */
    public PaikewCommentEntity.ItemPaikewCommentEntity.ItemPaikewCommentReplyEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_reply_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        PaikewCommentEntity.ItemPaikewCommentEntity.ItemPaikewCommentReplyEntity entity = getItem(position);
        if (null != entity) {
            AppUtil.loadAvatarImg(entity.getHead_pic_path(), holder.avatarImg);
            holder.replyNameTv.setText(TextUtils.isEmpty(entity.getNickname()) ? "" : entity.getNickname());
            holder.userNameTv.setText(TextUtils.isEmpty(entity.getComment_nickname()) ? "" : entity.getComment_nickname());
            holder.replyContentTv.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    showCommentReplyDialog(String.valueOf(entity.getId()));
                }
                lastTime = currentTime;
            });
        }
    }

    /**
     * 显示评论回复的 dialog
     */
    private void showCommentReplyDialog(final String commentId) {
        EventBus.getDefault().post(new EventMessage("openPaiKewReplyDialog", commentId));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView avatarImg;
        private final TextView replyNameTv;
        private final TextView userNameTv;
        private final TextView replyContentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.img_item_avatar_comment_sub_detail_news);
            replyNameTv = itemView.findViewById(R.id.txt_item_replyName_comment_sub_detail_news);
            userNameTv = itemView.findViewById(R.id.txt_item_username_comment_sub_detail_news);
            replyContentTv = itemView.findViewById(R.id.txt_item_describe_comment_sub_detail_news);
        }
    }
}