package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.EarnVideoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemVideoListEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 别人的拍客视频 /或者我 拍的拍客视频
 *
 * @author kpinfo
 */
public class UserPaikewVideoFragment extends Fragment implements BlankViewClickListener, OnRefreshListener, OnLoadMoreListener {
    private View decorView;
    private Unbinder unbinder;
    @BindView(R.id.list_earn_video)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_earn_video)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private EarnVideoAdapter adapter;
    private int currentPageIndex = 1;
    private String paikewUid;
    private Context context;

    public static UserPaikewVideoFragment newInstance() {
        return new UserPaikewVideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EarnVideoAdapter();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_user_paikew_video, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initList();
        initBlankView();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(getString(R.string.str_video_paikew_none));
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化List
     */
    private void initList() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context, 2), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 获取 用户提交的 视频
     */
    private void requestVideoList() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.get(Constant.LIST_VIDEO_USER_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, paikewUid)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (refreshLayout != null && blankView != null) {
                            refreshLayout.finishRefresh();
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                currentPageIndex += 1;
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    });
        } else {
            refreshLayout.setVisibility(View.GONE);
            blankView.setVisibility(View.VISIBLE);
            blankView.setClickable(true);
            blankView.setBlankView(getString(R.string.network_error_try_click), getString(R.string.try_again));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (TextUtils.equals("userPaiKewId", message.getLabel())) {
                paikewUid = message.getContent();
                requestVideoList();
            }
            // 网络错误
            else if (TextUtils.equals("networkError", message.getLabel())) {
                if (null != adapter && adapter.getList().isEmpty()) {
                    // 没有网络
                    refreshLayout.setVisibility(View.GONE);
                    blankView.setVisibility(View.VISIBLE);
                    blankView.setBlankView(getString(R.string.network_error_try_click), getString(R.string.try_again));
                    blankView.setClickable(true);
                }
            }
        }
    }

    @Override
    public void onBlankViewClickListener(View view) {
        blankView.setClickable(false);
        requestUserInfo();
    }

    private void requestUserInfo() {
        EventBus.getDefault().post(new EventMessage("", "updateUserInfo"));
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout2) {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_VIDEO_USER_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, paikewUid)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (refreshLayout != null && blankView != null) {
                            refreshLayout.finishRefresh();
                            if (null != list && !list.isEmpty()) {
                                adapter.setData(list);
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                currentPageIndex += 1;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if (recyclerView != null) {
                            refreshLayout.finishRefresh();
                        }
                    });
        } else {
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout2) {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.get(Constant.LIST_VIDEO_USER_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, paikewUid)
                    .asResponseList(ItemVideoListEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (recyclerView != null) {
                            refreshLayout.finishLoadMore();
                            if (null != list && !list.isEmpty()) {
                                adapter.addData(list);
                                refreshLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                currentPageIndex += 1;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if (recyclerView != null) {
                            refreshLayout.setEnableLoadMore(false);
                            refreshLayout.finishLoadMore();
                        }
                    });
        } else {
            refreshLayout.setEnableLoadMore(false);
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
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}