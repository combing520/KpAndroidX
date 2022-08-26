package cn.cc1w.app.ui.ui.home.apps.fragment;

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
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.AppsClassifyAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsListEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.DatabaseOpenHelper;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 应用号 list Fragment
 *
 * @author kpinfo
 */
public class AppListFragment extends Fragment implements OnItemClickListener, OnRefreshListener {
    @BindView(R.id.list_app_list)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_list_apps)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private LoadingDialog loading;
    private boolean isViewCreated;
    private boolean isUiVisible;
    private View decorView;
    private Unbinder unbinder;
    private boolean isFirstLoad = true;
    private AppsClassifyAdapter adapter;
    private Context context;
    private String id;

    public static AppListFragment newInstance(String title, String id) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString(Constant.TAG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(Constant.TAG_ID);
            adapter = new AppsClassifyAdapter();
            getCacheData();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_app_list, container, false);
        }
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();
        requestData();
    }
    /**
     * 初始化
     */
    private void init() {
        initList();
        initLoading();
        initBlankView();
    }

    /**
     * 当前页面是否可见
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
     * 获取缓存数据
     */
    private void getCacheData() {
        try {
            List<AppsListEntity.ItemAppsListEntity> list = DatabaseOpenHelper.getInstance().selector(AppsListEntity.ItemAppsListEntity.class).where("group_id", "=", id).findAll();
            if (null != list && !list.isEmpty()) {
                adapter.setData(list);
            }
        } catch (DbException e) {
            e.printStackTrace();
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
     * 更新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.APPS_LIST)
                    .add("cw_group_id", id)
                    .add(Constant.STR_CW_MACHINE_ID, Constant.CW_MACHINE_ID)
                    .asResponseList(AppsListEntity.ItemAppsListEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        if (list != null && !list.isEmpty()) {
                            adapter.setData(list);
                            try {
                                DbManager db = DatabaseOpenHelper.getInstance();
                                db.saveOrUpdate(list);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            showBlankView(false);
                        } else {
                            adapter.clearData();
                            try {
                                DbManager db = DatabaseOpenHelper.getInstance();
                                db.delete(AppsListEntity.ItemAppsListEntity.class);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            showBlankView(true);
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        showBlankView(true);
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
            }
        }
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.apps_empty, "抱歉 ! 没有相关应用号");
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(context);
    }

    /**
     * 初始化 RecycleView
     */
    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(this);
    }

    private void showBlankView(boolean isShow){
        if(refreshLayout != null && blankView != null){
            if(isShow){
                refreshLayout.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
            }else{
                refreshLayout.setVisibility(View.VISIBLE);
                blankView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(View targetView, final int pos) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
            return;
        }
        final AppsListEntity.ItemAppsListEntity entity = adapter.getItem(pos);
        if (null != entity) {

            if (!entity.isAttention()) {
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.ADD_APPS)
                        .add("cw_app_id", entity.getId())
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id(), "appList"));
                            adapter.addApps(pos);
                            List<AppsListEntity.ItemAppsListEntity> dataSet = adapter.getDataSet();
                            try {
                                DbManager db = DatabaseOpenHelper.getInstance();
                                db.saveOrUpdate(dataSet);
                            } catch (DbException e) {
                                e.printStackTrace();
                                LogUtil.e("保存数据库失败 ");
                            }
                        }, (OnError) error -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(context, LoginActivity.class);
                            }
                        });
            } else {
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.APPS_FOCUS_CANCEL)
                        .add("cw_app_id", entity.getId())
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            adapter.cancleApps(pos);
                            List<AppsListEntity.ItemAppsListEntity> dataSet = adapter.getDataSet();
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id(), "appList"));
                            try {
                                DbManager db = DatabaseOpenHelper.getInstance();
                                db.saveOrUpdate(dataSet);
                            } catch (DbException e) {
                                e.printStackTrace();
                                LogUtil.e("保存数据库失败 " + e.getMessage());
                            }
                        }, (OnError) error -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(context, LoginActivity.class);
                            }
                        });
            }
        }
    }

    /**
     * 接收EventBus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (TextUtils.equals(Constant.STATE_APPS_UPDATE, message.getLabel()) && !TextUtils.equals(message.getFrom(), "appList")) {
                refreshData();
            }
        }
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
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
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
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshData();
    }

}