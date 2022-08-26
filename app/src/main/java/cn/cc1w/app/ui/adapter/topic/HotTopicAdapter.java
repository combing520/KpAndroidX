package cn.cc1w.app.ui.adapter.topic;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.HotTopicEntity;
import cn.cc1w.app.ui.R;

/**
 * 热门话题
 *
 * @author kpinfo
 */
public class HotTopicAdapter extends RecyclerView.Adapter<HotTopicAdapter.ViewHolder> {
    private final List<HotTopicEntity.ItemHotTopicEntity> dataSet = new ArrayList<>();
    private TagClickListener listener;

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
     * 获取对应位置的条目
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
    public void setOnItemClickListener(TagClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_hot_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HotTopicEntity.ItemHotTopicEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getTopic_name()) ? "" : entity.getTopic_name());
        holder.cntTv.setText((entity.getParticipation()) + "人参与");
        holder.itemView.setOnClickListener(v -> {
            if (null != listener) {
                listener.onTagClick(v, position);
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

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_topic_hot_title);
            cntTv = itemView.findViewById(R.id.txt_item_topic_hot_cnt);
        }
    }

    public interface TagClickListener {
        void onTagClick(View v, int pos);
    }
}
