package cn.cc1w.app.ui.adapter;


import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.ItemSelectEntity;
import cn.cc1w.app.ui.R;

/**
 * 选中的Adapter
 *
 * @author kpinfo
 */

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {
    private final List<ItemSelectEntity> dataSet = new ArrayList<>();
    private int selectPos = 0;

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemSelectEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public ItemSelectEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_choose_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ItemSelectEntity entity = getItem(position);
        holder.titleTv.setText(entity.getTitle());
        holder.cardNumberTv.setText(entity.getCardNumber());
        if (selectPos == position) {
            holder.itemSelectTv.setVisibility(View.VISIBLE);
        } else {
            holder.itemSelectTv.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            selectPos = position;
            for (int i = 0; i < dataSet.size(); i++) {
                if (selectPos == i) {
                    entity.setSelect(true);
                } else {
                    entity.setSelect(false);
                }
            }
            notifyDataSetChanged();
        });
    }

    /**
     * 获取选中的条目位置
     *
     * @return 选中的pos
     */
    public int getSelectPos() {
        return selectPos;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final TextView itemSelectTv;
        private final TextView cardNumberTv;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_bank_choose);
            itemSelectTv = itemView.findViewById(R.id.txt_item_bank_choose_select);
            cardNumberTv = itemView.findViewById(R.id.txt_item_cardNumber_bank_choose);
        }
    }
}