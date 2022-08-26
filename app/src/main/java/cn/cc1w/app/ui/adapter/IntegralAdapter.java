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
import cn.cc1w.app.ui.entity.SignInfoEntity;

/**
 * 积分 Adapter
 * @author kpinfo
 */
public class IntegralAdapter extends RecyclerView.Adapter<IntegralAdapter.ViewHolder> {
    private final List<SignInfoEntity.SignInfo.CreditRecordBean> dataSet = new ArrayList<>();

    /**
     * 设置列表数据
     *
     * @param list 数据源
     */
    public void setData(List<SignInfoEntity.SignInfo.CreditRecordBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public SignInfoEntity.SignInfo.CreditRecordBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_integral_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SignInfoEntity.SignInfo.CreditRecordBean item = getItem(position);
        holder.reasonTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        holder.detailTv.setText(TextUtils.isEmpty(item.getRemark()) ? "" : item.getRemark());
        holder.describeTv.setText((TextUtils.isEmpty(item.getRecord_sum()) ? "" : item.getRecord_sum()) + " / " + (TextUtils.isEmpty(item.getTop_credit()) ? "" : item.getTop_credit()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView reasonTv;
        private final TextView detailTv;
        private final TextView describeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            reasonTv = itemView.findViewById(R.id.txt_item_reason_integral);
            detailTv = itemView.findViewById(R.id.txt_item_detail_integral);
            describeTv = itemView.findViewById(R.id.txt_item_describe_integral);
        }
    }
}