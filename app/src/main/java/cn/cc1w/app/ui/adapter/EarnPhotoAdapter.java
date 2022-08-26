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
import cn.cc1w.app.ui.entity.ItemPhotoRecordEntity;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PhotoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 收益照片
 * @author kpinfo
 */
public class EarnPhotoAdapter extends RecyclerView.Adapter<EarnPhotoAdapter.ViewHolder> {
    private final List<ItemPhotoRecordEntity.DataBean> dataSet = new ArrayList<>();
    private static final int TYPE_VIDEO = 1;

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemPhotoRecordEntity.DataBean> list) {
        dataSet.clear();
        dataSet.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param list 数据源
     */
    public void addData(List<ItemPhotoRecordEntity.DataBean> list) {
        dataSet.addAll(list);
        notifyDataSetChanged();
    }

    public List<ItemPhotoRecordEntity.DataBean> getList() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earn_photo_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemPhotoRecordEntity.DataBean entity = getItem(position);
        AppUtil.loadPaikewBigPic(entity.getCover(), holder.img);
        holder.priseTv.setText(String.valueOf(entity.getPraise_number()).concat("赞"));
        holder.itemView.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (entity.getType() == TYPE_VIDEO) { // 视频
                    intent.setClass(context, PaikewVideoDetailActivity.class);
                    holder.playImg.setVisibility(View.VISIBLE);
                } else {
                    intent.setClass(context, PhotoDetailActivity.class);
                    holder.playImg.setVisibility(View.GONE);
                }
                intent.putExtra(Constant.TAG_ID, entity.getId());
                context.startActivity(intent);
            }
        });
    }

    public ItemPhotoRecordEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView priseTv;
        private final ImageView playImg;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_path_earn_photo);
            priseTv = itemView.findViewById(R.id.txt_earn_photo);
            playImg = itemView.findViewById(R.id.img_video_earn_photo);
        }
    }
}