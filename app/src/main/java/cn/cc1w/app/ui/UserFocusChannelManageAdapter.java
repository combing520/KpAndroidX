package cn.cc1w.app.ui;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.HomeChannelEntity;

/**
 * @author kpinfo
 * on 2020-12-17
 * 用户关注的 栏目
 */
public class UserFocusChannelManageAdapter extends RecyclerView.Adapter<UserFocusChannelManageAdapter.ViewHolder> {
    private final List<HomeChannelEntity.ItemHomeChannelEntity> mDataSet = new ArrayList<>();
    private OnItemDeleteListener mListener;

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

    public void setOnItemClickListener(OnItemDeleteListener listener) {
        mListener = listener;
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


    /**
     * 删除对应位置的条目
     *
     * @param pos 对应的位置
     */
    public void deleteItem(int pos) {
        mDataSet.remove(pos);
        notifyDataSetChanged();
    }

    public List<HomeChannelEntity.ItemHomeChannelEntity> getDataSet() {
        return mDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_manage_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeChannelEntity.ItemHomeChannelEntity item = getItem(position);
        if (item.isIs_fix()) {
            holder.nameTv.setTextColor(Color.parseColor("#89898A"));
            holder.nameTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        } else {
            holder.nameTv.setTextColor(Color.parseColor("#D71718"));
            holder.nameTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName() + " - ");
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.deleteItem(v, position);
            }
        });
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

    public interface OnItemDeleteListener {
        /**
         * @param v   对应操作的View
         * @param pos 删除的位置
         */
        void deleteItem(View v, int pos);
    }
}