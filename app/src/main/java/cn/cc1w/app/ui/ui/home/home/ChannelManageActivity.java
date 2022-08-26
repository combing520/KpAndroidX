package cn.cc1w.app.ui.ui.home.home;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.UserFocusChannelManageAdapter;
import cn.cc1w.app.ui.adapter.AllChannelManageAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 频道管理
 *
 * @author kpinfo
 */
public class ChannelManageActivity extends CustomActivity implements AllChannelManageAdapter.ItemAddListener, UserFocusChannelManageAdapter.OnItemDeleteListener {
    private Unbinder mBind;
    @BindView(R.id.channel_manage_all_recycle)
    RecyclerView mAllChannelRecycleView;
    @BindView(R.id.channel_manage_column_recycle)
    RecyclerView mFocusRecycleView;
    private AllChannelManageAdapter mAllChannelAdapter;
    private UserFocusChannelManageAdapter mFocusAdapter;
    private static final int CNT_GRID = 4;
    private LoadingDialog mLoadingView;
    private boolean isHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_manage);
        init();
    }

    private void init() {
        overridePendingTransition(0, 0);
        mBind = ButterKnife.bind(this);
        initStatusBar();
        initView();
        initData();
    }

    private void initStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).init();
    }

    private void initView() {
        initLoading();
        initRecycleView();
    }

    private void initLoading() {
        mLoadingView = AppUtil.getLoading(this);
    }

    private void initRecycleView() {
        mAllChannelAdapter = new AllChannelManageAdapter();
        mAllChannelRecycleView.setLayoutManager(new GridLayoutManager(this, CNT_GRID));
        mAllChannelRecycleView.setAdapter(mAllChannelAdapter);
        mAllChannelAdapter.setOnItemAddListener(this);
        mFocusAdapter = new UserFocusChannelManageAdapter();
        mFocusRecycleView.setLayoutManager(new GridLayoutManager(this, CNT_GRID));
        mFocusRecycleView.setAdapter(mFocusAdapter);
        mFocusAdapter.setOnItemClickListener(this);
    }

    private void initData() {
        requestUserFocusChannelList();
        requestAllChannelList();
    }

    /**
     * 请求频道列表数据
     */
    private void requestAllChannelList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.CHANNEL_INDEX_TOTAL)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(HomeChannelEntity.ItemHomeChannelEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (!isFinishing()) {
                            mAllChannelAdapter.setData(dataSet);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 请求用户关注的 频道
     */
    private void requestUserFocusChannelList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.CHANNEL_FOCUS_USER2)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(HomeChannelEntity.ItemHomeChannelEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            mFocusAdapter.setData(list);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    private void updateUserFocusChannel() {
        if (NetUtil.isNetworkConnected(this)) {
            if (!mFocusAdapter.getDataSet().isEmpty()) {
                if (isHasChanged) {
                    if (null != mLoadingView) {
                        mLoadingView.show();
                    }
                    StringBuilder channelIds = new StringBuilder();
                    for (HomeChannelEntity.ItemHomeChannelEntity userFocusChannel : mFocusAdapter.getDataSet()) {
                        channelIds.append(userFocusChannel.getId()).append(",");
                    }
                    RxHttp.postJson(Constant.CHANNEL_HOME_ADD).add("cw_channel_ids", channelIds.toString())
                            .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (null != mLoadingView && mLoadingView.isShow()) {
                                    mLoadingView.close();
                                }
                                if (!isFinishing()) {
                                    if(data != null){
                                        ToastUtil.showShortToast(data.getMessage());
                                    }
                                    EventBus.getDefault().post(new EventMessage("updateUserFocusChannel", "updateUserFocusChannel"));
                                    finish();
                                }
                            }, (OnError) error -> {
                                if (null != mLoadingView && mLoadingView.isShow()) {
                                    mLoadingView.close();
                                }
                                ToastUtil.showShortToast(error.getErrorMsg());
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(this, LoginActivity.class);
                                }
                            });
                } else {
                    finish();
                }
            } else {
                ToastUtil.showShortToast("请至少选择一个频道");
            }
        } else {
            finish();
        }
    }

    @Override
    public void addItem(View v, int pos) {
        HomeChannelEntity.ItemHomeChannelEntity item = mAllChannelAdapter.getItem(pos);
        if (!item.isAttention()) {
            item.setAttention(true);
            mAllChannelAdapter.removeItem(item);
            mAllChannelAdapter.notifyDataSetChanged();
            mFocusAdapter.addItem(item);
            isHasChanged = true;
        }
    }

    @Override
    public void deleteItem(View v, int pos) {
        HomeChannelEntity.ItemHomeChannelEntity entity = mFocusAdapter.getItem(pos);
        mAllChannelAdapter.addItem(entity);
        if (null != entity && !entity.isIs_fix()) {
            mFocusAdapter.deleteItem(pos);
            for (HomeChannelEntity.ItemHomeChannelEntity focusItem : mAllChannelAdapter.getDataList()) {
                if (TextUtils.equals(entity.getId(), focusItem.getId())) {
                    focusItem.setAttention(false);
                    mAllChannelAdapter.notifyDataSetChanged();
                }
            }
            isHasChanged = true;
        }
    }

    @OnClick({R.id.channel_manage_close_btn, R.id.channel_manage_save_btn})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.channel_manage_save_btn) {
            updateUserFocusChannel();
        } else if (id == R.id.channel_manage_close_btn) {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mLoadingView && mLoadingView.isShow()) {
            mLoadingView.close();
        }
        mBind.unbind();
    }
}