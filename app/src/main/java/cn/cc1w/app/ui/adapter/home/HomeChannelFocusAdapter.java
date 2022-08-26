package cn.cc1w.app.ui.adapter.home;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.interfaces.ItemTouchHelperAdapter;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.R;

/**
 * 首页 频道关注的adapter
 * @author kpinfo
 */
public class HomeChannelFocusAdapter extends RecyclerView.Adapter<HomeChannelFocusAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private final List<HomeChannelEntity.ItemHomeChannelEntity> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

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
     * 给对应的位置插入数据
     *
     * @param item 对应的数据
     */
    public void addItem(HomeChannelEntity.ItemHomeChannelEntity item) {
        if (null != item) {
            dataSet.add(item);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除对应位置的条目
     *
     * @param pos 对应的位置
     */
    public void deleteItem(int pos) {
        dataSet.remove(pos);
        notifyDataSetChanged();
    }

    public List<HomeChannelEntity.ItemHomeChannelEntity> getDataSet() {
        return dataSet;
    }

    /**
     * 获取对应位置上的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public HomeChannelEntity.ItemHomeChannelEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_edit_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeChannelEntity.ItemHomeChannelEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
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

    /**
     * 条目被移动
     */
    @Override
    public void onItemMove(RecyclerView.ViewHolder holder, int fromPosition, int targetPosition) {
        if (fromPosition < dataSet.size() && targetPosition < dataSet.size()) {
            Collections.swap(dataSet, fromPosition, targetPosition);
            notifyItemMoved(fromPosition, targetPosition);
        }
    }

    /**
     * 条目被选中
     */
    @Override
    public void onItemSelect(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0.8f);
        holder.itemView.setScaleY(0.8f);
    }

    /**
     * 条目被释放
     *
     * @param holder holder
     */
    @Override
    public void onItemClear(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(1.0f);
        holder.itemView.setScaleY(1.0f);
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder holder) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_item_channel);
        }
    }
}