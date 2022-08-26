package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.os.Bundle;

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

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemCommentEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 评论 适配器
 *
 * @author kpinfo
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<ItemCommentEntity.DataBean> dataSet = new ArrayList<>();
    private OnItemClickListener listener;
    private final Context context;

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public CommentAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemCommentEntity.DataBean> list) {
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
    public void addData(List<ItemCommentEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 将对应的位置进行点赞
     *
     * @param pos 对应的位置
     */
    public void doAppreciate(int pos) {
        ItemCommentEntity.DataBean entity = dataSet.get(pos);
        entity.setPraise(true);
        entity.setGood_num(entity.getGood_num() + 1);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应位置上的条目
     */
    public ItemCommentEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 删除对应位置上的条目
     *
     * @param pos 对应的位置
     */
    public void deleteItem(int pos) {
        dataSet.remove(pos);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ItemCommentEntity.DataBean item = getItem(position);
        AppUtil.loadAvatarImg(item.getUser_headpic(), holder.avatarImg);
        holder.userNameTv.setText(TextUtils.isEmpty(item.getUser_nickname()) ? "" : item.getUser_nickname());
        holder.timeTv.setText(TextUtils.isEmpty(item.getCreate_time()) ? "" : item.getCreate_time());
        holder.titleTv.setText(TextUtils.isEmpty(item.getNews_title()) ? "" : item.getNews_title());
        holder.contentTv.setText(TextUtils.isEmpty(item.getContent()) ? "" : item.getContent());
        if (item.getGood_num() > 0) {
            holder.appreciateCntTv.setVisibility(View.VISIBLE);
            holder.appreciateCntTv.setText(String.valueOf(item.getGood_num()));
        } else {
            holder.appreciateCntTv.setVisibility(View.GONE);
        }
        if (item.isPraise()) {
            holder.appreciateImg.setClickable(false);
            AppUtil.loadRes(R.mipmap.ic_appreciate_hl, holder.appreciateImg);
        } else {
            holder.appreciateImg.setClickable(true);
            AppUtil.loadRes(R.mipmap.ic_appreciate_normal, holder.appreciateImg);
            holder.appreciateImg.setOnClickListener(v -> {
                if (null != listener) {
                    listener.onItemClick(v, position);
                }
            });
            holder.titleTv.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(item.getNews_id()) && (null != context)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TAG_ID, item.getNews_id());
                    IntentUtil.startActivity(context, NewsDetailNewActivity.class, bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView avatarImg;
        private final TextView contentTv;
        private final TextView timeTv;
        private final TextView appreciateCntTv;
        private final ImageView appreciateImg;
        private final TextView userNameTv;
        private final TextView titleTv;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.img_avatar_item_comment);
            contentTv = itemView.findViewById(R.id.txt_content_item_comment);
            timeTv = itemView.findViewById(R.id.txt_time_item_comment);
            appreciateCntTv = itemView.findViewById(R.id.txt_appreciate_item_comment);
            appreciateImg = itemView.findViewById(R.id.img_appreciate_item_comment);
            userNameTv = itemView.findViewById(R.id.txt_username_item_comment);
            titleTv = itemView.findViewById(R.id.txt_title_item_comment);
        }
    }
}