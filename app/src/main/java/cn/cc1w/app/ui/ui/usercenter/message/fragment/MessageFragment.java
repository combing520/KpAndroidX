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

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.MessageAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemMessageEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 我的通知
 *
 * @author kpinfo
 */
public class MessageFragment extends Fragment {
    @BindView(R.id.list_message)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_message)
    BlankView blankView;
    private MessageAdapter adapter;
    private Unbinder unbinder;
    private View decorView;
    private Context context;
    private LoadingDialog loading;

    public MessageFragment() {
    }

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MessageAdapter();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_message, container, false);
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
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.notice_empty, getString(R.string.notice_none));
    }

    /**
     * 初始化
     */
    private void init() {
        initList();
        initBlankView();
        initLoading();
        requestData();
    }

    private void initLoading() {
        loading = AppUtil.getLoading(context);
    }

    /**
     * 获取数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(context)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.USER_NOTICE_LIST)
                    .asResponseList(ItemMessageEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (recyclerView != null && blankView != null) {
                            if (null != list && !list.isEmpty()) {
                                recyclerView.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                                adapter.setData(list);
                            } else {
                                blankView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if(blankView != null && recyclerView != null){
                            blankView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(context, LoginActivity.class);
                        }
                    });
        }
    }

    /**
     * 初始化list
     */
    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context, 1), 1));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
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