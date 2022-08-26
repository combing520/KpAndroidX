package cn.cc1w.app.ui.adapter.topic.rank;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 视频排名
 * @author kpinfo
 */
public class VideoRankAdapter extends RecyclerView.Adapter<VideoRankAdapter.ViewHolder> {
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_PIC = 2;
    private OnItemClickListener listener;
    private final List<ItemVideoListEntity.DataBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemVideoListEntity.DataBean> list) {
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
    public void addData(List<ItemVideoListEntity.DataBean> list) {
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
    public ItemVideoListEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_video_paikew_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemVideoListEntity.DataBean entity = getItem(position);
        holder.rankTv.setText(String.valueOf(position + 1));
        holder.titleTv.setText(TextUtils.isEmpty(entity.getTitle()) ? "" : entity.getTitle());
        holder.usernameTv.setText(TextUtils.isEmpty(entity.getNickname()) ? "" : entity.getNickname());
        holder.cntTv.setText(String.valueOf(entity.getClick_number()).concat("人参与"));
        AppUtil.loadNewsImg(entity.getCover(), holder.postImg);
        AppUtil.loadAvatarImg(entity.getHead_pic_path(), holder.userAvatar);
        if (entity.getType() == TYPE_VIDEO) {
            holder.playerImg.setVisibility(View.VISIBLE);
        } else if (entity.getType() == TYPE_PIC) {
            holder.playerImg.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != listener) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rankTv;
        private final TextView titleTv;
        private final TextView usernameTv;
        private final TextView cntTv;
        private final ImageView postImg;
        private final RoundAngleImageView userAvatar;
        private final ImageView playerImg;

        public ViewHolder(View itemView) {
            super(itemView);
            rankTv = itemView.findViewById(R.id.txt_rank_rank_recycle);
            titleTv = itemView.findViewById(R.id.txt_title_rank_recycle);
            usernameTv = itemView.findViewById(R.id.txt_username_rank_recycle);
            cntTv = itemView.findViewById(R.id.txt_cnt_rank_recycle);
            postImg = itemView.findViewById(R.id.img_post_rank_recycle);
            userAvatar = itemView.findViewById(R.id.img_avatar_rank_recycle);
            playerImg = itemView.findViewById(R.id.img_player_rank_recycle);
        }
    }
}