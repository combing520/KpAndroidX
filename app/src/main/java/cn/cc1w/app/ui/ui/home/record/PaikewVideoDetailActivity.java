package cn.cc1w.app.ui.ui.home.record;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.rxjava.rxlife.RxLife;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadCircleView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import cn.cc1w.app.ui.adapter.PaiKeVideoPlayAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.widget.video.PaikewVideoPlayer;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;
import rxhttp.RxHttpNoBodyParam;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 拍客视频详情
 *
 * @author kpinfo
 */
public class PaikewVideoDetailActivity extends CustomActivity implements BlankViewClickListener {
    private ViewPager2 viewpager;
    private BlankView blankView;
    private LinearLayout footView;
    private TextView loadingTv;
    private LoadCircleView loadingView;
    private int id;
    private boolean isUserPaikewWork = false;
    private int currentPageIndex = 1;
    private long lastClickTime = System.currentTimeMillis();
    private long lastEntityTime = System.currentTimeMillis();
    private LoadingDialog loading;
    private PaikewVideoPlayer mVideoPlayer;
    private List<ItemVideoListEntity.DataBean> dataSet = new ArrayList<>();
    private PaiKeVideoPlayAdapter mAdapter;
    private int mCurPos = 0;
    private final Handler handler = new Handler(Looper.myLooper());
    private boolean isLastPageSwiped = false;
    private int counterPageScroll = 0;
    private boolean isLoadMoreData = false;
    private static final int SIZE = 10;
    private boolean hasMoreData = true;
    private static final String TXT_LOADING = "加载中...";
    private static final String TXT_FINISH = "加载完成";
    private XPopup.Builder builder = null;
    private BasePopupView popView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paikew_video_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(false).init();
        EventBus.getDefault().register(this);
        initView();
        initViewPager2();
        setUpViewPlayer();
        initLoading();
        initBlankView();
        initData();
        fetchData();
    }

    private void initView() {
        footView = findViewById(R.id.loading_container_video_paikew);
        loadingTv = findViewById(R.id.loading_txt_container_video_paikew);
        viewpager = findViewById(R.id.viewPager_detail_video_paikew);
        blankView = findViewById(R.id.video_detail_blank);
        loadingView = findViewById(R.id.loading_video_paikew);
        findViewById(R.id.img_back_detail_video_paikew).setOnClickListener(v -> finish());
    }

    private void initViewPager2() {
        viewpager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewpager.registerOnPageChangeCallback(pageChangeCallBack);
        mAdapter = new PaiKeVideoPlayAdapter(this, this);
        viewpager.setAdapter(mAdapter);
    }

    private void initPlayerOption() {
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);
        VideoOptionModel videoOptionModel2 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        VideoOptionModel videoOptionModel3 = new VideoOptionModel(1, "probesize", 150);
        VideoOptionModel videoOptionModel4 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        VideoOptionModel videoOptionModel5 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);

        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        list.add(videoOptionModel2);
        list.add(videoOptionModel3);
        list.add(videoOptionModel4);
        list.add(videoOptionModel5);
        GSYVideoManager.instance().setOptionModelList(list);
    }

    private void setUpViewPlayer() {
        initPlayerOption();
        mVideoPlayer = new PaikewVideoPlayer(this);
        mVideoPlayer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mVideoPlayer.setLooping(true);
        mVideoPlayer.setLockLand(false);
        mVideoPlayer.setShowFullAnimation(false);
        mVideoPlayer.setRotateViewAuto(false);
        mVideoPlayer.setIsTouchWiget(true);
        mVideoPlayer.getTitleTextView().setVisibility(View.GONE);
        mVideoPlayer.getBackButton().setVisibility(View.GONE);
        mVideoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                if (!dataSet.isEmpty() && !TextUtils.isEmpty(url)) {
                    for (ItemVideoListEntity.DataBean item : dataSet) {
                        if (TextUtils.equals(url, item.getVideo_path())) {
                            setVideoClick(item.getId());
                        }
                    }
                }
            }

            @Override
            public void onPlayError(String url, Object... objects) {
                super.onPlayError(url, objects);
                if (!dataSet.isEmpty() && !TextUtils.isEmpty(url)) {
                    int pos = -1;
                    for (int i = 0; i < dataSet.size(); i++) {
                        if (TextUtils.equals(url, dataSet.get(i).getVideo_path())) {
                            pos = i;
                        }
                    }
                    if (pos != -1 && pos < dataSet.size()) {
                        doVideoPlay(pos);
                    }
                }
            }
        });
    }

    private void setVideoClick(int id) {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.CALL_BACK_PLAY_VIDEO_PAIKE).add(Constant.TAG_ID, id).asMsgResponse(MsgResonse.class).subscribe();
        }
    }

    private final ViewPager2.OnPageChangeCallback pageChangeCallBack = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (position == dataSet.size() - 1 && !isLastPageSwiped) {
                if (counterPageScroll != 0) {
                    isLastPageSwiped = true;
                    loadMoreData();
                }
                counterPageScroll++;
            } else {
                isLastPageSwiped = false;
                counterPageScroll = 0;
            }
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (position == mCurPos) {
                return;
            } else {
                viewpager.post(() -> doVideoPlay(position));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    };

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, "你所查看的视频不存在,点击重试", "点击重试");
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        id = getIntent().getIntExtra(Constant.TAG_ID, 0);
        isUserPaikewWork = getIntent().getBooleanExtra("isUserPaikewWork", false);
    }

    /**
     * 请求数据
     */
    private void fetchData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            if (loading != null) {
                loading.show();
            }
            RxHttpNoBodyParam params = RxHttp.get(Constant.LIST_DETAIL_VIDEO_PAIKEW);
            params.add(Constant.TAG_ID, id);
            if (!TextUtils.isEmpty(Constant.CW_UID_PAIKEW) && isUserPaikewWork) {
                params.add(Constant.STR_CW_UID_SYSTEM, Constant.CW_UID_PAIKEW);
            }
            params.add("size", SIZE);
            params.add(Constant.STR_PAGE, currentPageIndex);
            params.asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (loading != null && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (data != null && !data.isEmpty()) {
                                showBlankView(false);
                                dataSet = data;
                                mAdapter.setData(dataSet);
                                viewpager.post(() -> doVideoPlay(0));
                                currentPageIndex += 1;
                            } else {
                                showBlankView(true);
                            }
                        }
                    }, (OnError) error -> {
                        showBlankView(true);
                        if (loading != null && loading.isShow()) {
                            loading.close();
                        }
                    });
        } else {
            showBlankView(true);
        }
    }

    private void refreshData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            RxHttpNoBodyParam params = RxHttp.get(Constant.LIST_DETAIL_VIDEO_PAIKEW);
            params.add(Constant.TAG_ID, id);
            if (!TextUtils.isEmpty(Constant.CW_UID_PAIKEW) && isUserPaikewWork) {
                params.add(Constant.STR_CW_UID_SYSTEM, Constant.CW_UID_PAIKEW);
            }
            params.add("size", SIZE);
            params.add(Constant.STR_PAGE, currentPageIndex);
            params.asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            if (data != null && !data.isEmpty()) {
                                dataSet = data;
                                mAdapter.setData(dataSet);
                                if (mCurPos < data.size()) {
                                    handler.postDelayed(() -> doVideoPlay(mCurPos), 100);
                                }
                                currentPageIndex += 1;
                            }
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this) && !isLoadMoreData && hasMoreData) {
            footView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.VISIBLE);
            loadingTv.setText(TXT_LOADING);
            isLoadMoreData = true;
            RxHttpNoBodyParam params = RxHttp.get(Constant.LIST_DETAIL_VIDEO_PAIKEW);
            params.add(Constant.TAG_ID, id);
            if (!TextUtils.isEmpty(Constant.CW_UID_PAIKEW) && isUserPaikewWork) {
                params.add(Constant.STR_CW_UID_SYSTEM, Constant.CW_UID_PAIKEW);
            }
            params.add("size", SIZE);
            params.add(Constant.STR_PAGE, currentPageIndex);
            params.asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            loadingView.setVisibility(View.GONE);
                            loadingTv.setText(TXT_FINISH);
                            hideFootView();
                            isLoadMoreData = false;
                            if (data != null && !data.isEmpty()) {
                                showBlankView(false);
                                dataSet.addAll(data);
                                mAdapter.addData(dataSet);
                                currentPageIndex += 1;
                                hasMoreData = true;
                            } else {
                                hasMoreData = false;
                            }
                        }
                    }, (OnError) error -> {
                        isLoadMoreData = false;
                        loadingView.setVisibility(View.GONE);
                        loadingTv.setText(TXT_FINISH);
                        hideFootView();
                    });
        }
    }

    private void showCommentDialog(String paikeId) {
        PaiKeCommentPop popupView = new PaiKeCommentPop(PaikewVideoDetailActivity.this);
        popupView.setPaiKeId(paikeId);
        popupView.setLifeCycleOwner(this);
        if (builder == null) {
            builder = new XPopup.Builder(PaikewVideoDetailActivity.this).moveUpToKeyboard(false);
        }
        popView = builder.asCustom(popupView);
        popView.show();
    }

    private void hideFootView() {
        handler.postDelayed(() -> {
            if (footView != null) {
                footView.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private void showBlankView(boolean isShow) {
        if (blankView != null && viewpager != null) {
            if (isShow) {
                viewpager.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
            } else {
                viewpager.setVisibility(View.VISIBLE);
                blankView.setVisibility(View.GONE);
            }
        }
    }

    private void doVideoPlay(int pos) {
        RecyclerView recycleImp = (RecyclerView) viewpager.getChildAt(0);
        for (int i = 0; i < recycleImp.getChildCount(); i++) {
            ViewGroup itemView = (ViewGroup) recycleImp.getChildAt(i);
            PaiKeVideoPlayAdapter.ViewHolder holder = (PaiKeVideoPlayAdapter.ViewHolder) itemView.getTag();
            if (holder.getMPosition() == pos) {
                AppUtil.removeViewFormParent(mVideoPlayer);
                itemView.addView(mVideoPlayer, 0);
                GSYVideoManager.releaseAllVideos();
                ItemVideoListEntity.DataBean item = dataSet.get(pos);
                addRemainAction(item);
                mVideoPlayer.setUp(item.getVideo_path(), false, item.getTitle());
                AppUtil.loadImgWithPlaceholder(item.getCover(), mVideoPlayer.getThumbImageView());
                mVideoPlayer.setThumbImageView(mVideoPlayer.getThumbImageView());
                handler.postDelayed(() -> mVideoPlayer.startPlayLogic(), 50);
                mCurPos = pos;
            }
        }
    }

    private void addRemainAction(ItemVideoListEntity.DataBean entity) {
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - lastEntityTime;
        if (NetUtil.isNetworkConnected(this) && null != entity) {
            RxHttp.postJson(Constant.REMAIN_PAIKEW).add("shoot_id", entity.getId()).add("cw_site_time", duration)
                    .asMsgResponse(MsgResonse.class).subscribe();
        }
        lastEntityTime = currentTime;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (!TextUtils.isEmpty(message.getLabel())) {
            LogUtil.e("onMessage  ---- >>> " + message.getLabel());
            if (TextUtils.equals("updateUserInfo", message.getLabel()) || TextUtils.equals("updateMobile", message.getLabel())) {
                refreshData();
            } else if (TextUtils.equals("showCommentDialog", message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                showCommentDialog(message.getContent());
            }
        }
    }

    @Override
    public void onBlankViewClickListener(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            fetchData();
        }
        lastClickTime = currentTime;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        mVideoPlayer.release();
        GSYVideoManager.releaseAllVideos();
        viewpager.unregisterOnPageChangeCallback(pageChangeCallBack);
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        if (loading != null && loading.isShow()) {
            loading.close();
        }
        if (popView != null) {
            popView.destroy();
        }
        LogUtil.e("拍客视频播放 onDestroy");
        super.onDestroy();
    }
}