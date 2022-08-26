package cn.cc1w.app.ui.ui.home.record.fragment;

import cn.cc1w.app.ui.R;

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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.EarnPhotoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemPhotoRecordEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemGridDecoration;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 最爱 （包含视频和照片）
 *
 * @author kpinfo
 */
public class FavoriteFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_earn_like)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_earn_like)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View decorView;
    private Unbinder unbinder;
    private int currentPageIndex = 1;
    private String paikewUid;
    private EarnPhotoAdapter adapter;
    private Context context;

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_favorite, container, false);
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
        requestFavoriteList();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(getString(R.string.str_like_paikew_none));
    }

    /**
     * 初始化List
     */
    private void initList() {
        recyclerView.setMotionEventSplittingEnabled(false);
        adapter = new EarnPhotoAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new SpacesItemGridDecoration(3, AppUtil.dip2px(context, 2), true));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 请求照片数据信息
     */
    private void requestFavoriteList() {
        if (NetUtil.isNetworkConnected(context)) {
            if (null != adapter) {
                refreshLayout.autoRefresh();
            }
        } else {
            refreshLayout.setVisibility(View.GONE);
            blankView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_FAVORITE_USER_PAIKEW)
                    .add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, TextUtils.isEmpty(paikewUid) ? Constant.CW_UID_PAIKEW : paikewUid)
                    .asResponseList(ItemPhotoRecordEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (blankView != null && refreshLayout != null) {
                            refreshLayout.finishRefresh();
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                blankView.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);
                                currentPageIndex += 1;
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
            RxHttp.get(Constant.LIST_FAVORITE_USER_PAIKEW)
                    .add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, TextUtils.isEmpty(paikewUid) ? Constant.CW_UID_PAIKEW : paikewUid)
                    .asResponseList(ItemPhotoRecordEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (recyclerView != null) {
                            if (null != list && !list.isEmpty()) {
                                adapter.addData(list);
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(true);
                                }
                                currentPageIndex += 1;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && TextUtils.equals(Constant.STR_CW_UID_PAIKEW, message.getLabel())) {
                LogUtil.e("照片 id = " + paikewUid);
                paikewUid = message.getContent();
                requestFavoriteList();
            }
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}