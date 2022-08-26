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

import cn.cc1w.app.ui.R;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.FocusAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.entity.ItemFocusEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;

/**
 * 关注
 *
 * @author kpinfo
 */
public class FocusActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_focus)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.blankView_focus)
    BlankView blankView;
    private FocusAdapter adapter;
    private int currentPageIndex = 1;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        iniNavigation();
        initList();
        initBlankView();
        requestFocusList();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.focus_empty, getString(R.string.focus_none));
    }

    /**
     * 初始化List
     */
    private void initList() {
        adapter = new FocusAdapter();
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
        titleTv.setText("关注");
        uid = getIntent().getStringExtra(Constant.TAG_ID);
    }

    /**
     * 请求 关注列表
     */
    private void requestFocusList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.LIST_FOCUS_PAIKEW).add(Constant.STR_CW_UID_SYSTEM, uid).add(Constant.STR_PAGE, String.valueOf(currentPageIndex))
                    .asResponseList(ItemFocusEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (refreshLayout != null && blankView != null) {
                            if (list != null && !list.isEmpty()) {
                                adapter.setData(list);
                                currentPageIndex += 1;
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (refreshLayout != null && blankView != null) {
                            refreshLayout.setVisibility(View.GONE);
                            blankView.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_FOCUS_PAIKEW).add(Constant.STR_CW_UID_SYSTEM, uid).add(Constant.STR_PAGE, String.valueOf(currentPageIndex))
                    .asResponseList(ItemFocusEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
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
     * 加载更多
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.LIST_FOCUS_PAIKEW).add(Constant.STR_CW_UID_SYSTEM, uid).add(Constant.STR_PAGE, String.valueOf(currentPageIndex))
                    .asResponseList(ItemFocusEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.addData(list);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
                        }
                        if (null != refreshLayout) {
                            refreshLayout.setEnableLoadMore(false);
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

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}