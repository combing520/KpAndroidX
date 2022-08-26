package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.topic.rank.VideoRankAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 热门视频排行榜
 *
 * @author kpinfo
 */
public class HotVideoRankFragment extends Fragment implements OnItemClickListener, OnRefreshListener, OnLoadMoreListener, BlankViewClickListener {
    @BindView(R.id.list_video_hot_rank)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.blank)
    BlankView blankView;
    private View decorView;
    private Unbinder unbinder;
    private VideoRankAdapter adapter;
    private int currentPageIndex = 1;
    private boolean isViewCreated;
    private boolean isUIVisible;
    private boolean isFirstLoad = true;
    private Context context;
    private long lastClickTime = System.currentTimeMillis();

    public HotVideoRankFragment() {

    }

    public static HotVideoRankFragment newInstance() {
        HotVideoRankFragment fragment = new HotVideoRankFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_hot_video_rank, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initBlankView();
        initList();
        requestData();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.img_empty, "暂无内容~ 点击重试", getString(R.string.try_again));
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化 list
     */
    private void initList() {
        adapter = new VideoRankAdapter();
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        adapter.setOnItemClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 当前页面是否可见
     *
     * @param isVisibleToUser 当前页面的可见情况
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUIVisible = true;
            requestData();
        } else {
            isUIVisible = false;
        }
    }

    private void showBlankView(boolean isShow) {
        if (blankView != null && refreshLayout != null) {
            if (isShow) {
                refreshLayout.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
            } else {
                blankView.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (isUIVisible && isViewCreated) {
            if (isFirstLoad && NetUtil.isNetworkConnected(context)) {
                isFirstLoad = false;
                isUIVisible = false;
                refreshLayout.autoRefresh();
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_VIDEO_PAIKEW).add(Constant.STR_PAGE, currentPageIndex).add("is_topic", 1)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
                            showBlankView(false);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
                            showBlankView(true);
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        showBlankView(true);
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
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.get(Constant.LIST_VIDEO_PAIKEW).add(Constant.STR_PAGE, currentPageIndex).add("is_topic", 1)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.addData(list);
                            currentPageIndex += 1;
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
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData();
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        ItemVideoListEntity.DataBean entity = adapter.getItem(pos);
        if (null != entity) {
            Intent intent = new Intent();
            intent.setClass(context, PaikewVideoDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constant.TAG_ID, entity.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(context, getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        KpLog.getInstance().onAppViewScreenOut(context, getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onBlankViewClickListener(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            isFirstLoad = true;
            isViewCreated = true;
            showBlankView(false);
            requestData();
        }
        lastClickTime = currentTime;
    }
}