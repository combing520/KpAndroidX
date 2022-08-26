package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsListEntity;
import cn.cc1w.app.ui.ui.home.apps.FavoriteAppsAddActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 推荐APP Adapter
 * @author kpinfo
 */
public class RecommendAppsAdapter extends RecyclerView.Adapter<RecommendAppsAdapter.ViewHolder> {
    private final List<AppsListEntity.ItemAppsListEntity> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<AppsListEntity.ItemAppsListEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public AppsListEntity.ItemAppsListEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apps_recommend_favorite_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppsListEntity.ItemAppsListEntity entity = getItem(position);
        AppUtil.loadNewsGroupImg(entity.getBg_path(), holder.img);
        holder.img.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setClass(context, FavoriteAppsAddActivity.class);
                intent.putExtra(Constant.TAG_ID, entity.getId());
                intent.putExtra(Constant.TAG_GROUP_ID,entity.getGroup_id());
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
        private final RoundAngleImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_recommend_favorite);
        }
    }
}