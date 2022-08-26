package cn.cc1w.app.ui.ui.fragment;

import java.io.File;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.base.ViewPagerFragment;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.FileUtils;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.verticaltxt.AutoVerticalViewView;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;
import rxhttp.RxHttpJsonParam;

/**
 * 首页
 *
 * @author kpinfo
 */
public class HomeFragment extends ViewPagerFragment implements BlankViewClickListener, OnRefreshListener, OnLoadMoreListener {
    private Unbinder unbinder;
    private View decorView;
    @BindView(R.id.list_home)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.blankView)
    BlankView blankView;
    private HomeNewsAdapter adapter;
    private int currentPageIndex = 1;
    private final String title = "news";
    private boolean isFirstLoad = true;
    private String channelId;
    private boolean isIndex;
    private LinearLayoutManager manager;
    private static final int TYPE_NEWS_ROLL = 1700;
    private boolean isBlankViewCanClick = true;
    private boolean hasCacheData = false;
    private long lastClickTime = System.currentTimeMillis();

    public static HomeFragment newInstance(String channelId, boolean isIndex) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("channelId", channelId);
        args.putBoolean("isIndex", isIndex);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EventBus.getDefault().register(this);
            channelId = getArguments().getString("channelId");
            isIndex = getArguments().getBoolean("isIndex");
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void frResume() {

    }

    @Override
    public void frPause() {

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
        initBlankView();
        initList();
        getCacheData();
    }

    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, "暂无推荐~ 点击重试", getString(R.string.try_again));
        blankView.setOnBlankViewClickListener(this);
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
            if (null != adapter && isIndex) {
                setFlipEnable(true);
            }
        } else {
            if (null != adapter && isIndex) {
                setFlipEnable(false);
            }
        }
    }

    /**
     * 设置 首页的 焦点新闻是否 滚动
     *
     * @param enable 焦点新闻滚动与否
     */
    private void setFlipEnable(boolean enable) {
        if (null != adapter && isIndex) {
            if (null != manager) {
                int scrollNewsPos = adapter.getScrollNewsPos();
                if (scrollNewsPos > -1) {
                    int viewType = adapter.getItemViewType(scrollNewsPos);
                    if (viewType == TYPE_NEWS_ROLL) {
                        try {
                            View view = manager.findViewByPosition(scrollNewsPos + 1);
                            if (null != view) {
                                AutoVerticalViewView autoVerticalViewView = view.findViewById(R.id.txt_item_autoVertical);
                                if (null != autoVerticalViewView) {
                                    if (enable) {
                                        if (!autoVerticalViewView.isFlipping()) {
                                            autoVerticalViewView.startFlipping();
                                        }
                                    } else {
                                        if (autoVerticalViewView.isFlipping()) {
                                            autoVerticalViewView.stopFlipping();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化 list
     */
    private void initList() {
        adapter = new HomeNewsAdapter(mContext,getViewLifecycleOwner());
        manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 获取缓存
     */
    private void getCacheData() {
        if (null != adapter.getDataSet() && !adapter.getDataSet().isEmpty()) {
            return;
        }
        try {
            if (mContext.getExternalCacheDir() != null) {
                File file = new File(mContext.getExternalCacheDir().getAbsolutePath().concat(File.separator).concat(title).concat(channelId).concat(Constant.SUFFIX_CACHE));
                if (file.exists()) {
                    String content = FileUtils.getContent(file);
                    if (!TextUtils.isEmpty(content)) {
                        List<HomeNewsEntity.ItemHomeNewsEntity> list = JsonUtil.getList(content, HomeNewsEntity.ItemHomeNewsEntity.class);
                        if (!list.isEmpty()) {
                            hasCacheData = true;
                            adapter.setData(list);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (isFirstLoad) {
            isFirstLoad = false;
            refreshLayout.autoRefresh();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(mContext)) {
            currentPageIndex = 1;
            RxHttpJsonParam param = RxHttp.postJson(isIndex ? Constant.NEWS_RECOMMEND_HOME : Constant.LIST_INDEX_HOME);
            if (!isIndex) {
                param.add("cw_channel_id", channelId);
                param.add("cw_page", currentPageIndex);
            }
            LogUtil.e("requestData " + isIndex + "  id " + channelId);
            param.asResponseList(HomeNewsEntity.ItemHomeNewsEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != data && !data.isEmpty()) {
                            adapter.setData(data);
                            currentPageIndex += 1;
                            isBlankViewCanClick = false;
                            showBlankView(false);
                            updateCache(new Gson().toJson(data));
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(false);
                            }
                            if(!hasCacheData){
                                isBlankViewCanClick = true;
                                showBlankView(true);
                            }
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        if(!hasCacheData){
                            isBlankViewCanClick = true;
                            showBlankView(true);
                        }
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
            }
            if (!hasCacheData) {
                showBlankView(true);
            }
        }
    }

    /**
     * 请求推荐新闻
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(mContext)) {
            RxHttpJsonParam param = RxHttp.postJson(isIndex ? Constant.NEWS_RECOMMEND_HOME_MORE_NEW : Constant.LIST_INDEX_HOME);
            if (isIndex) {
                param.add(Constant.STR_INDEX_PAGE, currentPageIndex);
            } else {
                param.add("cw_channel_id", channelId);
                param.add("cw_page", currentPageIndex);
            }
            param.asResponseList(HomeNewsEntity.ItemHomeNewsEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != data && !data.isEmpty()) {
                            adapter.addDataSet(data);
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
     * 保存缓存
     *
     * @param json 缓存内容
     */
    private void updateCache(String json) {
        try {
            if (mContext.getExternalCacheDir() != null) {
                hasCacheData = true;
                FileUtils.saveContent(json, new File(mContext.getExternalCacheDir().getAbsolutePath().concat(File.separator).concat(title).concat(channelId).concat(Constant.SUFFIX_CACHE)));
            }
        } catch (Exception e) {
            e.getStackTrace();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                if (TextUtils.equals("updateFunction", message.getLabel()) && isIndex) {
                    refreshData();
                }
                // 更新应用号状态
                else if (TextUtils.equals(Constant.STATE_APPS_UPDATE, message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                    if (!TextUtils.equals("home", message.getFrom())) {
                        refreshData();
                    }
                } else if (TextUtils.equals("updateUserInfo", message.getLabel()) && isIndex) {
                    refreshData();
                }
            }
        }
    }

    private void showBlankView(boolean isShow) {
        if (null != refreshLayout && null != blankView) {
            if (isShow) {
                refreshLayout.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
            } else {
                refreshLayout.setVisibility(View.VISIBLE);
                blankView.setVisibility(View.GONE);
            }
        } else {
            LogUtil.e("showBlankView 为空 ---- ");
        }
    }

    @Override
    public void onBlankViewClickListener(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.TIME_INTERVAL) {
            if (NetUtil.isNetworkConnected(mContext) && isBlankViewCanClick) {
                showBlankView(false);
                isFirstLoad = true;
                requestData();
            }
        }
        lastClickTime = currentTime;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Override
    public void lazyInit() {
        requestData();
    }
}