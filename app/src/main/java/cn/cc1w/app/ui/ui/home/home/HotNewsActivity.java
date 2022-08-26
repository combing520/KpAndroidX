package cn.cc1w.app.ui.ui.home.home;

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

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 热点新闻更多
 *
 * @author kpinfo
 */
public class HotNewsActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_news_hot)
    RecyclerView hotNewsList;
    @BindView(R.id.blankView_news_hot)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private AppsDetailNewsAdapter adapter;
    private LoadingDialog loading;
    private Unbinder unbinder;
    private String newsModule;
    private String newsId;
    private int currentPageIndex = 1;
    private boolean isFirstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_news);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initBlankView();
        initLoading();
        initList();
        requestData();
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("热点新闻");
        newsModule = getIntent().getStringExtra("module");
        newsId = getIntent().getStringExtra(Constant.TAG_ID);
        if (TextUtils.isEmpty(newsModule)) {
            titleTv.setText("更多新闻");
        } else {
            titleTv.setText("热点新闻");
        }
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, getString(R.string.news_none));
    }

    /**
     * 初始化 list
     */
    private void initList() {
        adapter = new AppsDetailNewsAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        hotNewsList.setLayoutManager(manager);
        hotNewsList.setAdapter(adapter);
        hotNewsList.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            if (null != loading && !isFirstLoad) {
                loading.show();
            }
            isFirstLoad = false;
            RxHttp.postJson(Constant.LIST_NEWS_HOT)
                    .add("cw_channel_id", newsId).add("cw_page", String.valueOf(currentPageIndex)).add("cw_module", newsModule)
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        if (!isFinishing()) {
                            if (null != dataSet && !dataSet.isEmpty()) {
                                if (null != hotNewsList) {
                                    hotNewsList.setVisibility(View.VISIBLE);
                                    blankView.setVisibility(View.GONE);
                                }
                                adapter.setData(dataSet);
                                currentPageIndex += 1;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                if (null != hotNewsList) {
                                    hotNewsList.setVisibility(View.GONE);
                                    blankView.setVisibility(View.VISIBLE);
                                    refreshLayout.setEnableLoadMore(false);
                                }
                            }
                        }
                    }, (OnError) error -> {
                        if (!isFinishing()) {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (null != refreshLayout) {
                                refreshLayout.finishRefresh();
                            }
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
            RxHttp.postJson(Constant.LIST_NEWS_HOT)
                    .add("cw_channel_id", newsId).add("cw_page", String.valueOf(currentPageIndex)).add("cw_module", newsModule)
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (!isFinishing()) {
                            if (null != dataSet && !dataSet.isEmpty()) {
                                adapter.addData(dataSet);
                                currentPageIndex += 1;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(false);
                                }
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

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestData();
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
            loading = null;
        }
        unbinder.unbind();
    }
}