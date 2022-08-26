package cn.cc1w.app.ui.ui.detail;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;

import android.Manifest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.NewsDetailCommentAdapter;
import cn.cc1w.app.ui.adapter.advertisement.PostBottomAdvertisementNewAdapter;
import cn.cc1w.app.ui.adapter.advertisement.PostTopAdvertisementNewAdapter;
import cn.cc1w.app.ui.adapter.newDetail.NewsDetailRelateAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.HtmlFormatUtil;
import cn.cc1w.app.ui.utils.ManagedMediaPlayer;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.NewsCommentEntity;
import cn.cc1w.app.ui.entity.NewsDetailEntity;
import cn.cc1w.app.ui.entity.NewsDetailRelatedEntity;
import cn.cc1w.app.ui.entity.PostAdEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.home.MoreCommentListActivity;
import cn.cc1w.app.ui.ui.share.NewsShareActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.MWebView;
import cn.cc1w.app.ui.widget.input.InputDialog;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;

/**
 * 开屏新闻 7.0的 新闻详情
 *
 * @author kpinfo
 */
public class NewsDetailNewActivity extends CustomActivity implements BlankViewClickListener, OnItemClickListener, EasyPermissions.PermissionCallbacks {
    private Unbinder unbinder;
    @BindView(R.id.news_detail_ad_front_recycle)
    RecyclerView mFrontAdvertisementRecycleView; // 文首 贴片广告 ； 要求 宽：高 = 7.5： 1
    @BindView(R.id.news_detail_title_tv)
    TextView mTitleTv;
    @BindView(R.id.news_detail_type_tv)
    TextView mSourceTv;
    @BindView(R.id.news_detail_create_time_tv)
    TextView mCreateTimeTv;
    @BindView(R.id.news_detail_browse_cnt_tv)
    TextView mBrowseCntTv;
    @BindView(R.id.news_detail_container)
    MWebView mWebView;
    @BindView(R.id.news_detail_ad_back_recycle)
    RecyclerView mBackAdvertisementRecycleView; // 文末 贴片广告 ； 要求 宽：高 = 2.5： 1
    @BindView(R.id.news_detail_relate_container)
    LinearLayout mRelateNewsContainer;
    @BindView(R.id.news_detail_relate_recycle)
    RecyclerView mRelateNewsRecycleView;
    @BindView(R.id.news_detail_comment_container)
    LinearLayout mCommentContainer;
    @BindView(R.id.news_detail_comment_recycle)
    RecyclerView mCommentRecycleView;
    @BindView(R.id.news_detail_recommend_container)
    LinearLayout mRecommendContainer;
    @BindView(R.id.news_detail_recommend_recycle)
    RecyclerView mRecommendNewsRecycleView;
    @BindView(R.id.news_detail_blank)
    BlankView mBlankView;
    @BindView(R.id.news_detail_scroll)
    NestedScrollView mScrollView;
    @BindView(R.id.news_detail_bottom_ll)
    LinearLayout mBottomContainer;
    @BindView(R.id.news_detail_like_img)
    ImageView mPriseBtn;

