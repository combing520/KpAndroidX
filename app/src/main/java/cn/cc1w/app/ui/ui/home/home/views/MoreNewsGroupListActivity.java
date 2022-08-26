package cn.cc1w.app.ui.ui.home.home.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.entity.NewsGroupDetailEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 新闻组详情
 *
 * @author kpinfo
 */
public class MoreNewsGroupListActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_news_news_group_list_more)
    RecyclerView recyclerView;
    @BindView(R.id.layout)
    ConstraintLayout container;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.img)
    RoundAngleImageView img;
    @BindView(R.id.title)
    TextView newsDetailGroupTitleTv;
    private AppsDetailNewsAdapter adapter;
    private int currentPageIndex = 1;
    private Unbinder unbinder;
    private String newsGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_news_group_list);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
        getNewsGroupDetail();
    }

    private void getNewsGroupDetail() {
        RxHttp.postJson(Constant.DETAIL_NEWS_GROUP)
                .add("cw_news_groud_id", newsGroupId)
                .asResponse(NewsGroupDetailEntity.DataBean.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    if(!isFinishing()){
                        if (data != null) {
                            AppUtil.loadNewsImg(data.getPic_path(), img);
                            newsDetailGroupTitleTv.setText(TextUtils.isEmpty(data.getTitle()) ? "" : data.getTitle());
                            container.setVisibility(View.VISIBLE);
                        }
                        refreshLayout.autoRefresh();
                    }
                }, (OnError) error -> {
                });
    }

    private void getNewsGroupList() {
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(newsGroupId)) {
            currentPageIndex = 1;
            RxHttp.postJson(Constant.LIST_NEWS_GROUP).add("cw_news_groud_id", newsGroupId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != dataSet && !dataSet.isEmpty()) {
                            adapter.setData(dataSet);
                            currentPageIndex += 1;
                            if (refreshLayout != null) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                refreshLayout.setEnableLoadMore(true);
                            }
                        } else {
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(false);
                            }
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

    private void initList() {
        adapter = new AppsDetailNewsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(newsGroupId)) {
            RxHttp.postJson(Constant.LIST_NEWS_GROUP).add("cw_news_groud_id", newsGroupId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != dataSet && !dataSet.isEmpty()) {
                            adapter.addData(dataSet);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
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
            if (null != refreshLayout) {
                refreshLayout.finishLoadMore();
            }
        }
    }

    /**
     * 初始化 navigation
     */
    private void initNavigation() {
        titleTv.setText("新闻组");
        newsGroupId = getIntent().getStringExtra(Constant.TAG_ID);
        LogUtil.e("id = " + newsGroupId);
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getNewsGroupList();
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