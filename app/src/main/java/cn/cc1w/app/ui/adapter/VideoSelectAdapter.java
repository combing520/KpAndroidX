package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import cn.cc1w.app.ui.R;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.VideoGroupEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;

/**
 * 选中的视频的adpater
 * @author kpinfo
 */
public class VideoSelectAdapter extends RecyclerView.Adapter<VideoSelectAdapter.ViewHolder> {
    private final List<VideoGroupEntity.ItemVideoGroupEntity.VideogroupBean> dataSet = new ArrayList<>();
    private OnItemClickListener listener;
    private final Drawable selectDrawable;
    private final Drawable normalDrawable;
    private int selectPos = 0;

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<VideoGroupEntity.ItemVideoGroupEntity.VideogroupBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应位置
     * @return 对应条目
     */
    public VideoGroupEntity.ItemVideoGroupEntity.VideogroupBean getItem(int pos) {
        return dataSet.get(pos);
    }

    public VideoSelectAdapter(Context context) {
        selectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_container_video_select);
        normalDrawable = ContextCompat.getDrawable(context, R.drawable.bg_container_video_normal);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_select_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        VideoGroupEntity.ItemVideoGroupEntity.VideogroupBean entity = getItem(position);
        holder.titleTv.setText("视频".concat(String.valueOf((position + 1))));
        holder.describeTv.setText(TextUtils.isEmpty(entity.getTitle())?"":entity.getTitle());
        if (selectPos == position) {
            holder.titleTv.setBackground(selectDrawable);
        } else {
            holder.titleTv.setBackground(normalDrawable);
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != listener) {
                listener.onItemClick(v, position);
            }
        });
    }

    /**
     * 设置选中位置
     *
     * @param pos 位置
     */
    public void setSelectPos(int pos) {
        selectPos = pos;
        notifyDataSetChanged();
    }

    /**
     * 获取 选中位置
     *
     * @return 选中位置
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
        private final TextView describeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_name_video_group);
            describeTv = itemView.findViewById(R.id.txt_item_describe_video_group);
        }
    }
}