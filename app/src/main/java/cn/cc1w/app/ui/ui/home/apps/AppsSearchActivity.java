package cn.cc1w.app.ui.ui.home.apps;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;

import org.xutils.ex.DbException;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.AppsSearchAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsListEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.DatabaseOpenHelper;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 应用号搜索
 *
 * @author kpinfo
 */
public class AppsSearchActivity extends CustomActivity implements BlankViewClickListener, TextView.OnEditorActionListener, AppsSearchAdapter.OnAppsItemClickListener {
    private Unbinder unbinder;
    @BindView(R.id.list_apps_search)
    RecyclerView recyclerView;
    @BindView(R.id.txt_recommend_search)
    TextView appsSearchTv;
    @BindView(R.id.blankView_apps_search)
    BlankView blankView;
    @BindView(R.id.edit_apps_search)
    EditText searchEdit;
    @BindView(R.id.tv_data_none_apps_search)
    TextView hintTv;
    private LoadingDialog loading;
    private AppsSearchAdapter adapter;
    private static final String ID_RECOMMEND = "recommend";
    private long lastTime;
    private long lastSearchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_search);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        unbinder = ButterKnife.bind(this);
        initList();
        initEditText();
        initLoading();
        initBlankView();
        requestData();
    }

    /**
     * 初始化list
     */
    private void initList() {
        lastTime = System.currentTimeMillis();
        lastSearchTime = System.currentTimeMillis();
        adapter = new AppsSearchAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(this, 10), 1));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
    }

    /**
     * 初始化 Edit信息
     */
    private void initEditText() {
        searchEdit.setOnEditorActionListener(this);
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.apps_empty, "抱歉! 没有相关政务号,点击重试", "点击重试");
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.APPS_LIST)
                    .add("cw_group_id", ID_RECOMMEND)
                    .add(Constant.STR_CW_MACHINE_ID, Constant.CW_MACHINE_ID)
                    .asResponseList(AppsListEntity.ItemAppsListEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                appsSearchTv.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                hintTv.setVisibility(View.GONE);
                                blankView.setClickable(false);
                                adapter.setData(list);
                            } else {
                                appsSearchTv.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                                blankView.setClickable(true);
                            }
                        }
                    }, (OnError) error -> {
                        if (!isFinishing()) {
                            blankView.setBlankView(R.mipmap.apps_empty, getString(R.string.result_apps_none));
                            appsSearchTv.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            blankView.setVisibility(View.VISIBLE);
                            blankView.setClickable(false);
                        }
                    });
        } else {
            appsSearchTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            blankView.setVisibility(View.VISIBLE);
            blankView.setClickable(true);
        }
    }

    /**
     * 搜索 应用号
     *
     * @param keyword 应用号搜索的关键字
     */
    private void searchApps(String keyword) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSearchTime >= Constant.MIN_TIME_INTERVAL) {
            if (null != loading) {
                loading.show();
            }
            int currentPageIndex = 1;
            RxHttp.postJson(Constant.CHANNEL_SEARCH).add("keyword", keyword).add("cw_page", currentPageIndex)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(AppsListEntity.ItemAppsListEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            blankView.setVisibility(View.GONE);
                            if (null != list && !list.isEmpty()) {
                                appsSearchTv.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                hintTv.setVisibility(View.GONE);
                                blankView.setClickable(false);
                                adapter.setData(list);
                            } else {
                                appsSearchTv.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                hintTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        }
        lastSearchTime = currentTime;
    }

    /**
     * 空白页点击事件
     *
     * @param view 点击的View
     */
    @Override
    public void onBlankViewClickListener(View view) {
        blankView.setClickable(false);
        requestData();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String content = searchEdit.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            if (NetUtil.isNetworkConnected(this)) {
                searchApps(content);
            } else {
                ToastUtil.showShortToast(getString(R.string.network_error));
            }
        } else {
            LogUtil.e("搜索内容");
        }
        return true;
    }

    @OnClick({R.id.btn_cancel_apps_search, R.id.img_apps_search})
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.btn_cancel_apps_search) {
                finish();
            } else if (id == R.id.img_apps_search) {
                String keyword = searchEdit.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    searchApps(keyword);
                } else {
                    ToastUtil.showShortToast("请输入搜索关键字");
                }
            }
        }
        lastTime = currentTime;
    }

    @Override
    public void onAppsClick(int pos, View v) {
        if (!NetUtil.isNetworkConnected(this)) {
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
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(this, LoginActivity.class);
                            }
                        });
            } else {
                if (null != loading) {
                    loading.show();
                }
                // 取消关注
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
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(this, LoginActivity.class);
                            }
                        });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
