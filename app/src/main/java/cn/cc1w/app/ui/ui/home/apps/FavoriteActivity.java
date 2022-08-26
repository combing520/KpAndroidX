package cn.cc1w.app.ui.ui.home.apps;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.RecommendAppsAdapter;
import cn.cc1w.app.ui.adapter.UserFocusAppsAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsListEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserFocusAppsEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemGridDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 我的最爱
 *
 * @author kpinfo
 */
public class FavoriteActivity extends CustomActivity implements OnItemClickListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_recommend_favorite)
    RecyclerView recommendList;
    @BindView(R.id.list_focus_favorite)
    RecyclerView focusList;
    @BindView(R.id.btn_edit_favorite)
    TextView editTxt;
    private LoadingDialog loading;
    private Unbinder unbinder;
    private RecommendAppsAdapter recommendAdapter;
    private UserFocusAppsAdapter focusAppsAdapter;
    private boolean isShow = false;
    private long enterTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
        initLoading();
        requestRecommend();
        requestFocusAppsList();
    }

    /**
     * 初始化 导航信息
     */
    private void initNavigation() {
        titleTv.setText("我的最爱");
        enterTime = System.currentTimeMillis();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化 list
     */
    private void initList() {
        recommendAdapter = new RecommendAppsAdapter();
        LinearLayoutManager recommendManager = new LinearLayoutManager(this);
        recommendManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendList.setLayoutManager(recommendManager);
        recommendList.setAdapter(recommendAdapter);
        focusAppsAdapter = new UserFocusAppsAdapter();
        focusList.setLayoutManager(new GridLayoutManager(this, 3));
        focusList.addItemDecoration(new SpacesItemGridDecoration(AppUtil.dip2px(this, 10), 3, false));
        focusList.setAdapter(focusAppsAdapter);
        focusAppsAdapter.setOnItemDeleteClickListener(this);
        recommendList.setMotionEventSplittingEnabled(false);
        focusList.setMotionEventSplittingEnabled(false);
    }

    /**
     * 请求推荐
     */
    private void requestRecommend() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.RECOMMEND_LIST_APPS)
                    .asResponseList(AppsListEntity.ItemAppsListEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        recommendAdapter.setData(list);
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        }
    }

    /**
     * 获取关注的应用号列表
     */
    private void requestFocusAppsList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.LIST_APPS_USER)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(UserFocusAppsEntity.ItemUserFocusAppsEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
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
                        }
                    }, (OnError) error -> {
                    });
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
            editTxt.setText("取消");
        }
        isShow = !isShow;
    }

    /**
     * 分享 APP
     */
    private void shareApp() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - enterTime >= Constant.MIN_TIME_INTERVAL) {
            Gson gson = new Gson();
            Bundle bundle = new Bundle();
            ShareEntity shareEntity = new ShareEntity();
            shareEntity.setNewsId("");
            shareEntity.setRedirect_url("");
            shareEntity.setTitle(Constant.TILE_SHARE);
            shareEntity.setSummary(Constant.SUMMARY_SHARE);
            shareEntity.setUrl("https://appkp.ccwb.cn/app/download.html");
            shareEntity.setType(Constant.TYPE_SHARE_NEWS);
            String shareContent = gson.toJson(shareEntity);
            bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
            IntentUtil.startActivity(this, ShareActivity.class, bundle);
        }
        enterTime = currentTime;
    }

    @Override
    public void onItemClick(View targetView, final int pos) {
        final UserFocusAppsEntity.ItemUserFocusAppsEntity entity = focusAppsAdapter.getItem(pos);
        // 删除当前关注的应用号
        if (null != entity) {
            if (!NetUtil.isNetworkConnected(this)) {
                ToastUtil.showShortToast(getString(R.string.network_error));
            } else {
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.APPS_FOCUS_CANCEL).add("cw_app_id", entity.getId())
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id()));
                            focusAppsAdapter.deleteItem(pos);
                        }, (OnError) error -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(this, LoginActivity.class);
                            }
                        });
            }
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.img_share_header_not_title, R.id.btn_edit_favorite, R.id.ll_search_favorite})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.img_share_header_not_title) {
            shareApp();
        } else if (id == R.id.btn_edit_favorite) {
            editFavoriteApps();
        } else if (id == R.id.ll_search_favorite) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(this, AppsSearchActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 接收EventBus
     *
     * @param message eventBus接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (TextUtils.equals(Constant.STATE_APPS_UPDATE, message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                requestFocusAppsList();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}