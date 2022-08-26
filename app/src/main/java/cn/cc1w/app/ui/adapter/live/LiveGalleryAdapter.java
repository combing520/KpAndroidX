package cn.cc1w.app.ui.adapter.live;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.detail.ShowWebViewGalleryDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 直播图集
 *
 * @author kpinfo
 */
public class LiveGalleryAdapter extends RecyclerView.Adapter<LiveGalleryAdapter.ViewHolder> {
    private final List<LiveHostEntity.LiveHostItemEntity.JsonBean> dataSet = new ArrayList<>();
    private long lastTime;

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<LiveHostEntity.LiveHostItemEntity.JsonBean> list) {
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
    public void addData(List<LiveHostEntity.LiveHostItemEntity.JsonBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public LiveGalleryAdapter() {
        lastTime = System.currentTimeMillis();
    }

    /**
     * 获取对应位置条目
     */
    public LiveHostEntity.LiveHostItemEntity.JsonBean getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 获取图片地址集合
     */
    private ArrayList<String> getPicList() {
        ArrayList<String> list = new ArrayList<>();
        for (LiveHostEntity.LiveHostItemEntity.JsonBean item : dataSet) {
            list.add(item.getPic_path());
        }
        return list;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_live, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        LiveHostEntity.LiveHostItemEntity.JsonBean entity = getItem(position);
        if (null != entity) {
            AppUtil.loadNewsImg(entity.getPic_path(), holder.postImg);
            holder.postImg.setOnClickListener(v -> {
                Context context = AppContext.getAppContext();
                long currentTime = System.currentTimeMillis();
                if (null != context && (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL)) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, ShowWebViewGalleryDetailActivity.class);
                    intent.putExtra("picList", getPicList());
                    intent.putExtra("selectPos", position);
                    context.startActivity(intent);
                }
                lastTime = currentTime;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImg;

        public ViewHolder(View itemView) {
            super(itemView);
            postImg = itemView.findViewById(R.id.img_live_gallery);
        }
    }
}
