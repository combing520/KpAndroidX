package cn.cc1w.app.ui.ui.usercenter.collection;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.UserCollectionAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.CollectionEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;

import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 我的收藏
 */
public class CollectionActivity extends CustomActivity implements SwipeRefreshLayout.OnRefreshListener, SwipeRecyclerView.LoadMoreListener {
    private Unbinder unbinder;
    @BindView(R.id.list_collection)
    SwipeRecyclerView swipeRecyclerView;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_header_not_label)
    TextView clearListTv;
    @BindView(R.id.blankView_collection)
    BlankView blankView;
    @BindView(R.id.refresh_collection)
    SwipeRefreshLayout refreshLayout;
    private LoadingDialog loading;
    private UserCollectionAdapter adapter;
    private int currentPageIndex = 1;
    private boolean isRefreshing;
    private boolean isLoadMore;
    private static final int CNT_ITEM_LIST_MIN = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initList();
        initNavigation();
        initBlankView();
        initLoading();
        initRefreshLayout();
        requestData();
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
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.history_empty, getString(R.string.collection_none));
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
            RxHttp.postJson(Constant.LIST_COLLECTION)
                    .add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(CollectionEntity.ItemCollectionEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && blankView != null && refreshLayout != null && swipeRecyclerView != null) {
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                blankView.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);
                                currentPageIndex += 1;
                                // 没有更多了
                                if (list.size() < CNT_ITEM_LIST_MIN) {
                                    swipeRecyclerView.loadMoreFinish(true, false);
                                } else {
                                    swipeRecyclerView.loadMoreFinish(false, true);
                                }
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (blankView != null && refreshLayout != null) {
                            refreshLayout.setVisibility(View.GONE);
                            blankView.setVisibility(View.VISIBLE);
                        }
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        }
    }

    private void refreshData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            isRefreshing = true;
            RxHttp.postJson(Constant.LIST_COLLECTION).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(CollectionEntity.ItemCollectionEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && blankView != null && refreshLayout != null && swipeRecyclerView != null) {
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                blankView.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);
                                currentPageIndex += 1;
                                swipeRecyclerView.loadMoreFinish(false, true);
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
                            refreshLayout.setRefreshing(false);
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.setRefreshing(false);
                        }
                        isRefreshing = false;
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.setRefreshing(false);
            }
            isRefreshing = false;
        }
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        // 加载更多
        if (NetUtil.isNetworkConnected(this)) {
            isLoadMore = true;
            RxHttp.postJson(Constant.LIST_COLLECTION).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(CollectionEntity.ItemCollectionEntity.class)
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
                            isLoadMore = false;
                        }
                    }, (OnError) error -> {
                        if (null != swipeRecyclerView) {
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

    /**
     * 初始化导航
     */
    private void initNavigation() {
        titleTv.setText("我的收藏");
        clearListTv.setVisibility(View.VISIBLE);
        clearListTv.setText("清空");
    }

    /**
     * 初始化 list
     */
    private void initList() {
        adapter = new UserCollectionAdapter(this);
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

    SwipeMenuCreator swipeMenuCreator = (leftMenu, rightMenu, viewType) -> {
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = AppUtil.dip2px(CollectionActivity.this, 80);
        SwipeMenuItem deleteItem = new SwipeMenuItem(CollectionActivity.this)
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
            deleteCollectionItem(position);
        }
    };

    /**
     * 删除指定位置的收藏条目
     *
     * @param pos 指定的位置
     */
    private void deleteCollectionItem(final int pos) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (null != loading) {
                loading.show();
            }
            CollectionEntity.ItemCollectionEntity item = adapter.getItem(pos);
            RxHttp.postJson(Constant.DELETE_COLLECTION_ITEM_LIST)
                    .add("cw_collection_id", item.getId())
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && refreshLayout != null && blankView != null) {
                            adapter.deleteCollectionItem(pos);
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
     * 清空数据
     */
    private void clearList() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (adapter.getItemCount() == 0) {
                ToastUtil.showShortToast("暂无数据");
            } else {
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.CLEAR_COLLECTION_LIST)
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (!isFinishing() && refreshLayout != null && blankView != null) {
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                                adapter.clearCollection();
                                int listSize = adapter.getItemCount();
                                if (listSize == 0) {
                                    refreshLayout.setVisibility(View.GONE);
                                    blankView.setVisibility(View.VISIBLE);
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
    }

    /**
     * 下拉刷新
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

    /**
     * 接收EventBus
     *
     * @param message EventBus 接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                // 修改收藏状态
                if (TextUtils.equals("changeCollection", message.getLabel())) {
                    requestData();
                }
            }
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
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}