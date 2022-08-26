package cn.cc1w.app.ui.adapter.home;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 应用号中的 视频组的子 Adapter
 *
 * @author kpinfo
 */
public class HomeVideoListSubAdapter extends RecyclerView.Adapter<HomeVideoListSubAdapter.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setData(List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     */
    public HomeNewsEntity.ItemHomeNewsEntity.NewsBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_group_sub_home_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HomeNewsEntity.ItemHomeNewsEntity.NewsBean entity = getItem(position);
        holder.titleTv.setText(entity.getTitle());
        AppUtil.loadBannerImg(entity.getPic_path(), holder.coverIv);
        holder.itemView.setOnClickListener(v -> IntentUtil.startActivity2DetailWithEntity(holder.itemView.getContext(), entity));
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