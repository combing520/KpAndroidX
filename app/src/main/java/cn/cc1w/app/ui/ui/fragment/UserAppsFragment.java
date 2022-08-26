package cn.cc1w.app.ui.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.DbManager;

import org.xutils.ex.DbException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.AppsSearchAdapter;
import cn.cc1w.app.ui.adapter.RecommendAppsAdapter;
import cn.cc1w.app.ui.adapter.UserAppsBannerAdapter;
import cn.cc1w.app.ui.adapter.UserFocusAppsAdapter;
import cn.cc1w.app.ui.base.BaseFragment;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsListEntity;
import cn.cc1w.app.ui.ui.home.apps.AppDetailActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ScrollAppsEntity;
import cn.cc1w.app.ui.entity.UserFocusAppsEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.home.apps.AppsSearchActivity;
import cn.cc1w.app.ui.ui.home.apps.FavoriteAppsAddActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.DatabaseOpenHelper;
import cn.cc1w.app.ui.utils.FileUtils;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemGridDecoration;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 开屏号
 *
 * @author kpinfo
 */
public class UserAppsFragment extends BaseFragment implements OnItemClickListener, AppsSearchAdapter.OnAppsItemClickListener {
    @BindView(R.id.list_recommend_apps)
    RecyclerView recommendRecycleView;
    @BindView(R.id.list_focus_favorite_apps)
    RecyclerView focusRecycleView;
    @BindView(R.id.btn_edit_favorite_apps)
    TextView editTxt;
    @BindView(R.id.list_banner_apps)
    Banner banner;
    @BindView(R.id.list_appsRecommend_apps)
    RecyclerView recommendAppsRecycleView;
    private RecommendAppsAdapter recommendAdapter;
    private UserFocusAppsAdapter focusAppsAdapter;
    private AppsSearchAdapter recommendAppsAdapter;
    private static final String ID_RECOMMEND = "recommend";
    private boolean isShow = false;
    private final String title = "recommendAppsNews";
    private Context context;
    private UserAppsBannerAdapter mBannerAdapter;
    private List<ScrollAppsEntity.ItemScrollAppsEntity> mBannerList = new ArrayList<>();

    public UserAppsFragment() {
    }

