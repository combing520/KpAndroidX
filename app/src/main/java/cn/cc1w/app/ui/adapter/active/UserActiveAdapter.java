package cn.cc1w.app.ui.adapter.active;

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

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.UserActiveEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 用户中心的 活动
 *
 * @author kpinfo
 */
public class UserActiveAdapter extends RecyclerView.Adapter<UserActiveAdapter.ViewHolder> {
    private final List<UserActiveEntity.DataBean> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * 设置数据
     */
    public void setData(List<UserActiveEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     */
    public void addData(List<UserActiveEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 设置监听
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取对应位置条目
     */
    public UserActiveEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_usercenter_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        UserActiveEntity.DataBean item = getItem(position);
        if (null != item) {
            holder.titleTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
            AppUtil.loadBannerImg(item.getPic_path(), holder.postImg);
            holder.itemView.setOnClickListener(v -> {
                if (null != listener) {
                    listener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImg;
        private final TextView titleTv;

        public ViewHolder(View itemView) {
            super(itemView);
            postImg = itemView.findViewById(R.id.img_item_active_usercenter_recycle);
            titleTv = itemView.findViewById(R.id.title_item_active_usercenter_recycle);
        }
    }
}