package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.topic.rank.TopicDetailVideoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.record.PhotoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemGridDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 话题详情照片
 *
 * @author kpinfo
 */
public class TopicDetailPicFragment extends Fragment implements OnItemClickListener, OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_detail_pic_topic)
    RecyclerView recyclerView;
    @BindView(R.id.hintTv)
    TextView hintTv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private Unbinder unbinder;
    private View decorView;
    private TopicDetailVideoAdapter adapter;
    private int currentPageIndex = 1;
    private boolean isViewCreated;
    private boolean isUiVisible;
    private boolean isFirstLoad = true;
    private Context context;

    public TopicDetailPicFragment() {
    }

    public static TopicDetailPicFragment newInstance() {
        TopicDetailPicFragment fragment = new TopicDetailPicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_topic_detail_pic, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();
    }

    private void showBlankView(boolean showBlankView) {
        if (refreshLayout != null && hintTv != null) {
            if (showBlankView) {
                refreshLayout.setVisibility(View.GONE);
                hintTv.setVisibility(View.VISIBLE);
            } else {
                refreshLayout.setVisibility(View.VISIBLE);
                hintTv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 初始化
     */
    private void init() {
        initList();
        requestData();
    }

    /**
     * 初始化list
     */
    private void initList() {
        adapter = new TopicDetailVideoAdapter();
        recyclerView.setMotionEventSplittingEnabled(false);
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new SpacesItemGridDecoration(AppUtil.dip2px(context, 1), 2, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        adapter.setOnItemClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);

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
        if (isUiVisible && isViewCreated) {
            if (isFirstLoad && NetUtil.isNetworkConnected(context)) {
                isFirstLoad = false;
                isUiVisible = false;
                refreshLayout.autoRefresh();
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_PHOTO_PAIKEW).add(Constant.STR_PAGE, currentPageIndex).add("is_topic", 1).add("topic_id", Constant.TOPIC_ID)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                            showBlankView(false);
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            showBlankView(true);
                            refreshLayout.setEnableLoadMore(false);
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        showBlankView(true);
                        ToastUtil.showShortToast(error.getErrorMsg());
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
            RxHttp.get(Constant.LIST_PHOTO_PAIKEW).add(Constant.STR_PAGE, currentPageIndex).add("is_topic", 1).add("topic_id", Constant.TOPIC_ID)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.addData(list);
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

    @Override
    public void onItemClick(View targetView, int pos) {
        ItemVideoListEntity.DataBean entity = adapter.getItem(pos);
        if (null != entity) {
            int paikewId = entity.getId();
            Intent intent = new Intent();
            intent.setClass(context, PhotoDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constant.TAG_ID, paikewId);
            startActivity(intent);
        }
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