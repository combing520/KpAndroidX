package cn.cc1w.app.ui.adapter.details_apps;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youth.banner.Banner;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.ui.home.apps.view.ActiveListView;
import cn.cc1w.app.ui.ui.home.apps.view.AlbumGroupNewsListView;
import cn.cc1w.app.ui.ui.home.apps.view.AlbumListView;
import cn.cc1w.app.ui.ui.home.apps.view.BuzzListView;
import cn.cc1w.app.ui.ui.home.apps.view.FocusListView;
import cn.cc1w.app.ui.ui.home.apps.view.LiveListView;
import cn.cc1w.app.ui.ui.home.apps.view.NewsGroupListView;
import cn.cc1w.app.ui.ui.home.apps.view.NormalNewsBigPhotoListView;
import cn.cc1w.app.ui.ui.home.apps.view.NormalNewsListView;
import cn.cc1w.app.ui.ui.home.apps.view.NormalNewsNoPhotoListView;
import cn.cc1w.app.ui.ui.home.apps.view.NormalNewsPhotosListView;
import cn.cc1w.app.ui.ui.home.apps.view.NormalTopicNewsListView;
import cn.cc1w.app.ui.ui.home.apps.view.OtherListView;
import cn.cc1w.app.ui.ui.home.apps.view.SpecialListView;
import cn.cc1w.app.ui.ui.home.apps.view.SuddenListView;
import cn.cc1w.app.ui.ui.home.apps.view.TeamGroupListView;
import cn.cc1w.app.ui.ui.home.apps.view.VideoListGroupView;
import cn.cc1w.app.ui.ui.home.apps.view.VideoListView;
import cn.cc1w.app.ui.R;

/**
 * 应用号详情新闻实体类
 *
 * @author kpinfo
 */
