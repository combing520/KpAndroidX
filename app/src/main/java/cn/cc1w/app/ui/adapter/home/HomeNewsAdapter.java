package cn.cc1w.app.ui.adapter.home;

import java.util.ArrayList;
import java.util.List;

import com.youth.banner.Banner;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.home.views.EmailBannerView;
import cn.cc1w.app.ui.ui.home.home.views.HomeActiveNewsListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeAlbumGroupListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeAlbumListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeAppsClassifyListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeAppsListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeAppsLoadMoreListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeBuzzListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeFocusNewsListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeFunctionListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeKpTopicListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeLiveListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeNewsGroupListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeNormalNewsBigPhotoListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeNormalNewsListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeNormalNewsNoPhotoListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeNormalNewsPhotosListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeNormalTopicListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeObsListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeOtherListView;
import cn.cc1w.app.ui.ui.home.home.views.HomePaikewPicListView;
import cn.cc1w.app.ui.ui.home.home.views.HomePaikewVideoListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeRecommendListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeScrollNewsListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeSpecialListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeSuddenListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeTeamGroupListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeUrlListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeVideoDynamicListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeVideoGroupListView;
import cn.cc1w.app.ui.ui.home.home.views.HomeVideoListView;
import cn.cc1w.app.ui.widget.VerticalScrollLayout;
import cn.cc1w.app.ui.widget.verticaltxt.AutoVerticalViewView;

/**
 * 首页新闻 Adapter
 *
 * @author kpinfo
 */
