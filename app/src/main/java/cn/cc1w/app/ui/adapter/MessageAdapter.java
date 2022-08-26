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

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemMessageEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 我的通知 Adapter
 *
 * @author kpinfo
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final List<ItemMessageEntity.DataBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemMessageEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public ItemMessageEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemMessageEntity.DataBean item = getItem(position);
        holder.typeTv.setText(TextUtils.isEmpty(item.getType_text()) ? "" : item.getType_text());
        holder.describeTv.setText(TextUtils.isEmpty(item.getContent()) ? "" : item.getContent());
        holder.timeTv.setText(TextUtils.isEmpty(item.getCreate_time()) ? "" : item.getCreate_time());
        AppUtil.loadAvatarImg(Constant.CW_AVATAR, holder.avatarImg);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView avatarImg;
        private final TextView typeTv;
        private final TextView timeTv;
        private final TextView describeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.img_avatar_item_message);
            typeTv = itemView.findViewById(R.id.txt_type_item_message);
            timeTv = itemView.findViewById(R.id.txt_time_item_message);
            describeTv = itemView.findViewById(R.id.txt_describe_item_message);
        }
    }
}