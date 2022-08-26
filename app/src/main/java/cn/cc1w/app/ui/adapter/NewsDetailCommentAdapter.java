package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.goodView.GoodView;
import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsCommentEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 新闻详情评论 Adapter
 *
 * @author kpinfo
 */
public class NewsDetailCommentAdapter extends RecyclerView.Adapter<NewsDetailCommentAdapter.ViewHolder> {
    private final List<NewsCommentEntity.ItemNewsCommentEntity> dataSet = new ArrayList<>();
    private OnItemClickListener listener;
    private final Context context;
    private final LifecycleOwner owner;

    public NewsDetailCommentAdapter(Context context, LifecycleOwner owner) {
        this.context = context;
        this.owner = owner;
    }

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<NewsCommentEntity.ItemNewsCommentEntity> list) {
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
    public void addData(List<NewsCommentEntity.ItemNewsCommentEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应位置
     * @return 对应条目
     */
    public NewsCommentEntity.ItemNewsCommentEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment2_detail_news_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {
        final NewsCommentEntity.ItemNewsCommentEntity entity = getItem(position);
        if (null != entity && null != context) {
            holder.usernameTv.setText(TextUtils.isEmpty(entity.getUser_nickname()) ? "" : entity.getUser_nickname());
            holder.createTimeTv.setText(TextUtils.isEmpty(entity.getCreate_time()) ? "" : entity.getCreate_time());
            holder.commentContentTv.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            if (entity.getGood_num() == 0) {
                holder.commentCntTv.setText("");
            } else {
                holder.commentCntTv.setText(String.valueOf(entity.getGood_num()));
            }
            if (entity.isPraise()) {
                AppUtil.loadRes(R.mipmap.ic_appreciate_hl, holder.priseImg);
                holder.priseImg.setClickable(false);
            } else {
                AppUtil.loadRes(R.mipmap.ic_appreciate_normal, holder.priseImg);
                holder.priseImg.setOnClickListener(v -> {
                    if (!NetUtil.isNetworkConnected(context)) {
                        ToastUtil.showShortToast(context.getResources().getString(R.string.network_error));
                    } else {
                        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                            Intent intent = new Intent();
                            intent.setClass(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            RxHttp.postJson(Constant.APPRECIATE_LIST_COMMENT_DETAIL_NEWS).add("cw_comment_id", entity.getId())
                                    .asResponse(JsonObject.class)
                                    .as(RxLife.asOnMain(owner))
                                    .subscribe(data -> {
                                        entity.setPraise(true);
                                        holder.commentCntTv.setText(String.valueOf(entity.getGood_num() + 1));
                                        GoodView goodView = new GoodView(context);
                                        goodView.setTextInfo("+1", Color.parseColor("#f66467"), 13);
                                        goodView.show(holder.priseImg);
                                        AppUtil.loadRes(R.mipmap.ic_appreciate_hl, holder.priseImg);
                                        holder.priseImg.setClickable(false);
                                    }, (OnError) error -> {
                                        ToastUtil.showShortToast(error.getErrorMsg());
                                    });
                        }
                    }
                });
            }
            if (null != entity.getReply_comment() && !entity.getReply_comment().isEmpty()) {
                holder.replyList.setVisibility(View.VISIBLE);
                NewsCommentReplyAdapter adapter = new NewsCommentReplyAdapter();
                holder.replyList.setLayoutManager(new LinearLayoutManager(context));
                adapter.setData(entity.getReply_comment());
                holder.replyList.setAdapter(adapter);
            } else {
                holder.replyList.setVisibility(View.GONE);
            }
            holder.replyContainer.setOnClickListener(v -> {
                if (null != listener) {
                    listener.onItemClick(v, position);
                }
            });
            AppUtil.loadAvatarImg(entity.getUser_headpic(), holder.avatarImg);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView avatarImg;
        private final TextView usernameTv;
        private final TextView createTimeTv;
        private final TextView commentContentTv;
        private final ImageView priseImg;
        private final TextView commentCntTv;
        private final LinearLayout replyContainer;
        private final RecyclerView replyList;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.img_item_avatar_comment_detail_news);
            usernameTv = itemView.findViewById(R.id.txt_item_username_comment_detail_news);
            createTimeTv = itemView.findViewById(R.id.txt_item_time_comment_detail_news);
            commentContentTv = itemView.findViewById(R.id.txt_item_describe_comment_detail_news);
            priseImg = itemView.findViewById(R.id.img_item_appreciate_comment_detail_news);
            commentCntTv = itemView.findViewById(R.id.txt_item_appreciate_cnt_comment_detail_news);
            replyContainer = itemView.findViewById(R.id.ll_item_comment_detail_news);
            replyList = itemView.findViewById(R.id.list_item_comment_detail_news);
        }
    }
}