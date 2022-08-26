package cn.cc1w.app.ui.ui.home.home;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.home.HomeAllChannelAdapter;
import cn.cc1w.app.ui.adapter.home.HomeChannelFocusAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.interfaces.DefaultItemCallback;
import cn.cc1w.app.ui.interfaces.DefaultItemTouchHelper;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 编辑频道
 * @author kpinfo
 */
public class ChannelEditActivity extends CustomActivity implements OnItemClickListener {
    private Unbinder unbinder;
    @BindView(R.id.list_all_channel)
    RecyclerView allChannelList;
    @BindView(R.id.list_user_channel)
    RecyclerView userFocusList;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_edit_user_channel)
    TextView submitBtn;
    private HomeAllChannelAdapter allChannelAdapter;
    private HomeChannelFocusAdapter userAdapter;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_edit);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
        initLoading();
        initData();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化导航头信息
     */
    private void initNavigation() {
        titleTv.setText("频道订阅");
    }

    /**
     * 初始化 RecycleView
     */
    private void initList() {
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        allChannelAdapter = new HomeAllChannelAdapter();
        allChannelList.setLayoutManager(manager);
        allChannelList.addItemDecoration(new GridSpacingItemDecoration(4, AppUtil.dip2px(this, 8), true));
        allChannelList.setAdapter(allChannelAdapter);
        allChannelAdapter.setOnAllChannelItemClickListener((targetView, pos) -> {
            HomeChannelEntity.ItemHomeChannelEntity entity = allChannelAdapter.getItem(pos);
            entity.setAttention(true);
            allChannelAdapter.notifyDataSetChanged();
            userAdapter.addItem(entity);
        });
        userAdapter = new HomeChannelFocusAdapter();
        GridLayoutManager manager2 = new GridLayoutManager(this, 4);
        userFocusList.setLayoutManager(manager2);
        userFocusList.addItemDecoration(new GridSpacingItemDecoration(4, AppUtil.dip2px(this, 8), true));
        userFocusList.setAdapter(userAdapter);
        DefaultItemCallback callback = new DefaultItemCallback(userAdapter);
        DefaultItemTouchHelper helper = new DefaultItemTouchHelper(callback);
        helper.attachToRecyclerView(userFocusList);
        userAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        List<HomeChannelEntity.ItemHomeChannelEntity> allChannelDataSet = getIntent().getParcelableArrayListExtra("AllChannelList");
        allChannelAdapter.setData(allChannelDataSet);
        List<HomeChannelEntity.ItemHomeChannelEntity> userChannelDataSet = getIntent().getParcelableArrayListExtra("userChannelList");
        userAdapter.setData(userChannelDataSet);
    }


    /**
     * 选中的 频道的条目点击
     *
     * @param targetView 目标View
     * @param pos        对应的位置
     */
    @Override
    public void onItemClick(View targetView, int pos) {
        HomeChannelEntity.ItemHomeChannelEntity entity = userAdapter.getItem(pos);
        if (null != entity) {
            userAdapter.deleteItem(pos);
            for (HomeChannelEntity.ItemHomeChannelEntity focusItem : allChannelAdapter.getDataList()) {
                if (TextUtils.equals(entity.getId(), focusItem.getId())) {
                    focusItem.setAttention(false);
                    allChannelAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 更新用户关注的频道
     */
    private void updateUserFocusChannel() {
        if (NetUtil.isNetworkConnected(this)) {
            if (!userAdapter.getDataSet().isEmpty()) {
                submitBtn.setClickable(false);
                if (null != loading) {
                    loading.show();
                }
                StringBuilder channelIds = new StringBuilder();
                for (HomeChannelEntity.ItemHomeChannelEntity userFocusChannel : userAdapter.getDataSet()) {
                    channelIds.append(userFocusChannel.getId()).append(",");
                }
                LogUtil.e("频道id = " + channelIds);
                RxHttp.postJson(Constant.CHANNEL_HOME_ADD)
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .add("cw_channel_ids", channelIds.toString())
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.show();
                            }
                            if(data != null){
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            EventBus.getDefault().post(new EventMessage("updateUserFocusChannel", "updateUserFocusChannel"));
                            finish();
                        }, (OnError) error -> {
                            if (null != loading && loading.isShow()) {
                                loading.show();
                            }
                            if (submitBtn != null) {
                                submitBtn.setClickable(true);
                            }
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(this, LoginActivity.class);
                            }
                        });
            } else {
                ToastUtil.showShortToast("保存失败，至少选择一个频道");
            }
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_edit_user_channel})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
            EventBus.getDefault().post(new EventMessage("closeLast", "closeLast"));
        } else if (id == R.id.txt_edit_user_channel) {
            updateUserFocusChannel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}