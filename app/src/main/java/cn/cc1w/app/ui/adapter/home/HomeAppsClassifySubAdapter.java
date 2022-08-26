package cn.cc1w.app.ui.adapter.home;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.apps.FavoriteAppsAddActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 新闻组 sub Adapter
 * @author kpinfo
 */
public class HomeAppsClassifySubAdapter extends RecyclerView.Adapter<HomeAppsClassifySubAdapter.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> dataSet = new ArrayList<>();
    private long lastTime;
    private final Context context;

    public HomeAppsClassifySubAdapter(Context context) {
        lastTime = System.currentTimeMillis();
        this.context = context;
    }

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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apps_recommend_favorite_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HomeNewsEntity.ItemHomeNewsEntity.NewsBean entity = getItem(position);
        AppUtil.loadNewsGroupImg(entity.getBg_path(), holder.img);
        holder.container.setOnClickListener(rippleView -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                if (!TextUtils.isEmpty(entity.getName()) && null != context) {
                    Bundle bundle = new Bundle();
                    bundle.putString("topTitle", entity.getName());
                    IntentUtil.startActivity(context, FavoriteAppsAddActivity.class, bundle);
                }
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
        private final ConstraintLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_recommend_favorite);
            container = itemView.findViewById(R.id.container_recommend_favorite);
        }
    }
}