package cn.cc1w.app.ui.ui.detail;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gyf.immersionbar.ImmersionBar;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.rxjava.rxlife.RxLife;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.goodView.GoodView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ConnectBrokeRoomEntity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemLiveHostEntity;
import cn.cc1w.app.ui.entity.ItemLiveRoomMessage;
import cn.cc1w.app.ui.entity.LiveDetailEntity;
import cn.cc1w.app.ui.entity.MessageSendResultEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.ui.home.home.live.ChatRoomFragment;
import cn.cc1w.app.ui.ui.home.home.live.HostRoomFragment;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.ThumbUpView.ThumbUpView;
import cn.cc1w.app.ui.widget.input.InputDialog;
import cn.cc1w.app.ui.widget.video.MultipleVideoPlayer;
import cn.cc1w.app.ui.widget.video.PlayBackVideoPlayer;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import cn.jzvd.Jzvd;
import rxhttp.RxHttp;

import static cn.cc1w.app.ui.constants.Constant.HOST_SOCKET;

/**
 * 新闻中的直播详情
 *
 * @author kpinfo
 */
public class LiveDetailActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_host_room_detail_live)
    TextView hostRoomTv;
    @BindView(R.id.txt_chatRoom_detail_live)
    TextView chatRoomTv;
    @BindView(R.id.video_player_detail_live)
    MultipleVideoPlayer videoPlayer;
    @BindView(R.id.video_player_back_detail_live)
    PlayBackVideoPlayer playBackPlayer;
    @BindView(R.id.img_post_detail_live)
    ImageView postImg;
    @BindView(R.id.container_tab_detail_live)
    LinearLayout tabContainerLayout;
    @BindView(R.id.container_detail_live)
    FrameLayout frameLayout;
    @BindView(R.id.blankView_detail_live)
    BlankView blankView;
    @BindView(R.id.img_collection_detail_live)
    ThumbUpView collectionImg;
    @BindView(R.id.img_share_detail_live)
    ImageView shareImg;
    @BindView(R.id.img_prise_detail_live)
    ImageView priseImg;
    @BindView(R.id.txt_cnt_prise_detail_live)
    TextView priseCntTv;
    @BindView(R.id.ll_prise_detail_live)
    RelativeLayout priseLayout;
    @BindView(R.id.ll_bottom_detail_live)
    LinearLayout bottomLayout;
    @BindView(R.id.ll_container_player_detail_live)
    ConstraintLayout playerLayout;
    private boolean isCollectionOriginal;
    private boolean isCollection;
    private HostRoomFragment hostRoomFragment;
    private ChatRoomFragment chatRoomFragment;
    private FragmentManager manager;
    private Fragment currentFragment;
    private static final int POS_ROOM_HOST = 0;
    private static final int POS_ROOM_CHAT = 1;
    private int currentPos = 0;
    private String liveNewsId;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private boolean isAppreciate;
    private WebSocket webSocket;
    private String liveType;
    private static final int TIME_OUT_CONNECT = 15 * 1000;
    private static final int MAX_SIZE_QUEUE = 5;
    private Gson gson;
    private String cwLiveId;
    private static final String TYPE_MESSAGE_SEND = "sendMessage";
    private static final String TYPE_MESSAGE_LIST_GET = "getMessageLists";
    private static final int CODE_RE_CONNECT = 100;
    private long lastTime;
    private LiveDetailEntity.DataBean entity;
    private long lastCommentTime;
    private long lastPriseTime;
    private static final long MIN_CLICK_INTERVAL = 1000;
    private static final String CHARSET = "UTF-8";
    // 1：等待直播 2：正在直播 3：直播已结束
    private static final String TYPE_PLAY_BACK = "3";
    // 直播视频类型
    private static final String TYPE_VIDEO_ACTION_LIVE = "3";
    // 录播视频类型
    private static final String TYPE_VIDEO_ACTION_WATCH = "4";
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == CODE_RE_CONNECT) {
                reConnect();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        gson = new Gson();
        initNavigation();
        initCollectionBtn();
        initBlankView();
        initVideoInfo();
        getLiveDetailInfo();
        initFragment();
        setRedThemeInfo();
    }

    private void setRedThemeInfo() {
        if (Constant.IS_SHOW_RED_MODE) {
            ImmersionBar.with(this).statusBarColor(R.color.color_red_theme).statusBarDarkFont(false).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).init();
        }
    }

    /**
     * 初始化 收藏按钮
     */
    private void initCollectionBtn() {
        collectionImg.setUnLikeType(ThumbUpView.LikeType.broken);
        collectionImg.setCracksColor(Color.parseColor("#b3b3b3"));
        collectionImg.setEdgeColor(Color.parseColor("#b3b3b3"));
        collectionImg.setFillColor(Color.parseColor("#E8382F"));
        collectionImg.setBgColor(Color.parseColor("#F0F0F0"));
        collectionImg.setEnabled(false);
        collectionImg.setClickable(false);
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, "你所查看的直播不存在", "直播");
    }

    /**
     * 连接webSocket
     */
    private synchronized void initWebSocketInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            try {
                WebSocketFactory factory = new WebSocketFactory();
                factory.setVerifyHostname(false);
                webSocket = factory.createSocket(HOST_SOCKET, TIME_OUT_CONNECT).setFrameQueueSize(MAX_SIZE_QUEUE).setMissingCloseFrameAllowed(false).addListener(new WsListener()).sendContinuation().connectAsynchronously();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 重新连接
     */
    private void reConnect() {
        if (NetUtil.isNetworkConnected(this)) {
            try {
                WebSocketFactory factory = new WebSocketFactory();
                factory.setVerifyHostname(false);
                webSocket = factory.createSocket(HOST_SOCKET, TIME_OUT_CONNECT).setFrameQueueSize(MAX_SIZE_QUEUE).setMissingCloseFrameAllowed(false).addListener(new WsListener()).sendContinuation().connectAsynchronously();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化信息
     */
    private void initVideoInfo() {
        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
        playBackPlayer.getTitleTextView().setVisibility(View.GONE);
        playBackPlayer.getBackButton().setVisibility(View.GONE);
    }

    /**
     * 初始化播放器
     */
    private void initVideoPlayer(StandardGSYVideoPlayer videoPlayer) {
        orientationUtils = new OrientationUtils(this, videoPlayer);
        orientationUtils.setEnable(false);
    }

    /**
     * 获取直播详情
     */
    private void getLiveDetailInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.DETAIL_LIVE_NEWS)
                    .add("cw_news_id", liveNewsId)
                    .asResponse(LiveDetailEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            entity = data;
                            if (null != entity) {
                                String headType = entity.getHead_type();
                                // 直播的情况 ； 1 等待直播; 2 正在直播  3 直播已经结束
                                liveType = entity.getType();
                                String picPath = entity.getPic_path();
                                String videoPath = entity.getVideo_path();
                                String livePath = entity.getVideo_path();
                                initWebSocketInfo();
                                cwLiveId = entity.getId();
                                renderImgStatus(entity);
                                getHostRoomDataInfo();
                                if ("pic".equals(headType)) {
                                    bindData2ShotTracker(picPath);
                                } else if ("video".equals(headType)) {
                                    bindData2VideoPlayer(videoPath, picPath);
                                } else if ("livevideo".equals(headType)) {
                                    if (TextUtils.equals(TYPE_PLAY_BACK, entity.getType())) {
                                        bindData2VideoPlayer(videoPath, picPath);
                                    } else {
                                        bindData2Live(livePath, picPath);
                                    }
                                }
                            } else {
                                showBlankView();
                            }
                        }
                    }, (OnError) error -> showBlankView());
        }
    }

    /**
     * 获取主播厅信息
     */
    private void getHostRoomDataInfo() {
        if (!TextUtils.isEmpty(cwLiveId)) {
            EventBus.getDefault().post(new EventMessage("deliveryMessage", cwLiveId));
        }
    }

    private void showBlankView() {
        if (null != playerLayout && null != bottomLayout && null != frameLayout && null != tabContainerLayout && null != postImg && null != blankView) {
            playerLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            postImg.setVisibility(View.GONE);
            tabContainerLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            blankView.setVisibility(View.VISIBLE);
        }
    }

    private void renderImgStatus(LiveDetailEntity.DataBean entity) {
        if (null != entity) {
            isCollection = entity.isIs_collection();
            isCollectionOriginal = entity.isIs_collection();
            priseLayout.setVisibility(View.VISIBLE);
            boolean isPrise = entity.isIs_praise();
            boolean isCollection = entity.isIs_collection();
            AppUtil.loadRes((isPrise ? R.mipmap.ic_prise_live_select : R.mipmap.ic_prise_live_normal), priseImg);
            if (isPrise) {
                priseImg.setClickable(false);
            }
            if (isCollection) {
                collectionImg.like();
            } else {
                collectionImg.unLike();
            }
            priseCntTv.setText(TextUtils.isEmpty(entity.getGood_num()) ? "" : entity.getGood_num());
        }
    }

    /**
     * 绑定直播 信息
     */
    private void bindData2Live(String livePath, String picPath) {
        initVideoPlayer(videoPlayer);
        postImg.setVisibility(View.GONE);
        playBackPlayer.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.VISIBLE);
        setLiveVideoPlayerInfo(livePath, picPath, videoPlayer);
    }

    /**
     * 录播
     */
    private void bindData2VideoPlayer(String videoPath, String picPath) {
        initVideoPlayer(playBackPlayer);
        postImg.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.GONE);
        playBackPlayer.setVisibility(View.VISIBLE);
        setVideoPlayerInfo(videoPath, picPath, playBackPlayer);
    }

    /**
     * 图文直播
     */
    private void bindData2ShotTracker(String picPath) {
        postImg.setVisibility(View.VISIBLE);
        videoPlayer.setVisibility(View.GONE);
        playerLayout.setVisibility(View.GONE);
        AppUtil.loadBigImg(picPath, postImg);
    }

    /**
     * 设置播放信息
     */
    private void setVideoPlayerInfo(String videoPath, String picPath, PlayBackVideoPlayer videoPlayer) {
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        AppUtil.loadBigImg(picPath, videoPlayer.getThumbImageView());
        gsyVideoOption.setThumbImageView(videoPlayer.getThumbImageView()).setIsTouchWiget(true).setRotateViewAuto(false).setLockLand(false).setAutoFullWithSize(false).setShowFullAnimation(false).setNeedLockFull(false).setUrl(videoPath).setCacheWithPlay(false)
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
                            videoPlayer.release();
                        }
                    }
                }).setLockClickListener((view, lock) -> {
            if (orientationUtils != null) {
                orientationUtils.setEnable(!lock);
            }
        }).build(videoPlayer);
        videoPlayer.getFullscreenButton().setOnClickListener(v -> {
            orientationUtils.resolveByClick();
            videoPlayer.startWindowFullscreen(LiveDetailActivity.this, true, true);
        });
        videoPlayer.startPlayLogic();
    }

    /**
     * 设置直播播放信息
     */
    private void setLiveVideoPlayerInfo(String videoPath, String picPath, MultipleVideoPlayer videoPlayer) {
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        AppUtil.loadBigImg(picPath, videoPlayer.getThumbImageView());
        gsyVideoOption.setThumbImageView(videoPlayer.getThumbImageView()).setIsTouchWiget(true).setRotateViewAuto(false).setLockLand(false).setAutoFullWithSize(false).setShowFullAnimation(false).setNeedLockFull(false).setUrl(videoPath).setCacheWithPlay(false)
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
                }).setLockClickListener((view, lock) -> {
            if (orientationUtils != null) {
                orientationUtils.setEnable(!lock);
            }
        }).build(videoPlayer);
        videoPlayer.getFullscreenButton().setOnClickListener(v -> {
            orientationUtils.resolveByClick();
            videoPlayer.startWindowFullscreen(LiveDetailActivity.this, true, true);
        });
        videoPlayer.startPlayLogic();
    }

    /**
     * 初始化 导航头
     */
    private void initNavigation() {
        titleTv.setText("直播");
        liveNewsId = getIntent().getStringExtra(Constant.TAG_ID);
        lastTime = System.currentTimeMillis();
        lastCommentTime = System.currentTimeMillis();
        lastPriseTime = System.currentTimeMillis();
        AppUtil.loadRes(R.mipmap.ic_prise_live_normal, priseImg);
    }

    /**
     * 初始化 Fragment
     */
    private void initFragment() {
        manager = getSupportFragmentManager();
        hostRoomFragment = HostRoomFragment.newInstance();
        chatRoomFragment = ChatRoomFragment.newInstance();
        setSelectTab(POS_ROOM_HOST);
        switchFragment(hostRoomFragment);
    }

    class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
            super.onBinaryMessage(websocket, binary);
            String message = new String(binary, CHARSET);
            LogUtil.e("接收到发送消息 = " + message);
            if (!TextUtils.isEmpty(message)) {
                ItemLiveHostEntity itemLiveHostEntity = JsonUtil.getObject(message, ItemLiveHostEntity.class);
                if (null != itemLiveHostEntity) {
                    String singleMessageMethod = itemLiveHostEntity.getMethod();
                    if (TextUtils.equals(TYPE_MESSAGE_SEND, singleMessageMethod)) {
                        EventBus.getDefault().post(new EventMessage("messageItem", itemLiveHostEntity.getData()));
                    }
                } else {
                    ItemLiveRoomMessage roomMessage = JsonUtil.getObject(message, ItemLiveRoomMessage.class);
                    if (null != roomMessage) {
                        String roomMessageMethod = roomMessage.getMethod();
                        if (TextUtils.equals(TYPE_MESSAGE_LIST_GET, roomMessageMethod)) {
                            EventBus.getDefault().post(new EventMessage("messageList", roomMessage.getData()));
                        }
                    }
                }
            }
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            doAuth();
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            super.onConnectError(websocket, exception);
            if (null != websocket && !isFinishing()) {
                Message message = handler.obtainMessage();
                message.arg1 = CODE_RE_CONNECT;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            LogUtil.e("断开连接");
            if (!isFinishing()) {
                Message message = handler.obtainMessage();
                message.arg1 = CODE_RE_CONNECT;
                handler.sendMessage(message);
            }
        }
    }

    /**
     * 授权
     */
    private void doAuth() {
        ConnectBrokeRoomEntity connectBrokeRoomEntity = new ConnectBrokeRoomEntity();
        connectBrokeRoomEntity.setUrl("/live/auth");
        ConnectBrokeRoomEntity.ArgumentsBean argumentsBean = new ConnectBrokeRoomEntity.ArgumentsBean();
        ConnectBrokeRoomEntity.ArgumentsBean.PostBean postBean = new ConnectBrokeRoomEntity.ArgumentsBean.PostBean();
        postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
        postBean.setSource_id(Constant.ID_APP);
        postBean.setCw_live_id(cwLiveId);
        postBean.setMethod("auth");
        argumentsBean.setPost(postBean);
        connectBrokeRoomEntity.setArguments(argumentsBean);
        String content = gson.toJson(connectBrokeRoomEntity);
        if (null != webSocket) {
            try {
                webSocket.sendBinary(content.getBytes(CHARSET));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置当前选中的 tab
     */
    private void setSelectTab(int pos) {
        if (pos == POS_ROOM_HOST) {
            hostRoomTv.setTextColor(getResources().getColor(R.color.colorPrimary));
            chatRoomTv.setTextColor(getResources().getColor(R.color.colorTxtTitle));
        } else if (pos == POS_ROOM_CHAT) {
            hostRoomTv.setTextColor(getResources().getColor(R.color.colorTxtTitle));
            chatRoomTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    /**
     * 切换 Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (currentFragment != targetFragment) {
            if (!targetFragment.isAdded()) {
                if (null != currentFragment) {
                    manager.beginTransaction().hide(currentFragment).commit();
                }
                manager.beginTransaction().add(R.id.container_detail_live, targetFragment).commit();
            } else {
                manager.beginTransaction().hide(currentFragment).show(targetFragment).commit();
            }
            currentFragment = targetFragment;
        }
    }

    /**
     * 设置选中的 fragment
     */
    private void setSelectedPage(int selectPos) {
        if (currentPos != selectPos) {
            if (selectPos == POS_ROOM_HOST) {
                switchFragment(hostRoomFragment);
            } else if (selectPos == POS_ROOM_CHAT) {
                switchFragment(chatRoomFragment);
            }
        }
        setSelectTab(selectPos);
        currentPos = selectPos;
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_host_room_detail_live, R.id.txt_chatRoom_detail_live, R.id.edit_detail_live, R.id.img_share_detail_live, R.id.ll_prise_detail_live, R.id.relate_collection_detail_news})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_host_room_detail_live) {
            setSelectedPage(POS_ROOM_HOST);
        } else if (id == R.id.txt_chatRoom_detail_live) {
            setSelectedPage(POS_ROOM_CHAT);
        } else if (id == R.id.edit_detail_live) {
            if (null != entity) {
                showInputDialog();
            }
        } else if (id == R.id.img_share_detail_live) {
            shareLiveInfo();
        } else if (id == R.id.ll_prise_detail_live) {
            doAppreciate();
        } else if (id == R.id.relate_collection_detail_news) {
            doCollectionLive();
        }
    }

    /**
     * 收操
     */
    private void doCollectionLive() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                if (null == entity || TextUtils.isEmpty(entity.getNews_id())) {
                    return;
                }
                if (isCollection) {
                    RxHttp.postJson(Constant.COLLECTION_CANCEL)
                            .add("cw_news_id", entity.getNews_id())
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing()) {
                                    if (null != collectionImg) {
                                        collectionImg.unLike();
                                    }
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
                            .add("cw_news_id", entity.getNews_id())
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing()) {
                                    if (null != collectionImg) {
                                        collectionImg.like();
                                    }
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

    /**
     * 点赞
     */
    private void doAppreciate() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPriseTime >= MIN_CLICK_INTERVAL) {
            if (null == entity || TextUtils.isEmpty(entity.getNews_id())) {
                ToastUtil.showShortToast("获取直播信息错误");
                return;
            }
            if (!NetUtil.isNetworkConnected(this)) {
                ToastUtil.showShortToast(getString(R.string.network_error));
            } else {
                if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    if (isAppreciate) {
                        return;
                    }
                    priseImg.setClickable(false);
                    RxHttp.postJson(Constant.PRAISE_ADD_DETAIL)
                            .add("cw_news_id", entity.getNews_id())
                            .asResponse(MessageSendResultEntity.DataBean.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing() && data != null) {
                                    priseCntTv.setText(TextUtils.isEmpty(data.getData()) ? "" : data.getData());
                                    AppUtil.loadRes(R.mipmap.ic_prise_live_select, priseImg);
                                    GoodView goodView = new GoodView(LiveDetailActivity.this);
                                    goodView.setTextInfo("+1", Color.parseColor("#f66467"), 14);
                                    goodView.show(priseImg);
                                    isAppreciate = true;
                                }
                            }, (OnError) error -> {
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(this, LoginActivity.class);
                                }
                                ToastUtil.showShortToast(error.getErrorMsg());
                            });
                } else {
                    IntentUtil.startActivity(this, LoginActivity.class, null);
                }
            }
        }
        lastPriseTime = currentTime;
    }

    /**
     * 显示评论框
     */
    private void showInputDialog() {
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            InputDialog inputDialog = new InputDialog(this).builder().setOnSendClickListener(this::sendChatMessage);
            inputDialog.setHint("写评论...");
            inputDialog.setNeedCheckLogin(true);
            inputDialog.show();
        } else {
            IntentUtil.startActivity(this, LoginActivity.class);
        }
    }

    /**
     * 分享直播信息
     */
    private void shareLiveInfo() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCommentTime >= MIN_CLICK_INTERVAL) {
            if (null != entity) {
                Gson gson = new Gson();
                Bundle bundle = new Bundle();
                ShareEntity shareEntity = new ShareEntity();
                shareEntity.setNewsId(TextUtils.isEmpty(entity.getNews_id()) ? "" : entity.getNews_id());
                shareEntity.setRedirect_url("");
                shareEntity.setSummary(TextUtils.isEmpty(entity.getInfo()) ? "" : entity.getInfo());
                shareEntity.setTitle(TextUtils.isEmpty(entity.getTitle()) ? "" : entity.getTitle());
                shareEntity.setUrl(TextUtils.isEmpty(entity.getUrl()) ? "" : entity.getUrl());
                shareEntity.setType(Constant.TYPE_SHARE_NEWS);
                String shareContent = gson.toJson(shareEntity);
                bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                IntentUtil.startActivity(this, ShareActivity.class, bundle);
            } else {
                ToastUtil.showShortToast(getString(R.string.error_content_share));
            }
        }
        lastCommentTime = currentTime;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPlay && !isPause) {
            if (videoPlayer.getVisibility() == View.VISIBLE) {
                videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
            } else {
                playBackPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
            }
        }
    }

    @Override
    protected void onPause() {
        LogUtil.e("onPause--->>> 直播详情  videoPlayer.isInPlayingState() = " + videoPlayer.isInPlayingState());
        if (videoPlayer.getVisibility() == View.VISIBLE) {
            if (null != entity && !TextUtils.isEmpty(entity.getVideo_path()) && null != videoPlayer) {
                LogUtil.e("onPause --->>>> 直播详情");
                videoPlayer.onVideoPause();
            }
        } else {
            if (null != entity && !TextUtils.isEmpty(entity.getVideo_path()) && null != playBackPlayer && playBackPlayer.getVisibility() == View.VISIBLE) {
                LogUtil.e("onPause --->>>> 直播详情");
                playBackPlayer.onVideoPause();
            }
        }
        isPause = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (videoPlayer.getVisibility() == View.VISIBLE) {
            if (null != entity && !TextUtils.isEmpty(entity.getVideo_path()) && null != videoPlayer) {
                videoPlayer.onVideoResume();
            }
        } else {
            if (null != entity && !TextUtils.isEmpty(entity.getVideo_path()) && null != playBackPlayer && playBackPlayer.getVisibility() == View.VISIBLE) {
                playBackPlayer.onVideoResume();
            }
        }
        isPause = false;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (null != orientationUtils && orientationUtils.getIsLand() == 1) {
            orientationUtils.backToProtVideo();
            return;
        }
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("sendChatMessage", message.getLabel())) {
                sendChatMessage(message.getContent());
            } else if (TextUtils.equals(TYPE_MESSAGE_LIST_GET, message.getLabel())) {
                getMessageList(message.getContent());
            }
        }
    }

    /**
     * 获取聊天室内容列表
     */
    private void getMessageList(String pageIndex) {
        ConnectBrokeRoomEntity connectBrokeRoomEntity = new ConnectBrokeRoomEntity();
        connectBrokeRoomEntity.setUrl("/live/getMessageLists");
        ConnectBrokeRoomEntity.ArgumentsBean argumentsBean = new ConnectBrokeRoomEntity.ArgumentsBean();
        ConnectBrokeRoomEntity.ArgumentsBean.PostBean postBean = new ConnectBrokeRoomEntity.ArgumentsBean.PostBean();
        postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
        postBean.setCw_live_id(cwLiveId);
        postBean.setSource_id(Constant.ID_APP);
        postBean.setMethod("getMessageLists");
        postBean.setCw_page(pageIndex);
        argumentsBean.setPost(postBean);
        connectBrokeRoomEntity.setArguments(argumentsBean);
        String info = gson.toJson(connectBrokeRoomEntity);
        if (null != webSocket) {
            try {
                if (!TextUtils.isEmpty(info)) {
                    webSocket.sendBinary(info.getBytes(CHARSET));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送 一条聊天信息
     *
     * @param content 聊天内容
     */
    private void sendChatMessage(String content) {
        ConnectBrokeRoomEntity connectBrokeRoomEntity = new ConnectBrokeRoomEntity();
        connectBrokeRoomEntity.setUrl("/live/sendMessage");
        ConnectBrokeRoomEntity.ArgumentsBean argumentsBean = new ConnectBrokeRoomEntity.ArgumentsBean();
        ConnectBrokeRoomEntity.ArgumentsBean.PostBean postBean = new ConnectBrokeRoomEntity.ArgumentsBean.PostBean();
        postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
        postBean.setSource_id(Constant.ID_APP);
        postBean.setCw_live_id(cwLiveId);
        postBean.setMethod("sendMessage");
        postBean.setCw_type("1");
        postBean.setCw_content(content);
        argumentsBean.setPost(postBean);
        connectBrokeRoomEntity.setArguments(argumentsBean);
        String info = gson.toJson(connectBrokeRoomEntity);
        if (null != webSocket) {
            try {
                webSocket.sendBinary(info.getBytes(CHARSET));
                if (currentPos == POS_ROOM_HOST) {
                    setSelectedPage(POS_ROOM_CHAT);
                    setSelectTab(POS_ROOM_CHAT);
                    currentPos = POS_ROOM_CHAT;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加直播行为   liveType  直播的情况 ； 1 等待直播; 2 正在直播  3 直播已经结束
     */
    private void addLiveAction() {
        if (TextUtils.equals("1", liveType) || TextUtils.equals("2", liveType)) {
            if (NetUtil.isNetworkConnected(this)) {
                long currentTime = System.currentTimeMillis();
                RxHttp.postJson(Constant.REMAIN_LIVE_VIDEO)
                        .add("cw_live_id", cwLiveId).add("cw_site_time", (currentTime - lastTime)).add("viewFeedids", "").add("cw_type", TYPE_VIDEO_ACTION_LIVE)
                        .asResponse(JsonObject.class).subscribe();
            }
        } else {
            if (NetUtil.isNetworkConnected(this)) {
                long currentTime = System.currentTimeMillis();
                RxHttp.postJson(Constant.PLAYBACK_LIVE_VIDEO)
                        .add("cw_live_id", cwLiveId).add("cw_play_time", (currentTime - lastTime)).add("viewFeedids", "").add("cw_type", TYPE_VIDEO_ACTION_WATCH)
                        .asResponse(JsonObject.class).subscribe();
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (isCollectionOriginal != isCollection) {
            EventBus.getDefault().post(new EventMessage("changeCollection", "changeCollection"));
        }
        addLiveAction();
        if (null != videoPlayer) {
            videoPlayer.getCurrentPlayer().release();
        }
        if (null != playBackPlayer) {
            playBackPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        if (null != webSocket) {
            webSocket.disconnect();
            webSocket = null;
        }
        GSYVideoManager.releaseAllVideos();
        Jzvd.releaseAllVideos();
        handler.removeCallbacksAndMessages(null);
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}