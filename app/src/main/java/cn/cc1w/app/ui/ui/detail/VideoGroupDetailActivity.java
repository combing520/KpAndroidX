package cn.cc1w.app.ui.ui.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
//import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.rxjava.rxlife.RxLife;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import net.arvin.itemdecorationhelper.ItemDecorationFactory;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import cn.cc1w.app.ui.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.NewsDetailCommentAdapter;
import cn.cc1w.app.ui.adapter.VideoSelectAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.NewsCommentEntity;
import cn.cc1w.app.ui.entity.VideoGroupEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.home.MoreCommentListActivity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.ThumbUpView.ThumbUpView;
import cn.cc1w.app.ui.widget.input.InputDialog;
import cn.cc1w.app.ui.widget.video.CoverVideoPlayer;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 视频组详情
 */
public class VideoGroupDetailActivity extends CustomActivity implements OnItemClickListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_title_video_group_detail)
    TextView videoTitleTv;
    @BindView(R.id.img_thumbnail_video_group_detail)
    RoundAngleImageView thumbnailImg;
    @BindView(R.id.txt_type_video_group_detail)
    TextView videoTypeTv;
    @BindView(R.id.txt_time_video_group_detail)
    TextView videoCreateTimeTv;
    @BindView(R.id.txt_summary_video_group_detail)
    TextView videoSummaryTv;
    @BindView(R.id.txt_total_cnt_video_group)
    TextView videoCntTv;
    @BindView(R.id.list_video_group)
    RecyclerView videoList;
    @BindView(R.id.list_commend_detail_video_group)
    RecyclerView commentList;
    @BindView(R.id.img_collection_detail_video_group)
    ThumbUpView collectionImg;
    @BindView(R.id.videoPlayer_videoGroup_detail)
    CoverVideoPlayer videoPlayer;
    @BindView(R.id.ll_bottom_detail_video_group)
    LinearLayout bottomLayout;
    @BindView(R.id.scroll_detail_video_group)
    NestedScrollView scrollView;
    @BindView(R.id.ll_comment_detail_news)
    LinearLayout commentLayout;
    private VideoSelectAdapter adapter;
    private NewsDetailCommentAdapter commentAdapter;
    private String videoId;
    private LoadingDialog loading;
    private Unbinder unbinder;
    private String currentVideoUrl;
    private boolean isCollection;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private long enterTime;
    private VideoGroupEntity.ItemVideoGroupEntity videoGroupInfo;
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    private InputDialog mInputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recoverData(savedInstanceState);
        setContentView(R.layout.activity_video_group_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
        initLoading();
        initCollectionInfo();
        initVideoPlayer();
        requestVideoInfo();
        requestCommentList();
        setRedThemeInfo();
    }


    private void setRedThemeInfo() {
        if (Constant.IS_SHOW_RED_MODE) {
            ImmersionBar.with(this).statusBarColor(R.color.color_red_theme).statusBarDarkFont(false)
                    .init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true)
                    .init();
        }
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
     * 初始化list
     */
    private void initList() {
        adapter = new VideoSelectAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoList.setLayoutManager(manager);
        videoList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        commentAdapter = new NewsDetailCommentAdapter(this, this);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setAdapter(commentAdapter);

        RecyclerView.ItemDecoration itemDecoration = new ItemDecorationFactory.DividerBuilder()
                .dividerHeight(2)
                .dividerColor(ContextCompat.getColor(this, R.color.colorGray))
                .showLastDivider(false)
                .build(commentList);
        commentList.addItemDecoration(itemDecoration);
        commentAdapter.setOnItemClickListener((targetView, pos) -> {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                doLogin();
            } else {
                NewsCommentEntity.ItemNewsCommentEntity entity = commentAdapter.getItem(pos);
                if (null != entity && !TextUtils.isEmpty(entity.getId())) {
                    showInputDialogWithCommentId(entity.getId());
                }
            }
        });
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化播放器
     */
    private void initVideoPlayer() {
        PlayerFactory.setPlayManager(SystemPlayerManager.class);
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
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
     * 请求视频信息
     */
    private void requestVideoInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.DETAIL_VIDEO_GROUP)
                    .add("cw_news_id", videoId)
                    .asResponse(VideoGroupEntity.ItemVideoGroupEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            if (data != null) {
                                videoGroupInfo = data;
                                updateVideoInfo(data);
                            }
                        }
                    }, (OnError) error -> {
                        if (!isFinishing() && null != videoPlayer) {
                            videoPlayer.setVisibility(View.GONE);
                        }
                    });
        } else {
            videoPlayer.setVisibility(View.GONE);
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
                                commentAdapter.setData(list);
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
     * 进行 评论的回复
     *
     * @param commentId      被回复者的id
     * @param commentContent 评论的内容
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


    @SuppressLint("SetTextI18n")
    private void updateVideoInfo(VideoGroupEntity.ItemVideoGroupEntity data) {
        videoTitleTv.setText(TextUtils.isEmpty(data.getTitle()) ? "" : data.getTitle());
        videoTypeTv.setText(TextUtils.isEmpty(data.getType_name()) ? "" : data.getType_name());
        videoCreateTimeTv.setText(TextUtils.isEmpty(data.getCreate_time()) ? "" : data.getCreate_time());
        if (null != data.getVideogroup() && !data.getVideogroup().isEmpty()) {
            videoCntTv.setText("分集 (" + data.getVideogroup().size() + ")");
        }
        AppUtil.loadMenuImg(data.getApp_logo_pic_path(), thumbnailImg);
        videoSummaryTv.setText(TextUtils.isEmpty(data.getSummary()) ? "" : data.getSummary());
        isCollection = data.isIs_collection();
        if (null != data.getVideogroup() && !data.getVideogroup().isEmpty()) {
            adapter.setData(data.getVideogroup());
            currentVideoUrl = adapter.getItem(adapter.getSelectPos()).getVideo_path();
            AppUtil.loadBannerImg(adapter.getItem(adapter.getSelectPos()).getPic_path(), videoPlayer.getThumbImageView());
            if (!TextUtils.isEmpty(currentVideoUrl)) {
                videoPlayer.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
                gsyVideoOption.setThumbImageView(videoPlayer.getThumbImageView())
                        .setIsTouchWiget(true)
                        .setRotateViewAuto(false)
                        .setLockLand(false)
                        .setLooping(true)
                        .setAutoFullWithSize(true)
                        .setShowFullAnimation(false)
                        .setNeedLockFull(true)
                        .setUrl(currentVideoUrl)
                        .setVideoAllCallBack(new GSYSampleCallBack() {
                            @Override
                            public void onPrepared(String url, Object... objects) {
                                super.onPrepared(url, objects);
                                orientationUtils.setEnable(true);
                                isPlay = true;
                            }

                            @Override
                            public void onQuitFullscreen(String url, Object... objects) {
                                super.onQuitFullscreen(url, objects);
                                if (orientationUtils != null) {
                                    orientationUtils.backToProtVideo();
                                }
                            }

                            @Override
                            public void onAutoComplete(String url, Object... objects) {
                                super.onAutoComplete(url, objects);
                                if (!isFinishing()) {
                                    getCurPlay().release();
                                }
                            }
                        }).setLockClickListener((view, lock) -> {
                    if (orientationUtils != null) {
                        orientationUtils.setEnable(!lock);
                    }
                }).build(videoPlayer);
                videoPlayer.getFullscreenButton().setOnClickListener(v -> {
                    orientationUtils.resolveByClick();
                    videoPlayer.startWindowFullscreen(VideoGroupDetailActivity.this, true, true);
                });
                videoPlayer.startPlayLogic();
            }
        }
    }

    private GSYVideoPlayer getCurPlay() {
        if (videoPlayer.getFullWindowPlayer() != null) {
            return videoPlayer.getFullWindowPlayer();
        }
        return videoPlayer;
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
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("");
        videoId = getIntent().getStringExtra(Constant.TAG_ID);
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    /**
     * 评论新闻
     */
    private void doCommentNews() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            doLogin();
        } else {
            // 进行评论
            showInputDialog();
        }
    }

    /**
     * 进行评论
     *
     * @param content 评论内容
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
                                if (!isFinishing() && collectionImg != null) {
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
                                if (!isFinishing() && collectionImg != null) {
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
                IntentUtil.startActivity(this, LoginActivity.class, null);
            }
        }
    }

    /**
     * 进行登录
     */
    private void doLogin() {
        IntentUtil.startActivity(this, LoginActivity.class, null);
    }

    /**
     * 接收EventBus
     *
     * @param message 接受到的消息对象
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

    /**
     * 保存播放的信息
     */
    private void savePlayerState() {
    }

    @OnClick({R.id.img_back_header_not_title, R.id.img_comment_detail_video_group,
            R.id.edit_comment_detail_video_group, R.id.relate_collection_detail_news,
            R.id.txt_comment_all_detail_video_group, R.id.img_share_detail_video_group, R.id.img_share_detail_video_group_comment})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.img_comment_detail_video_group || id == R.id.edit_comment_detail_video_group || id == R.id.img_share_detail_video_group_comment) {
            doCommentNews();
        } else if (id == R.id.relate_collection_detail_news) {
            collectionNews();
        } else if (id == R.id.txt_comment_all_detail_video_group) {
            showCommentList();
        } else if (id == R.id.img_share_detail_video_group) {
            doShareVideoInfo();
        }
    }

    /**
     * 分享视频详情
     */
    private void doShareVideoInfo() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - enterTime >= Constant.MIN_TIME_INTERVAL) {
            if (NetUtil.isNetworkConnected(this)) {
                if (null != videoGroupInfo) {
                    ShareEntity shareEntity = new ShareEntity();
                    shareEntity.setNewsId(TextUtils.isEmpty(videoGroupInfo.getNews_id()) ? "" : videoGroupInfo.getNews_id());
                    shareEntity.setRedirect_url("");
                    shareEntity.setSummary(TextUtils.isEmpty(videoGroupInfo.getSummary()) ? Constant.SUMMARY_SHARE : videoGroupInfo.getSummary());
                    shareEntity.setTitle(TextUtils.isEmpty(videoGroupInfo.getTitle()) ? Constant.TILE_SHARE : videoGroupInfo.getTitle());
                    shareEntity.setUrl(TextUtils.isEmpty(videoGroupInfo.getUrl()) ? "" : videoGroupInfo.getUrl());
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

    @Override
    public void onItemClick(View targetView, int pos) {
        int selectPos = adapter.getSelectPos();
        String videoUrl = adapter.getItem(pos).getVideo_path();
        if (selectPos != pos) {
            adapter.setSelectPos(pos);
            currentVideoUrl = videoUrl;
            if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(currentVideoUrl)) {
                if (currentVideoUrl.startsWith("https")) {
                    currentVideoUrl = currentVideoUrl.replace("https", "http");
                }
                videoPlayer.setUp(currentVideoUrl, false, "");
                videoPlayer.startPlayLogic();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void recoverData(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            LogUtil.e("恢复数据");
            if (!TextUtils.isEmpty(savedInstanceState.getString("avatar"))) {
                Constant.CW_AVATAR = savedInstanceState.getString("avatar");
            }
            if (!TextUtils.isEmpty(savedInstanceState.getString("token"))) {
                Constant.CW_AUTHORIZATION = savedInstanceState.getString("token");
                EventBus.getDefault().post(new EventMessage("updateUserInfo", "updateUserInfo"));
            }
            AppUtil.initPublicParams(this);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("avatar", TextUtils.isEmpty(Constant.CW_AVATAR) ? "" : Constant.CW_AVATAR);
        outState.putString("token", TextUtils.isEmpty(Constant.CW_AUTHORIZATION) ? "" : Constant.CW_AUTHORIZATION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != videoPlayer) {
            videoPlayer.onVideoPause();
        }
        super.onResume();
        isPause = true;
    }

    @Override
    protected void onResume() {
        if (null != videoPlayer) {
            videoPlayer.onVideoResume();
        }
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePlayerState();
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
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        if (isPlay) {
            videoPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        GSYVideoManager.releaseAllVideos();
        unbinder.unbind();
        super.onDestroy();
    }
}
