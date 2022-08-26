package cn.cc1w.app.ui.ui.home.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import net.arvin.itemdecorationhelper.ItemDecorationFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.details_apps.SmallVideoSubAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * @author kpinfo
 * 小视频 ---更多
 */
public class MoreSmallVideoActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_news_video_list_more)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int currentPageIndex = 1;
    private Unbinder unbinder;
    private String videoId;
    private SmallVideoSubAdapter adapter;
    private static final int NUM_GRID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_small_video);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
        requestData();
    }

    /**
     * 初始化List
     */
    private void initList() {
        adapter = new SmallVideoSubAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_GRID);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new ItemDecorationFactory.DividerBuilder()
                .dividerHeight(2)
                .dividerColor(ContextCompat.getColor(this,R.color.colorGray))
                .showLastDivider(false)
                .build(recyclerView);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 初始化 navigation
     */
    private void initNavigation() {
        titleTv.setText("小视频");
        videoId = getIntent().getStringExtra(Constant.TAG_ID);
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
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(videoId)) {
            currentPageIndex = 1;
            RxHttp.postJson(Constant.LIST_MORE).add("cw_id", videoId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != dataSet && !dataSet.isEmpty()) {
                            adapter.setData(dataSet);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
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

    /**
     * 加载更多
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(videoId)) {
            RxHttp.postJson(Constant.LIST_MORE).add("cw_id", videoId).add("cw_page", String.valueOf(currentPageIndex))
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

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData();
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}