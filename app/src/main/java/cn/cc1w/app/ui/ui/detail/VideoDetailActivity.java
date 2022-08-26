package cn.cc1w.app.ui.ui.detail;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import net.arvin.itemdecorationhelper.ItemDecorationFactory;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.expand.ExpandableTextView;
import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.NewsDetailCommentAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.NewsCommentEntity;
import cn.cc1w.app.ui.entity.VideoDetailEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.home.MoreCommentListActivity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.ThumbUpView.ThumbUpView;
import cn.cc1w.app.ui.widget.input.InputDialog;
import cn.cc1w.app.ui.widget.video.CoverVideoPlayer;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;

/**
 * 视频详情
 *
 * @author kpinfo
 */
public class VideoDetailActivity extends CustomActivity implements OnItemClickListener, BlankViewClickListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_black_title)
    TextView titleTv;
    @BindView(R.id.txt_title_video_detail)
    TextView videoTitleTv;
    @BindView(R.id.img_thumbnail_video_detail)
    RoundAngleImageView thumbnailImg;
    @BindView(R.id.txt_type_video_detail)
    TextView videoTypeTv;
    @BindView(R.id.txt_time_video_detail)
    TextView videoCreateTimeTv;
    @BindView(R.id.txt_summary_video_detail)
    ExpandableTextView videoSummaryTv;
    @BindView(R.id.list_commend_detail_video)
    RecyclerView commentList;
    @BindView(R.id.txt_comment_all_detail_video)
    TextView moreCommentTv;
    @BindView(R.id.img_collection_detail_video)
    ThumbUpView collectionImg;
    @BindView(R.id.img_comment_detail_video)
    ImageView commentImg;
    @BindView(R.id.ll_comment_detail_news)
    LinearLayout commentLayout;
    @BindView(R.id.video_player_detail_video)
    CoverVideoPlayer videoPlayer;
    @BindView(R.id.scroll_detail_video)
    NestedScrollView scrollView;
    @BindView(R.id.ll_bottom_detail_video)
    LinearLayout bottomLayout;
    @BindView(R.id.blank_parent_home)
    BlankView blankView;
    @BindView(R.id.news_detail_like_img)
    ImageView priseImg;
    private OrientationUtils orientationUtils;
    private NewsDetailCommentAdapter adapter;
    private LoadingDialog loading;
    private String videoId;
    private long lastTime;
    private boolean isCollection;
    private boolean isPlay;
    private boolean isPause;
    private long videoDuration;
    private int currentPercent = 0;
    private long enterTime;
    private VideoDetailEntity.DataBean videoEntity;
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    private InputDialog mInputDialog;
    private long lastBlankViewClickTime = System.currentTimeMillis();
    private long lastClickTime = System.currentTimeMillis();
    private static final String[] PERMISSION_LIST = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private  int retryTimes = 0;
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 && videoPlayer != null) {
                videoPlayer.startPlayLogic();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initBlankView();
        initLoading();
        initVideoPlayer();
        initList();
        initCollectionInfo();
        requestData();
        setRedThemeInfo();
    }

    private void setRedThemeInfo() {
        if (Constant.IS_SHOW_RED_MODE) {
            ImmersionBar.with(this).statusBarColor(R.color.color_red_theme).statusBarDarkFont(false).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).init();
        }
    }

    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, "你所查看的新闻不存在,点击重试", "点击重试");
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化收藏按钮的相关信息
     */
    private void initCollectionInfo() {
        collectionImg.setUnLikeType(ThumbUpView.LikeType.broken);
        collectionImg.setCracksColor(Color.parseColor("#b3b3b3"));
        collectionImg.setEdgeColor(Color.parseColor("#b3b3b3"));
        collectionImg.setFillColor(Color.parseColor("#c81f1d"));
        collectionImg.setBgColor(Color.parseColor("#F5F5F5"));
        collectionImg.setEnabled(false);
        collectionImg.setClickable(false);
    }

    /**
     * 显示评论框
     */
    private void showInputDialog() {
        mInputDialog = new InputDialog(this).builder().setOnSendClickListener(this::sendComment);
        mInputDialog.setHint("写评论...");
        mInputDialog.setNeedCheckLogin(true);
        mInputDialog.show();
    }

    /**
     * 显示评论回复的dialog
     *
     * @param commentId 待回复的 id
     */
    private void showInputDialogWithCommentId(String commentId) {
        mInputDialog = new InputDialog(this).builder().setOnSendClickListener(content -> doCommentReply(commentId, content));
        mInputDialog.setHint("写评论...");
        mInputDialog.setNeedCheckLogin(true);
        mInputDialog.show();
    }

    /**
     * 初始化播放器
     */
    private void initVideoPlayer() {
        PlayerFactory.setPlayManager(SystemPlayerManager.class);
        orientationUtils = new OrientationUtils(this, videoPlayer);
        orientationUtils.setEnable(false);
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化 list
     */
    private void initList() {
        adapter = new NewsDetailCommentAdapter(this, this);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new ItemDecorationFactory.DividerBuilder()
                .dividerHeight(2).dividerColor(ContextCompat.getColor(this, R.color.colorGray))
                .showLastDivider(false)
                .build(commentList);
        commentList.addItemDecoration(itemDecoration);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    private void showBlankView(boolean isShow) {
        if (scrollView != null && bottomLayout != null && videoPlayer != null && blankView != null) {
            if (isShow) {
                scrollView.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                videoPlayer.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
            } else {
                scrollView.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                videoPlayer.setVisibility(View.VISIBLE);
                blankView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.DETAIL_VIDEO)
                    .add("cw_news_id", videoId)
                    .asResponse(VideoDetailEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (data != null) {
                                showBlankView(false);
                                videoEntity = data;
                                updateVideoInfo(videoEntity);
                                requestCommentList();
                            } else {
                                showBlankView(true);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        showBlankView(true);
                    });
        }
    }

    /**
     * 请求评论列表
     */
    private void requestCommentList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.LIST_COMMENT_DETAIL_NEWS)
                    .add("cw_page", "1").add("cw_news_id", videoId).add("cw_limit", String.valueOf(10))
                    .asResponseList(NewsCommentEntity.ItemNewsCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                commentLayout.setVisibility(View.VISIBLE);
                            } else {
                                commentLayout.setVisibility(View.GONE);
                            }
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 更新视频信息
     *
     * @param entity 视频信息实体类
     */
    private void updateVideoInfo(VideoDetailEntity.DataBean entity) {
        videoTitleTv.setText(AppUtil.getNotEmtpyString(entity.getTitle()));
        videoSummaryTv.setText(TextUtils.isEmpty(entity.getSummary()) ? "" : entity.getSummary());
        videoTypeTv.setText(TextUtils.isEmpty(entity.getType_name()) ? "" : entity.getType_name());
        videoCreateTimeTv.setText(TextUtils.isEmpty(entity.getCreate_time()) ? "" : entity.getCreate_time());
        if (!TextUtils.isEmpty(entity.getApp_logo_pic_path())) {
            AppUtil.loadAppsImg(entity.getApp_logo_pic_path(), thumbnailImg);
        } else {
            thumbnailImg.setVisibility(View.GONE);
        }
        isCollection = entity.isIs_collection();
        if (entity.isIs_collection()) {
            collectionImg.like();
        }
        if (!TextUtils.isEmpty(entity.getVideo_path())) {
            AppUtil.loadBigImg(entity.getPic_path(), videoPlayer.getThumbImageView());
            GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
            gsyVideoOption.setThumbImageView(videoPlayer.getThumbImageView())
                    .setIsTouchWiget(true).setRotateViewAuto(false).setLockLand(false)
                    .setAutoFullWithSize(true).setShowFullAnimation(false).setSeekRatio(1)
                    .setCacheWithPlay(EasyPermissions.hasPermissions(this, PERMISSION_LIST) && entity.getVideo_path().endsWith(".mp4"))
                    .setUrl(entity.getVideo_path()).setVideoTitle(entity.getTitle())
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            orientationUtils.setEnable(videoPlayer.isRotateWithSystem());
                            isPlay = true;
                            handler.postDelayed(() -> {
                                if (null != videoPlayer) {
                                    videoDuration = videoPlayer.getDuration();
                                }
                            }, 50);
                        }

                        @Override
                        public void onQuitFullscreen(String url, Object... objects) {
                            super.onQuitFullscreen(url, objects);
                            if (orientationUtils != null) {
                                orientationUtils.backToProtVideo();
                            }
                        }

                        @Override
                        public void onClickSeekbar(String url, Object... objects) {
                            super.onClickSeekbar(url, objects);
                            currentPercent = videoPlayer.getSeekBarPos();
                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects) {
                            super.onAutoComplete(url, objects);
                            currentPercent = 100;
                            if (!isFinishing()) {
                                getCurPlay().release();
                            }
                        }

                        @Override
                        public void onPlayError(String url, Object... objects) {
                            super.onPlayError(url, objects);
                            if(retryTimes <= 2){
                                handler.postDelayed(() -> {
                                    videoPlayer.setUp(url, false, "");
                                    videoPlayer.startPlayLogic();
                                }, 50);
                                retryTimes += 1;
                            }
                        }
                    }).build(videoPlayer);
            videoPlayer.getFullscreenButton().setOnClickListener(v -> {
                orientationUtils.resolveByClick();
                videoPlayer.startWindowFullscreen(VideoDetailActivity.this, true, true);
            });
            videoPlayer.setLockClickListener((view, lock) -> {
                if (orientationUtils != null) {
                    orientationUtils.setEnable(!lock);
                }
            });
            handler.sendEmptyMessageDelayed(1, 500);
        }
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        videoId = getIntent().getStringExtra(Constant.TAG_ID);
        lastTime = System.currentTimeMillis();
        enterTime = System.currentTimeMillis();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            doLogin();
        } else {
            NewsCommentEntity.ItemNewsCommentEntity entity = adapter.getItem(pos);
            if (null != entity && !TextUtils.isEmpty(entity.getId())) {
                showInputDialogWithCommentId(entity.getId());
            }
        }
    }

    /**
     * 进行 评论的回复
     */
    private void doCommentReply(String commentId, String commentContent) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.COMMENT_DETAIL_NEWS_ADD)
                    .add("cw_news_id", videoId).add("cw_comment_id", commentId).add("cw_content", commentContent)
                    .asMsgResponse(MsgResonse.class).as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 显示评论列表
     */
    private void showCommentList() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            intent.setClass(this, MoreCommentListActivity.class);
            intent.putExtra(Constant.TAG_ID, videoId);
        } else {
            intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
    }

    /**
     * 进行评论
     */
    private void sendComment(String content) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.COMMENT_DETAIL_NEWS_ADD)
                    .add("cw_news_id", videoId).add("cw_content", content)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    /**
     * 收藏视频
     */
    private void collectionNews() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                if (isCollection) {
                    RxHttp.postJson(Constant.COLLECTION_CANCEL)
                            .add("cw_news_id", videoId)
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing()) {
                                    collectionImg.unLike();
                                    isCollection = false;
                                }
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                            }, (OnError) error -> {
                                ToastUtil.showShortToast(error.getErrorMsg());
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(this, LoginActivity.class);
                                }
                            });
                } else {
                    RxHttp.postJson(Constant.COLLECTION_NEWS)
                            .add("cw_news_id", videoId)
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing()) {
                                    collectionImg.like();
                                    isCollection = true;
                                }
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                            }, (OnError) error -> {
                                ToastUtil.showShortToast(error.getErrorMsg());
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(this, LoginActivity.class);
                                }
                            });
                }
            } else {
                doLogin();
            }
        }
    }

    private void doPrise() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            RxHttp.postJson(Constant.PRAISE_ADD_DETAIL)
                    .add("cw_news_id", videoId)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                    }, (OnError) error -> {
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            doLogin();
        }
    }

    /**
     * 进行登录
     */
    private void doLogin() {
        IntentUtil.startActivity(this, LoginActivity.class, null);
    }

    /**
     * 添加视频观看行为
     */
    private void addVideoPlayAction() {
        currentPercent = 0;
        currentPercent = videoPlayer.getSeekBarPos();
        long videoPlayDuration = videoDuration * currentPercent / 100;
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.NEWS_ACTION_VIDEO_PLAY_TIME)
                    .add("cw_news_id", videoId).add("cw_play_time", videoPlayDuration).add("cw_type", 2).add("viewFeedids", "").add("videoTime", videoPlayDuration)
                    .asResponse(JsonObject.class).subscribe();
        }
    }

    /**
     * 添加新闻停留行为
     */
    private void addNewsRemainAction() {
        if (NetUtil.isNetworkConnected(this)) {
            long currentTime = System.currentTimeMillis();
            RxHttp.postJson(Constant.NEWS_ACTION_REMAIN)
                    .add("cw_news_id", videoId).add("cw_site_time", (currentTime - lastTime)).add("cw_type", 2).add("viewFeedids", "")
                    .asResponse(JsonObject.class).subscribe();
        }
    }

    /**
     * 进行分享
     */
    private void doShareVideoInfo() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - enterTime >= Constant.MIN_TIME_INTERVAL) {
            if (NetUtil.isNetworkConnected(this)) {
                if (null != videoEntity) {
                    ShareEntity shareEntity = new ShareEntity();
                    shareEntity.setNewsId(TextUtils.isEmpty(videoEntity.getNews_id()) ? "" : videoEntity.getNews_id());
                    shareEntity.setRedirect_url("");
                    shareEntity.setSummary(TextUtils.isEmpty(videoEntity.getSummary()) ? Constant.SUMMARY_SHARE : videoEntity.getSummary());
                    shareEntity.setTitle(TextUtils.isEmpty(videoEntity.getTitle()) ? Constant.TILE_SHARE : videoEntity.getTitle());
                    shareEntity.setUrl(TextUtils.isEmpty(videoEntity.getUrl()) ? "" : videoEntity.getUrl());
                    shareEntity.setType(Constant.TYPE_SHARE_NEWS);
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    bundle.putString(Constant.TAG_SHARE_CONTENT, gson.toJson(shareEntity));
                    IntentUtil.startActivity(this, ShareActivity.class, bundle);
                } else {
                    ToastUtil.showShortToast("无法获取分享信息");
                }
            } else {
                ToastUtil.showShortToast(getString(R.string.network_error));
            }
        }
        enterTime = currentTime;
    }

    /**
     * 接收EventBus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (TextUtils.equals("openReplyDialog", message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    IntentUtil.startActivity(this, LoginActivity.class, null);
                } else {
                    showInputDialogWithCommentId(message.getContent());
                }
            }
        }
    }

    @OnClick({R.id.img_back_header_black_title, R.id.img_comment_detail_video, R.id.edit_comment_detail_video, R.id.txt_comment_all_detail_video, R.id.relate_collection_detail_news, R.id.img_share_detail_video, R.id.news_detail_comment_img, R.id.news_detail_like_img})
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.img_back_header_black_title) {
                finish();
            } else if (id == R.id.img_comment_detail_video || id == R.id.edit_comment_detail_video || id == R.id.news_detail_comment_img) {
                showInputDialog();
            } else if (id == R.id.txt_comment_all_detail_video) {
                showCommentList();
            } else if (id == R.id.relate_collection_detail_news) {
                collectionNews();
            } else if (id == R.id.img_share_detail_video) {
                doShareVideoInfo();
            } else if (id == R.id.news_detail_like_img) {
                doPrise();
            }
        }
        lastClickTime = currentTime;
    }

    @Override
    public void onBlankViewClickListener(View view) {
        if (NetUtil.isNetworkConnected(this)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBlankViewClickTime >= Constant.TIME_INTERVAL) {
                requestData();
            }
            lastBlankViewClickTime = currentTime;
        }
    }

    private GSYVideoPlayer getCurPlay() {
        if (videoPlayer.getFullWindowPlayer() != null) {
            return videoPlayer.getFullWindowPlayer();
        }
        return videoPlayer;
    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        isPause = false;
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        addVideoPlayAction();
        addNewsRemainAction();
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        if (isPlay) {
            getCurPlay().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        GSYVideoManager.releaseAllVideos();
        handler.removeCallbacksAndMessages(null);
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
