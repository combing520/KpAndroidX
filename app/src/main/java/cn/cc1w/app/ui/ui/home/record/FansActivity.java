package cn.cc1w.app.ui.ui.home.record;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.FansAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.entity.ItemFansEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;

/**
 * 粉丝
 *
 * @author kpinfo
 */
public class FansActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.list_fans)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_fans)
    BlankView blankView;
    private FansAdapter adapter;
    private int currentPageIndex = 1;
    private LoadingDialog loading;
    private boolean isFistLoad = true;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        iniNavigation();
        initLoading();
        initBlankView();
        initList();
        requestFansInfo();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.fans_empty, getString(R.string.fans_none));
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化RecycleView
     */
    private void initList() {
        adapter = new FansAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(this, 1), 1));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 初始化导航
     */
    private void iniNavigation() {
        titleTv.setText("粉丝");
        uid = getIntent().getStringExtra(Constant.STR_CW_UID_SYSTEM);
    }

    /**
     * 请求粉丝信息
     */
    private void requestFansInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading && isFistLoad) {
                loading.show();
                isFistLoad = false;
            }
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_FANS_PAIKEW).add(Constant.STR_CW_UID_SYSTEM, uid).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(ItemFansEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (refreshLayout != null && blankView != null) {
                            refreshLayout.finishRefresh();
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                currentPageIndex += 1;
                                blankView.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                blankView.setVisibility(View.VISIBLE);
                                refreshLayout.setVisibility(View.GONE);
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (refreshLayout != null && blankView != null) {
                            blankView.setVisibility(View.VISIBLE);
                            refreshLayout.setVisibility(View.GONE);
                            refreshLayout.finishRefresh();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            if (!isFinishing()) {
                blankView.setBlankView(R.mipmap.img_network_error, getString(R.string.fans_none));
                blankView.setVisibility(View.VISIBLE);
                if (null != recyclerView) {
                    refreshLayout.finishRefresh();
                }
            }
        }
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.LIST_FANS_PAIKEW).add(Constant.STR_CW_UID_SYSTEM, uid).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(ItemFansEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.addData(list);
                            currentPageIndex += 1;
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(true);
                            }
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
            if (!isFinishing()) {
                if (null != refreshLayout) {
                    refreshLayout.finishLoadMore();
                }
            }
        }
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestFansInfo();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading && loading.isShow()) {
            loading.close();
        }
        unbinder.unbind();
    }
}