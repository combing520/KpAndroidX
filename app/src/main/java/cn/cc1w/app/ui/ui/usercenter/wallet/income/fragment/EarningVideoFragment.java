package cn.cc1w.app.ui.ui.usercenter.wallet.income.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.EarnVideoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemGridDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 我的视频
 *
 * @author kpinfo
 */
public class EarningVideoFragment extends Fragment implements BlankViewClickListener, OnRefreshListener, OnLoadMoreListener {
    private View decorView;
    private Unbinder unbinder;
    @BindView(R.id.list_earn_video)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_earn_video)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private EarnVideoAdapter adapter;
    private int currentPageIndex = 1;
    private String paikewUid;
    private Context context;

    public static EarningVideoFragment newInstance() {
        return new EarningVideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EarnVideoAdapter();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_earning_video, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initList();
        initBlankView();
    }

    private void initList() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new SpacesItemGridDecoration(3, AppUtil.dip2px(context, 2), true));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
    }

    private void initBlankView() {
        blankView.setBlankView(getString(R.string.str_video_paikew_none_click));
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 获取 用户提交的 视频
     */
    private void requestVideoList() {
        if (NetUtil.isNetworkConnected(context)) {
            refreshLayout.autoRefresh();
        } else {
            if (null != refreshLayout) {
                refreshLayout.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
            }
            blankView.setBlankView(getString(R.string.network_error_try_click), getString(R.string.try_again));
            blankView.setClickable(true);
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_VIDEO_USER_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, TextUtils.isEmpty(paikewUid) ? Constant.CW_UID_PAIKEW : paikewUid)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (blankView != null && refreshLayout != null) {
                            refreshLayout.finishRefresh();
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                currentPageIndex += 1;
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if (blankView != null && refreshLayout != null) {
                            refreshLayout.finishRefresh();
                            blankView.setVisibility(View.VISIBLE);
                            blankView.setClickable(true);
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
            RxHttp.get(Constant.LIST_VIDEO_USER_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, TextUtils.isEmpty(paikewUid) ? Constant.CW_UID_PAIKEW : paikewUid)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (blankView != null && refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                            if (null != list && !list.isEmpty()) {
                                adapter.addData(list);
                                currentPageIndex += 1;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (TextUtils.equals(Constant.STR_CW_UID_PAIKEW, message.getLabel())) {
                paikewUid = message.getContent();
                requestVideoList();
            }
            else if (TextUtils.equals("networkError", message.getLabel())) {
                if (null != adapter && adapter.getList().isEmpty()) {
                    // 没有网络
                    if (null != refreshLayout) {
                        refreshLayout.setVisibility(View.GONE);
                        blankView.setVisibility(View.VISIBLE);
                    }
                    blankView.setBlankView(getString(R.string.network_error_try_click), getString(R.string.try_again));
                    blankView.setClickable(true);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Override
    public void onBlankViewClickListener(View view) {
        if (NetUtil.isNetworkConnected(context)) {
            blankView.setClickable(false);
            requestUserInfo();
            if (!TextUtils.isEmpty(Constant.CW_UID_PAIKEW)) {
                requestVideoList();
            }
        }
    }

    private void requestUserInfo() {
        EventBus.getDefault().post(new EventMessage("", "updateUserInfo"));
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
}
