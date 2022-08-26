package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.HomeChannelEntity;

/**
 * @author kpinfo
 * on 2020-12-15
 * 开屏新闻 7.0 频道管理
 */
public class AllChannelManageAdapter extends RecyclerView.Adapter<AllChannelManageAdapter.ViewHolder> {
    private ItemAddListener mListener;
    private final List<HomeChannelEntity.ItemHomeChannelEntity> mDataSet = new ArrayList<>();

    public void setData(List<HomeChannelEntity.ItemHomeChannelEntity> list) {
        if (null != list && !list.isEmpty()) {
            mDataSet.clear();
            mDataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public HomeChannelEntity.ItemHomeChannelEntity getItem(int pos) {
        return mDataSet.get(pos);
    }

    public void setOnItemAddListener(ItemAddListener listener) {
        this.mListener = listener;
    }

    /**
     * 获取数据集合
     *
     * @return 数据集合
     */
    public List<HomeChannelEntity.ItemHomeChannelEntity> getDataList() {
        return mDataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_manage_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        HomeChannelEntity.ItemHomeChannelEntity item = getItem(position);
        if (!item.isAttention()) {
            holder.nameTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName() + " + ");
        } else {
            holder.nameTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.addItem(v, position);
            }
        });
    }

    /**
     * 给对应的位置插入数据
     *
     * @param item 对应的数据
     */
    public void addItem(HomeChannelEntity.ItemHomeChannelEntity item) {
        if (null != item && !mDataSet.contains(item)) {
            mDataSet.add(item);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除
     *
     * @param item 对应的数据
     */
    public void removeItem(HomeChannelEntity.ItemHomeChannelEntity item) {
        if (null != item && mDataSet.contains(item)) {
            mDataSet.remove(item);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.function_name_tv);
        }
    }

    public interface ItemAddListener {
        void addItem(View v, int pos);
    }
}