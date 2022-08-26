package cn.cc1w.app.ui.adapter.home;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.R;

/**
 * 频道
 * @author kpinfo
 */
public class HomeChannelAdapter extends RecyclerView.Adapter<HomeChannelAdapter.ViewHolder> {
    private final List<HomeChannelEntity.ItemHomeChannelEntity> dataSet = new ArrayList<>();
    private OnHomeChannelLongClickListener listener;

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void addHomeChannelLongClickListener(OnHomeChannelLongClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<HomeChannelEntity.ItemHomeChannelEntity> list) {
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
    public void addData(List<HomeChannelEntity.ItemHomeChannelEntity> list) {
        if (null != list && !list.isEmpty()) {
            int startPos = dataSet.size();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取列表对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目内容
     */
    public HomeChannelEntity.ItemHomeChannelEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 获取数据集合
     *
     * @return 数据集合
     */
    public List<HomeChannelEntity.ItemHomeChannelEntity> getDataList() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeChannelEntity.ItemHomeChannelEntity entity = getItem(position);
        holder.channelTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
        holder.itemView.setOnLongClickListener(v -> {
            if (null != listener) {
                listener.onHomeChannelLongClick(v, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView channelTv;

        public ViewHolder(View itemView) {
            super(itemView);
            channelTv = itemView.findViewById(R.id.tv_item_channel);
        }
    }

    public interface OnHomeChannelLongClickListener {
        boolean onHomeChannelLongClick(View view, int position);
    }
}