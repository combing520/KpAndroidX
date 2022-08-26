package cn.cc1w.app.ui.adapter.advertisement;

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
import cn.cc1w.app.ui.entity.AppConfigEntity;
import cn.cc1w.app.ui.entity.PostAdEntity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoPlayDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;

/**
 * 新闻详情中的 广告 adapter
 *
 * @author kpinfo
 */
public class PostBottomAdvertisementNewAdapter extends RecyclerView.Adapter<PostBottomAdvertisementNewAdapter.ViewHolder> {
    private final List<PostAdEntity.DataBean> dataSet = new ArrayList<>();
    private static final String TYPE_PIC_AD = "1";
    private static final String TYPE_GIF_AD = "2";
    private static final String TYPE_VIDEO_AD = "3";
    private long lastTime;
    private final Context context;

    public PostBottomAdvertisementNewAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<PostAdEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加条目
     */
    public void addItem(PostAdEntity.DataBean item) {
        if (null != item) {
            dataSet.add(item);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对于位置上的条目
     */
    public PostAdEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 获取list
     */
    public List<PostAdEntity.DataBean> getList() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_bottom_new_detail_news_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        PostAdEntity.DataBean postAdEntity = getItem(position);
        if (null != postAdEntity) {
            AppUtil.loadNewsBottomAdvertisement(postAdEntity.getPic_path(), holder.imageView);
            holder.imageView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String newsId = postAdEntity.getId();
                    String type = postAdEntity.getType();
                    String url = postAdEntity.getUrl();
                    String title = postAdEntity.getTitle();
                    String summary = postAdEntity.getDesc();
                    String picPath = postAdEntity.getPic_path();
                    if (!TextUtils.isEmpty(type)) {
                        if (TextUtils.equals(type, TYPE_PIC_AD) || TextUtils.equals(type, TYPE_GIF_AD)) { // 图片
                            if (!TextUtils.isEmpty(url)) {
                                AppConfigEntity.AppConfigDetail config = SharedPreferenceUtil.getAppConfigInfo();
                                if (config != null && config.isAllow_jump_url()) {
                                    List<String> whiteList = config.getApp_white_list();
                                    boolean result = false;
                                    for (String s : whiteList) {
                                        LogUtil.d("Bottom -- onBindViewHolder--s = " + s);
                                        if (url.startsWith(s)) {
                                            result = true;
                                            break;
                                        }
                                    }
                                    if (result) {
                                        Intent intent = new Intent();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setClass(context, UrlDetailActivity.class);
                                        intent.putExtra(Constant.TAG_URL, url);
                                        intent.putExtra(Constant.TAG_TITLE, TextUtils.isEmpty(title) ? "" : title);
                                        intent.putExtra(Constant.TAG_ID, newsId);
                                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(summary) ? "" : summary);
                                        context.startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(context, UrlDetailActivity.class);
                                    intent.putExtra(Constant.TAG_URL, url);
                                    intent.putExtra(Constant.TAG_TITLE, TextUtils.isEmpty(title) ? "" : title);
                                    intent.putExtra(Constant.TAG_ID, newsId);
                                    intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(summary) ? "" : summary);
                                    context.startActivity(intent);
                                }
                            }
                        } else if (TextUtils.equals(type, TYPE_VIDEO_AD)) {
                            if (!TextUtils.isEmpty(url)) {
                                Intent intent = new Intent();
                                intent.setClass(context, VideoPlayDetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("videoUrl", url);
                                intent.putExtra("videoPostUrl", picPath);
                                context.startActivity(intent);
                            }
                        }
                    }
                }
                lastTime = currentTime;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.banner_detail_news);
        }
    }
}