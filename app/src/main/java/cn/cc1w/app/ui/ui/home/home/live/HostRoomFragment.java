package cn.cc1w.app.ui.ui.home.home.live;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.live.HostRoomMessageAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.jzvd.Jzvd;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static cn.cc1w.app.ui.constants.Constant.mCurrentVideoPlayer;

import cn.cc1w.app.ui.R;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 主播室
 *
 * @author kpinfo
 */
public class HostRoomFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_host_room)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View decorView;
    private Unbinder unbinder;
    private String cw_live_id;
    private int cw_page = 1;
    private HostRoomMessageAdapter adapter;
    private LinearLayoutManager manager;
    private int currentPlayPos = 1;
    private Context mContext;

    public static HostRoomFragment newInstance() {
        HostRoomFragment fragment = new HostRoomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EventBus.getDefault().register(this);
            adapter = new HostRoomMessageAdapter(mContext);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_host_room, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initList();
    }

    /**
     * 初始化 list
     */
    private void initList() {
        manager = new LinearLayoutManager(mContext);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    int currentPos = getCurrentViewIndex();
                    if (recyclerView.getAdapter() == null || currentPos >= recyclerView.getAdapter().getItemCount()) {
                        return;
                    }
                    try {
                        int viewType = recyclerView.getAdapter().getItemViewType(currentPos);
                        if (viewType == HostRoomMessageAdapter.TYPE_VIDEO) {
                            if (null != manager) {
                                View view = manager.findViewByPosition(currentPos);
                                if (null != view) {
                                    if (null != mCurrentVideoPlayer) {
                                        if (currentPlayPos != currentPos) {
//                                                mCurrentVideoPlayer.release();
                                            mCurrentVideoPlayer.mediaInterface.release();
                                            mCurrentVideoPlayer = null;
                                            mCurrentVideoPlayer = view.findViewById(R.id.video_post_video_live_host);
                                            mCurrentVideoPlayer.startVideo();
                                        }
                                    } else {
                                        mCurrentVideoPlayer = view.findViewById(R.id.video_post_video_live_host);
                                        mCurrentVideoPlayer.startVideo();
                                    }
//                                        currentVideoUlr = adapter.getItem(currentPos).getJson().get(0).getUrl();
                                    currentPlayPos = currentPos;
                                }
                            }
                        } else {
                            // 关闭列表播放
                            if (null != mCurrentVideoPlayer) {
                                mCurrentVideoPlayer.onStatePause();
//                                    mCurrentVideoPlayer.release();
                                mCurrentVideoPlayer.mediaInterface.release();
                                mCurrentVideoPlayer = null;
                                currentPlayPos = -1;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private int getCurrentViewIndex() {
        int firstVisibleItem = manager.findFirstVisibleItemPosition();
        int lastVisibleItem = manager.findLastVisibleItemPosition();
        int currentIndex = firstVisibleItem;
        int lastHeight = 0;
        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            View view = manager.getChildAt(i - firstVisibleItem);
            if (null == view) {
                continue;
            }
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            Rect localRect = new Rect();
            view.getLocalVisibleRect(localRect);
            int showHeight = localRect.bottom - localRect.top;
            if (showHeight > lastHeight) {
                currentIndex = i;
                lastHeight = showHeight;
            }
        }
        if (currentIndex < 0) {
            currentIndex = 0;
        }
        return currentIndex;
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(mContext)) {
            RxHttp.postJson(Constant.DETAIL_HOST_LIVE).add("cw_live_id", cw_live_id).add("cw_page", cw_page)
                    .asResponseList(LiveHostEntity.LiveHostItemEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            cw_page += 1;
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 初始化数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(mContext)) {
            cw_page = 1;
            RxHttp.postJson(Constant.DETAIL_HOST_LIVE).add("cw_live_id", cw_live_id).add("cw_page", cw_page)
                    .asResponseList(LiveHostEntity.LiveHostItemEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            cw_page += 1;
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
            }
        }
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(mContext)) {
            RxHttp.postJson(Constant.DETAIL_HOST_LIVE).add("cw_live_id", cw_live_id).add("cw_page", cw_page)
                    .asResponseList(LiveHostEntity.LiveHostItemEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            cw_page += 1;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishLoadMore();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishLoadMore();
                        }
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishLoadMore();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        Jzvd.releaseAllVideos();
        KpLog.getInstance().onAppViewScreenOut(mContext, getClass().getSimpleName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            // 接送消息
            if (TextUtils.equals("deliveryMessage", message.getLabel())) {
                cw_live_id = message.getContent();
                requestData();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData();
    }

    @Override
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(mContext, getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        if (null != mCurrentVideoPlayer) {
            mCurrentVideoPlayer.mediaInterface.release();
            mCurrentVideoPlayer = null;
        }
        try {
            GSYVideoManager.releaseAllVideos();
            Jzvd.releaseAllVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }
        unbinder.unbind();
        super.onDestroyView();
    }
}