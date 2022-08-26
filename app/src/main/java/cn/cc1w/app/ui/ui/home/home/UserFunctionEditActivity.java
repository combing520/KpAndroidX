package cn.cc1w.app.ui.ui.home.home;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import org.xutils.http.RequestParams;

import java.util.HashMap;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.AllFunctionAdapter;
import cn.cc1w.app.ui.adapter.FunctionFocusAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.FunctionEntity;
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
 * 用户功能区编辑
 *
 * @author kpinfo
 */
public class UserFunctionEditActivity extends CustomActivity implements OnItemClickListener {
    @BindView(R.id.txt_function_edit_user)
    TextView titleTv;
    @BindView(R.id.list_function_edit_focused_all)
    RecyclerView focusedRecycleView;
    @BindView(R.id.list_function_edit_all)
    RecyclerView allFunctionRecycleView;
    private FunctionFocusAdapter focusAdapter;
    private AllFunctionAdapter allFunctionAdapter;
    private LoadingDialog loading;
    private Unbinder unbinder;
    private static final int MAX_SIZE_FUNCTION = 7;
    private List<FunctionEntity.ItemFunctionEntity> focusFunctionList;
    private List<FunctionEntity.ItemFunctionEntity> allFunctionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_function_edit);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
        initList();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化list
     */
    private void initList() {
        focusAdapter = new FunctionFocusAdapter();
        allFunctionAdapter = new AllFunctionAdapter();
        focusedRecycleView.setLayoutManager(new GridLayoutManager(this, 4));
        allFunctionRecycleView.setLayoutManager(new GridLayoutManager(this, 4));
        focusAdapter.setData(focusFunctionList);
        allFunctionAdapter.setData(allFunctionList);
        focusedRecycleView.setAdapter(focusAdapter);
        allFunctionRecycleView.setAdapter(allFunctionAdapter);
        DefaultItemCallback callback = new DefaultItemCallback(focusAdapter);
        DefaultItemTouchHelper helper = new DefaultItemTouchHelper(callback);
        helper.attachToRecyclerView(focusedRecycleView);
        focusAdapter.setOnItemClickListener(this);
        allFunctionAdapter.setOnItemClickListener((targetView, pos) -> {
            int focusFunctionCnt = focusAdapter.getItemCount();
            if (focusFunctionCnt < MAX_SIZE_FUNCTION) {
                FunctionEntity.ItemFunctionEntity entity = allFunctionAdapter.getItem(pos);
                entity.setAttention(true);
                allFunctionAdapter.notifyDataSetChanged();
                focusAdapter.addItem(entity);
            } else {
                ToastUtil.showShortToast("最多只能关注7个功能");
            }
        });
    }

    /**
     * 初始化导航
     */
    private void initNavigation() {
        titleTv.setText("全部功能");
        focusFunctionList = (List<FunctionEntity.ItemFunctionEntity>) getIntent().getSerializableExtra("focus");
        allFunctionList = (List<FunctionEntity.ItemFunctionEntity>) getIntent().getSerializableExtra("all");
    }

    /**
     * 更新用户关注的 功能区
     */
    private void updateFocusFunctionInfo() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            List<FunctionEntity.ItemFunctionEntity> functionList = focusAdapter.getData();
            if (!functionList.isEmpty()) {
                StringBuilder ids = new StringBuilder();
                HashMap<String, Object> map = new HashMap<>();
                for (FunctionEntity.ItemFunctionEntity item : functionList) {
                    ids.append(item.getId()).append(",");
                }
                map.put("cw_function_ids", ids);
                RequestParams params = AppUtil.configRequestParamsWithToken(Constant.LIST_FUNCTION_FORMULATE, map);
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.LIST_FUNCTION_FORMULATE)
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .add("cw_function_ids", ids)
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            EventBus.getDefault().post(new EventMessage("updateFunction", "updateFunction"));
                            finish();
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
            } else {
                ToastUtil.showShortToast("不能全部删除");
            }
        }
    }

    @OnClick({R.id.img_back_function_edit_user, R.id.txt_edit_function_edit_user})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_function_edit_user) {
            EventBus.getDefault().post(new EventMessage("finish", "finish"));
            finish();
        } else if (id == R.id.txt_edit_function_edit_user) {
            updateFocusFunctionInfo();
        }
    }

    /**
     * 顶部 recycleView 设置点击事件
     *
     * @param targetView 目标View
     * @param pos        对应的位置
     */
    @Override
    public void onItemClick(View targetView, int pos) {
        FunctionEntity.ItemFunctionEntity entity = focusAdapter.getItem(pos);
        if (null != entity) {
            focusAdapter.deleteItem(pos);
            for (FunctionEntity.ItemFunctionEntity functionEntity : allFunctionAdapter.getDataSet()) {
                if (TextUtils.equals(entity.getId(), functionEntity.getId())) {
                    functionEntity.setAttention(false);
                    allFunctionAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}