package cn.cc1w.app.ui.adapter.home;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PhotoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 首页拍客中的视频和图片的 adapter
 *
 * @author kpinfo
 */
public class HomePaikewPicAndVideoAdapter extends RecyclerView.Adapter<HomePaikewPicAndVideoAdapter.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> dataSet = new ArrayList<>();
    private final Context context;
    private long lastTime;
    private static final String TYPE_PIC = "shootphoto";
    private static final String TYPE_VIDEO = "shootvideo";

    public HomePaikewPicAndVideoAdapter(Context context) {
        this.context = context;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 设置数据
     */
    public void setData(List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     */
    public HomeNewsEntity.ItemHomeNewsEntity.NewsBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_topic_sub_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        HomeNewsEntity.ItemHomeNewsEntity.NewsBean entity = getItem(position);
        String itemType = entity.getIn_type();
        if (TextUtils.equals(TYPE_PIC, itemType)) {
            holder.playBtnImg.setVisibility(View.GONE);
        } else if (TextUtils.equals(TYPE_VIDEO, itemType)) {
            holder.playBtnImg.setVisibility(View.VISIBLE);
        }
        AppUtil.loadPaikewSmallPic(entity.getPic_path(), holder.postImg);
        holder.itemView.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (TextUtils.equals(TYPE_PIC, itemType)) {
                    intent.setClass(context, PhotoDetailActivity.class);
                    intent.putExtra(Constant.TAG_ID, Integer.parseInt(entity.getNews_id()));
                    context.startActivity(intent);
                } else if (TextUtils.equals(TYPE_VIDEO, itemType)) {
                    intent.setClass(context, PaikewVideoDetailActivity.class);
                    intent.putExtra(Constant.TAG_ID, Integer.parseInt(entity.getNews_id()));
                    context.startActivity(intent);
                }
            }
            lastTime = currentTime;
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImg;
        private final ImageView playBtnImg;

        public ViewHolder(View itemView) {
            super(itemView);
            postImg = itemView.findViewById(R.id.img_recommend_sub_post);
            playBtnImg = itemView.findViewById(R.id.img_recommend_sub_play);
        }
    }
}