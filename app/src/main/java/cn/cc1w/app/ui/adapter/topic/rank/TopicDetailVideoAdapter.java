package cn.cc1w.app.ui.adapter.topic.rank;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 话题详情中 视频Adapter
 * @author kpinfo
 */
public class TopicDetailVideoAdapter extends RecyclerView.Adapter<TopicDetailVideoAdapter.ViewHolder> {
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_PIC = 2;
    private final List<ItemVideoListEntity.DataBean> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

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
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取对应位置条目
     */
    public ItemVideoListEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_video_topic_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemVideoListEntity.DataBean entity = getItem(position);
        AppUtil.loadSplashImg(entity.getCover(), holder.postImg);
        if (entity.getType() == TYPE_VIDEO) {
            holder.prizeCntTv.setVisibility(View.GONE);
            holder.playImg.setVisibility(View.VISIBLE);
            holder.videoLayout.setVisibility(View.VISIBLE);
            holder.videoPlayCntTv.setText(String.valueOf(entity.getClick_number()).concat("次播放"));
        } else if (entity.getType() == TYPE_PIC) {
            holder.prizeCntTv.setVisibility(View.VISIBLE);
            holder.playImg.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.prizeCntTv.setText(String.valueOf(entity.getPraise_number()).concat("点赞"));
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
        private final ImageView postImg;
        private final ImageView playImg;
        private final LinearLayout videoLayout;
        private final TextView videoPlayCntTv;
        private final TextView prizeCntTv;

        public ViewHolder(View itemView) {
            super(itemView);
            postImg = itemView.findViewById(R.id.img_post_detail_video_topic);
            playImg = itemView.findViewById(R.id.img_play_detail_video_topic);
            videoLayout = itemView.findViewById(R.id.ll_video_detail_video_topic);
            videoPlayCntTv = itemView.findViewById(R.id.txt_cnt_detail_video_topic);
            prizeCntTv = itemView.findViewById(R.id.txt_prise_detail_video_topic);
        }
    }
}