package cn.cc1w.app.ui.adapter.topic;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.RecommendTopicEntity;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PhotoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 热门推荐的信息的子条目
 *
 * @author kpinfo
 */
public class SubRecommendTopicAdapter extends RecyclerView.Adapter<SubRecommendTopicAdapter.ViewHolder> {
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_PICTURE = 2;
    private final List<RecommendTopicEntity.ItemRecommendTopicEntity.ShootsBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<RecommendTopicEntity.ItemRecommendTopicEntity.ShootsBean> list) {
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
    public RecommendTopicEntity.ItemRecommendTopicEntity.ShootsBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_topic_sub_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        RecommendTopicEntity.ItemRecommendTopicEntity.ShootsBean entity = getItem(position);
        if (entity.getType() == TYPE_VIDEO) {
            holder.playerImg.setVisibility(View.VISIBLE);
        } else if (entity.getType() == TYPE_PICTURE) {
            holder.playerImg.setVisibility(View.GONE);
        }
        AppUtil.loadPaikewSmallPic(entity.getCover(), holder.postImg);
        holder.itemView.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (entity.getType() == TYPE_VIDEO) {
                    intent.setClass(context, PaikewVideoDetailActivity.class);
                    intent.putExtra(Constant.TAG_ID, entity.getId());
                    context.startActivity(intent);
                } else if (entity.getType() == TYPE_PICTURE) {
                    intent.setClass(context, PhotoDetailActivity.class);
                    intent.putExtra(Constant.TAG_ID, entity.getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImg;
        private final ImageView playerImg;

        public ViewHolder(View itemView) {
            super(itemView);
            postImg = itemView.findViewById(R.id.img_recommend_sub_post);
            playerImg = itemView.findViewById(R.id.img_recommend_sub_play);
        }
    }
}
