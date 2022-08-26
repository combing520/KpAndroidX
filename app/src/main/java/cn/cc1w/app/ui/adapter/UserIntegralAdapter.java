package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.entity.ItemUserIntegralEntity;
import cn.cc1w.app.ui.ui.usercenter.integral.IntegralExchangeDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 我的积分 Adapter
 * @author kpinfo
 */
public class UserIntegralAdapter extends RecyclerView.Adapter<UserIntegralAdapter.ViewHolder> {
    private final List<ItemUserIntegralEntity> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemUserIntegralEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应位置
     * @return 对应条目
     */
    public ItemUserIntegralEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_integral_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemUserIntegralEntity entity = getItem(position);
        AppUtil.loadNetworkImg(entity.getPrizePath(), holder.img);
        holder.describeTv.setText(entity.getDescribe());
        final Context context = AppContext.getAppContext();
        if (null != context) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(context, IntegralExchangeDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView describeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_pic_integral_user);
            describeTv = itemView.findViewById(R.id.txt_item_describe_integral_user);
        }
    }
}