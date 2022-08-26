package cn.cc1w.app.ui.ui.xg_push;

import android.content.Intent;
import android.net.Uri;
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

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.push.PushNewsGroupAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.home.MainNewActivity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PushNewsGroupEntity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 推送的 【新闻组列表】
 *
 * @author kpinfo
 */
public class NewsGroupPushListActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_news_group_push)
    RecyclerView recyclerView;
    @BindView(R.id.txt_header_news_group_push)
    TextView titleTv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private Unbinder unbinder;
    private PushNewsGroupAdapter adapter;
    private String pushNewsId = "";
    private int currentPageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_group_push_list);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
        initData();
    }

    /**
     * 初始化 Navigation
     */
    private void initNavigation() {
        titleTv.setText("推送新闻");
    }

    private void initData() {
        pushNewsId = getIntent().getStringExtra(Constant.TAG_ID);
        if (TextUtils.isEmpty(pushNewsId)) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                Set<String> nameSet = uri.getQueryParameterNames();
                for (String name : nameSet) {
                    if (TextUtils.equals(name, Constant.TAG_ID)) {
                        pushNewsId = uri.getQueryParameter(Constant.TAG_ID);
                    }
                }
            }

        }
        if (!TextUtils.isEmpty(pushNewsId)) {
            requestData();
        }
    }

    /**
     * 初始化 list
     */
    private void initList() {
        adapter = new PushNewsGroupAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
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
            currentPageIndex = 1;
            RxHttp.postJson(Constant.LIST_NEWS_PUSH).add("cw_push_id", pushNewsId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(PushNewsGroupEntity.ItemPushNewsGroupEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                currentPageIndex += 1;
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(true);
                                }
                            } else {
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(false);
                                }
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
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.LIST_NEWS_PUSH).add("cw_push_id", pushNewsId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(PushNewsGroupEntity.ItemPushNewsGroupEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
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

    @OnClick({R.id.img_back_news_group_push})
    public void onClick(View v) {
        if (v.getId() == R.id.img_back_news_group_push) {
            if (!Constant.isMainActivityCreated) {
                EventBus.getDefault().post(new EventMessage(Constant.TAG_SPLASH_FINISH, Constant.TAG_SPLASH_FINISH));
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(this, MainNewActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!Constant.isMainActivityCreated) {
            EventBus.getDefault().post(new EventMessage(Constant.TAG_SPLASH_FINISH, Constant.TAG_SPLASH_FINISH));
            Intent intent = new Intent(this, MainNewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}