    @BindView(R.id.news_detail_audio_iv)
    ImageView mAudioBtn;
    @BindView(R.id.audiobarLayout)
    View audiobarLayout;
    @BindView(R.id.audiobar_play_iv)
    ImageView audiobarPlayIv;
    @BindView(R.id.audiobar_time_tv)
    TextView audiobarTimeTv;
    @BindView(R.id.audiobar_seekbar)
    SeekBar audiobarSeekbar;
    @BindView(R.id.audiobar_close_iv)
    View audiobarCloseIv;
    private LoadingDialog mLoading;
    private NewsDetailCommentAdapter mCommentAdapter;
    private NewsDetailRelateAdapter mRecommendListAdapter;
    private NewsDetailRelateAdapter mRelateListAdapter;
    private PostTopAdvertisementNewAdapter mTopAdvertiseAdapter;
    private PostBottomAdvertisementNewAdapter mBottomAdvertiseAdapter;
    private long mLastClickTime;
    private long mEnterTime;
    private String mNewsId;
    private boolean isActivityFront;
    private boolean isBlankViewCanClick = false;
    private NewsDetailEntity.DataBean mNewsDetailEntity;
    private static final int PROGRESS_SHOW = 70;
    private static final String POS_TOP = "1";  // 广告位置：0文章后 1文章前
    private static final String POS_BOTTOM = "0";
    private boolean isCollection;
    private static final String[] PERMISSION_LIST = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,};
    private InputDialog mInputDialog;
    private boolean imgClickFunctionHasInitialized = false;
    private MediaPlayer mMediaPlayer;
    private String currentAudioSize = "";
    private Timer mTimer;
    private boolean isSeek = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail_new);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initStatusBar();
        initView();
        mWebView.setOnPageFinishListener(new MWebView.OnPageFinishListener() {
            @Override
            public void onFinish(String title) {

            }

            @Override
            public void onProgressChanged(int newProgress) {
                if (newProgress >= PROGRESS_SHOW) {
                    mLoading.close();
                }
                if (newProgress == 100) {
                    if (!imgClickFunctionHasInitialized) {
                        imgClickFunctionHasInitialized = true;
                        HtmlFormatUtil.addImageClickListener(mWebView);
                        fetchData();
                    }
                }
            }
        });
        initData();
    }

    private void initView() {
        initLoading();
        initBlankView();
        initRecycleView();
    }

    private void initLoading() {
        mLoading = AppUtil.getLoading(this);
    }

    private void initBlankView() {
        mBlankView.setBlankView(R.mipmap.news_empty, "你所查看的新闻不存在,点击重试", "点击重试");
        mBlankView.setOnBlankViewClickListener(this);
    }

    private void initRecycleView() {
        mCommentAdapter = new NewsDetailCommentAdapter(this, this);
        mCommentAdapter.setOnItemClickListener(this);
        mCommentRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mCommentRecycleView.setAdapter(mCommentAdapter);
        mCommentRecycleView.setNestedScrollingEnabled(false);
        mCommentRecycleView.setMotionEventSplittingEnabled(false);
        mRecommendListAdapter = new NewsDetailRelateAdapter(this);
        mRecommendNewsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecommendNewsRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecommendNewsRecycleView.setAdapter(mRecommendListAdapter);
        mRecommendNewsRecycleView.setNestedScrollingEnabled(false);
        mRecommendNewsRecycleView.setMotionEventSplittingEnabled(false);
        mRelateListAdapter = new NewsDetailRelateAdapter(this);
        mRelateNewsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRelateNewsRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRelateNewsRecycleView.setAdapter(mRelateListAdapter);
        mRelateNewsRecycleView.setNestedScrollingEnabled(false);
        mRelateNewsRecycleView.setMotionEventSplittingEnabled(false);
        mTopAdvertiseAdapter = new PostTopAdvertisementNewAdapter(this);
        mFrontAdvertisementRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mFrontAdvertisementRecycleView.setAdapter(mTopAdvertiseAdapter);
        mFrontAdvertisementRecycleView.setNestedScrollingEnabled(false);
        mFrontAdvertisementRecycleView.setMotionEventSplittingEnabled(false);
        mBottomAdvertiseAdapter = new PostBottomAdvertisementNewAdapter(this);
        mBackAdvertisementRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mBackAdvertisementRecycleView.setAdapter(mBottomAdvertiseAdapter);
        mBackAdvertisementRecycleView.setNestedScrollingEnabled(false);
        mBackAdvertisementRecycleView.setMotionEventSplittingEnabled(false);
    }

    private void initData() {
        AppUtil.initPublicParams(this);
        initTimeInfo();
        initIntentInfo();
    }

    /**
     * 初始化时间信息
     */
    private void initTimeInfo() {
        mLastClickTime = System.currentTimeMillis();
        mEnterTime = mLastClickTime;
    }

    /**
     * 初始化 intent 信息
     */
    private void initIntentInfo() {
        mNewsId = getIntent().getStringExtra(Constant.TAG_ID);
        getNewsDetailInfo(mNewsId);
    }

    /**
     * 初始化 状态栏
     */
    private void initStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).keyboardEnable(true).init();
    }

    /**
     * 显示评论列表
     */
    private void showCommentList() {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_ID, mNewsId);
        IntentUtil.startActivity(this, MoreCommentListActivity.class, bundle);
    }

    /**
     * 分享
     */
    private void doShare() {
        if (null != mNewsDetailEntity) {
            Gson gson = new Gson();
            Bundle bundle = new Bundle();
            ShareEntity shareEntity = new ShareEntity();
            shareEntity.setNewsId(TextUtils.isEmpty(mNewsDetailEntity.getNews_id()) ? "" : mNewsDetailEntity.getNews_id());
            shareEntity.setRedirect_url("");
            shareEntity.setSummary(TextUtils.isEmpty(mNewsDetailEntity.getSummary()) ? "" : mNewsDetailEntity.getSummary());
            shareEntity.setTitle(TextUtils.isEmpty(mNewsDetailEntity.getTitle()) ? "" : mNewsDetailEntity.getTitle());
            shareEntity.setUrl(TextUtils.isEmpty(mNewsDetailEntity.getUrl()) ? "" : mNewsDetailEntity.getUrl());
            shareEntity.setType(Constant.TYPE_SHARE_NEWS);
            shareEntity.setCollect(isCollection);
            String shareContent = gson.toJson(shareEntity);
            bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
            IntentUtil.startActivity(this, NewsShareActivity.class, bundle);
        } else {
            ToastUtil.showShortToast(getString(R.string.error_content_share));
        }
    }

    /**
     * 显示评论框
     */
    private void showInputDialog() {
        mInputDialog = new InputDialog(this).builder().setOnSendClickListener(this::doComment);
        mInputDialog.setHint("写评论...");
        mInputDialog.setNeedCheckLogin(true);
        mInputDialog.show();
    }

    /**
     * 显示评论回复的dialog
     */
    private void showInputDialogWithCommentId(String commentId) {
        mInputDialog = new InputDialog(this).builder().setOnSendClickListener(content -> doCommentReply(commentId, content));
        mInputDialog.setHint("写评论...");
        mInputDialog.setNeedCheckLogin(true);
        mInputDialog.show();
    }

    /**
     * 设置 空白页
     */
    private void showBlankView(boolean isShow) {
        if (mBlankView != null && mScrollView != null && mBottomContainer != null) {
            if (isShow) {
                isBlankViewCanClick = true;
                mBlankView.setVisibility(View.VISIBLE);
                mScrollView.setVisibility(View.GONE);
                mBottomContainer.setVisibility(View.GONE);
            } else {
                isBlankViewCanClick = false;
                mBlankView.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                mBottomContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private final Handler timeHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int progress = mMediaPlayer.getCurrentPosition();
            audiobarSeekbar.setProgress(progress);
            audiobarTimeTv.setText(duration2String(Long.parseLong(String.valueOf(progress))) + " / " + currentAudioSize);
        }
    };

    private void initTimer() {
        mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isSeek) {
                    return;
                }
                timeHandler.sendMessage(timeHandler.obtainMessage());
            }
        };
        mTimer.schedule(mTimerTask, 0, 300);
    }

    private void initMediePlayer() {
        mMediaPlayer = new ManagedMediaPlayer();
    }

    public void playAudio() {
        if (mNewsDetailEntity.getAudio() != null && !TextUtils.isEmpty(mNewsDetailEntity.getAudio().getUrl())) {
            audiobarLayout.setVisibility(View.VISIBLE);
            currentAudioSize = duration2String(Long.parseLong(mNewsDetailEntity.getAudio().getTime()) * 1000L);
            audiobarTimeTv.setText("00:00 / " + currentAudioSize);
            audiobarSeekbar.setMax(Integer.parseInt(mNewsDetailEntity.getAudio().getTime()) * 1000);
            initTimer();
            mAudioBtn.setVisibility(View.GONE);
            audiobarPlayIv.setImageResource(R.mipmap.ic_audio_pause);
            audiobarSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSeek = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mMediaPlayer.seekTo(seekBar.getProgress());
                    isSeek = false;
                }
            });
            try {
                mMediaPlayer.reset();
                String path = mNewsDetailEntity.getAudio().getUrl();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepare();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnPreparedListener(mp -> {
                    timeHandler.postDelayed(() -> mMediaPlayer.start(), 100);
                });
                mMediaPlayer.setOnCompletionListener(mp -> {
                    if (audiobarPlayIv != null) {
                        audiobarPlayIv.setImageResource(R.mipmap.ic_audio_play);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String duration2String(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);
        DecimalFormat format = new DecimalFormat("00");
        return format.format(minute) + ":" + format.format(second);
    }

    public boolean isAudioPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pauseAudio() {
        if (mMediaPlayer != null && isAudioPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resumeAudio() {
        if (mMediaPlayer != null && !isAudioPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            if (mTimer != null) {
                mTimer.cancel();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 更新 新闻详情信息
     */
    private void updateNewsTitleInfo(NewsDetailEntity.DataBean item) {
        imgClickFunctionHasInitialized = false;
        mTitleTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
        mCreateTimeTv.setText(TextUtils.isEmpty(item.getCreate_time()) ? "" : item.getCreate_time());
        mSourceTv.setText(TextUtils.isEmpty(item.getApp_name()) ? "" : item.getApp_name());
        isCollection = item.isIs_collection();
        if (TextUtils.isEmpty(item.getClick_num())) {
            mBrowseCntTv.setVisibility(View.GONE);
        } else {
            mBrowseCntTv.setText(item.getClick_num());
            mBrowseCntTv.setVisibility(View.VISIBLE);
        }
        //20220801 新增audio图标
        if (item.getAudio() != null && !TextUtils.isEmpty(item.getAudio().getUrl())) {
            mAudioBtn.setVisibility(View.VISIBLE);
        } else {
            mAudioBtn.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(item.getContent())) {
            String mimeType = "text/html";
            String encoding = "utf-8";
            mWebView.loadDataWithBaseURL(null, AppUtil.getHtmlData(item.getContent()), mimeType, encoding, null);
        } else {
            mLoading.close();
        }
    }

    /**
     * 获取新闻详情信息
     */
    private void getNewsDetailInfo(String newsId) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != mLoading && !mLoading.isShow()) {
                mLoading.show();
            }
            RxHttp.postJson(Constant.DETAIL_NEWS_NORMAL)
                    .add("cw_news_id", newsId)
                    .asResponse(NewsDetailEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            if (data != null) {
                                mNewsDetailEntity = data;
                                showBlankView(false);
                                updateNewsTitleInfo(mNewsDetailEntity);
                            } else {
                                if (null != mLoading && mLoading.isShow()) {
                                    mLoading.close();
                                }
                                showBlankView(true);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != mLoading && mLoading.isShow()) {
                            mLoading.close();
                        }
                        showBlankView(true);
                    });
        } else {
            showBlankView(true);
        }
    }

    private void fetchData() {
        getPostAdvertisement();
        requestCommentList();
        requestRelateAndRecommendNews();
    }

    /**
     * 获取贴片广告信息
     */
    private void getPostAdvertisement() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.AD_DETAIL_NEWS)
                    .add("cw_news_id", mNewsId)
                    .asResponseList(PostAdEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (data != null && !isFinishing()) {
                            List<PostAdEntity.DataBean> topList = new ArrayList<>();
                            List<PostAdEntity.DataBean> bottomList = new ArrayList<>();
                            for (PostAdEntity.DataBean item : data) {
                                if (TextUtils.equals(POS_TOP, item.getPosition())) {
                                    topList.add(item);
                                } else if (TextUtils.equals(POS_BOTTOM, item.getPosition())) {
                                    bottomList.add(item);
                                }
                            }
                            mTopAdvertiseAdapter.setData(topList);
                            mBottomAdvertiseAdapter.setData(bottomList);
                            if (!topList.isEmpty()) {
                                mFrontAdvertisementRecycleView.setVisibility(View.VISIBLE);
                            } else {
                                mFrontAdvertisementRecycleView.setVisibility(View.GONE);
                            }
                            if (!bottomList.isEmpty()) {
                                mBackAdvertisementRecycleView.setVisibility(View.VISIBLE);
                            } else {
                                mBackAdvertisementRecycleView.setVisibility(View.GONE);
                            }
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 获取评论列表
     */
    private void requestCommentList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.LIST_COMMENT_DETAIL_NEWS)
                    .add("cw_page", "1").add("cw_news_id", mNewsId).add("cw_limit", String.valueOf(10))
                    .asResponseList(NewsCommentEntity.ItemNewsCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                mCommentAdapter.setData(list);
                                mCommentContainer.setVisibility(View.VISIBLE);
                            } else {
                                mCommentContainer.setVisibility(View.GONE);
                            }
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 获取关联新闻
     */
    private void requestRelateAndRecommendNews() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.LIST_NEWS_DETAIL_RELATE)
                    .add("cw_news_id", mNewsId)
                    .asResponse(NewsDetailRelatedEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            if (data != null) {
                                if ((null == data.getRelated() || data.getRelated().isEmpty())) {
                                    if (mRelateNewsContainer != null) {
                                        mRelateNewsContainer.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (null != mRelateNewsContainer) {
                                        mRelateNewsContainer.setVisibility(View.VISIBLE);
                                    }
                                    mRelateListAdapter.setData(data.getRelated());
                                }
                                if ((null == data.getRecommend() || data.getRecommend().isEmpty())) {
                                    if (null != mRecommendContainer) {
                                        mRecommendContainer.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (null != mRecommendContainer) {
                                        mRecommendContainer.setVisibility(View.VISIBLE);
                                    }
                                    mRecommendListAdapter.setData(data.getRecommend());
                                }
                            } else {
                                if (null != mRecommendContainer) {
                                    mRecommendContainer.setVisibility(View.GONE);
                                }
                                if (null != mRelateNewsContainer) {
                                    mRelateNewsContainer.setVisibility(View.GONE);
                                }
                            }
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 评论
     */
    private void doComment(String content) {
        if (null != mLoading && !mLoading.isShow()) {
            mLoading.show();
        }
        RxHttp.postJson(Constant.COMMENT_DETAIL_NEWS_ADD)
                .add("cw_news_id", mNewsId).add("cw_content", content)
                .asMsgResponse(MsgResonse.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    if (null != mLoading) {
                        mLoading.close();
                    }
                    if (!isFinishing()) {
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                    }
                }, (OnError) error -> {
                    if (null != mLoading) {
                        mLoading.close();
                    }
                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(this, LoginActivity.class);
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                });
    }

    /**
     * 对 新闻 / 新闻的评论进行 回复
     */
    private void doCommentReply(String commentId, String content) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (null != mLoading) {
                mLoading.show();
            }
            RxHttp.postJson(Constant.COMMENT_DETAIL_NEWS_ADD)
                    .add("cw_news_id", mNewsId).add("cw_comment_id", commentId).add("cw_content", content)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != mLoading) {
                            mLoading.close();
                        }
                        if (!isFinishing()) {
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                        }
                    }, (OnError) error -> {
                        if (null != mLoading) {
                            mLoading.close();
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
     * 添加新闻停留行为
     */
    private void addNewsRemainAction() {
        long currentTime = System.currentTimeMillis();
        RxHttp.postJson(Constant.NEWS_ACTION_REMAIN)
                .add("cw_news_id", mNewsId).add("cw_site_time", (currentTime - mEnterTime)).add("cw_type", 1)
                .asResponse(JsonObject.class).subscribe();
    }

    /**
     * 点赞
     */
    private void doAppreciate() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                if (mNewsDetailEntity == null || mNewsDetailEntity.isIs_praise()) {
                    return;
                }
                if (null != mLoading && !mLoading.isShow()) {
                    mLoading.show();
                }
                mPriseBtn.setClickable(false);
                RxHttp.postJson(Constant.PRAISE_ADD_DETAIL)
                        .add("cw_news_id", mNewsId)
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != mLoading && mLoading.isShow()) {
                                mLoading.close();
                            }
                            if (!isFinishing()) {
                                mPriseBtn.setClickable(true);
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                            }
                        }, (OnError) error -> {
                            if (null != mLoading && mLoading.isShow()) {
                                mLoading.close();
                            }
                            if (!isFinishing()) {
                                ToastUtil.showShortToast(error.getErrorMsg());
                                mPriseBtn.setClickable(true);
                            }
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(this, LoginActivity.class);
                            }
                        });
            } else {
                IntentUtil.startActivity(this, LoginActivity.class, null);
            }
        }
    }

    /**
     * 收藏新闻
     */
    private void collectionNews() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                if (isCollection) {
                    RxHttp.postJson(Constant.COLLECTION_CANCEL)
                            .add("cw_news_id", mNewsId)
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing()) {
                                    isCollection = false;
                                    if (data != null) {
                                        ToastUtil.showShortToast(data.getMessage());
                                    }
                                }
                            }, (OnError) error -> {
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(this, LoginActivity.class);
                                }
                                ToastUtil.showShortToast(error.getErrorMsg());
                            });
                } else {
                    RxHttp.postJson(Constant.COLLECTION_NEWS)
                            .add("cw_news_id", mNewsId)
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing()) {
                                    isCollection = true;
                                    if (data != null) {
                                        ToastUtil.showShortToast(data.getMessage());
                                    }
                                }
                            }, (OnError) error -> {
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(this, LoginActivity.class);
                                }
                                ToastUtil.showShortToast(error.getErrorMsg());
                            });
                }
            } else {
                IntentUtil.startActivity(this, LoginActivity.class, null);
            }
        }
    }

    @Override
    public void onBlankViewClickListener(View view) {
        if (isBlankViewCanClick && NetUtil.isNetworkConnected(this)) {
            showBlankView(false);
            isBlankViewCanClick = false;
            getNewsDetailInfo(mNewsId);
        }
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            NewsCommentEntity.ItemNewsCommentEntity entity = mCommentAdapter.getItem(pos);
            if (null != entity && !TextUtils.isEmpty(entity.getId())) {
                showInputDialogWithCommentId(entity.getId());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (TextUtils.equals("openReplyDialog", message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    IntentUtil.startActivity(this, LoginActivity.class, null);
                } else {
                    showInputDialogWithCommentId(message.getContent());
                }
            } else if (TextUtils.equals(Constant.TAG_DOWNLOAD_COMPLETE, message.getLabel())) {
                if (isActivityFront) {
                    ToastUtil.showShortToast("下载完成");
                }
            } else if (TextUtils.equals(Constant.TAG_REFRESH, message.getLabel())) {
                getNewsDetailInfo(mNewsId);
            } else if (TextUtils.equals(Constant.TAG_COLLECTION, message.getLabel())) {
                collectionNews();
            } else if (TextUtils.equals("updateUserInfo", message.getLabel())) {
                requestCommentList();
            } else if (TextUtils.equals(Constant.TAG_DOWNLOAD_COMPLETE, message.getLabel())) {
                if (isActivityFront) {
                    ToastUtil.showShortToast("下载完成");
                }
            }
        }
    }

    @OnClick({
            R.id.news_detail_back_navigation_img, R.id.news_detail_share_navigation_img, R.id.news_detail_comment_ll, R.id.news_detail_comment_img,
            R.id.news_detail_like_img, R.id.news_detail_share_img, R.id.news_detail_commend_more_btn, R.id.news_detail_audio_iv, R.id.audiobar_close_iv, R.id.audiobar_play_iv
    })
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime >= Constant.MIN_TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.news_detail_back_navigation_img) {
                finish();
            } else if (id == R.id.news_detail_share_navigation_img || id == R.id.news_detail_share_img) {
                doShare();
            } else if (id == R.id.news_detail_comment_ll || id == R.id.news_detail_comment_img) {
                showInputDialog();
            } else if (id == R.id.news_detail_commend_more_btn) {
                showCommentList();
            } else if (id == R.id.news_detail_like_img) {
                doAppreciate();
            } else if (id == R.id.news_detail_audio_iv) {
                if (mMediaPlayer == null) {
                    initMediePlayer();
                }
                playAudio();
            } else if (id == R.id.audiobar_close_iv) {
                mAudioBtn.setVisibility(View.VISIBLE);
                audiobarLayout.setVisibility(View.GONE);
                stopMusic();
            } else if (id == R.id.audiobar_play_iv) {
                if (mMediaPlayer != null) {
                    if (isAudioPlaying()) {
                        audiobarPlayIv.setImageResource(R.mipmap.ic_audio_play);
                        pauseAudio();
                    } else {
                        audiobarPlayIv.setImageResource(R.mipmap.ic_audio_pause);
                        resumeAudio();
                    }
                }
            }
        }
        mLastClickTime = currentTime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == MWebView.CODE_REQUEST_PERMISSION && EasyPermissions.hasPermissions(this, PERMISSION_LIST) && mWebView != null) {
            if (!TextUtils.isEmpty(mWebView.getFilePath()) && mWebView.getFilePath().startsWith("http")) {
                ToastUtil.showShortToast("下载开始");
                mWebView.downLoadFile();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == MWebView.CODE_REQUEST_PERMISSION && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            ToastUtil.showShortToast("权限拒绝，下载失败");
            new AppSettingsDialog.Builder(this).setTitle("权限请求").setRationale("开屏新闻需要获取SD卡读写权限用于读写缓存").build().show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityFront = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityFront = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        addNewsRemainAction();
    }

    @Override
    protected void onDestroy() {
        stopMusic();
        if (null != mWebView) {
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        if (null != mLoading && mLoading.isShow()) {
            mLoading.close();
        }
        timeHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        super.onDestroy();
    }
}