    public static UserAppsFragment newInstance() {
        UserAppsFragment fragment = new UserAppsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentResId() {
        return R.layout.fragment_user_apps;
    }

    @Override
    protected void onFindView(View rootView) {
        initBanner();
        initList();
    }

    @Override
    protected void onBindListener() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onApplyData() {
        getCacheData();
        requestData();
    }

    @Override
    public void frResume() {

    }

    @Override
    public void frPause() {

    }

    /**
     * 获取缓存
     */
    private void getCacheData() {
        getRecommendDataFromCache();
        getUserFocusFromCache();
        getScrollAppsListFromCache();
        getRecommendAppsFromCache();
    }

    /**
     * 请求数据
     */
    private void requestData() {
        requestRecommend();
        requestFocusAppsList();
        requestScrollApps();
        requestRecommendAppsList();
    }

    private void getRecommendDataFromCache() {
        try {
            List<AppsListEntity.ItemAppsListEntity> list = DatabaseOpenHelper.getInstance().findAll(AppsListEntity.ItemAppsListEntity.class);
            if (null != list && !list.isEmpty()) {
                recommendAdapter.setData(list);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void getUserFocusFromCache() {
        try {
            List<UserFocusAppsEntity.ItemUserFocusAppsEntity> list = DatabaseOpenHelper.getInstance().findAll(UserFocusAppsEntity.ItemUserFocusAppsEntity.class);
            if (null != list && !list.isEmpty()) {
                focusAppsAdapter.setData(list);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void getScrollAppsListFromCache() {
        try {
            List<ScrollAppsEntity.ItemScrollAppsEntity> list = DatabaseOpenHelper.getInstance().findAll(ScrollAppsEntity.ItemScrollAppsEntity.class);
            if (null != list && !list.isEmpty()) {
                mBannerList = list;
                mBannerAdapter.setDatas(mBannerList);
                mBannerAdapter.notifyDataSetChanged();
                banner.setVisibility(View.VISIBLE);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void getRecommendAppsFromCache() {
        try {
            if (context.getExternalCacheDir() != null) {
                File file = new File(mContext.getExternalCacheDir(), File.separator.concat(title).concat(Constant.SUFFIX_CACHE));
                if (file.exists()) {
                    String content = FileUtils.getContent(file);
                    if (!TextUtils.isEmpty(content)) {
                        LogUtil.d("getRecommendAppsFromCache !!! " + content);
                        List<AppsListEntity.ItemAppsListEntity> list = JsonUtil.getList(content, AppsListEntity.ItemAppsListEntity.class);
                        if (!list.isEmpty()) {
                            recommendAppsAdapter.setData(list);
                        }
                    } else {
                        LogUtil.d("getRecommendAppsFromCache 为空 ！！！！+++++ ");
                    }
                }
            } else {
                LogUtil.d("getRecommendAppsFromCache 为空 ！！！");
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void initBanner() {
        mBannerAdapter = new UserAppsBannerAdapter(mBannerList);
        banner.setAdapter(mBannerAdapter).setIndicator(new CircleIndicator(context));
        banner.setOnBannerListener((data, position) -> {
            ScrollAppsEntity.ItemScrollAppsEntity bannerItem = mBannerList.get(position);
            String newsId = bannerItem.getId();
            if (!TextUtils.isEmpty(newsId)) {
                Bundle bundle = new Bundle();
                bundle.putString("id", bannerItem.getId());
                bundle.putString("title", bannerItem.getName());
                bundle.putString("group_id", bannerItem.getGroup_id());
                IntentUtil.startActivity(context, AppDetailActivity.class, bundle);
            }
        });
    }

    /**
     * 初始化 list
     */
    private void initList() {
        recommendAdapter = new RecommendAppsAdapter();
        LinearLayoutManager recommendManager = new LinearLayoutManager(context);
        recommendManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendRecycleView.setLayoutManager(recommendManager);
        recommendRecycleView.setAdapter(recommendAdapter);
        focusAppsAdapter = new UserFocusAppsAdapter();
        focusRecycleView.setLayoutManager(new GridLayoutManager(context, 3));
        focusRecycleView
                .addItemDecoration(new SpacesItemGridDecoration(AppUtil.dip2px(context, AppUtil.dip2px(context, 3)), 3, false));
        focusRecycleView.setAdapter(focusAppsAdapter);
        focusAppsAdapter.setOnItemDeleteClickListener(this);

        recommendAppsAdapter = new AppsSearchAdapter();
        recommendAppsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        recommendAppsRecycleView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context, 10), 1));
        recommendAppsRecycleView.setAdapter(recommendAppsAdapter);
        recommendAppsAdapter.setOnItemClickListener(this);
        recommendRecycleView.setMotionEventSplittingEnabled(false);
        focusRecycleView.setMotionEventSplittingEnabled(false);
        recommendAppsRecycleView.setMotionEventSplittingEnabled(false);
        try {
            recommendRecycleView.setNestedScrollingEnabled(false);
            recommendRecycleView.setHasFixedSize(true);
            focusRecycleView.setNestedScrollingEnabled(false);
            focusRecycleView.setHasFixedSize(true);
            recommendAppsRecycleView.setNestedScrollingEnabled(false);
            recommendAppsRecycleView.setHasFixedSize(true);
            banner.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_edit_favorite_apps, R.id.ll_search_apps, R.id.ll_more_apps})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_edit_favorite_apps) {
            editFavoriteApps();
        } else if (id == R.id.ll_search_apps) {
            IntentUtil.startActivity(context, AppsSearchActivity.class, null);
        } else if (id == R.id.ll_more_apps) {
            IntentUtil.startActivity(context, FavoriteAppsAddActivity.class, null);
        }
    }

    /**
     * 编辑应用号
     */
    private void editFavoriteApps() {
        if (isShow) {
            focusAppsAdapter.hideDeleteIcon();
            editTxt.setText("编辑");
        } else {
            focusAppsAdapter.showDeleteIcon();
            editTxt.setText("完成");
        }
        isShow = !isShow;
    }

    /**
     * 请求推荐
     */
    private void requestRecommend() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.get(Constant.RECOMMEND_LIST_APPS)
                    .asResponseList(AppsListEntity.ItemAppsListEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            try {
                                DbManager db = DatabaseOpenHelper.getInstance();
                                db.delete(AppsListEntity.ItemAppsListEntity.class);
                                db.saveOrUpdate(list);
                            } catch (DbException e) {
                                e.printStackTrace();
                                LogUtil.e("保存数据库失败 ");
                            }
                            recommendAdapter.setData(list);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 获取关注的应用号列表
     */
    private void requestFocusAppsList() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.LIST_APPS_USER)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(UserFocusAppsEntity.ItemUserFocusAppsEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (list != null && !list.isEmpty()) {
                            try {
                                DbManager db = DatabaseOpenHelper.getInstance();
                                db.delete(UserFocusAppsEntity.ItemUserFocusAppsEntity.class);
                                db.saveOrUpdate(list);
                            } catch (DbException e) {
                                e.printStackTrace();
                                LogUtil.e("保存【用户关注的应用号】数据库失败 " + e.getMessage());
                            }
                        }
                        focusAppsAdapter.setData(list);
                        UserFocusAppsEntity.ItemUserFocusAppsEntity appsInfo = new UserFocusAppsEntity.ItemUserFocusAppsEntity();
                        appsInfo.setRes(R.mipmap.ic_add_apps);
                        appsInfo.setName("添加+");
                        focusAppsAdapter.addItem(focusAppsAdapter.getItemCount(), appsInfo);
                        if (isShow) {
                            focusAppsAdapter.showDeleteIcon();
                        } else {
                            focusAppsAdapter.hideDeleteIcon();
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 获取滚动的 应用号
     */
    private void requestScrollApps() {
        RxHttp.postJson(Constant.LIST_APPS_CHANNEL_SCROLL)
                .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                .asResponseList(ScrollAppsEntity.ItemScrollAppsEntity.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    if (data != null && !data.isEmpty()) {
                        try {
                            DbManager db = DatabaseOpenHelper.getInstance();
                            db.delete(ScrollAppsEntity.ItemScrollAppsEntity.class);
                            db.saveOrUpdate(data);
                        } catch (DbException e) {
                            e.printStackTrace();
                            LogUtil.e("保存【滚动的应用号】数据库失败 " + e.getMessage());
                        }
                        mBannerList = data;
                        mBannerAdapter.setDatas(data);
                        mBannerAdapter.notifyDataSetChanged();
                        banner.setVisibility(View.VISIBLE);
                    } else {
                        banner.setVisibility(View.GONE);
                    }
                }, (OnError) error -> {
                });
    }

    // 请求推荐的 应用号
    private void requestRecommendAppsList() {
        if (NetUtil.isNetworkConnected(mContext)) {
            RxHttp.postJson(Constant.APPS_LIST)
                    .add("cw_group_id", ID_RECOMMEND)
                    .add(Constant.STR_CW_MACHINE_ID, Constant.CW_MACHINE_ID)
                    .asResponseList(AppsListEntity.ItemAppsListEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (data != null && !data.isEmpty()) {
                            updateCache(new Gson().toJson(data));
                            recommendAppsAdapter.setData(data);
                        }
                    }, (OnError) error -> {
                        LogUtil.e("requestRecommendAppsList 出错=" + error.getErrorMsg());
                    });
        }
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        final UserFocusAppsEntity.ItemUserFocusAppsEntity entity = focusAppsAdapter.getItem(pos);
        //删除当前关注的应用号
        if (null != entity) {
            if (!NetUtil.isNetworkConnected(mContext)) {
                ToastUtil.showShortToast(getString(R.string.network_error));
            } else {
                showLoading();
                RxHttp.postJson(Constant.APPS_FOCUS_CANCEL)
                        .add("cw_app_id", entity.getId())
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            hideLoading();
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id()));
                            focusAppsAdapter.deleteItem(pos);
                            requestRecommendAppsList();
                        }, (OnError) error -> {
                            hideLoading();
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(mContext, LoginActivity.class);
                            }
                        });
            }
        }
    }

    @Override
    public void onAppsClick(int pos, View v) {
        if (!NetUtil.isNetworkConnected(mContext)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
            return;
        }
        final AppsListEntity.ItemAppsListEntity entity = recommendAppsAdapter.getItem(pos);
        if (null != entity) {
            if (!entity.isAttention()) {
                showLoading();
                RxHttp.postJson(Constant.ADD_APPS)
                        .add("cw_app_id", entity.getId())
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .asResponse(JsonObject.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            hideLoading();
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id(), "appList"));
                            recommendAppsAdapter.addApps(pos);
                            List<AppsListEntity.ItemAppsListEntity> dataSet = recommendAppsAdapter.getDataSet();
                            requestFocusAppsList();
                            if (dataSet != null && !dataSet.isEmpty()) {
                                try {
                                    DbManager db = DatabaseOpenHelper.getInstance();
                                    db.saveOrUpdate(dataSet);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                    LogUtil.e("保存数据库失败 " + e.getMessage());
                                }
                            }
                        }, (OnError) error -> {
                            hideLoading();
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(mContext, LoginActivity.class);
                            }
                        });
            } else {
                showLoading();
                RxHttp.postJson(Constant.APPS_FOCUS_CANCEL)
                        .add("cw_app_id", entity.getId())
                        .asResponse(JsonObject.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            hideLoading();
                            recommendAppsAdapter.cancelApps(pos);
                            List<AppsListEntity.ItemAppsListEntity> dataSet = recommendAppsAdapter.getDataSet();
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id(), "appList"));
                            requestFocusAppsList();
                            if (dataSet != null && !dataSet.isEmpty()) {
                                try {
                                    DbManager db = DatabaseOpenHelper.getInstance();
                                    db.saveOrUpdate(dataSet);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                    LogUtil.e("保存数据库失败 " + e.getMessage());
                                }
                            }
                        }, (OnError) error -> {
                            hideLoading();
                            ToastUtil.showShortToast(error.getErrorMsg());
                        });
            }
        }
    }

    /**
     * 接收EventBus
     *
     * @param message eventBus接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals(Constant.STATE_APPS_UPDATE, message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                // 关注的 应用号列表
                requestFocusAppsList();
                // 推荐的应用号列表
                requestRecommendAppsList();
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
            if (context.getExternalCacheDir() != null) {
                FileUtils.deleteFile(mContext.getExternalCacheDir().getAbsolutePath() + File.separator.concat(title).concat(Constant.SUFFIX_CACHE));
                File file = new File(mContext.getExternalCacheDir(), File.separator.concat(title).concat(Constant.SUFFIX_CACHE));
                FileUtils.saveContent(json, file);
            }
        } catch (Exception e) {
            e.getStackTrace();
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
    }
}