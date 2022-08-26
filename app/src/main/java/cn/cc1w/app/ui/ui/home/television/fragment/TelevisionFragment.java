package cn.cc1w.app.ui.ui.home.television.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.TelevisionAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemTelevisonListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 电视台
 *
 * @author kpinfo
 */
public class TelevisionFragment extends Fragment implements OnItemClickListener, OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_television)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private TelevisionAdapter adapter;
    private View decorView;
    private Unbinder unbinder;
    private boolean isViewCreated;
    private boolean isUiVisible;
    private boolean isFirstLoad = true;
    private String groupId;
    private int currentPos;
    private int currentPageIndex = 1;
    private Context context;

    public static TelevisionFragment newInstance(String groupId, int currentPos) {
        TelevisionFragment fragment = new TelevisionFragment();
        Bundle args = new Bundle();
        args.putString("groupId", groupId);
        args.putInt("currentPos", currentPos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString("groupId");
            currentPos = getArguments().getInt("currentPos", 0);
            adapter = new TelevisionAdapter();
            adapter.setOnItemClickListener(this);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_television, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
        init();
        requestData();
    }

    /**
     * 初始化
     */
    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context, 1), 1));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
    }

    /**
     * 当前页面是否可见
     *
     * @param isVisibleToUser 当前页面的可见情况
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUiVisible = true;
            requestData();
        } else {
            isUiVisible = false;
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (isUiVisible && isViewCreated && isFirstLoad) {
            isUiVisible = false;
            isViewCreated = false;
            if (!TextUtils.isEmpty(groupId)) {
                RxHttp.postJson(Constant.LIST_STATION_TELEVISON).add("cw_group_id", groupId).add("cw_page", currentPageIndex)
                        .asResponseList(ItemTelevisonListEntity.DataBean.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(list -> {
                            isFirstLoad = false;
                            if (list != null && !list.isEmpty()) {
                                adapter.setData(list);
                                if (currentPos == 0) {
                                    String selectItemPath = list.get(0).getTv_path();
                                    String selectItemTitle = list.get(0).getTitle();
                                    String selectPost = list.get(0).getLogo_path();
                                    EventBus.getDefault().post(new EventMessage("updateVideo", selectItemPath, selectItemTitle, selectPost));
                                }
                                currentPageIndex += 1;
                            }
                        }, (OnError) error -> {
                        });
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.postJson(Constant.LIST_STATION_TELEVISON).add("cw_group_id", groupId).add("cw_page", currentPageIndex)
                    .asResponseList(ItemTelevisonListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (list != null && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPos += 1;
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
     * 加载更多数据
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.LIST_STATION_TELEVISON).add("cw_group_id", groupId).add("cw_page", currentPageIndex)
                    .asResponseList(ItemTelevisonListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (list != null && !list.isEmpty()) {
                            adapter.addData(list);
                            currentPos += 1;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
                            if (null != refreshLayout) {
                                refreshLayout.finishLoadMore(true);
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
    public void onItemClick(View targetView, int pos) {
        ItemTelevisonListEntity.DataBean entity = adapter.getItem(pos);
        if (null != entity) {
            String videoPath = entity.getTv_path();
            String videoTitle = entity.getTitle();
            String videoPost = entity.getLogo_path();
            EventBus.getDefault().post(new EventMessage("updateVideo", videoPath, videoTitle, videoPost));
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
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(context, getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        KpLog.getInstance().onAppViewScreenOut(context, getClass().getSimpleName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}