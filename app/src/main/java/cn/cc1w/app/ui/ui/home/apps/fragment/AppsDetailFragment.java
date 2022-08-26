package cn.cc1w.app.ui.ui.home.apps.fragment;

import android.content.Context;
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

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 应用号详情的 Fragment
 *
 * @author kpinfo
 */
public class AppsDetailFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_detail_apps)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_apps_detail)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private String id;
    private boolean isViewCreated;
    private boolean isUiVisible;
    private View decorView;
    private Unbinder unbinder;
    private boolean isFirstLoad = true;
    private Context context;
    private int currentPageIndex = 1;
    private AppsDetailNewsAdapter appsDetailNewsAdapter;

    public AppsDetailFragment() {
    }

    public static AppsDetailFragment newInstance(String title, String id) {
        AppsDetailFragment fragment = new AppsDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constant.TAG_ID, id);
        args.putString(Constant.TAG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(Constant.TAG_ID);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_apps_detail, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();
        requestData();
    }

    /**
     * 初始化
     */
    private void init() {
        initList();
        initBlankView();
    }

    /**
     * 初始化 空白页码
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, getString(R.string.news_apps_none));
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        appsDetailNewsAdapter = new AppsDetailNewsAdapter(context);
        recyclerView.setAdapter(appsDetailNewsAdapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUiVisible = true;
            requestData();
        } else {
            isUiVisible = false;
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (isUiVisible && isViewCreated) {
            if (isFirstLoad && NetUtil.isNetworkConnected(context)) {
                isFirstLoad = false;
                isUiVisible = false;
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
            RxHttp.postJson(Constant.NEWS_RECOMMEND_DETAILS_APPS)
                    .add("cw_page", String.valueOf(currentPageIndex)).add("cw_channel_id", id)
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataList -> {
                        if (null != dataList && !dataList.isEmpty()) {
                            if (null != refreshLayout) {
                                blankView.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);
                                appsDetailNewsAdapter.setData(dataList);
                                currentPageIndex += 1;
                                refreshLayout.setEnableRefresh(true);
                            }
                        } else {
                            if (null != refreshLayout) {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                                refreshLayout.setEnableRefresh(false);
                            }
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                            refreshLayout.setVisibility(View.GONE);
                        }
                        if(blankView != null){
                            blankView.setVisibility(View.VISIBLE);
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
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.NEWS_RECOMMEND_DETAILS_APPS)
                    .add("cw_page", String.valueOf(currentPageIndex)).add("cw_channel_id", id)
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataList -> {
                        if (null != dataList && !dataList.isEmpty()) {
                            appsDetailNewsAdapter.addData(dataList);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}