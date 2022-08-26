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
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * @author kpinfo
 * @date 2018/9/12
 * 收益视频
 */
public class EarnVideoAdapter extends RecyclerView.Adapter<EarnVideoAdapter.ViewHolder> {
    private final List<ItemVideoListEntity.DataBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemVideoListEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param list 数据源
     */
    public void addData(List<ItemVideoListEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_earn_recycle, parent, false));
    }

    public ItemVideoListEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    public List<ItemVideoListEntity.DataBean> getList() {
        return dataSet;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemVideoListEntity.DataBean entity = getItem(position);
        AppUtil.loadPaikewBigPic(entity.getCover(), holder.img);
        holder.playCntTv.setText(String.valueOf(entity.getClick_number()).concat("次播放"));
        holder.itemView.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setClass(context, PaikewVideoDetailActivity.class);
                intent.putExtra(Constant.TAG_ID, entity.getId());
                intent.putExtra("isUserPaikewWork", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView playCntTv;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_earn);
            playCntTv = itemView.findViewById(R.id.txt_cnt_play_earn);
        }
    }
}