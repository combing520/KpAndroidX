package cn.cc1w.app.ui.adapter.details_apps;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 新闻组 sub Adapter
 *
 * @author kpinfo
 */
public class PhotoGroupSubAdapter extends RecyclerView.Adapter<PhotoGroupSubAdapter.ViewHolder> {
    private final List<NewsEntity.DataBean.ItemNewsEntity> dataSet = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setData(List<NewsEntity.DataBean.ItemNewsEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     */
    public NewsEntity.DataBean.ItemNewsEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_group_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsEntity.DataBean.ItemNewsEntity entity = getItem(position);
        holder.titleTv.setText(entity.getTitle());
        AppUtil.loadNewsImg(entity.getPic_path(), holder.coverIv);
        holder.itemView.setOnClickListener(v -> IntentUtil.startActivity2DetailWithEntity2(holder.itemView.getContext(), entity));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final RoundAngleImageView coverIv;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title);
            coverIv = itemView.findViewById(R.id.cover);
        }
    }
}