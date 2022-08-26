package cn.cc1w.app.ui.ui.usercenter.integral.fragment;

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

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.IntegralRankAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.IntegralRankEntity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 总积分排名
 *
 * @author kpinfo
 */
public class TotalRankFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_rank_total)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private Unbinder unbinder;
    private View decorView;
    private boolean isViewCreated;
    private boolean isUiVisible;
    private boolean isFirstLoad = true;
    private Context context;
    private int currentPageIndex = 1;
    private final String PARAMS_QUERY = "ranking_total";
    private IntegralRankAdapter adapter;

    public static TotalRankFragment newInstance() {
        return new TotalRankFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_total_rank, container, false);
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
        initList();
        requestData();
    }


    private void initList() {
        adapter = new IntegralRankAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
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
            if (isFirstLoad) {
                isFirstLoad = false;
                isUiVisible = false;
                isViewCreated = false;
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
            RxHttp.postJson(Constant.RANK_INTEGRAL).add("cw_page", String.valueOf(currentPageIndex)).add("cw_query", PARAMS_QUERY)
                    .asResponseList(IntegralRankEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                            if (null != list && !list.isEmpty()) {
                                currentPageIndex += 1;
                                adapter.setData(list);
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
            }
        }
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.RANK_INTEGRAL).add("cw_page", String.valueOf(currentPageIndex)).add("cw_query", PARAMS_QUERY)
                    .asResponseList(IntegralRankEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                            if (null != list && !list.isEmpty()) {
                                currentPageIndex += 1;
                                adapter.addData(list);
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setEnableLoadMore(false);
                            }
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}