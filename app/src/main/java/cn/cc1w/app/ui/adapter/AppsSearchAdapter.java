package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsListEntity;
import cn.cc1w.app.ui.ui.home.apps.AppDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 应用号搜索Adapter
 *
 * @author kpinfo
 */
public class AppsSearchAdapter extends RecyclerView.Adapter<AppsSearchAdapter.ViewHolder> {
    private final List<AppsListEntity.ItemAppsListEntity> dataSet = new ArrayList<>();
    private OnAppsItemClickListener listener;

    /**
     * 设置条目点击事件
     */
    public void setOnItemClickListener(OnAppsItemClickListener listener) {
        this.listener = listener;
    }

    public void cancelApps(int pos) {
        AppsListEntity.ItemAppsListEntity item = getItem(pos);
        if (item.isAttention()) {
            item.setAttention(false);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据
     *
     * @param list 数据集合
     */
    public void setData(List<AppsListEntity.ItemAppsListEntity> list) {
        if (!list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param list 数据集合
     */
    public void addData(List<AppsListEntity.ItemAppsListEntity> list) {
        if (!list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置的 条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public AppsListEntity.ItemAppsListEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 返回数据源
     *
     * @return 数据集合
     */
    public List<AppsListEntity.ItemAppsListEntity> getDataSet() {
        return dataSet;
    }

    /**
     * 关注或者打开 对应的位置的应用号 （如果未曾关注则关注，如果关注就打开）
     *
     * @param pos 对应的 位置
     */
    public void addApps(int pos) {
        AppsListEntity.ItemAppsListEntity item = getItem(pos);
        if (!item.isAttention()) {
            item.setAttention(true);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消关注
     *
     * @param pos 对应的 位置
     */
    public void cancleApps(int pos) {
        AppsListEntity.ItemAppsListEntity item = getItem(pos);
        if (item.isAttention()) {
            item.setAttention(false);
        }
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apps_search_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppsListEntity.ItemAppsListEntity item = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        holder.describeTv.setText(TextUtils.isEmpty(item.getSummary()) ? "" : item.getSummary());
        AppUtil.loadNewsGroupImg(item.getLogo_pic_path(), holder.icon);
        if (item.isAttention()) {
            AppUtil.loadRes(R.mipmap.ic_choose, holder.addImg);
        } else {
            AppUtil.loadRes(R.mipmap.ic_add, holder.addImg);
        }
        holder.addImg.setOnClickListener(v -> {
            if (null != listener) {
                listener.onAppsClick(position, v);
            }
        });
        holder.topContainer.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setClass(context, AppDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.TAG_ID, item.getId());
                intent.putExtra(Constant.TAG_TITLE, item.getName());
                intent.putExtra("group_id", item.getGroup_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final TextView describeTv;
        private final ImageView addImg;
        private final RoundAngleImageView icon;
        private final RelativeLayout topContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_title_item_app_list);
            describeTv = itemView.findViewById(R.id.txt_describe_item_app_list);
            addImg = itemView.findViewById(R.id.img_btn_add_item_app_list);
            icon = itemView.findViewById(R.id.img_item_app_list);
            topContainer = itemView.findViewById(R.id.ll_top_item_app);
        }
    }

    public interface OnAppsItemClickListener {
        void onAppsClick(int pos, View v);
    }
}