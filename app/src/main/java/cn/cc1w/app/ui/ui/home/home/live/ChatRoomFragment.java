package cn.cc1w.app.ui.ui.home.home.live;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.BottomDialogAdapter;
import cn.cc1w.app.ui.adapter.live.UserChatAdapter;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.kpinfo.log.KpLog;

/**
 * 聊天室
 *
 * @author kpinfo
 */
public class ChatRoomFragment extends Fragment implements UserChatAdapter.OnItemLongClickListener, OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_room_chat)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View decorView;
    private Unbinder unbinder;
    private int cwPage = 1;
    private UserChatAdapter adapter;
    private boolean isPullRefreshData = true;
    private LoadingDialog mLoading;
    private static final int CODE = 1;
    private static final int CODE_DATA = 2;
    private Context mContext;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                if (msg.what == CODE) {
                    mLoading.close();
                    ToastUtil.showSuccessToast("举报成功");
                } else if (msg.what == CODE_DATA) {
                    fetchData();
                }
            }
        }
    };

    public static ChatRoomFragment newInstance() {
        ChatRoomFragment fragment = new ChatRoomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EventBus.getDefault().register(this);
            adapter = new UserChatAdapter();
            adapter.setOnItemLongClickListener(this);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_chat_room, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        requestData();
    }

    /**
     * 初始化
     */
    private void init() {
        initList();
        initLoading();
    }

    /**
     * 初始化 list
     */
    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(mContext, 1), 1));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
    }

    private void initLoading() {
        mLoading = AppUtil.getLoading(mContext);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(mContext)) {
            Message msg = mHandler.obtainMessage();
            msg.what = CODE_DATA;
            mHandler.sendMessageDelayed(msg, 500);
        }
    }

    private void fetchData() {
        if (null != adapter && adapter.getList().isEmpty()) {
            EventBus.getDefault().post(new EventMessage("getMessageLists", String.valueOf(cwPage)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            // 获取到 聊天室列表
            if (TextUtils.equals("messageList", message.getLabel())) {
                if (isPullRefreshData) {
                    if (null != message.getLiveChatList() && !message.getLiveChatList().isEmpty()) {
                        adapter.setData(message.getLiveChatList());
                        recyclerView.scrollToPosition(0);
                        cwPage += 1;
                    }
                    if (null != refreshLayout) {
                        refreshLayout.finishRefresh();
                    }
                    isPullRefreshData = false;
                } else {
                    if (null != message.getLiveChatList() && !message.getLiveChatList().isEmpty()) {
                        adapter.addData(message.getLiveChatList());
                        cwPage += 1;
                        if (null != refreshLayout) {
                            refreshLayout.setEnableLoadMore(true);
                        }
                    } else {
                        if (null != refreshLayout) {
                            refreshLayout.setEnableLoadMore(false);
                        }
                    }
                    if (null != refreshLayout) {
                        refreshLayout.finishRefresh();
                    }
                }
            } else if (TextUtils.equals("messageItem", message.getLabel())) {
                adapter.addItem(0, message.getItem());
                recyclerView.scrollToPosition(0);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (NetUtil.isNetworkConnected(mContext)) {
            cwPage = 1;
            EventBus.getDefault().post(new EventMessage("getMessageLists", String.valueOf(cwPage)));
            isPullRefreshData = true;
        } else {
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (NetUtil.isNetworkConnected(mContext)) {
            EventBus.getDefault().post(new EventMessage("getMessageLists", String.valueOf(cwPage)));
            isPullRefreshData = false;
        } else {
            refreshLayout.autoLoadMore();
        }
    }

    @Override
    public void onItemLongClickListener(int pos) {
        showReportDialog();
    }

    @SuppressLint("InflateParams")
    private void showReportDialog() {
        if (getActivity() != null) {
            AlertDialog dialog = new AlertDialog.Builder(mContext).create();
            View view = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
            dialog.setView(view);
            dialog.show();
            if (dialog.getWindow() != null) {
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = displayMetrics.widthPixels;
                dialog.getWindow().setAttributes(params);

                RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                BottomDialogAdapter bottomDialogAdapter = new BottomDialogAdapter();
                List<String> dataSet = new ArrayList<>();
                dataSet.add("举报");
                dataSet.add("取消");
                bottomDialogAdapter.setData(dataSet);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(bottomDialogAdapter);
                recyclerView.setMotionEventSplittingEnabled(false);
                bottomDialogAdapter.setOnItemClickListener((targetView, pos) -> {
                    if (pos == 0) {
                        if (NetUtil.isNetworkConnected(mContext)) {
                            mLoading.show();
                            Message msg = mHandler.obtainMessage();
                            msg.what = CODE;
                            mHandler.sendMessageDelayed(msg, 500);
                        } else {
                            ToastUtil.showShortToast(getResources().getString(R.string.network_error));
                        }
                    }
                    dialog.dismiss();
                });
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(mContext, getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        KpLog.getInstance().onAppViewScreenOut(mContext, getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        unbinder.unbind();
        super.onDestroyView();
    }
}