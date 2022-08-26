package cn.cc1w.app.ui.adapter;

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

import cn.cc1w.app.ui.entity.ItemTelevisonListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 视频推荐列表
 * @author kpinfo
 */

public class TelevisionAdapter extends RecyclerView.Adapter<TelevisionAdapter.ViewHolder> {
    private final List<ItemTelevisonListEntity.DataBean> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置数据
     *
     * @param list 数据集合
     */
    public void setData(List<ItemTelevisonListEntity.DataBean> list) {
        dataSet.clear();
        dataSet.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param list 数据集合
     */
    public void addData(List<ItemTelevisonListEntity.DataBean> list) {
        dataSet.addAll(list);
        notifyDataSetChanged();
    }

    public ItemTelevisonListEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_television_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        ItemTelevisonListEntity.DataBean entity = getItem(position);
        if (null != entity) {
            AppUtil.loadNewsImg(entity.getLogo_path(), holder.img);
            holder.titleTv.setText(TextUtils.isEmpty(entity.getTitle()) ? "" : entity.getTitle());
            holder.container.setOnClickListener(v -> {
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
        private final ImageView img;
        private final TextView titleTv;
        private final RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_television);
            titleTv = itemView.findViewById(R.id.txt_television);
            container = itemView.findViewById(R.id.container_television);
        }
    }
}
