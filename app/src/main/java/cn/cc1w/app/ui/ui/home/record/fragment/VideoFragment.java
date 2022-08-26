package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


import net.arvin.itemdecorationhelper.ItemDecorationFactory;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.RecordVideoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 视频
 *
 * @author kpinfo
 */
public class VideoFragment extends Fragment implements OnItemClickListener, BlankViewClickListener, OnRefreshListener, OnLoadMoreListener {
    private View decorView;
    private Unbinder unbinder;
    @BindView(R.id.list_video_record)
    RecyclerView recyclerView;
    @BindView(R.id.blank_video_record)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.img_banner_video_record)
    ImageView topBannerImg;
    private RecordVideoAdapter adapter;
    private int currentPageIndex = 1;
    private boolean isFirstLoad = true;
    private boolean isFirstLoadData = true;
    private Context context;
    private long lastClickTime = System.currentTimeMillis();

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RecordVideoAdapter();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_video, container, false);
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
        initBlankView();
        AppUtil.loadRes(R.mipmap.ic_video_paikew_top, topBannerImg);
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.video_empty, "暂无视频~ 点击重试", getString(R.string.try_again));
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化 list
     */
    private void initList() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        RecyclerView.ItemDecoration itemDecoration = new ItemDecorationFactory.DividerBuilder()
                .dividerHeight(2)
                .dividerColor(ContextCompat.getColor(context, R.color.colorWhite))
                .showLastDivider(false)
                .build(recyclerView);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
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
        if (isVisibleToUser && isFirstLoadData) {
            isFirstLoadData = false;
            requestData();
        }
    }

    /**
     * 请求照片详情
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(context)) {
            if (isFirstLoad && recyclerView != null) {
                isFirstLoad = false;
                refreshLayout.autoRefresh();
            }
        } else {
            if (blankView != null) {
                blankView.setClickable(true);
            }
            if (null == adapter.getList() || adapter.getList().isEmpty()) {
                if (blankView != null) {
                    blankView.setVisibility(View.VISIBLE);
                }
                if (refreshLayout != null) {
                    refreshLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_VIDEO_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex))
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                            if (null != blankView && blankView.getVisibility() == View.VISIBLE) {
                                blankView.setVisibility(View.GONE);
                            }
                            if (null != refreshLayout && refreshLayout.getVisibility() == View.GONE) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                refreshLayout.setEnableLoadMore(true);
                            }
                        } else {
                            if (null != blankView && blankView.getVisibility() == View.GONE) {
                                blankView.setVisibility(View.VISIBLE);
                            }
                            if (null != refreshLayout && refreshLayout.getVisibility() == View.VISIBLE) {
                                refreshLayout.setVisibility(View.GONE);
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        if (null != blankView && blankView.getVisibility() == View.GONE) {
                            blankView.setVisibility(View.VISIBLE);
                        }
                        if (null != refreshLayout && refreshLayout.getVisibility() == View.VISIBLE) {
                            refreshLayout.setVisibility(View.GONE);
                        }
                    });
        } else { // 没有网络
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
            RxHttp.get(Constant.LIST_VIDEO_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex))
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
        } else { // 没有网络
            if (null != refreshLayout) {
                refreshLayout.finishLoadMore();
            }
        }
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        ItemVideoListEntity.DataBean entity = adapter.getItem(pos);
        if (null != entity) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, PaikewVideoDetailActivity.class);
            intent.putExtra(Constant.TAG_ID, entity.getId());
            intent.putExtra("isUserPaikewWork", false);
            startActivity(intent);
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
    public void onBlankViewClickListener(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.TIME_INTERVAL) {
            if (NetUtil.isNetworkConnected(context)) {
                if (refreshLayout != null && blankView != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    blankView.setVisibility(View.GONE);
                    isFirstLoad = true;
                    requestData();
                }
            }
        }
        lastClickTime = currentTime;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}