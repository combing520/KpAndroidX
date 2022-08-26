package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


import net.arvin.itemdecorationhelper.ItemDecorationFactory;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.RecordPhotoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemPhotoRecordEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.record.PhotoDetailActivity;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 照片
 *
 * @author kpinfo
 */
public class PhotoFragment extends Fragment implements OnItemClickListener, BlankViewClickListener, OnRefreshListener, OnLoadMoreListener {
    private Unbinder unbinder;
    private View decorView;
    @BindView(R.id.list_photo_record)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.blank_photo_record)
    BlankView blankView;
    private int currentPageIndex = 1;
    private RecordPhotoAdapter adapter;
    private boolean isFirstLoad = true;
    private Context context;
    private long lastClickTime = System.currentTimeMillis();

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RecordPhotoAdapter();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_photo, container, false);
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
        requestData();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.img_empty, "暂无图片~ 点击重试", getString(R.string.try_again));
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化list
     */
    private void initList() {
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        RecyclerView.ItemDecoration itemDecoration = new ItemDecorationFactory.DividerBuilder()
                .dividerHeight(2)
                .dividerColor(ContextCompat.getColor(context, R.color.colorWhite))
                .showLastDivider(false)
                .build(recyclerView);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 请求照片详情
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(context)) {
            if (isFirstLoad && refreshLayout != null) {
                isFirstLoad = false;
                refreshLayout.autoRefresh();
            }
        } else {
            if (blankView != null) {
                blankView.setClickable(true);
                blankView.setVisibility(View.VISIBLE);
            }
            if (refreshLayout != null) {
                refreshLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_PHOTO_PAIKEW).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(ItemPhotoRecordEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                            if (null != blankView && blankView.getVisibility() == View.VISIBLE) {
                                blankView.setVisibility(View.GONE);
                            }
                            if (null != refreshLayout && refreshLayout.getVisibility() == View.GONE) {
                                refreshLayout.setVisibility(View.VISIBLE);
                            }
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(true);
                            }
                        } else {
                            if (null != blankView && blankView.getVisibility() == View.GONE) {
                                blankView.setVisibility(View.VISIBLE);
                            }
                            if (null != refreshLayout && refreshLayout.getVisibility() == View.VISIBLE) {
                                refreshLayout.setVisibility(View.GONE);
                            }
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
                        if (null != blankView && blankView.getVisibility() == View.GONE) {
                            blankView.setVisibility(View.VISIBLE);
                        }
                        if (null != refreshLayout && refreshLayout.getVisibility() == View.VISIBLE) {
                            refreshLayout.setVisibility(View.GONE);
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
            RxHttp.postJson(Constant.LIST_PHOTO_PAIKEW).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(ItemPhotoRecordEntity.DataBean.class)
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
    public void onItemClick(View targetView, int pos) {
        ItemPhotoRecordEntity.DataBean entity = adapter.getItem(pos);
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
    public void onBlankViewClickListener(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.TIME_INTERVAL) {
            if (NetUtil.isNetworkConnected(context)) {
                if (refreshLayout != null && blankView != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    blankView.setVisibility(View.GONE);
                    isFirstLoad = true;
                    requestData();
                }
            }
        }
        lastClickTime = currentTime;
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