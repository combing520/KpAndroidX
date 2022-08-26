package cn.cc1w.app.ui.ui.usercenter.history;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;

import com.rxjava.rxlife.RxLife;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.HistoryAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HistoryEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 历史浏览
 */
public class HistoryActivity extends CustomActivity implements SwipeRefreshLayout.OnRefreshListener, SwipeRecyclerView.LoadMoreListener {
    private static final int CNT_ITEM_LIST_MIN = 10;
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;// title
    @BindView(R.id.list_history)
    SwipeRecyclerView swipeRecyclerView;
    @BindView(R.id.txt_header_not_label)
    TextView clearListTv;
    @BindView(R.id.blankView)
    BlankView blankView;
    @BindView(R.id.refresh_history)
    SwipeRefreshLayout refreshLayout;
    private LoadingDialog loading;
    private HistoryAdapter adapter;
    private int currentPageIndex = 1;
    private boolean isRefreshing;
    private boolean isLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
        initRefreshLayout();
        initList();
        initBlankView();
        requestData();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText(getString(R.string.history_browse));
        clearListTv.setVisibility(View.VISIBLE);
        clearListTv.setText(getString(R.string.clear));
    }

    /**
     * 初始化 list
     */
    private void initList() {
        adapter = new HistoryAdapter(this);
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(this, 1), 1));
        swipeRecyclerView.setOnItemMenuClickListener(menuItemClickListener);
        DefineLoadMoreView loadMoreView = new DefineLoadMoreView(this);
        swipeRecyclerView.addFooterView(loadMoreView);
        swipeRecyclerView.setLoadMoreView(loadMoreView);
        swipeRecyclerView.setLoadMoreListener(this);
        swipeRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        swipeRecyclerView.setAdapter(adapter);
        swipeRecyclerView.setMotionEventSplittingEnabled(false);
    }

    /**
     * 初始化 refresh信息
     */
    private void initRefreshLayout() {
        refreshLayout.setColorSchemeColors(
                Color.parseColor("#d7a101"),
                Color.parseColor("#54c745"),
                Color.parseColor("#f16161"),
                Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(this);
    }

    /**
     * 初始化 空白页信息
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.history_empty, getString(R.string.record_history_none));
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.LIST_HISTORY).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(HistoryEntity.ItemHistoryEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && refreshLayout != null && blankView != null && swipeRecyclerView != null) {
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                currentPageIndex += 1;
                                if (list.size() < CNT_ITEM_LIST_MIN) {
                                    swipeRecyclerView.loadMoreFinish(true, false);
                                } else {
                                    swipeRecyclerView.loadMoreFinish(false, true);
                                }
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                                swipeRecyclerView.loadMoreFinish(true, false);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && refreshLayout != null && blankView != null) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            blankView.setVisibility(View.GONE);
                        }
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.setRefreshing(false);
                isRefreshing = false;
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            isRefreshing = true;
            RxHttp.postJson(Constant.LIST_HISTORY).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(HistoryEntity.ItemHistoryEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && refreshLayout != null && blankView != null && swipeRecyclerView != null) {
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                currentPageIndex += 1;
                                if (list.size() < CNT_ITEM_LIST_MIN) {
                                    swipeRecyclerView.loadMoreFinish(true, false);
                                } else {
                                    swipeRecyclerView.loadMoreFinish(false, true);
                                }
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                            isRefreshing = false;
                            refreshLayout.setRefreshing(false);
                        }
                    }, (OnError) error -> {
                        if (!isFinishing() && refreshLayout != null && blankView != null) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            blankView.setVisibility(View.GONE);
                            isRefreshing = false;
                            refreshLayout.setRefreshing(false);
                        }
                    });
        } else {
            if (null != refreshLayout) {
                isRefreshing = false;
                refreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this)) {
            isLoadMore = true;
            RxHttp.postJson(Constant.LIST_HISTORY).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(HistoryEntity.ItemHistoryEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && swipeRecyclerView != null) {
                            if (null != list && !list.isEmpty()) {
                                adapter.addData(list);
                                currentPageIndex += 1;
                                if (list.size() < CNT_ITEM_LIST_MIN) {
                                    swipeRecyclerView.loadMoreFinish(true, false);
                                } else {
                                    swipeRecyclerView.loadMoreFinish(false, true);
                                }
                            } else {
                                swipeRecyclerView.loadMoreFinish(true, false);
                            }
                        }
                        isLoadMore = false;
                    }, (OnError) error -> {
                        if (!isFinishing() && swipeRecyclerView != null) {
                            isLoadMore = false;
                            swipeRecyclerView.loadMoreFinish(true, false);
                        }
                    });
        } else {
            if (null != swipeRecyclerView) {
                isLoadMore = false;
                swipeRecyclerView.loadMoreFinish(false, true);
            }
        }
    }

    SwipeMenuCreator swipeMenuCreator = (leftMenu, rightMenu, viewType) -> {
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = AppUtil.dip2px(HistoryActivity.this, 80);
        SwipeMenuItem deleteItem = new SwipeMenuItem(HistoryActivity.this)
                .setText("删除")
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.WHITE)
                .setTextSize(18)
                .setWidth(width)
                .setHeight(height);
        rightMenu.addMenuItem(deleteItem);
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private final OnItemMenuClickListener menuItemClickListener = (menuBridge, position) -> {
        menuBridge.closeMenu();
        int direction = menuBridge.getDirection();
        if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
            deleteHistoryItem(position);
        }
    };

    /**
     * 删除 指定位置的历史浏览的条目
     *
     * @param pos 指定的位置
     */
    private void deleteHistoryItem(final int pos) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (null != loading) {
                loading.show();
            }
            HistoryEntity.ItemHistoryEntity item = adapter.getItem(pos);
            RxHttp.postJson(Constant.DELETE_HISTORY_ITEM_LIST).add("cw_history_id", item.getId())
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            adapter.deleteHistoryItem(pos);
                            int listSize = adapter.getItemCount();
                            if (listSize == 0) {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
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
     * 清空列表
     */
    private void clearList() {
        if (adapter.getListSize() == 0) {
            ToastUtil.showShortToast("暂无数据");
            return;
        }
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            clearListTv.setClickable(false);
            RxHttp.postJson(Constant.CLEAR_HISTORY_ITEM_LIST)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            adapter.clearHistory();
                            int listSize = adapter.getItemCount();
                            if (listSize == 0) {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            if (null != clearListTv) {
                                clearListTv.setClickable(true);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != clearListTv) {
                            clearListTv.setClickable(true);
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
     * 刷数据
     */
    @Override
    public void onRefresh() {
        if (isLoadMore) {
            refreshLayout.setRefreshing(false);
        } else {
            refreshData();
        }
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        if (isRefreshing) {
            swipeRecyclerView.loadMoreFinish(false, true);
        } else {
            loadMoreData();
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_header_not_label})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_header_not_label) {
            clearList();
        }
    }

    /**
     * 这是这个类的主角，如何自定义LoadMoreView。
     */
    static final class DefineLoadMoreView extends LinearLayout implements SwipeRecyclerView.LoadMoreView, View.OnClickListener {
        private final ProgressBar mProgressBar;
        private final TextView mTvMessage;
        private SwipeRecyclerView.LoadMoreListener mLoadMoreListener;

        public DefineLoadMoreView(Context context) {
            super(context);
            setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            setGravity(Gravity.CENTER);
            setVisibility(GONE);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int minHeight = (int) (displayMetrics.density * 60 + 0.5);
            setMinimumHeight(minHeight);
            inflate(context, R.layout.layout_fotter_loadmore, this);
            mProgressBar = findViewById(R.id.progress_bar);
            mTvMessage = findViewById(R.id.tv_message);
            setOnClickListener(this);
        }

        @Override
        public void onLoading() {
            setVisibility(VISIBLE);
            mProgressBar.setVisibility(VISIBLE);
            mTvMessage.setVisibility(VISIBLE);
            mTvMessage.setText("正在加载...");
        }

        @Override
        public void onLoadFinish(boolean dataEmpty, boolean hasMore) {
            if (!hasMore) {
                setVisibility(VISIBLE);
                if (dataEmpty) {
                    mProgressBar.setVisibility(GONE);
                    mTvMessage.setVisibility(VISIBLE);
                    mTvMessage.setText("没有更多数据啦...");
                } else {
                    mProgressBar.setVisibility(GONE);
                    mTvMessage.setVisibility(VISIBLE);
                    mTvMessage.setText("没有更多数据啦");
                }
            } else {
                setVisibility(INVISIBLE);
            }
        }

        @Override
        public void onWaitToLoadMore(SwipeRecyclerView.LoadMoreListener loadMoreListener) {
            this.mLoadMoreListener = loadMoreListener;
            setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
            mTvMessage.setVisibility(VISIBLE);
            mTvMessage.setText("点击加载更多");
        }

        @Override
        public void onLoadError(int errorCode, String errorMessage) {
            setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
            mTvMessage.setVisibility(VISIBLE);
            mTvMessage.setText(errorMessage);
        }

        @Override
        public void onClick(View v) {
            if (mLoadMoreListener != null) {
                mLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}