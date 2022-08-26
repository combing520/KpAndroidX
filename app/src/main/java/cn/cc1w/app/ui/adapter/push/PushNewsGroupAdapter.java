package cn.cc1w.app.ui.adapter.push;

import android.content.Context;

import androidx.cardview.widget.CardView;
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

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.PushNewsGroupEntity;
import cn.cc1w.app.ui.ui.xg_push.view.PushAlbumGroupView;
import cn.cc1w.app.ui.ui.xg_push.view.PushAlbumView;
import cn.cc1w.app.ui.ui.xg_push.view.PushLiveView;
import cn.cc1w.app.ui.ui.xg_push.view.PushNewsGroupView;
import cn.cc1w.app.ui.ui.xg_push.view.PushNormalNewsView;
import cn.cc1w.app.ui.ui.xg_push.view.PushOtherView;
import cn.cc1w.app.ui.ui.xg_push.view.PushSpecialView;
import cn.cc1w.app.ui.ui.xg_push.view.PushUrlView;
import cn.cc1w.app.ui.ui.xg_push.view.PushVideoGroupView;
import cn.cc1w.app.ui.ui.xg_push.view.PushVideoView;

/**
 * 推送中的 新闻组的 Adapter
 */
public class PushNewsGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<PushNewsGroupEntity.ItemPushNewsGroupEntity> dataSet = new ArrayList<>();
    private final LayoutInflater mInflater;
    private static final int TYPE_NEWS_NORMAL = 100;
    private static final int TYPE_NEWS_GROUP = 200;
    private static final int TYPE_VIDEO = 300;
    private static final int TYPE_VIDEO_GROUP = 400;
    private static final int TYPE_ALBUM = 500;
    private static final int TYPE_LIVE = 600;
    private static final int TYPE_ACTIVE = 700;
    private static final int TYPE_SPECIAL = 800;
    private static final int TYPE_ALBUM_GROUP = 900;
    private static final int TYPE_OTHER = 1000;

    public PushNewsGroupAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据集合
     *
     * @param list 数据源
     */
    public void setData(List<PushNewsGroupEntity.ItemPushNewsGroupEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据集合
     *
     * @param list 数据源
     */
    public void addData(List<PushNewsGroupEntity.ItemPushNewsGroupEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_NEWS_NORMAL) {
            viewHolder = new NormalNewsHolder(mInflater.inflate(R.layout.item_news_group_normal_push, parent, false));
        } else if (viewType == TYPE_NEWS_GROUP) {
            viewHolder = new NewsGroupHolder(mInflater.inflate(R.layout.item_news_group_normal_push, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            viewHolder = new VideoHolder(mInflater.inflate(R.layout.item_news_group_video_push, parent, false));
        } else if (viewType == TYPE_VIDEO_GROUP) {
            viewHolder = new VideoGroupHolder(mInflater.inflate(R.layout.item_news_group_video_push, parent, false));
        } else if (viewType == TYPE_ALBUM) {
            viewHolder = new AlbumHolder(mInflater.inflate(R.layout.item_news_group_normal_push, parent, false));
        } else if (viewType == TYPE_LIVE) {
            viewHolder = new LiveHolder(mInflater.inflate(R.layout.item_news_group_video_push, parent, false));
        } else if (viewType == TYPE_ACTIVE) {
            viewHolder = new UrlHolder(mInflater.inflate(R.layout.item_news_group_normal_push, parent, false));
        } else if (viewType == TYPE_SPECIAL) {
            viewHolder = new SpecialHolder(mInflater.inflate(R.layout.item_news_group_normal_push, parent, false));
        } else if (viewType == TYPE_ALBUM_GROUP) {
            viewHolder = new AlbumGroupHolder(mInflater.inflate(R.layout.item_news_group_normal_push, parent, false));
        } else {
            viewHolder = new OtherHolder(mInflater.inflate(R.layout.item_news_group_normal_push, parent, false));
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(dataSet.get(position).getShow_type())) {
            return TYPE_NEWS_NORMAL;
        }
        if (TextUtils.equals(Constant.TYPE_NEWS_NORMAL, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_NORMAL;
        } else if (TextUtils.equals(Constant.TYPE_NEWS_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_GROUP;
        } else if (TextUtils.equals(Constant.TYPE_VIDEO_NORMAL, dataSet.get(position).getIn_type())) {
            return TYPE_VIDEO;
        } else if (TextUtils.equals(Constant.TYPE_VIDEO_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_VIDEO_GROUP;
        } else if (TextUtils.equals(Constant.TYPE_PHOTO, dataSet.get(position).getIn_type())) {
            return TYPE_ALBUM;
        } else if (TextUtils.equals(Constant.TYPE_LIVE, dataSet.get(position).getIn_type())) {
            return TYPE_LIVE;
        } else if (TextUtils.equals(Constant.TYPE_URL, dataSet.get(position).getIn_type())) {
            return TYPE_ACTIVE;
        } else if (TextUtils.equals(Constant.TYPE_TOPIC, dataSet.get(position).getIn_type())) {
            return TYPE_SPECIAL;
        } else if (TextUtils.equals(Constant.TYPE_PHOTO_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_ALBUM_GROUP;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        PushNewsGroupEntity.ItemPushNewsGroupEntity entity = dataSet.get(position);
        if (holder instanceof NormalNewsHolder) {
            PushNormalNewsView pushNormalNewsView = new PushNormalNewsView(context, holder, entity);
            pushNormalNewsView.initView();
        } else if (holder instanceof NewsGroupHolder) {
            PushNewsGroupView pushNewsGroupView = new PushNewsGroupView(context, holder, entity);
            pushNewsGroupView.initView();
        } else if (holder instanceof VideoHolder) {
            PushVideoView pushVideoView = new PushVideoView(context, holder, entity);
            pushVideoView.initView();
        } else if (holder instanceof VideoGroupHolder) {
            PushVideoGroupView pushVideoGroupView = new PushVideoGroupView(context, holder, entity);
            pushVideoGroupView.initView();
        } else if (holder instanceof AlbumHolder) {
            PushAlbumView pushAlbumView = new PushAlbumView(context, holder, entity);
            pushAlbumView.initView();
        } else if (holder instanceof LiveHolder) {
            PushLiveView pushLiveView = new PushLiveView(holder, entity);
            pushLiveView.initView();
        } else if (holder instanceof UrlHolder) {
            PushUrlView pushUrlView = new PushUrlView(context, position, holder, entity);
            pushUrlView.initView();
        } else if (holder instanceof SpecialHolder) {
            PushSpecialView pushSpecialView = new PushSpecialView(context, holder, entity);
            pushSpecialView.initView();
        } else if (holder instanceof AlbumGroupHolder) {
            PushAlbumGroupView pushAlbumGroupView = new PushAlbumGroupView(context, holder, entity);
            pushAlbumGroupView.initView();
        } else if (holder instanceof OtherHolder) {
            PushOtherView pushOtherView = new PushOtherView(context, holder, entity);
            pushOtherView.initView();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * 普通新闻
     */
    public static class NormalNewsHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        NormalNewsHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_normal_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_normal_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_normal_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_normal_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_normal_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_normal_push);
            container = itemView.findViewById(R.id.container_news_group_normal_push);
        }
    }

    /**
     * 新闻组
     */
    public static class NewsGroupHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        NewsGroupHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_normal_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_normal_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_normal_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_normal_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_normal_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_normal_push);
            container = itemView.findViewById(R.id.container_news_group_normal_push);
        }
    }

    /**
     * 视频
     */
    public static class VideoHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        VideoHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_video_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_video_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_video_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_video_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_video_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_video_push);
            container = itemView.findViewById(R.id.container_news_group_video_push);
        }
    }

    /**
     * 视频组
     */
    public static class VideoGroupHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        VideoGroupHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_video_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_video_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_video_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_video_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_video_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_video_push);
            container = itemView.findViewById(R.id.container_news_group_video_push);
        }
    }

    /**
     * 图集
     */
    public static class AlbumHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        AlbumHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_normal_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_normal_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_normal_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_normal_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_normal_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_normal_push);
            container = itemView.findViewById(R.id.container_news_group_normal_push);
        }
    }

    /**
     * 直播
     */
    public static class LiveHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        LiveHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_video_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_video_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_video_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_video_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_video_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_video_push);
            container = itemView.findViewById(R.id.container_news_group_video_push);
        }
    }

    /**
     * 活动
     */
    public static class UrlHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        UrlHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_normal_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_normal_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_normal_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_normal_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_normal_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_normal_push);
            container = itemView.findViewById(R.id.container_news_group_normal_push);
        }
    }

    /**
     * 专题
     */
    public static class SpecialHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        SpecialHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_normal_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_normal_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_normal_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_normal_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_normal_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_normal_push);
            container = itemView.findViewById(R.id.container_news_group_normal_push);
        }
    }

    /**
     * 图集组
     */
    public static class AlbumGroupHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        AlbumGroupHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_normal_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_normal_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_normal_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_normal_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_normal_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_normal_push);
            container = itemView.findViewById(R.id.container_news_group_normal_push);
        }
    }

    /**
     * 其他
     */
    public static class OtherHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView timeTv;
        public TextView sourceTv;
        public TextView smallTitleTv;
        public TextView describeTv;
        public ImageView postImg;
        public CardView container;

        OtherHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_title_news_group_normal_push);
            timeTv = itemView.findViewById(R.id.txt_item_time_news_group_normal_push);
            sourceTv = itemView.findViewById(R.id.txt_item_source_news_group_normal_push);
            smallTitleTv = itemView.findViewById(R.id.txt_item_title_small_news_group_normal_push);
            describeTv = itemView.findViewById(R.id.txt_item_describe_news_group_normal_push);
            postImg = itemView.findViewById(R.id.img_item_post_news_group_normal_push);
            container = itemView.findViewById(R.id.container_news_group_normal_push);
        }
    }
}
