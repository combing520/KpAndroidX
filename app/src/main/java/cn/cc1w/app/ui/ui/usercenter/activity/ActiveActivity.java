package cn.cc1w.app.ui.ui.usercenter.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.active.UserActiveAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.UserActiveEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 活动
 *
 * @author kpinfo
 */
public class ActiveActivity extends CustomActivity implements OnItemClickListener, OnRefreshListener, OnLoadMoreListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_active)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_active)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private UserActiveAdapter adapter;
    private int currentPageSize = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initBlankView();
        initList();
        requestData();
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.active_none, "没有参加任何活动\n想要更多福利，需要积极参加哦~");
    }

    /**
     * 初始化list
     */
    private void initList() {
        adapter = new UserActiveAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        refreshLayout.autoRefresh();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageSize = 1;
            RxHttp.postJson(Constant.LIST_ACTIVE_ACTIVE)
                    .add("cw_page", currentPageSize)
                    .asResponseList(UserActiveEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && refreshLayout != null && blankView != null) {
                            refreshLayout.finishRefresh();
                            if (null != list && !list.isEmpty()) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                adapter.setData(list);
                                currentPageSize += 1;
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (!isFinishing() && refreshLayout != null && blankView != null) {
                            refreshLayout.finishRefresh();
                            refreshLayout.setVisibility(View.GONE);
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
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.LIST_ACTIVE_ACTIVE)
                    .add("cw_page", currentPageSize)
                    .asResponseList(UserActiveEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                            if (null != list && !list.isEmpty()) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                adapter.addData(list);
                                currentPageSize += 1;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != recyclerView) {
                            refreshLayout.finishLoadMore();
                        }
                    });
        } else {
            if (null != recyclerView) {
                refreshLayout.finishLoadMore();
            }
        }
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("我的活动");
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (NetUtil.isNetworkConnected(this)) {
            refreshData();
        } else {
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (NetUtil.isNetworkConnected(this)) {
            loadMoreData();
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        UserActiveEntity.DataBean entity = adapter.getItem(pos);
        if (null != entity && !TextUtils.isEmpty(entity.getIn_type())) {
            Bundle bundle = new Bundle();
            if (TextUtils.equals(Constant.TAG_URL, entity.getIn_type())) {
                bundle.putString(Constant.TAG_URL, entity.getContent());
                bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(entity.getId()) ? "" : entity.getId());
                bundle.putString(Constant.TAG_SUMMARY, "");
                IntentUtil.startActivity(this, UrlDetailActivity.class, bundle);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}