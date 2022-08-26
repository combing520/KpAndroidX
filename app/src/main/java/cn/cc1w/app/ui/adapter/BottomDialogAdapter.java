package cn.cc1w.app.ui.adapter;

import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;

/**
 * @author kpinfo
 * on 2021-08-23
 */
public class BottomDialogAdapter extends RecyclerView.Adapter<BottomDialogAdapter.ViewHolder> {
    private final List<String> mDataSet = new ArrayList<>();
    private OnItemClickListener listener;

    public void setData(List<String> list) {
        if (list != null && !list.isEmpty()) {
            mDataSet.clear();
            mDataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public String getItem(int pos) {
        return mDataSet.get(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_bottom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = getItem(position);
        holder.tv.setText(item);
        if (position == 0) {
            holder.tv.setTextColor(Color.parseColor("#333333"));
        } else {
            holder.tv.setTextColor(Color.parseColor("#3D7FFF"));
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}