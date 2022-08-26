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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.NewsDetailCommentAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsCommentEntity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 更多评论列表
 *
 * @author kpinfo
 */
public class MoreCommentListActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    private NewsDetailCommentAdapter adapter;
    @BindView(R.id.list_comment_more)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_list_comment)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int currentPageIndex = 1;
    private Unbinder unbinder;
    private String newsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_comment_list);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
        initBlankView();
        requestData();
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.comment_empty, getString(R.string.comment_none));
    }

    /**
     * 初始化 navigation
     */
    private void initNavigation() {
        titleTv.setText("评论列表");
        newsId = getIntent().getStringExtra(Constant.TAG_ID);
    }

    /**
     * 初始化 List
     */
    private void initList() {
        adapter = new NewsDetailCommentAdapter(this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 获取数据
     */
    private void requestData() {
        refreshLayout.autoRefresh();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(newsId)) {
            currentPageIndex = 1;
            RxHttp.postJson(Constant.LIST_COMMENT_DETAIL_NEWS)
                    .add("cw_news_id", newsId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(NewsCommentEntity.ItemNewsCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        if (!isFinishing()) {
                            if (null != dataSet && !dataSet.isEmpty()) {
                                adapter.setData(dataSet);
                                currentPageIndex += 1;
                                if (null != refreshLayout) {
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    blankView.setVisibility(View.GONE);
                                    refreshLayout.setEnableLoadMore(true);
                                }
                            } else {
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(false);
                                    blankView.setVisibility(View.VISIBLE);
                                    refreshLayout.setVisibility(View.GONE);
                                }
                            }
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
     * 获取数据
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(newsId)) {
            RxHttp.postJson(Constant.LIST_COMMENT_DETAIL_NEWS)
                    .add("cw_news_id", newsId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(NewsCommentEntity.ItemNewsCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishLoadMore();
                        }
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