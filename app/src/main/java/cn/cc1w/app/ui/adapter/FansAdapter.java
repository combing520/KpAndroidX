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

import cn.cc1w.app.ui.entity.ItemFansEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 粉丝 Adapter
 *
 * @author kpinfo
 */
public class FansAdapter extends RecyclerView.Adapter<FansAdapter.ViewHolder> {
    private final List<ItemFansEntity.DataBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemFansEntity.DataBean> list) {
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
    public void addData(List<ItemFansEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public ItemFansEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fans_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemFansEntity.DataBean entity = getItem(position);
        AppUtil.loadNetworkImg(entity.getHead_pic_path(), holder.avatarImg);
        holder.usernameTv.setText(TextUtils.isEmpty(entity.getNickname()) ? "" : entity.getNickname());
        holder.describeTv.setText(TextUtils.isEmpty(entity.getTag()) ? "这个用户很懒,什么东西都没有留下~" : entity.getTag());
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
            avatarImg = itemView.findViewById(R.id.img_item_fans);
            usernameTv = itemView.findViewById(R.id.txt_item_username_fans);
            describeTv = itemView.findViewById(R.id.txt_item_describe_fans);
        }
    }
}