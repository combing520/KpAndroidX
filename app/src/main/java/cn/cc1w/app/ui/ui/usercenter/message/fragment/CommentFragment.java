package cn.cc1w.app.ui.ui.usercenter.message.fragment;

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

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.CommentAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemCommentEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 我的评论
 *
 * @author kpinfo
 */
public class CommentFragment extends Fragment implements OnItemClickListener, OnRefreshListener, OnLoadMoreListener {
    private Unbinder unbinder;
    private View decorView;
    @BindView(R.id.list_comment)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_comment)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private CommentAdapter adapter;
    private Context context;
    private int currentPageIndex = 1;
    private LoadingDialog loading;

    public CommentFragment() {
    }

    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CommentAdapter(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_comment, container, false);
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
     * 初始化 list
     */
    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context, 8), 1));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.comment_empty, getString(R.string.comment_none));
    }

    /**
     * 获取数据
     */
    private void requestData() {
        refreshLayout.autoRefresh();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.postJson(Constant.USER_COMMENT_LIST).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(ItemCommentEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (refreshLayout != null && blankView != null ) {
                            refreshLayout.finishRefresh();
                            if (null != dataSet && !dataSet.isEmpty()) {
                                adapter.setData(dataSet);
                                refreshLayout.setVisibility(View.VISIBLE);
                                currentPageIndex += 1;
                                blankView.setVisibility(View.GONE);
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if ( blankView != null && refreshLayout != null) {
                            refreshLayout.finishRefresh();
                            refreshLayout.setVisibility(View.GONE);
                            blankView.setVisibility(View.VISIBLE);
                        }
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(context, LoginActivity.class);
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
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.USER_COMMENT_LIST).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(ItemCommentEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                            if (null != dataSet && !dataSet.isEmpty()) {
                                adapter.addData(dataSet);
                                currentPageIndex += 1;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                refreshLayout.setEnableLoadMore(false);
                            }
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
    public void onItemClick(View view, final int pos) {
        LogUtil.e("pos = " + pos);
        ItemCommentEntity.DataBean item = adapter.getItem(pos);
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.showShortToast(context.getResources().getString(R.string.network_error));
        } else {
            RxHttp.postJson(Constant.APPRECIATE_LIST_COMMENT).add("cw_comment_id", item.getId())
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        adapter.doAppreciate(pos);
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                    }, (OnError) error -> ToastUtil.showShortToast(error.getErrorMsg()));
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
    public void onDestroyView() {
        super.onDestroyView();
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}