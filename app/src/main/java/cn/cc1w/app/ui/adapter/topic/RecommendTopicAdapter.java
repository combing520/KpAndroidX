package cn.cc1w.app.ui.adapter.topic;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.RecommendTopicEntity;
import cn.cc1w.app.ui.ui.home.record.PaikewTopicActivity;
import cn.cc1w.app.ui.R;

/**
 * 推荐话题
 * @author kpinfo
 */

public class RecommendTopicAdapter extends RecyclerView.Adapter<RecommendTopicAdapter.ViewHolder> {
    private final List<RecommendTopicEntity.ItemRecommendTopicEntity> dataSet = new ArrayList<>();
    private final Context context;

    public RecommendTopicAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<RecommendTopicEntity.ItemRecommendTopicEntity> list) {
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
     * @return 对应条目
     */
    public RecommendTopicEntity.ItemRecommendTopicEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_recommend_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecommendTopicEntity.ItemRecommendTopicEntity entity = getItem(position);
        holder.titleTv.setText(TextUtils.isEmpty(entity.getTopic_name()) ? "" : entity.getTopic_name());
        if (null != entity.getShoots() && !entity.getShoots().isEmpty()) {
            holder.recyclerView.setVisibility(View.VISIBLE);
            SubRecommendTopicAdapter subRecommendTopicAdapter = new SubRecommendTopicAdapter();
            subRecommendTopicAdapter.setData(entity.getShoots());
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.recyclerView.setLayoutManager(manager);
            holder.recyclerView.setAdapter(subRecommendTopicAdapter);
        } else {
            holder.recyclerView.setVisibility(View.GONE);
        }
        holder.titleLayout.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(context, PaikewTopicActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constant.TAG_TOPIC, entity.getTopic_id());
            intent.putExtra(Constant.TAG_TITLE, entity.getTopic_name());
            Constant.TOPIC_ID = entity.getTopic_id();
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final RecyclerView recyclerView;
        private final LinearLayout titleLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_topic_recommend);
            recyclerView = itemView.findViewById(R.id.list_topic_recommend);
            titleLayout = itemView.findViewById(R.id.ll_item_title_topic_recommend);
        }
    }
}
