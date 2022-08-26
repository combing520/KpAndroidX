package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.FunctionEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * @author kpinfo
 * on 2020-12-14
 * 7。0+ 功能区域--首页
 */
public class FunctionNewAdapter extends RecyclerView.Adapter<FunctionNewAdapter.ViewHolder> {
    private final List<FunctionEntity.ItemFunctionEntity> mDataSet = new ArrayList<>();
    private OnItemClickListener mListener;

    public void setData(List<FunctionEntity.ItemFunctionEntity> list) {
        if (null != list && !list.isEmpty()) {
            mDataSet.clear();
            mDataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public FunctionEntity.ItemFunctionEntity getItem(int pos) {
        return mDataSet.get(pos);
    }

    public void setOnFunctionClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_function_item_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FunctionEntity.ItemFunctionEntity item = getItem(position);
        AppUtil.loadMenuImg(item.getPic_path(), holder.image);
        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img);
        }
    }
}