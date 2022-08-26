package cn.cc1w.app.ui.adapter.topic;

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

import cn.cc1w.app.ui.entity.HotTopicEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 热门话题
 * @author kpinfo
 */
public class HotTopicRankAdapter extends RecyclerView.Adapter<HotTopicRankAdapter.ViewHolder> {
    private final List<HotTopicEntity.ItemHotTopicEntity> dataSet = new ArrayList<>();
    private OnItemClickListener listener;
    private static final String PEOPLE_PART = "人参与";

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<HotTopicEntity.ItemHotTopicEntity> list) {
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
    public void addData(List<HotTopicEntity.ItemHotTopicEntity> list) {
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
    public HotTopicEntity.ItemHotTopicEntity getItem(int pos) {
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_hot_rank_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HotTopicEntity.ItemHotTopicEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getTopic_name()) ? "" : entity.getTopic_name());
        holder.cntTv.setText((entity.getParticipation()) + PEOPLE_PART);
        if (position < 3) {
            holder.rankTv.setVisibility(View.GONE);
            holder.rankImg.setVisibility(View.VISIBLE);
            if (position == 0) {
                AppUtil.loadRes(R.mipmap.ic_rank_first, holder.rankImg);
            } else if (position == 1) {
                AppUtil.loadRes(R.mipmap.ic_rank_second, holder.rankImg);
            } else {
                AppUtil.loadRes(R.mipmap.ic_rank_third, holder.rankImg);
            }
        } else {
            holder.rankTv.setVisibility(View.VISIBLE);
            holder.rankImg.setVisibility(View.GONE);
            holder.rankTv.setText(String.valueOf(position + 1));
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
        private final TextView titleTv;
        private final TextView cntTv;
        private final TextView rankTv;
        private final ImageView rankImg;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_topic_hot_rank_title);
            cntTv = itemView.findViewById(R.id.txt_item_topic_hot_rank_cnt);
            rankTv = itemView.findViewById(R.id.txt_item_topic_hot_rank_rank);
            rankImg = itemView.findViewById(R.id.img_item_topic_hot_rank_rank);
        }
    }
}
