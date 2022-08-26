package cn.cc1w.app.ui.adapter.home;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 首页所有的频道
 *
 * @author kpinfo
 */
public class HomeAllChannelAdapter extends RecyclerView.Adapter<HomeAllChannelAdapter.ViewHolder> {
    private final List<HomeChannelEntity.ItemHomeChannelEntity> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * 设置数据
     */
    public void setData(List<HomeChannelEntity.ItemHomeChannelEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取数据集合
     */
    public List<HomeChannelEntity.ItemHomeChannelEntity> getDataList() {
        return dataSet;
    }

    /**
     * 获取对应位置条目
     */
    public HomeChannelEntity.ItemHomeChannelEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 设置监听器
     */
    public void setOnAllChannelItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_all_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeChannelEntity.ItemHomeChannelEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
        if (entity.isAttention()) {
            AppUtil.loadRes(R.mipmap.ic_select_function, holder.stateImg);
        } else {
            AppUtil.loadRes(R.mipmap.ic_increate_function, holder.stateImg);
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != listener && !entity.isAttention()) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final ImageView stateImg;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_item_channel_all);
            stateImg = itemView.findViewById(R.id.img_item_channel_all);
        }
    }
}