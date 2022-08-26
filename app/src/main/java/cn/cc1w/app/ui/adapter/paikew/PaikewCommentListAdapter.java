package cn.cc1w.app.ui.adapter.paikew;

import android.content.Context;
import android.graphics.Color;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PaikewCommentEntity;
import cn.cc1w.app.ui.entity.VideoAndPicPriseEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.ThumbUpView.ThumbUpView;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;

/**
 * 拍客评论列表
 *
 * @author kpinfo
 */
public class PaikewCommentListAdapter extends RecyclerView.Adapter<PaikewCommentListAdapter.ViewHolder> {
    private final List<PaikewCommentEntity.ItemPaikewCommentEntity> dataSet = new ArrayList<>();
    private long lastTime;
    private static final int PRISE_SELECT = 1;
    private static final int PRISE_UNSELECT = 2;
    private final Context context;
    private final LifecycleOwner owner;

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public PaikewCommentListAdapter(Context context,LifecycleOwner owner) {
        lastTime = System.currentTimeMillis();
        this.context = context;
        this.owner = owner;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<PaikewCommentEntity.ItemPaikewCommentEntity> list) {
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
    public void addData(List<PaikewCommentEntity.ItemPaikewCommentEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<PaikewCommentEntity.ItemPaikewCommentEntity> getDataSet(){
        return  dataSet;
    }

    public PaikewCommentEntity.ItemPaikewCommentEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_detail_news_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        PaikewCommentEntity.ItemPaikewCommentEntity entity = dataSet.get(position);
        if (null != entity) {
            holder.usernameTv.setText(TextUtils.isEmpty(entity.getNickname()) ? "" : entity.getNickname());
            holder.createTimeTv.setText(TextUtils.isEmpty(entity.getCreate_time()) ? "" : entity.getCreate_time());
            holder.commentContentTv.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            AppUtil.loadAvatarImg(entity.getHead_pic_path(), holder.avatarImg);
            holder.priseImg.setUnLikeType(ThumbUpView.LikeType.broken);
            holder.priseImg.setCracksColor(Color.parseColor("#b3b3b3"));
            holder.priseImg.setEdgeColor(Color.parseColor("#b3b3b3"));
            holder.priseImg.setFillColor(Color.parseColor("#c81f1d"));
            holder.priseImg.setBgColor(Color.parseColor("#b3b3b3"));
            if (null != entity.getComment_reply() && !entity.getComment_reply().isEmpty()) {
                holder.replyList.setVisibility(View.VISIBLE);
                PaikewCommentReplyAdapter adapter = new PaikewCommentReplyAdapter();
                holder.replyList.setLayoutManager(new LinearLayoutManager(AppContext.getAppContext()));
                adapter.setData(entity.getComment_reply());
                holder.replyList.setAdapter(adapter);
            } else {
                holder.replyList.setVisibility(View.GONE);
            }
            if (entity.getPraise_select() == PRISE_SELECT) {
                holder.priseImg.like();
            } else {
                holder.priseImg.unLike();
            }
            if (entity.getPraise_number() == 0) {
                holder.commentCntTv.setText("");
            } else {
                holder.commentCntTv.setText(String.valueOf(entity.getPraise_number()));
            }
            // 点击条目进行评论
            holder.replyContainer.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    EventBus.getDefault().post(new EventMessage("openPaiKewReplyDialog", String.valueOf(entity.getId())));
                }
                lastTime = currentTime;
            });
            holder.priseImg.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    int commentId = entity.getId();
                    if (NetUtil.isNetworkConnected(context)) {
                        //  状态：值：（1-为已点赞状态，需要去取消点赞状态，2-为未点赞状态，需要进行点赞操作）
                        RxHttp.postJson(Constant.LIST_COMMENT_PAIKEW_PRISE).add("comment_id", commentId).add("status", (entity.getPraise_select() == 1 ? 2 : 1))
                                .asResponse(VideoAndPicPriseEntity.DataBean.class)
                                .as(RxLife.asOnMain(owner))
                                .subscribe(data -> {
                                    if (entity.getPraise_select() == PRISE_SELECT) {
                                        entity.setPraise_select(PRISE_UNSELECT);
                                        holder.priseImg.unLike();
                                    } else {
                                        entity.setPraise_select(PRISE_SELECT);
                                        holder.priseImg.like();
                                    }
                                    if (null != data) {
                                        if (data.getPraise_number() == 0) {
                                            holder.commentCntTv.setText("");
                                        } else {
                                            holder.commentCntTv.setText(String.valueOf(data.getPraise_number()));
                                        }
                                    }
                                }, (OnError) error -> {
                                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                        AppUtil.doUserLogOut();
                                        IntentUtil.startActivity(context, LoginActivity.class);
                                    }
                                    ToastUtil.showShortToast(error.getErrorMsg());
                                });
                    } else {
                        ToastUtil.showShortToast(context.getString(R.string.network_error));
                    }
                }
                lastTime = currentTime;
            });
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
        private final ThumbUpView priseImg;
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
