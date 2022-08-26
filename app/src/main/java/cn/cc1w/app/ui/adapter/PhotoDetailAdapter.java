package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 照片详情的Adapter
 * @author kpinfo
 */
public class PhotoDetailAdapter extends RecyclerView.Adapter<PhotoDetailAdapter.ViewHolder> {
    private final ArrayList<String> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<String> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应的位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public String getItem(int pos) {
        return dataSet.get(pos);
    }

    public ArrayList<String> getDataSet() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_detail_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppUtil.loadPaikewSmallPic(dataSet.get(position), holder.img);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_photo_detail);
        }
    }
}