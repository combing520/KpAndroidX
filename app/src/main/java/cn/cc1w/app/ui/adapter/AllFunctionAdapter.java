package cn.cc1w.app.ui.adapter;

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

import cn.cc1w.app.ui.entity.FunctionEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 所有的 功能区 Adapter
 * @author kpinfo
 */
public class AllFunctionAdapter extends RecyclerView.Adapter<AllFunctionAdapter.ViewHolder> {
    private final List<FunctionEntity.ItemFunctionEntity> dataSet = new ArrayList<>();
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
    public void setData(List<FunctionEntity.ItemFunctionEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应位置
     * @return 条目
     */
    public FunctionEntity.ItemFunctionEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 获取数据集合
     *
     * @return 数据集合
     */
    public List<FunctionEntity.ItemFunctionEntity> getDataSet() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_all_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FunctionEntity.ItemFunctionEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
        AppUtil.loadMenuImg(entity.getPic_path(), holder.iconImg);
        if (entity.isAttention()) {
            AppUtil.loadRes(R.mipmap.ic_select_function, holder.smallImg);
        } else {
            AppUtil.loadRes(R.mipmap.ic_increate_function, holder.smallImg);
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != listener && !entity.isAttention()) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView smallImg;
        private final ImageView iconImg;
        private final TextView titleTv;

        public ViewHolder(View itemView) {
            super(itemView);
            smallImg = itemView.findViewById(R.id.img_small_function_all);
            iconImg = itemView.findViewById(R.id.img_logo_function_all);
            titleTv = itemView.findViewById(R.id.txt_name_function_all);
        }
    }
}