public class HomeNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity> dataSet = new ArrayList<>();
    private final Context context;
    private final LayoutInflater mInflater;
    private static final int TYPE_NEWS_NORMAL = 100;
    private static final int TYPE_NEWS_NORMAL_BIG_PHOTO = 101;
    private static final int TYPE_NEWS_NORMAL_PHOTOS = 102;
    private static final int TYPE_NEWS_NORMAL_NO_PHOTO = 103;
    private static final int TYPE_NEWS_GROUP = 200;
    private static final int TYPE_VIDEO = 300;
    private static final int TYPE_VIDEO_GROUP = 400;
    private static final int TYPE_NEWS_ALBUM = 500;
    private static final int TYPE_NEWS_LIVE = 600;
    private static final int TYPE_NEWS_SUDDEN = 700;
    private static final int TYPE_NEWS_URL = 800;
    private static final int TYPE_SPECIAL = 900;
    private static final int TYPE_ALBUM_GROUP = 1000;
    private static final int TYPE_NEWS_FOCUS = 1100;
    private static final int TYPE_NEWS_TEAM_GROUP = 1200;
    private static final int TYPE_LOAD_MORE_APPS = 1300;
    private static final int TYPE_APPS = 1400;
    private static final int TYPE_CLASSIFY_APPS = 1500;
    private static final int TYPE_RECOMMEND = 1600;
    private static final int TYPE_NEWS_ROLL = 1700;
    private static final int TYPE_NEWS_FUNCTIONS = 1800;
    private static final int TYPE_NEWS_ACTIVE = 1900;
    private static final int TYPE_NEWS_TOPIC_NORMAL = 2000;
    private static final int TYPE_NEWS_PIC_PAIKEW = 2100;
    private static final int TYPE_NEWS_VIDEO_PAIKEW = 2200;
    private static final int TYPE_KP_TOPIC = 2300;  // 开屏置顶
    private static final int TYPE_VIDEO_DYNAMIC_NEWS = 2400;  // 小视频
    private static final int TYPE_OBS = 2500;  // 开屏观察
    private static final int TYPE_BUZZ = 2600;  // 开屏热议
    private static final int TYPE_EMAIL = 2700;
    private static final int TYPE_NEWS_OTHER = 6400;
    private int scrollNewsPos = -1;
    private final LifecycleOwner owner;

    public HomeNewsAdapter(Context context,LifecycleOwner owner) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.owner = owner;
    }

    public void setData(List<HomeNewsEntity.ItemHomeNewsEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public int getScrollNewsPos() {
        return scrollNewsPos;
    }

    /**
     * 添加数据
     */
    public void addDataSet(List<HomeNewsEntity.ItemHomeNewsEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取数据集合
     */
    public List<HomeNewsEntity.ItemHomeNewsEntity> getDataSet() {
        return dataSet;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_NEWS_NORMAL) {
            viewHolder = new NormalNewsHolder(mInflater.inflate(R.layout.item_news_normal_home_recycle_new, parent, false));
        } else if (viewType == TYPE_NEWS_NORMAL_BIG_PHOTO) {
            viewHolder = new NormalNewsBigPhotoHolder(mInflater.inflate(R.layout.item_news_big_photo, parent, false));
        } else if (viewType == TYPE_NEWS_NORMAL_PHOTOS) {
            viewHolder = new NormalNewsPhotosHolder(mInflater.inflate(R.layout.item_news_photos, parent, false));
        } else if (viewType == TYPE_NEWS_NORMAL_NO_PHOTO) {
            viewHolder = new NormalNewsNoPhotoHolder(mInflater.inflate(R.layout.item_news_normal_no_photo, parent, false));
        } else if (viewType == TYPE_NEWS_GROUP) {
            viewHolder = new NewsGroupHolder(mInflater.inflate(R.layout.item_news_normal_group_home_recycle, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            viewHolder = new VideoHolder(mInflater.inflate(R.layout.item_news_video_home_recycle, parent, false));
        } else if (viewType == TYPE_VIDEO_GROUP) {
            viewHolder = new VideoGroupHolder(mInflater.inflate(R.layout.item_news_video_group_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_ALBUM) {
            viewHolder = new AlbumHolder(mInflater.inflate(R.layout.item_news_album_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_LIVE) {
            viewHolder = new LiveHolder(mInflater.inflate(R.layout.item_news_live_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_SUDDEN) {
            viewHolder = new SuddenHolder(mInflater.inflate(R.layout.item_news_sudden_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_URL) {
            viewHolder = new UrlHolder(mInflater.inflate(R.layout.item_news_url_home_recycle, parent, false));
        } else if (viewType == TYPE_SPECIAL) {
            viewHolder = new SpecialHolder(mInflater.inflate(R.layout.item_news_special_topic, parent, false));
        } else if (viewType == TYPE_ALBUM_GROUP) {
            viewHolder = new AlbumGroupHolder(mInflater.inflate(R.layout.item_news_album_group_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_FOCUS) {
            viewHolder = new NewsFocusHolder(mInflater.inflate(R.layout.item_news_focus_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_TEAM_GROUP) {
            viewHolder = new TeamGroupHolder(mInflater.inflate(R.layout.item_news_team_group_home_recycle, parent, false));
        } else if (viewType == TYPE_LOAD_MORE_APPS) {
            viewHolder = new AppsLoadMoreHolder(mInflater.inflate(R.layout.item_news_load_more_apps_home_recycle, parent, false));
        } else if (viewType == TYPE_APPS) {
            viewHolder = new AppsHolder(mInflater.inflate(R.layout.item_news_apps_home_recycle, parent, false));
        } else if (viewType == TYPE_CLASSIFY_APPS) {
            viewHolder = new AppsClassifyHolder(mInflater.inflate(R.layout.item_news_classify_apps_home_recycle, parent, false));
        } else if (viewType == TYPE_RECOMMEND) {
            viewHolder = new RecommendHolder(mInflater.inflate(R.layout.item_news_recommend_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_ROLL) {
            viewHolder = new ScrollNewsHolder(mInflater.inflate(R.layout.item_news_scroll_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_FUNCTIONS) {
            viewHolder = new FunctionHolder(mInflater.inflate(R.layout.item_news_functions_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_ACTIVE) {
            viewHolder = new ActiveNewsHolder(mInflater.inflate(R.layout.item_home_video_dynamic, parent, false));
        } else if (viewType == TYPE_NEWS_TOPIC_NORMAL) {
            viewHolder = new NormalTopicHolder(mInflater.inflate(R.layout.item_news_normal_topic, parent, false));
        } else if (viewType == TYPE_NEWS_PIC_PAIKEW) {
            viewHolder = new PaikewPicHolder(mInflater.inflate(R.layout.item_pic_paikew_home_recycle, parent, false));
        } else if (viewType == TYPE_NEWS_VIDEO_PAIKEW) {
            viewHolder = new PaikewVideoHolder(mInflater.inflate(R.layout.item_video_paikew_home_recycle, parent, false));
        } else if (viewType == TYPE_KP_TOPIC) {
            viewHolder = new KpTopicHolder(mInflater.inflate(R.layout.item_home_kp_topic, parent, false));
        } else if (viewType == TYPE_VIDEO_DYNAMIC_NEWS) {
            viewHolder = new VideoDynamicNewsHolder(mInflater.inflate(R.layout.item_home_video_dynamic, parent, false));
        } else if (viewType == TYPE_OBS) {
            viewHolder = new ObsHolder(mInflater.inflate(R.layout.item_home_obs, parent, false));
        } else if (viewType == TYPE_BUZZ) {
            viewHolder = new BuzzHolder(mInflater.inflate(R.layout.item_home_buzz, parent, false));
        } else if (viewType == TYPE_EMAIL) {
            viewHolder = new EmailHolder(mInflater.inflate(R.layout.item_email_home_recycle, parent, false));
        } else {
            viewHolder = new OtherHolder(mInflater.inflate(R.layout.item_other_recycle, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        HomeNewsEntity.ItemHomeNewsEntity item = dataSet.get(position);
        if (holder instanceof NormalNewsHolder) {
            HomeNormalNewsListView homeNormalNewsView = new HomeNormalNewsListView(context, position, holder, item);
            homeNormalNewsView.initView();
        } else if (holder instanceof NormalNewsBigPhotoHolder) {
            HomeNormalNewsBigPhotoListView homeNormalNewsView = new HomeNormalNewsBigPhotoListView(context, holder, item);
            homeNormalNewsView.initView();
        } else if (holder instanceof NormalNewsPhotosHolder) {
            HomeNormalNewsPhotosListView homeNormalNewsView = new HomeNormalNewsPhotosListView(context, position, holder, item);
            homeNormalNewsView.initView();
        } else if (holder instanceof NormalNewsNoPhotoHolder) {
            HomeNormalNewsNoPhotoListView homeNormalNewsView = new HomeNormalNewsNoPhotoListView(context, position, holder, item);
            homeNormalNewsView.initView();
        } else if (holder instanceof NewsGroupHolder) {
            HomeNewsGroupListView homeNewsGroupView = new HomeNewsGroupListView(context, position, holder, item);
            homeNewsGroupView.initView();
        } else if (holder instanceof VideoHolder) {
            HomeVideoListView homeVideoListView = new HomeVideoListView(context, position, holder, item);
            homeVideoListView.initView();
        } else if (holder instanceof VideoGroupHolder) {
            HomeVideoGroupListView homeVideoGroupListView = new HomeVideoGroupListView(context, position, holder, item);
            homeVideoGroupListView.initView();
        } else if (holder instanceof AlbumHolder) {
            HomeAlbumListView homeAlbumListView = new HomeAlbumListView(context, position, holder, item);
            homeAlbumListView.initView();
        } else if (holder instanceof LiveHolder) {
            HomeLiveListView homeLiveListView = new HomeLiveListView(context, position, holder, item);
            homeLiveListView.initView();
        } else if (holder instanceof SuddenHolder) {
            HomeSuddenListView homeSuddenListView = new HomeSuddenListView(context, position, holder, item);
            homeSuddenListView.initView();
        } else if (holder instanceof UrlHolder) {
            HomeUrlListView homeUrlListView = new HomeUrlListView(context, position, holder, item);
            homeUrlListView.initView();
        } else if (holder instanceof SpecialHolder) {
            HomeSpecialListView specialListView = new HomeSpecialListView(context, position, holder, item);
            specialListView.initView();
        } else if (holder instanceof AlbumGroupHolder) {
            HomeAlbumGroupListView homeAlbumGroupListView = new HomeAlbumGroupListView(context, position, holder, item);
            homeAlbumGroupListView.initView();
        } else if (holder instanceof NewsFocusHolder) {
            HomeFocusNewsListView homeFocusNewsListView = new HomeFocusNewsListView(context, position, holder, item);
            homeFocusNewsListView.initView();
        } else if (holder instanceof TeamGroupHolder) {
            HomeTeamGroupListView homeTeamGroupListView = new HomeTeamGroupListView(context, position, holder, item);
            homeTeamGroupListView.initView();
        } else if (holder instanceof AppsLoadMoreHolder) {
            HomeAppsLoadMoreListView homeAppsLoadMoreListView = new HomeAppsLoadMoreListView(context, position, holder, item);
            homeAppsLoadMoreListView.initView();
        } else if (holder instanceof AppsHolder) {
            HomeAppsListView homeAppsListView = new HomeAppsListView(context, position, holder, item,owner);
            homeAppsListView.initView();
        } else if (holder instanceof ScrollNewsHolder) {
            HomeScrollNewsListView homeScrollNewsListView = new HomeScrollNewsListView(context, position, holder, item,owner);
            homeScrollNewsListView.initView();
            scrollNewsPos = position;
        } else if (holder instanceof FunctionHolder) {
            HomeFunctionListView homeFunctionListView = new HomeFunctionListView(context, position, holder, item);
            homeFunctionListView.initView();
        } else if (holder instanceof ActiveNewsHolder) {
            HomeActiveNewsListView homeActiveNewsListView = new HomeActiveNewsListView(context, position, holder, item);
            homeActiveNewsListView.initView();
        } else if (holder instanceof AppsClassifyHolder) {
            HomeAppsClassifyListView homeActiveNewsListView = new HomeAppsClassifyListView(context, position, holder, item);
            homeActiveNewsListView.initView();
        } else if (holder instanceof RecommendHolder) {
            HomeRecommendListView homeRecommendListView = new HomeRecommendListView(context, position, holder, item);
            homeRecommendListView.initView();
        } else if (holder instanceof NormalTopicHolder) {
            HomeNormalTopicListView homeNormalTopicListView = new HomeNormalTopicListView(context, position, holder, item);
            homeNormalTopicListView.initView();
        } else if (holder instanceof PaikewPicHolder) {
            HomePaikewPicListView homePaikewPicListView = new HomePaikewPicListView(context, position, holder, item);
            homePaikewPicListView.initView();
        } else if (holder instanceof PaikewVideoHolder) {
            HomePaikewVideoListView homePaikewVideoListView = new HomePaikewVideoListView(context, position, holder, item);
            homePaikewVideoListView.initView();
        } else if (holder instanceof KpTopicHolder) {
            HomeKpTopicListView homePaikewVideoListView = new HomeKpTopicListView(context, position, holder, item);
            homePaikewVideoListView.initView();
        } else if (holder instanceof VideoDynamicNewsHolder) {
            HomeVideoDynamicListView homePaikewVideoListView = new HomeVideoDynamicListView(context, position, holder, item);
            homePaikewVideoListView.initView();
        } else if (holder instanceof ObsHolder) {
            HomeObsListView homePaikewVideoListView = new HomeObsListView(context, position, holder, item);
            homePaikewVideoListView.initView();
        } else if (holder instanceof BuzzHolder) {
            HomeBuzzListView homePaikewVideoListView = new HomeBuzzListView(context, position, holder, item);
            homePaikewVideoListView.initView();
        } else if (holder instanceof EmailHolder) {
            EmailBannerView emailBannerView = new EmailBannerView(context, position, holder, item);
            emailBannerView.initView();
        } else {
            HomeOtherListView otherHolder = new HomeOtherListView(context, position, holder, item);
            otherHolder.initView();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSet.get(position).getShow_type() == null) {
            return TYPE_NEWS_NORMAL;
        }
        if (TextUtils.equals(Constant.TYPE_NEWS_NORMAL, dataSet.get(position).getIn_type())) {
            HomeNewsEntity.ItemHomeNewsEntity entity = dataSet.get(position);
            if (entity != null && entity.getNews() != null && !entity.getNews().isEmpty()) {
                HomeNewsEntity.ItemHomeNewsEntity.NewsBean newsBean = entity.getNews().get(0);
                if (newsBean.getApp_news_display_model() == 2) {
                    return TYPE_NEWS_NORMAL_BIG_PHOTO;
                } else if (newsBean.getApp_news_display_model() == 3) {
                    return TYPE_NEWS_NORMAL_PHOTOS;
                } else if (newsBean.getApp_news_display_model() == 4) {
                    return TYPE_NEWS_NORMAL_NO_PHOTO;
                } else {
                    return TYPE_NEWS_NORMAL;// 普通新闻
                }
            } else {
                return TYPE_NEWS_NORMAL;// 普通新闻
            }
        } else if (TextUtils.equals(Constant.TYPE_NEWS_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_GROUP;// 新闻组
        } else if (TextUtils.equals(Constant.TYPE_VIDEO_NORMAL, dataSet.get(position).getIn_type())) {
            return TYPE_VIDEO;// 视频
        } else if (TextUtils.equals(Constant.TYPE_VIDEO_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_VIDEO_GROUP;// 视频组
        } else if (TextUtils.equals(Constant.TYPE_PHOTO, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_ALBUM;// 图集
        } else if (TextUtils.equals(Constant.TYPE_LIVE, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_LIVE;// 直播
        } else if (TextUtils.equals(Constant.TYPE_SUDDEN, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_SUDDEN;// 突发
        } else if (TextUtils.equals(Constant.TYPE_URL, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_URL;// 活动
        } else if (TextUtils.equals(Constant.TYPE_TOPIC, dataSet.get(position).getIn_type()) || TextUtils.equals("special", dataSet.get(position).getIn_type())) {
            return TYPE_SPECIAL; // 专题
        } else if (TextUtils.equals(Constant.TYPE_PHOTO_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_ALBUM_GROUP;// 图集组
        } else if (TextUtils.equals(Constant.TYPE_FOCUS, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_FOCUS;// 焦点
        } else if (TextUtils.equals(Constant.TYPE_TEAM_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_TEAM_GROUP;// 组新闻
        } else if (TextUtils.equals(Constant.TYPE_MORE, dataSet.get(position).getIn_type())) {
            return TYPE_LOAD_MORE_APPS;// 应用号查看更多
        } else if (TextUtils.equals(Constant.TYPE_CHANNEL, dataSet.get(position).getIn_type())) {
            return TYPE_APPS;// 应用号
        } else if (TextUtils.equals(Constant.TYPE_CHANNEL_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_CLASSIFY_APPS;// 应用号分类
        } else if (TextUtils.equals(Constant.TYPE_ROLLING_NEWS, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_ROLL;// 专属焦点[滚动新闻]
        } else if (TextUtils.equals(Constant.TYPE_FUNCTIONS, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_FUNCTIONS;// 功能区
        } else if (TextUtils.equals(Constant.TYPE_DYNAMIC_NEWS, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_ACTIVE;// 动新闻
        } else if (TextUtils.equals(Constant.TYPE_GUESS_LIKE, dataSet.get(position).getIn_type())) { // 猜你喜欢
            return TYPE_RECOMMEND;
        } else if (TextUtils.equals(Constant.TYPE_NEWS_NORMAL_GROUP, dataSet.get(position).getIn_type())) {
            return TYPE_NEWS_TOPIC_NORMAL;
        } else if (TextUtils.equals(Constant.TYPE_SHOOT_PHOTO, dataSet.get(position).getIn_type())) { // 拍客照片
            return TYPE_NEWS_PIC_PAIKEW;
        } else if (TextUtils.equals(Constant.TYPE_SHOOT_VIDEO, dataSet.get(position).getIn_type())) { // 拍客视频
            return TYPE_NEWS_VIDEO_PAIKEW;
        } else if (TextUtils.equals(Constant.TYPE_KP_TOPIC, dataSet.get(position).getIn_type())) { //  开屏置顶
            return TYPE_KP_TOPIC;
        } else if (TextUtils.equals(Constant.TYPE_VIDEO_DYNAMIC_NEWS, dataSet.get(position).getIn_type())) { // 小视频
            return TYPE_VIDEO_DYNAMIC_NEWS;
        } else if (TextUtils.equals(Constant.TYPE_OBS, dataSet.get(position).getIn_type())) { // 开屏观察
            return TYPE_OBS;
        } else if (TextUtils.equals(Constant.TYPE_BUZZ, dataSet.get(position).getIn_type())) { // 开屏热议
            return TYPE_BUZZ;
        } else if (TextUtils.equals("email", dataSet.get(position).getIn_type())) {
            return TYPE_EMAIL;
        } else { // 其他
            return TYPE_NEWS_OTHER;
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
        public TextView newsTitleTv;// 新闻title
        public TextView newsAppsNameTv;//应用号名称
        public TextView createTimeTv;// 创建时间
        public RoundAngleImageView newsPicImg;// 新闻图片

        NormalNewsHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }

    /**
     * 普通新闻
     */
    public static class NormalNewsNoPhotoHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;// 新闻title
        public TextView newsAppsNameTv;//应用号名称
        public TextView createTimeTv;// 创建时间

        NormalNewsNoPhotoHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
        }
    }

    /**
     * 普通新闻
     */
    public static class NormalNewsPhotosHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;// 新闻title
        public TextView newsAppsNameTv;//应用号名称
        public TextView createTimeTv;// 创建时间
        public RecyclerView recyclerView;// 新闻图片

        NormalNewsPhotosHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
            recyclerView = view.findViewById(R.id.recyclerView);
        }
    }

    /**
     * 普通新闻--大图
     */
    public static class NormalNewsBigPhotoHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;// 新闻title
        public RoundAngleImageView newsPicImg;// 新闻图片

        NormalNewsBigPhotoHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }

    /**
     * 新闻组
     */
    public static class NewsGroupHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;
        public ImageView btnMore;

        NewsGroupHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.news_group_home);
            btnMore = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 视频
     */
    public static class VideoHolder extends RecyclerView.ViewHolder {
        public TextView nameTv; // 作者
        public TextView shareCntTv; // 分享数
        public TextView describeTv; // 描述文字
        public RoundAngleImageView postImg;// 视频的图片
        public RelativeLayout container;
        public ImageView shareImg;// 分享按钮
        public TextView watchTv; // 作者
        public TextView likeTv; // 作者

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
     * 视频组合
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
     * 图集
     */
    public static class AlbumHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView coverIv; // 图集的SubRecycle
        public TextView titleTv; // 标题

        AlbumHolder(View view) {
            super(view);
            coverIv = view.findViewById(R.id.cover);
            titleTv = view.findViewById(R.id.title);
        }
    }

    /**
     * 猜你喜欢
     */
    public static class RecommendHolder extends RecyclerView.ViewHolder {
        public LinearLayout container;
        public TextView titleTv; // 标题

        RecommendHolder(View view) {
            super(view);
            container = view.findViewById(R.id.ll_recommend_home);
            titleTv = view.findViewById(R.id.txt_recommend_home);
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
     * 突发
     */
    public static class SuddenHolder extends RecyclerView.ViewHolder {
        public Banner banner;

        SuddenHolder(View view) {
            super(view);
            banner = view.findViewById(R.id.viewPager_active_sudden_home);
        }
    }

    /**
     * 活动
     */
    public static class UrlHolder extends RecyclerView.ViewHolder {
        public Banner banner; // 活动列表的 Banner
        public LinearLayout showMoreLayout; // 显示更多
        public ImageView moreImageView;

        UrlHolder(View view) {
            super(view);
            banner = view.findViewById(R.id.viewPager_active_url_home);
            showMoreLayout = view.findViewById(R.id.ll_more_url_home);
            moreImageView = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 专题
     */
    public static class SpecialHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView coverIv; // 专题列表 的Banner
        public View moreSpecialLayout; // 查看更多
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
     * 焦点新闻 【 顶部的banner新闻 】
     */
    public static class NewsFocusHolder extends RecyclerView.ViewHolder {
        public Banner banner; // 焦点新闻的Banner
        public TextView titleTv;

        NewsFocusHolder(View view) {
            super(view);
            banner = view.findViewById(R.id.banner_top_home);
            titleTv = view.findViewById(R.id.title);
        }
    }

    /**
     * 组新闻
     */
    public static class TeamGroupHolder extends RecyclerView.ViewHolder {
        public TextView groupNameTv; // 组名称
        public LinearLayout moreLayout; // 更多
        public Banner banner; // 组新闻Banner

        TeamGroupHolder(View view) {
            super(view);
            groupNameTv = view.findViewById(R.id.txt_item_team_group_home);
            moreLayout = view.findViewById(R.id.ll_more_team_group_home);
            banner = view.findViewById(R.id.viewpager_team_group_home);
        }
    }

    /**
     * 应用号加载更多 【新闻加载更多】
     */
    public static class AppsLoadMoreHolder extends RecyclerView.ViewHolder {
        public RelativeLayout loadMoreLayout;
        public TextView titleTv; // 标题

        AppsLoadMoreHolder(View view) {
            super(view);
            loadMoreLayout = view.findViewById(R.id.ll_load_more_home);
            titleTv = view.findViewById(R.id.txt_load_more_home);
        }
    }

    /**
     * 应用号
     */
    public static class AppsHolder extends RecyclerView.ViewHolder {
        public RecyclerView appsRecycleView; // 应用号 Recycle

        AppsHolder(View view) {
            super(view);
            appsRecycleView = view.findViewById(R.id.list_item_apps_home);
        }
    }

    /**
     * 应用号分类
     */
    public static class AppsClassifyHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView; // 应用号分类Recycle

        AppsClassifyHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.list_item_classify_apps_home);
        }
    }

    /**
     * 专属焦点【滚动新闻】
     */
    public static class ScrollNewsHolder extends RecyclerView.ViewHolder {
        public AutoVerticalViewView verticalViewView; // 自动滚动 View
        public LinearLayout scrollNewsContainer;
        public ImageView imageView;

        ScrollNewsHolder(View view) {
            super(view);
            verticalViewView = view.findViewById(R.id.txt_item_autoVertical);
            scrollNewsContainer = view.findViewById(R.id.ll_autoVertical);
            imageView = view.findViewById(R.id.img_autoVertical);
        }
    }

    /**
     * 功能区
     */
    public static class FunctionHolder extends RecyclerView.ViewHolder {
        public RecyclerView functionList; // 功能区 Recycle

        FunctionHolder(View view) {
            super(view);
            functionList = view.findViewById(R.id.list_function_home);
        }
    }

    /**
     * 动新闻
     */
    public static class ActiveNewsHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;
        public TextView titleTv;
        public View btnMore;

        ActiveNewsHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recyclerView);
            titleTv = view.findViewById(R.id.dynamic_title);
            btnMore = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 普通专题类别的专题
     */
    public static class NormalTopicHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;// 新闻title
        public RoundAngleImageView newsPicImg;// 新闻图片

        NormalTopicHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }

    /**
     * 拍客照片
     */
    public static class PaikewPicHolder extends RecyclerView.ViewHolder {
        public RecyclerView paikewPicRecycleView;

        PaikewPicHolder(View view) {
            super(view);
            paikewPicRecycleView = view.findViewById(R.id.list_pic_paikew_home);
        }
    }

    /**
     * 拍客视频
     */
    public static class KpTopicHolder extends RecyclerView.ViewHolder {
        public VerticalScrollLayout verticalScrollLayout;
        public TextView textView;

        KpTopicHolder(View view) {
            super(view);
            verticalScrollLayout = view.findViewById(R.id.verticalScrollLayout);
            textView = view.findViewById(R.id.text);
        }
    }

    /**
     * 小视频
     */
    public static class VideoDynamicNewsHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;
        public TextView titleTv;
        public View btnMore;

        VideoDynamicNewsHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recyclerView);
            titleTv = view.findViewById(R.id.dynamic_title);
            btnMore = view.findViewById(R.id.btn_more);
        }
    }

    /**
     * 开屏观察
     */
    public static class ObsHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public ImageView coverIv;
        public View btnMore;

        ObsHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.title);
            coverIv = view.findViewById(R.id.cover);
            btnMore = view.findViewById(R.id.btn_more);
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
     * 县长邮箱
     */
    public static class EmailHolder extends RecyclerView.ViewHolder {
        public ImageView bannerImg;//图片

        public EmailHolder(View view) {
            super(view);
            bannerImg = view.findViewById(R.id.banner_email_home_item);
        }
    }

    /**
     * 拍客视频
     */
    public static class PaikewVideoHolder extends RecyclerView.ViewHolder {
        public RecyclerView paikewVideoRecycleView;

        PaikewVideoHolder(View view) {
            super(view);
            paikewVideoRecycleView = view.findViewById(R.id.list_video_paikew_home);
        }
    }

    /**
     * 其他
     */
    public static class OtherHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTv;// 新闻title
        public TextView newsAppsNameTv;//应用号名称
        public TextView createTimeTv;// 创建时间
        public RoundAngleImageView newsPicImg;// 新闻图片

        OtherHolder(View view) {
            super(view);
            newsTitleTv = view.findViewById(R.id.txt_item_title_news_normal_home);
            newsAppsNameTv = view.findViewById(R.id.txt_item_name_apps_news_normal_home);
            createTimeTv = view.findViewById(R.id.img_item_time_news_normal_home);
            newsPicImg = view.findViewById(R.id.img_item_pic_news_normal_home);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof ScrollNewsHolder) {
            if (null != ((ScrollNewsHolder) holder).verticalViewView) {
                ((ScrollNewsHolder) holder).verticalViewView.stopFlipping();
            }
        } else if (holder instanceof KpTopicHolder) {
            if (null != ((KpTopicHolder) holder).verticalScrollLayout) {
                ((KpTopicHolder) holder).verticalScrollLayout.stopFlipping();
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof ScrollNewsHolder) {
            if (null != ((ScrollNewsHolder) holder).verticalViewView) {
                ((ScrollNewsHolder) holder).verticalViewView.startFlipping();
            }
        } else if (holder instanceof KpTopicHolder) {
            if (null != ((KpTopicHolder) holder).verticalScrollLayout) {
                ((KpTopicHolder) holder).verticalScrollLayout.startFlipping();
            }
        }
    }
}