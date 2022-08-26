package cn.cc1w.app.ui.adapter.home;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 开屏置顶的子 Adapter
 * @author kpinfo
 */
public class HomeNormalNewsPhotosSubAdapter extends RecyclerView.Adapter<HomeNormalNewsPhotosSubAdapter.ViewHolder> {
    private final List<String> dataSet = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setData(List<String> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     */
    public String getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_normal_photos_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String entity = getItem(position);
        AppUtil.loadNewsImg(entity, holder.img);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }
}