package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.broke.PaiKewHotTopicAdapter;
import cn.cc1w.app.ui.adapter.topic.RecommendTopicAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HotTopicEntity;
import cn.cc1w.app.ui.entity.RecommendTopicEntity;
import cn.cc1w.app.ui.ui.home.record.TopicRankActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 热门拍客
 *
 * @author kpinfo
 */
public class HotPaikewWorksFragment extends Fragment implements BlankViewClickListener, OnRefreshListener {
    @BindView(R.id.list_tag_topic_hot)
    RecyclerView tagRecycleView;
    @BindView(R.id.list_works_topic_top)
    RecyclerView worksRecycleView;
    @BindView(R.id.ll_container_tag_topic)
    LinearLayout tagLayout;
    @BindView(R.id.ll_container_popularity)
    LinearLayout popularityLayout;
    @BindView(R.id.ll_container_hot_topic)
    LinearLayout hotWorksLayout;
    @BindView(R.id.blank_topic_top)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View decorView;
    private Unbinder unbinder;
    private PaiKewHotTopicAdapter tagAdapter;
    private RecommendTopicAdapter recommendAdapter;
    private LoadingDialog loading;
    private boolean isFirstLoadTag = true;
    private boolean isFirstLoadTopic = true;
    private boolean isHasRecommendData = false;
    private Context context;
    private long lastClickTime = System.currentTimeMillis();
    private boolean isUiCreated = false;

    public HotPaikewWorksFragment() {

    }

    public static HotPaikewWorksFragment newInstance() {
        HotPaikewWorksFragment fragment = new HotPaikewWorksFragment();
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
            decorView = inflater.inflate(R.layout.fragment_hot_paikew_works, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isUiCreated = true;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initLoading();
        initList();
        initBlankView();
    }

    /**
     * 初始化 list
     */
    private void initList() {
        tagAdapter = new PaiKewHotTopicAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        tagRecycleView.setLayoutManager(manager);
        tagRecycleView.setAdapter(tagAdapter);
        worksRecycleView.setMotionEventSplittingEnabled(false);
        tagRecycleView.setMotionEventSplittingEnabled(false);

        recommendAdapter = new RecommendTopicAdapter(context);
        LinearLayoutManager manager1 = new LinearLayoutManager(context);
        worksRecycleView.setLayoutManager(manager1);
        worksRecycleView.setAdapter(recommendAdapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableLoadMore(false);
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.apps_empty, "抱歉! 没有相关热门拍客,点击重试", "点击重试");
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(context);
    }

    /**
     * 显示空白页
     */
    private void showBlankView() {
        if (null != blankView && blankView.getVisibility() == View.GONE) {
            blankView.setVisibility(View.VISIBLE);
            blankView.setClickable(true);
        }
        if (null != tagLayout) {
            tagLayout.setVisibility(View.GONE);
        }
        if (null != hotWorksLayout) {
            hotWorksLayout.setVisibility(View.GONE);
        }
        if (null != popularityLayout) {
            popularityLayout.setVisibility(View.GONE);
        }
        if (null != refreshLayout) {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏空白页
     */
    private void hideBlankView() {
        if (null != tagLayout) {
            tagLayout.setVisibility(View.VISIBLE);
        }
        if (null != hotWorksLayout) {
            hotWorksLayout.setVisibility(View.VISIBLE);
        }
        if (null != popularityLayout) {
            popularityLayout.setVisibility(View.VISIBLE);
        }
        if (null != refreshLayout) {
            refreshLayout.setVisibility(View.VISIBLE);
        }
        if (null != blankView) {
            blankView.setVisibility(View.GONE);
            blankView.setClickable(false);
        }
    }

    /**
     * 当前页面是否可见
     *
     * @param isVisibleToUser 当前页面的可见情况
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("isVisibleToUser = " + isVisibleToUser + "  isUiCreated = " + isUiCreated);
        if (isVisibleToUser && isUiCreated) {
            requestData();
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(context)) {
            requestTagList();
        }
        else {
            if (!isHasRecommendData) {
                showBlankView();
            }
        }
    }

    /**
     * 请求标签 列表
     */
    private void requestTagList() {
        if (NetUtil.isNetworkConnected(context)) {
            if (!isFirstLoadTag) {
                return;
            }
            isFirstLoadTag = false;
            isUiCreated = false;
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.LIST_TOPIC_HOT)
                    .asResponseList(HotTopicEntity.ItemHotTopicEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        hideBlankView();
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (list != null && !list.isEmpty()) {
                            tagAdapter.setData(list);
                        }
                        requestRecommendTopicList();
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        requestRecommendTopicList();
                    });
        }
    }

    /**
     * 获取推荐列表
     */
    private void requestRecommendTopicList() {
        if (NetUtil.isNetworkConnected(context)) {
            if (!isFirstLoadTopic) {
                return;
            }
            isFirstLoadTopic = false;
            RxHttp.get(Constant.LIST_RECOMMEND_TOPIC)
                    .asResponseList(RecommendTopicEntity.ItemRecommendTopicEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        hideBlankView();
                        if (null != list && !list.isEmpty()) {
                            recommendAdapter.setData(list);
                            isHasRecommendData = true;
                        } else {
                            isHasRecommendData = false;
                        }
                    }, (OnError) error -> showBlankView());
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.get(Constant.LIST_RECOMMEND_TOPIC)
                    .asResponseList(RecommendTopicEntity.ItemRecommendTopicEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (list != null && !list.isEmpty()) {
                            recommendAdapter.setData(list);
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

    @OnClick({R.id.ll_rank_video_topic_hot, R.id.ll_rank_pic_topic_hot})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_rank_video_topic_hot) {
            Bundle bundle = new Bundle();
            bundle.putInt("selectPos", 1);
            IntentUtil.startActivity(context, TopicRankActivity.class, bundle);
        } else if (id == R.id.ll_rank_pic_topic_hot) {
            Bundle bundle2 = new Bundle();
            bundle2.putInt("selectPos", 2);
            IntentUtil.startActivity(context, TopicRankActivity.class, bundle2);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
    public void onBlankViewClickListener(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.TIME_INTERVAL && NetUtil.isNetworkConnected(context)) {
            blankView.setClickable(false);
            isFirstLoadTag = true;
            isFirstLoadTopic = true;
            isUiCreated = true;
            requestData();
        }
        lastClickTime = currentTime;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}