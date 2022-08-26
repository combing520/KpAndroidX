package cn.cc1w.app.ui.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
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

import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 拍客中的 视频 Adapter
 *
 * @author kpinfo
 */
public class RecordVideoAdapter extends RecyclerView.Adapter<RecordVideoAdapter.ViewHolder> {
    private final List<ItemVideoListEntity.DataBean> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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

    /**
     * 获取对应位置条目
     *
     * @param pos 对应位置
     * @return 对应位置条目
     */
    public ItemVideoListEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 获取数据集合
     *
     * @return 数据集合
     */
    public List<ItemVideoListEntity.DataBean> getList() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_record_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        ItemVideoListEntity.DataBean entity = getItem(position);
        if (null != entity) {
            AppUtil.loadPaikewBigPic(entity.getCover(), holder.img);
            holder.titleTv.setText(TextUtils.isEmpty(entity.getTitle()) ? "" : entity.getTitle());
            holder.clickNumberTv.setText(String.valueOf(entity.getClick_number()).concat("次播放"));
            holder.priseTv.setText(String.valueOf(entity.getPraise_number()).concat("赞"));
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
        private final TextView clickNumberTv;
        private final TextView priseTv;
        private final ConstraintLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_video_record);
            titleTv = itemView.findViewById(R.id.txt_title_video_record);
            clickNumberTv = itemView.findViewById(R.id.txt_cnt_video_record);
            priseTv = itemView.findViewById(R.id.txt_prise_cnt_video_record);
            container = itemView.findViewById(R.id.container_record);
        }
    }
}