public class AppsDetailNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<NewsEntity.DataBean> list = new ArrayList<>();
    private final LayoutInflater mInflater;
    private static final int TYPE_NEWS_NORMAL = 100;
    private static final int TYPE_NEWS_NORMAL_BIG_PHOTO = 101;
    private static final int TYPE_NEWS_NORMAL_PHOTOS = 102;
    private static final int TYPE_NEWS_NORMAL_NO_PHOTO = 103;
    private static final int TYPE_NEWS_GROUP = 200;
    private static final int TYPE_VIDEO = 300;
    private static final int TYPE_VIDEO_GROUP = 400;
    private static final int TYPE_ALBUM = 500;
    private static final int TYPE_LIVE = 600;
    private static final int TYPE_SUDDEN = 700;
    private static final int TYPE_ACTIVE = 800;
    private static final int TYPE_FOCUS = 900;
    private static final int TYPE_TEAM_GROUP = 1000;
    private static final int TYPE_SPECIAL = 1100;
    private static final int TYPE_ALBUM_GROUP = 1300;
    private static final int TYPE_NEWS_TOPIC_NORMAL = 1400;
    private static final int TYPE_OTHER = 1500;
    private static final int TYPE_OBS = 2500;
    private static final int TYPE_BUZZ = 2600;

    public AppsDetailNewsAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据
     *
     * @param data 数据源
     */
    public void setData(List<NewsEntity.DataBean> data) {
        if (null != data && !data.isEmpty()) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 加载数据
     *
     * @param data 数据源
     */
    public void addData(List<NewsEntity.DataBean> data) {
        if (null != data && !data.isEmpty()) {
            int startPos = list.size();
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_NEWS_NORMAL) {
            viewHolder = new NormalNewsHolder(mInflater.inflate(R.layout.item_news_normal_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_NORMAL_BIG_PHOTO) {
            viewHolder = new NormalNewsBigPhotoHolder(mInflater.inflate(R.layout.item_news_big_photo, parent, false));
        } else if (viewType == TYPE_NEWS_NORMAL_PHOTOS) {
            viewHolder = new NormalNewsPhotosHolder(mInflater.inflate(R.layout.item_news_photos, parent, false));
        } else if (viewType == TYPE_NEWS_NORMAL_NO_PHOTO) {
            viewHolder = new NormalNewsNoPhotoHolder(mInflater.inflate(R.layout.item_news_normal_no_photo, parent, false));
        } else if (viewType == TYPE_NEWS_GROUP) {
            viewHolder = new NewsGroupHolder(mInflater.inflate(R.layout.item_news_group_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            viewHolder = new VideoHolder(mInflater.inflate(R.layout.item_news_video_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_VIDEO_GROUP) {
            viewHolder = new VideoGroupHolder(mInflater.inflate(R.layout.item_news_video_group_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_ALBUM) {
            viewHolder = new AlbumHolder(mInflater.inflate(R.layout.item_news_album_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_LIVE) {
            viewHolder = new LiveHolder(mInflater.inflate(R.layout.item_news_live_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_SUDDEN) {
            viewHolder = new SuddenHolder(mInflater.inflate(R.layout.item_news_sudden_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_ACTIVE) {
            viewHolder = new ActiveHolder(mInflater.inflate(R.layout.item_news_active_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_FOCUS) {
            viewHolder = new FocusNewsHolder(mInflater.inflate(R.layout.item_news_focus_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_TEAM_GROUP) {
            viewHolder = new TeamGroupHolder(mInflater.inflate(R.layout.item_news_team_group_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_SPECIAL) {
            viewHolder = new SpecialHolder(mInflater.inflate(R.layout.item_news_special_home_recycle, parent, false));
        } else if (viewType == TYPE_ALBUM_GROUP) {
            viewHolder = new AlbumGroupHolder(mInflater.inflate(R.layout.item_news_album_group_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_TOPIC_NORMAL) {
            viewHolder = new NormalTopicHolder(mInflater.inflate(R.layout.item_news_normal_details_apps_recycle, parent, false));
        } else if (viewType == TYPE_BUZZ) {
            viewHolder = new BuzzHolder(mInflater.inflate(R.layout.item_home_buzz, parent, false));
        } else if (viewType == TYPE_OBS) {
            viewHolder = new ObsHolder(mInflater.inflate(R.layout.item_home_obs, parent, false));
        } else {
            viewHolder = new OtherHolder(mInflater.inflate(R.layout.item_other_recycle, parent, false));
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getShow_type() == null) {
            return TYPE_NEWS_NORMAL;
        }
        if (TextUtils.equals(Constant.TYPE_NEWS_NORMAL, list.get(position).getIn_type())) {
            NewsEntity.DataBean entity = list.get(position);
            if (entity != null && entity.getNews() != null && !entity.getNews().isEmpty()) {
                NewsEntity.DataBean.ItemNewsEntity newsBean = entity.getNews().get(0);
                if (newsBean.getApp_news_display_model() == 2) {
                    return TYPE_NEWS_NORMAL_BIG_PHOTO;
                } else if (newsBean.getApp_news_display_model() == 3) {
                    return TYPE_NEWS_NORMAL_PHOTOS;
                } else if (newsBean.getApp_news_display_model() == 4) {
                    return TYPE_NEWS_NORMAL_NO_PHOTO;
                } else {
                    return TYPE_NEWS_NORMAL;
                }
            } else {
                return TYPE_NEWS_NORMAL;
            }
        } else if (TextUtils.equals(Constant.TYPE_NEWS_GROUP, list.get(position).getIn_type())) {
            return TYPE_NEWS_GROUP;
        } else if (TextUtils.equals(Constant.TYPE_VIDEO_NORMAL, list.get(position).getIn_type())) {
            return TYPE_VIDEO;
        } else if (TextUtils.equals(Constant.TYPE_VIDEO_GROUP, list.get(position).getIn_type())) {
            return TYPE_VIDEO_GROUP;
        } else if (TextUtils.equals(Constant.TYPE_PHOTO, list.get(position).getIn_type())) {
            return TYPE_ALBUM;
        } else if (TextUtils.equals(Constant.TYPE_LIVE, list.get(position).getIn_type())) {
            return TYPE_LIVE;
        } else if (TextUtils.equals(Constant.TYPE_SUDDEN, list.get(position).getIn_type())) {
            return TYPE_SUDDEN;
        } else if (TextUtils.equals(Constant.TYPE_URL, list.get(position).getIn_type())) {
            return TYPE_ACTIVE;
        } else if (TextUtils.equals(Constant.TYPE_FOCUS, list.get(position).getIn_type())) {
            return TYPE_FOCUS;
        } else if (TextUtils.equals(Constant.TYPE_TEAM_GROUP, list.get(position).getIn_type())) {
            return TYPE_TEAM_GROUP;
        } else if (TextUtils.equals(Constant.TYPE_TOPIC, list.get(position).getIn_type()) || TextUtils.equals("special", list.get(position).getIn_type())) {
            return TYPE_SPECIAL;
        } else if (TextUtils.equals(Constant.TYPE_PHOTO_GROUP, list.get(position).getIn_type())) {
            return TYPE_ALBUM_GROUP;
        } else if (TextUtils.equals(Constant.TYPE_NEWS_NORMAL_GROUP, list.get(position).getIn_type())) {
            return TYPE_NEWS_TOPIC_NORMAL;
        } else if (TextUtils.equals(Constant.TYPE_BUZZ, list.get(position).getIn_type())) {
            return TYPE_BUZZ;
        } else if (TextUtils.equals(Constant.TYPE_OBS, list.get(position).getIn_type())) {
            return TYPE_OBS;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        NewsEntity.DataBean entity = list.get(position);
        if (holder instanceof NormalNewsHolder) {
            NormalNewsListView normalNewsListView = new NormalNewsListView(context, position, holder, entity);
            normalNewsListView.initView();
        } else if (holder instanceof NormalNewsBigPhotoHolder) {
            NormalNewsBigPhotoListView normalNewsView = new NormalNewsBigPhotoListView(context, position, holder, entity);
            normalNewsView.initView();
        } else if (holder instanceof NormalNewsPhotosHolder) {
            NormalNewsPhotosListView normalNewsView = new NormalNewsPhotosListView(context, position, holder, entity);
            normalNewsView.initView();
        } else if (holder instanceof NormalNewsNoPhotoHolder) {
            NormalNewsNoPhotoListView normalNewsView = new NormalNewsNoPhotoListView(context, position, holder, entity);
            normalNewsView.initView();
        } else if (holder instanceof NewsGroupHolder) {
            NewsGroupListView newsGroupListView = new NewsGroupListView(context, position, holder, entity);
            newsGroupListView.initView();
        } else if (holder instanceof VideoHolder) {
            VideoListView videoListView = new VideoListView(context, position, holder, entity);
            videoListView.initView();
        } else if (holder instanceof VideoGroupHolder) {
            VideoListGroupView videoListGroupView = new VideoListGroupView(context, position, holder, entity);
            videoListGroupView.initView();
        } else if (holder instanceof AlbumHolder) {
            AlbumListView albumListView = new AlbumListView(context, position, holder, entity);
            albumListView.initView();
        } else if (holder instanceof LiveHolder) {
            LiveListView liveListView = new LiveListView(context, position, holder, entity);
            liveListView.initView();
        } else if (holder instanceof SuddenHolder) {
            SuddenListView suddenListView = new SuddenListView(context, position, holder, entity);
            suddenListView.initView();
        } else if (holder instanceof ActiveHolder) {
            ActiveListView activeListView = new ActiveListView(context, position, holder, entity);
            activeListView.initView();
        } else if (holder instanceof FocusNewsHolder) {
            FocusListView focusListView = new FocusListView(context, position, holder, entity);
            focusListView.initView();
        } else if (holder instanceof TeamGroupHolder) {
            TeamGroupListView teamGroupListView = new TeamGroupListView(context, position, holder, entity);
            teamGroupListView.initView();
        } else if (holder instanceof SpecialHolder) {
            SpecialListView specialListView = new SpecialListView(context, position, holder, entity);
            specialListView.initView();
        } else if (holder instanceof AlbumGroupHolder) {
            AlbumGroupNewsListView albumGroupNewsListView = new AlbumGroupNewsListView(context, position, holder, entity);
            albumGroupNewsListView.initView();
        } else if (holder instanceof NormalTopicHolder) {
            NormalTopicNewsListView normalTopicNewsListView = new NormalTopicNewsListView(context, position, holder, entity);
            normalTopicNewsListView.initView();
        } else if (holder instanceof BuzzHolder) {
            BuzzListView buzzListView = new BuzzListView(context, position, holder, entity);
            buzzListView.initView();
        } else {
            OtherListView otherListView = new OtherListView(context, position, holder, entity);
            otherListView.initView();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 普通新闻
     */
    public static class NormalNewsHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;
        public TextView newsAppsNameTv;
        public TextView createTimeTv;
        public RoundAngleImageView newsPicImg;

        NormalNewsHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }

    /**
     * 新闻组
     */
    public static class NewsGroupHolder extends RecyclerView.ViewHolder {
        public RecyclerView newsList;
        public TextView titleTv;
        public LinearLayout container;

        NewsGroupHolder(View view) {
            super(view);
            newsList = view.findViewById(R.id.list_sub_news_group_detail_apps);
            titleTv = view.findViewById(R.id.txt_sub_news_group_detail_apps);
            container = view.findViewById(R.id.ll_sub_news_group_detail_apps);
        }
    }

    /**
     * 视频
     */
    public static class VideoHolder extends RecyclerView.ViewHolder {
        public TextView nameTv;
        public TextView shareCntTv;
        public TextView describeTv;
        public RoundAngleImageView postImg;
        public RelativeLayout container;
        public ImageView shareImg;
        public TextView watchTv;
        public TextView likeTv;

        VideoHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.txt_item_name_video_home);
            shareCntTv = view.findViewById(R.id.txt_item_cnt_video_home);
            describeTv = view.findViewById(R.id.txt_item_describe_video_home);
            postImg = view.findViewById(R.id.img_item_video_home);
            container = view.findViewById(R.id.container_item_active_news);
            shareImg = view.findViewById(R.id.img_share_video_home);
            watchTv = view.findViewById(R.id.txt_item_watch_video_home);
            likeTv = view.findViewById(R.id.txt_item_like_video_home);
        }
    }

    /**
     * 视频组
     */
    public static class VideoGroupHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView coverIv;
        public TextView groupTitleTv;
        public View btnMore;
        public TextView titleTv;

        VideoGroupHolder(View view) {
            super(view);
            coverIv = view.findViewById(R.id.cover);
            groupTitleTv = view.findViewById(R.id.group_title);
            btnMore = view.findViewById(R.id.btn_more);
            titleTv = view.findViewById(R.id.title);
        }
    }

    /**
     * 图集新闻
     */
    public static class AlbumHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView coverIv;
        public TextView titleTv;

        AlbumHolder(View view) {
            super(view);
            coverIv = view.findViewById(R.id.cover);
            titleTv = view.findViewById(R.id.title);
        }
    }

    /**
     * 直播
     */
    public static class LiveHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView coverIv;
        public TextView groupTitleTv;
        public View btnMore;
        public TextView titleTv;

        LiveHolder(View view) {
            super(view);
            coverIv = view.findViewById(R.id.cover);
            groupTitleTv = view.findViewById(R.id.group_title);
            btnMore = view.findViewById(R.id.btn_more);
            titleTv = view.findViewById(R.id.title);
        }
    }

    /**
     * 突发新闻
     */
    public static class SuddenHolder extends RecyclerView.ViewHolder {
        public Banner banner;

        SuddenHolder(View view) {
            super(view);
            banner = view.findViewById(R.id.viewPager_sudden_detail_apps);
        }
    }

    /**
     * 活动
     */
    public static class ActiveHolder extends RecyclerView.ViewHolder {
        public Banner banner;
        public LinearLayout showMoreLayout;
        public ImageView moreImageView;

        ActiveHolder(View view) {
            super(view);
            banner = view.findViewById(R.id.viewPager_active_url_home);
            showMoreLayout = view.findViewById(R.id.ll_more_url_home);
            moreImageView = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 焦点新闻
     */
    public static class FocusNewsHolder extends RecyclerView.ViewHolder {
        public Banner banner;
        public TextView title;

        FocusNewsHolder(View view) {
            super(view);
            banner = view.findViewById(R.id.banner_top_home);
            title = view.findViewById(R.id.title);
        }
    }

    /**
     * 组新闻
     */
    public static class TeamGroupHolder extends RecyclerView.ViewHolder {
        public Banner banner;
        public TextView groupNameTv;
        public LinearLayout moreLayout;

        TeamGroupHolder(View view) {
            super(view);
            banner = view.findViewById(R.id.viewPager_team_group_detail);
            groupNameTv = view.findViewById(R.id.txt_title_team_group_detail);
            moreLayout = view.findViewById(R.id.ll_team_group_detail);
        }
    }

    /**
     * 专题
     */
    public static class SpecialHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView coverIv;
        public View moreSpecialLayout;
        public TextView titleTv;

        SpecialHolder(View view) {
            super(view);
            coverIv = view.findViewById(R.id.img);
            moreSpecialLayout = view.findViewById(R.id.ll_more_special_home);
            titleTv = view.findViewById(R.id.title);
        }
    }

    /**
     * 图集组
     */
    public static class AlbumGroupHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;
        public TextView titleTv;
        public View btnMore;

        AlbumGroupHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recyclerView);
            titleTv = view.findViewById(R.id.group_title);
            btnMore = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 普通新闻类型的 专题
     */
    public static class NormalTopicHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;
        public TextView newsAppsNameTv;
        public TextView createTimeTv;
        public RoundAngleImageView newsPicImg;

        NormalTopicHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }

    /**
     * 开屏热议
     */
    public static class BuzzHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView sumaryTv;
        public ImageView moreBtn;

        BuzzHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.title);
            sumaryTv = view.findViewById(R.id.sumary);
            moreBtn = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 开屏观察
     */
    public static class ObsHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public RoundAngleImageView coverIv;
        public ImageView btnMore;

        ObsHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.title);
            coverIv = view.findViewById(R.id.cover);
            btnMore = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 普通新闻
     */
    public static class NormalNewsBigPhotoHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;
        public RoundAngleImageView newsPicImg;

        NormalNewsBigPhotoHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }

    /**
     * 普通新闻
     */
    public static class NormalNewsPhotosHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;
        public TextView newsAppsNameTv;
        public TextView createTimeTv;
        public RecyclerView recyclerView;

        NormalNewsPhotosHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
            recyclerView = view.findViewById(R.id.recyclerView);
        }
    }

    /**
     * 普通新闻
     */
    public static class NormalNewsNoPhotoHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;
        public TextView newsAppsNameTv;
        public TextView createTimeTv;

        NormalNewsNoPhotoHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
        }
    }

    /**
     * 其他
     */
    public static class OtherHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;
        public TextView newsAppsNameTv;
        public TextView createTimeTv;
        public RoundAngleImageView newsPicImg;

        OtherHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }
}
