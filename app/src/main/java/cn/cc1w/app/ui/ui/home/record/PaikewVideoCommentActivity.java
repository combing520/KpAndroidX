package cn.cc1w.app.ui.ui.home.record;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.paikew.PaikewCommentListAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PaikewCommentEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.input.InputDialog;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 拍客视频评论
 *
 * @author kpinfo
 */
public class PaikewVideoCommentActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.list_detail_news)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.txt_cnt_detail_news)
    TextView commentCntTv;
    @BindView(R.id.txt_detail_news)
    TextView describeTv;
    private PaikewCommentListAdapter adapter;
    private Unbinder unbinder;
    private LoadingDialog loading;
    private int currentPageIndex = 1;
    private int paikewVideoId;
    private final DisplayMetrics metrics = new DisplayMetrics();
    private InputDialog mInputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paikew_video_comment);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initList();
        initLoading();
        getCommentList();
    }

    /**
     * 初始化List
     */
    private void initList() {
        recyclerView.setMotionEventSplittingEnabled(false);
        adapter = new PaikewCommentListAdapter(this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        paikewVideoId = getIntent().getIntExtra(Constant.TAG_ID, 0);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    /**
     * 初始化Loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 获取评论列表
     */
    private void getCommentList() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.LIST_COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("type", 1).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(PaikewCommentEntity.ItemPaikewCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                describeTv.setVisibility(View.GONE);
                                adapter.setData(list);
                                currentPageIndex += 1;
                                commentCntTv.setText(String.valueOf(list.size()).concat(" 条评论"));
                            } else {
                                refreshLayout.setVisibility(View.GONE);
                                describeTv.setVisibility(View.VISIBLE);
                                commentCntTv.setText("0条评论");
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            refreshLayout.setVisibility(View.GONE);
                            describeTv.setVisibility(View.VISIBLE);
                            commentCntTv.setText(" 0条评论");
                        }
                    });
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 显示评论框
     */
    private void showInputDialog() {
        mInputDialog = new InputDialog(this).builder().setOnSendClickListener(this::doComment);
        mInputDialog.setHint("写评论...");
        mInputDialog.setNeedCheckLogin(true);
        mInputDialog.show();
    }

    /**
     * 显示评论回复的dialog
     *
     * @param commentId 待回复的 id
     */
    private void showInputDialogWithCommentId(String commentId) {
        mInputDialog = new InputDialog(this).builder().setOnSendClickListener(content -> doReply(commentId, content));
        mInputDialog.setHint("写评论...");
        mInputDialog.setNeedCheckLogin(true);
        mInputDialog.show();
    }

    /**
     * 显示评论的 dialog
     */
    private void showCommentDialog() {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_comment, null);
        if (null != view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            dialog.show();
            Window window = dialog.getWindow();
            assert window != null;
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            int statusBarHeight = AppUtil.getStatusBarHeight(this);
            if (statusBarHeight >= 0) {
                lp.height = metrics.heightPixels - statusBarHeight;
            } else {
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            window.setAttributes(lp);
            ImageView closeImg = view.findViewById(R.id.btn_close_dialog_commend);
            final TextView sendBtn = view.findViewById(R.id.btn_send_dialog_commend);
            final EditText contentEdit = view.findViewById(R.id.edit_dialog_commend);
            RelativeLayout containerLayout = view.findViewById(R.id.container_comment_dialog);
            containerLayout.setOnClickListener(v -> {
                dialog.dismiss();
                String commentContent = contentEdit.getText().toString();
                if (!TextUtils.isEmpty(commentContent)) {
                    SharedPreferenceUtil.saveUserCommentContent(commentContent);
                }
            });
            closeImg.setOnClickListener(v -> {
                        dialog.dismiss();
                        String commentContent = contentEdit.getText().toString();
                        if (!TextUtils.isEmpty(commentContent)) {
                            SharedPreferenceUtil.saveUserCommentContent(commentContent);
                        }
                    }
            );

            String commentContent = SharedPreferenceUtil.getUserSaveCommentContent();
            if (!TextUtils.isEmpty(commentContent) && null != contentEdit) {
                contentEdit.setText(commentContent);
            }
            sendBtn.setOnClickListener(v -> {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    IntentUtil.startActivity(PaikewVideoCommentActivity.this, LogUtil.class, null);
                } else {
                    if (null != contentEdit) {
                        String content = contentEdit.getText().toString();
                        if (!TextUtils.isEmpty(content)) {
                            dialog.dismiss();
                            doComment(content);
                            SharedPreferenceUtil.saveUserCommentContent(content);
                        } else {
                            ToastUtil.showShortToast("评论内容不能为空");
                        }
                    }
                }
            });
            dialog.setContentView(view);
        }
    }

    /**
     * 进行评论
     *
     * @param content 评论的内容
     */
    private void doComment(String content) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("content", content)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                        SharedPreferenceUtil.saveUserCommentContent(content);
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
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (NetUtil.isNetworkConnected(this)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("type", 1).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(PaikewCommentEntity.ItemPaikewCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                            if (null != commentCntTv) {
                                commentCntTv.setText(String.valueOf(list.size()).concat(" 条评论"));
                            }
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
            }
        }
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.LIST_COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("type", 1).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(PaikewCommentEntity.ItemPaikewCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.addData(list);
                            currentPageIndex += 1;
                            if (null != commentCntTv) {
                                commentCntTv.setText(String.valueOf(list.size()).concat(" 条评论"));
                            }
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishLoadMore();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishLoadMore();
                        }
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishLoadMore();
            }
        }
    }

    @OnClick({R.id.img_close_detail_news, R.id.edit_pop_list_comment_paikew, R.id.img_detail_news, R.id.news_detail_comment_img})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_close_detail_news) {
            finish();
        } else if (id == R.id.edit_pop_list_comment_paikew || id == R.id.news_detail_comment_img) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    if (TextUtils.isEmpty(userInfo.getMobile())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("from", Constant.PHONE_BIND);
                        IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
                        return;
                    }
                }
                showInputDialog();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.TAG_COMMENT_PAIKEW);
                IntentUtil.startActivity(this, LoginActivity.class, bundle);
            }
        } else if (id == R.id.img_detail_news) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    if (TextUtils.isEmpty(userInfo.getMobile())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("from", Constant.PHONE_BIND);
                        IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
                        return;
                    }
                }
                showCommentDialog();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.TAG_COMMENT_PAIKEW);
                IntentUtil.startActivity(this, LoginActivity.class, bundle);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (!TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("openPaiKewReplyDialog", message.getLabel())) {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    if (TextUtils.isEmpty(userInfo.getMobile())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("from", Constant.PHONE_BIND);
                        IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
                        return;
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.TAG_COMMENT_PAIKEW);
                    IntentUtil.startActivity(this, LoginActivity.class, bundle);
                }
                String authorId = message.getContent();
                showReplayDialog(authorId);
            }
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData();
    }

    /**
     * 显示评论的 dialog
     *
     * @param authorId 当前被点击的条目的评论者的id
     */
    private void showReplayDialog(String authorId) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_comment, null);
        if (null != view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialogView = builder.create();
            dialogView.show();
            Window window = dialogView.getWindow();
            assert window != null;
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            int statusBarHeight = AppUtil.getStatusBarHeight(this);
            if (statusBarHeight >= 0) {
                lp.height = metrics.heightPixels - statusBarHeight;
            } else {
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            window.setAttributes(lp);
            ImageView closeImg = view.findViewById(R.id.btn_close_dialog_commend);
            final TextView sendBtn = view.findViewById(R.id.btn_send_dialog_commend);
            final EditText contentEdit = view.findViewById(R.id.edit_dialog_commend);
            RelativeLayout containerLayout = view.findViewById(R.id.container_comment_dialog);
            containerLayout.setOnClickListener(v -> dialogView.dismiss());
            closeImg.setOnClickListener(v -> dialogView.dismiss());
            sendBtn.setOnClickListener(v -> {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    IntentUtil.startActivity(this, LoginActivity.class, null);
                } else {
                    String content = contentEdit.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        dialogView.dismiss();
//                        doReply(authorId, content);
                        if (!TextUtils.isEmpty(authorId)) {
                            showInputDialogWithCommentId(authorId);
                            SharedPreferenceUtil.saveUserCommentContent(content);
                        }

                    } else {
                        ToastUtil.showShortToast("评论内容不能为空");
                    }
                }
            });
            dialogView.setContentView(view);
        }
    }

    /**
     * 进行评论
     *
     * @param authorId 等被评论者的 id
     * @param content  评论的内容
     */
    private void doReply(String authorId, String content) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.REPLY_COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("comment_id", authorId).add("content", content)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
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
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0, 0);
        String commentContent = SharedPreferenceUtil.getUserSaveCommentContent();
        if (!TextUtils.isEmpty(commentContent)) {
            SharedPreferenceUtil.clearUserCommentContent();
        }
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}