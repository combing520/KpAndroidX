package cn.cc1w.app.ui.adapter.home;

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
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 新闻组
 *
 * @author kpinfo
 */
public class HomeNormalGroupSubAdapter extends RecyclerView.Adapter<HomeNormalGroupSubAdapter.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> dataSet = new ArrayList<>();
    private long lastTime;

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

    public HomeNormalGroupSubAdapter() {
        lastTime = System.currentTimeMillis();
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_normal_group_home_recycle_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeNewsEntity.ItemHomeNewsEntity.NewsBean item = getItem(position);
        AppUtil.loadNewsImg(item.getPic_path(), holder.img);
        holder.titleTv.setText(item.getTitle());
        holder.nameTv.setText(item.getName());
        holder.timeTv.setText(item.getCreate_time());
        holder.itemView.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                IntentUtil.startActivity2DetailWithEntity(holder.itemView.getContext(), item);
            }
            lastTime = currentTime;
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView img;
        private final TextView titleTv;
        private final TextView nameTv;
        private final TextView timeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.cover);
            titleTv = itemView.findViewById(R.id.title);
            nameTv = itemView.findViewById(R.id.name);
            timeTv = itemView.findViewById(R.id.time);
        }
    }
}