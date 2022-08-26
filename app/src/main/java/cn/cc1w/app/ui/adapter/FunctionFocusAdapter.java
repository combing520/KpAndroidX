package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.cc1w.app.ui.entity.FunctionEntity;
import cn.cc1w.app.ui.interfaces.ItemTouchHelperAdapter;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 已关注的 功能区
 * @author kpinfo
 */
public class FunctionFocusAdapter extends RecyclerView.Adapter<FunctionFocusAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private final List<FunctionEntity.ItemFunctionEntity> dataSet = new ArrayList<>();

    /**
     * 获取数据集合
     *
     * @return 数据源
     */
    public List<FunctionEntity.ItemFunctionEntity> getData() {
        return dataSet;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<FunctionEntity.ItemFunctionEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加条目
     *
     * @param item 条目
     */
    public void addItem(FunctionEntity.ItemFunctionEntity item) {
        dataSet.add(item);
        notifyDataSetChanged();
    }

    /**
     * 删除对应位置条目
     *
     * @param pos 条目
     */
    public void deleteItem(int pos) {
        dataSet.remove(pos);
        notifyDataSetChanged();
    }


    public FunctionEntity.ItemFunctionEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_focus_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        FunctionEntity.ItemFunctionEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
        AppUtil.loadAppsImg(entity.getPic_path(), holder.iconImg);
        holder.itemView.setOnClickListener(v -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconImg;
        private final TextView titleTv;

        public ViewHolder(View itemView) {
            super(itemView);
            iconImg = itemView.findViewById(R.id.img_logo_item_function_focus);
            titleTv = itemView.findViewById(R.id.txt_title_item_function_focus);
        }
    }

    /**
     * 条目拖动
     *
     * @param holder         holder
     * @param fromPosition   初始位置
     * @param targetPosition 目标位置
     */
    @Override
    public void onItemMove(RecyclerView.ViewHolder holder, int fromPosition, int targetPosition) {
        if (fromPosition < dataSet.size() && targetPosition < dataSet.size()) {
            Collections.swap(dataSet, fromPosition, targetPosition);
            notifyItemMoved(fromPosition, targetPosition);
        }
    }

    /**
     * 条目选中
     */
    @Override
    public void onItemSelect(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0.8f);
        holder.itemView.setScaleY(0.8f);
    }

    /**
     * 条目清空
     *
     * @param holder holder
     */
    @Override
    public void onItemClear(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(1.0f);
        holder.itemView.setScaleY(1.0f);
    }

    /**
     * 条目放开
     *
     * @param holder holder
     */
    @Override
    public void onItemDismiss(RecyclerView.ViewHolder holder) {

    }
}