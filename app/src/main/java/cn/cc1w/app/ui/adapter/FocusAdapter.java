package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.ItemFocusEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 关注 Adapter
 *
 * @author kpinfo
 */
public class FocusAdapter extends RecyclerView.Adapter<FocusAdapter.ViewHolder> {
    private final List<ItemFocusEntity.DataBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemFocusEntity.DataBean> list) {
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
    public void addData(List<ItemFocusEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
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
    public ItemFocusEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_focus_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemFocusEntity.DataBean entity = getItem(position);
        AppUtil.loadNetworkImg(entity.getHead_pic_path(), holder.avatarImg);
        holder.usernameTv.setText(TextUtils.isEmpty(entity.getNickname()) ? "" : entity.getNickname());
        holder.describeTv.setText(TextUtils.isEmpty(entity.getTag()) ? "" : entity.getTag());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView avatarImg;
        private final TextView usernameTv;
        private final TextView describeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.img_item_focus);
            usernameTv = itemView.findViewById(R.id.txt_item_username_focus);
            describeTv = itemView.findViewById(R.id.txt_item_describe_focus);
        }
    }
}