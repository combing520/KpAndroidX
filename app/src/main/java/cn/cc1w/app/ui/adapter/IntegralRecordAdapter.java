package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.IntegralRecordEntity;

/**
 * 积分兑换记录  Adapter
 *
 * @author kpinfo
 */
public class IntegralRecordAdapter extends RecyclerView.Adapter<IntegralRecordAdapter.ViewHolder> {
    private final List<IntegralRecordEntity> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<IntegralRecordEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public IntegralRecordEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_integral_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IntegralRecordEntity entity = getItem(position);
        holder.describeTv.setText(entity.getDescribe());
        holder.timeTx.setText(entity.getTime());
        holder.costTx.setText(entity.getMoney());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView describeTv;
        private final TextView timeTx;
        private final TextView costTx;

        public ViewHolder(View itemView) {
            super(itemView);
            describeTv = itemView.findViewById(R.id.txt_describe_record_integral);
            timeTx = itemView.findViewById(R.id.txt_time_record_integral);
            costTx = itemView.findViewById(R.id.txt_money_record_integral);
        }
    }
}
