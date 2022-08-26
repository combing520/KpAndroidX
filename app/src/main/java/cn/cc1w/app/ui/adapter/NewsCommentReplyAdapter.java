package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.NewsCommentEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 评论回复 Adapter
 *
 * @author kpinfo
 */

public class NewsCommentReplyAdapter extends RecyclerView.Adapter<NewsCommentReplyAdapter.ViewHolder> {
    private final List<NewsCommentEntity.ItemNewsCommentEntity.ReplyCommentBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setData(List<NewsCommentEntity.ItemNewsCommentEntity.ReplyCommentBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     */
    public NewsCommentEntity.ItemNewsCommentEntity.ReplyCommentBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_reply_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        final NewsCommentEntity.ItemNewsCommentEntity.ReplyCommentBean entity = getItem(position);
        if (null != entity) {
            AppUtil.loadAvatarImg(entity.getUser_headpic(), holder.avatarImg);
            holder.replyNameTv.setText(TextUtils.isEmpty(entity.getUser_nickname()) ? "" : entity.getUser_nickname());
            holder.userNameTv.setText(TextUtils.isEmpty(entity.getReply_nickname()) ? "" : entity.getReply_nickname());
            holder.replyContentTv.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            holder.itemView.setOnClickListener(v -> showCommentReplyDialog(entity.getId()));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * 显示评论回复的 dialog
     */
    private void showCommentReplyDialog(final String commentId) {
        EventBus.getDefault().post(new EventMessage("openReplyDialog", commentId));
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