package cn.cc1w.app.ui.ui.home.home;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.HomeFunctionAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.FunctionEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 用户功能
 *
 * @author kpinfo
 */
public class UserFunctionActivity extends CustomActivity implements HomeFunctionAdapter.HomeFunctionLongClickListener {
    @BindView(R.id.txt_function_user)
    TextView titleTv;
    @BindView(R.id.txt_edit_function_user)
    TextView editTv;
    @BindView(R.id.list_function_focused)
    RecyclerView focusedList;
    @BindView(R.id.list_function_all)
    RecyclerView allFunctionList;
    private HomeFunctionAdapter focusAdapter;
    private HomeFunctionAdapter allFunctionAdapter;
    private Unbinder unbinder;
    private LoadingDialog loading;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_function);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
        initList();
        requestFocusFunctionList();
        requestAllFunctionList();
    }

    /**
     * 初始化 navigation
     */
    private void initNavigation() {
        titleTv.setText("全部功能");
    }

    /**
     * init loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
        if (null != loading) {
            loading.setLoadingText(null);
        }
    }

    /**
     * 初始化List
     */
    private void initList() {
        focusAdapter = new HomeFunctionAdapter(this);
        allFunctionAdapter = new HomeFunctionAdapter(this);
        focusedList.setLayoutManager(new GridLayoutManager(this, 4));
        focusedList.setAdapter(focusAdapter);
        allFunctionList.setLayoutManager(new GridLayoutManager(this, 4));
        allFunctionList.setAdapter(allFunctionAdapter);
        focusAdapter.addFunctionLongClickListener(this);
        allFunctionAdapter.addFunctionLongClickListener(this);
    }

    /**
     * 获取关注的功能区列表
     */
    private void requestFocusFunctionList() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading && isFirstLoad) {
                loading.show();
            }
            isFirstLoad = false;
            RxHttp.postJson(Constant.LIST_FUNCTION_FOCUSED)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(FunctionEntity.ItemFunctionEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            focusAdapter.setData(list);
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        }
    }

    /**
     * 获取所有的功能区
     */
    private void requestAllFunctionList() {
        if (NetUtil.isNetworkConnected(this)) {
            LogUtil.e("获取所有功能区 = " + Constant.LIST_FUNCTION_ALL);
            RxHttp.postJson(Constant.LIST_FUNCTION_ALL)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(FunctionEntity.ItemFunctionEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            allFunctionAdapter.setData(list);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    @OnClick({R.id.img_back_function_user, R.id.txt_edit_function_user})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_function_user) {
            finish();
        } else if (id == R.id.txt_edit_function_user) {
            editFunction();
        }
    }

    private void editFunction() {
        if (null != allFunctionAdapter && allFunctionAdapter.getDataSet().size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("focus", (ArrayList<? extends Parcelable>) focusAdapter.getDataSet());
            bundle.putParcelableArrayList("all", (ArrayList<? extends Parcelable>) allFunctionAdapter.getDataSet());
            IntentUtil.startActivity(this, UserFunctionEditActivity.class, bundle);
        } else {
            ToastUtil.showShortToast("暂无功能区");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                //更新用户的名称
                if (TextUtils.equals("finish", message.getLabel())) {
                    finish();
                } else if (TextUtils.equals("updateFunction", message.getLabel())) {
                    requestFocusFunctionList();
                    requestAllFunctionList();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Override
    public boolean onItemLongClickListener(View view, int position) {
        LogUtil.e("onItemLongClickListener  pos = " + position);
        editFunction();
        return true;
    }
}