package cn.cc1w.app.ui.adapter.broke;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HotTopicEntity;
import cn.cc1w.app.ui.ui.home.record.PaikewTopicActivity;
import cn.cc1w.app.ui.R;

/**
 * 拍客热门中的标签
 * @author kpinfo
 */
public class PaiKewHotTopicAdapter extends RecyclerView.Adapter<PaiKewHotTopicAdapter.ViewHolder> {
    private final List<HotTopicEntity.ItemHotTopicEntity> dataSet = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setData(List<HotTopicEntity.ItemHotTopicEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     */
    public HotTopicEntity.ItemHotTopicEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_topic_hot_paikew_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HotTopicEntity.ItemHotTopicEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getTopic_name()) ? "" : entity.getTopic_name());
        holder.itemView.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setClass(context, PaikewTopicActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.TAG_TOPIC, entity.getTopic_id());
                intent.putExtra(Constant.TAG_TITLE, entity.getTopic_name());
                Constant.TOPIC_ID = entity.getTopic_id();
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

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_tag_topic_hot_paikew);
        }
    }
}