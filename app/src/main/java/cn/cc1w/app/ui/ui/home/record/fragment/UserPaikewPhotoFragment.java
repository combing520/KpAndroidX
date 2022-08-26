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

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.EarnPhotoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemPhotoRecordEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 别人的拍客照片 /或者我 拍的拍客照片
 *
 * @author kpinfo
 */
public class UserPaikewPhotoFragment extends Fragment implements BlankViewClickListener, OnRefreshListener, OnLoadMoreListener {
    private View decorView;
    private Unbinder unbinder;
    @BindView(R.id.list_earn_photo)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_earn_photo)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private EarnPhotoAdapter adapter;
    private int currentPageIndex = 1;
    private String paikewUid;
    private Context context;

    public static UserPaikewPhotoFragment newInstance() {
        return new UserPaikewPhotoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EarnPhotoAdapter();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_user_paikew_photo, container, false);
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
        initData();
        requestPhotoList();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(getString(R.string.str_picture_paikew_none));
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (TextUtils.isEmpty(paikewUid)) {
            paikewUid = Constant.UID_PAIKEW_OTHER;
        }
    }

    /**
     * 初始化list
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
     * 请求照片信息
     */
    private void requestPhotoList() {
        if (NetUtil.isNetworkConnected(context)) {
            if (null != adapter && adapter.getList().isEmpty()) {
                RxHttp.get(Constant.LIST_PHOTO_USER_PAIKEW).add(Constant.STR_PAGE, currentPageIndex).add(Constant.STR_CW_UID_SYSTEM, paikewUid)
                        .asResponseList(ItemPhotoRecordEntity.DataBean.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(list -> {
                            if (blankView != null && refreshLayout != null) {
                                if (null != list && !list.isEmpty()) {
                                    adapter.setData(list);
                                    blankView.setVisibility(View.GONE);
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    currentPageIndex += 1;
                                } else {
                                    refreshLayout.setVisibility(View.GONE);
                                    blankView.setVisibility(View.VISIBLE);
                                }
                                if (null != refreshLayout) {
                                    refreshLayout.finishRefresh();
                                }
                            }
                        }, (OnError) error -> {
                            if (null != refreshLayout) {
                                refreshLayout.finishRefresh();
                            }
                        });
            }
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
                refreshLayout.setVisibility(View.GONE);
            }
            if (null != blankView) {
                blankView.setVisibility(View.VISIBLE);
                blankView.setClickable(true);
                blankView.setBlankView(getString(R.string.network_error_try_click), getString(R.string.try_again));
            }
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_PHOTO_USER_PAIKEW).add(Constant.STR_PAGE, currentPageIndex).add(Constant.STR_CW_UID_SYSTEM, paikewUid)
                    .asResponseList(ItemPhotoRecordEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        refreshLayout.finishRefresh();
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                        }
                    }, (OnError) error -> {
                        refreshLayout.finishRefresh();
                    });
        } else {
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.get(Constant.LIST_PHOTO_USER_PAIKEW).add(Constant.STR_PAGE, currentPageIndex).add(Constant.STR_CW_UID_SYSTEM, paikewUid)
                    .asResponseList(ItemPhotoRecordEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        refreshLayout.finishLoadMore();
                        if (null != list && !list.isEmpty()) {
                            adapter.addData(list);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
                        }
                    }, (OnError) error -> {
                        refreshLayout.finishLoadMore();
                    });
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onBlankViewClickListener(View view) {
        blankView.setClickable(false);
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