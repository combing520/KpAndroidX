package cn.cc1w.app.ui.ui.home.record;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.core.BottomPopupView;
import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.paikew.PaikewCommentListAdapter;
import cn.cc1w.app.ui.constants.Constant;
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
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * @author kpinfo
 */
public class PaiKeCommentPop extends BottomPopupView implements OnRefreshListener, OnLoadMoreListener, View.OnClickListener {
    private SmartRefreshLayout refreshLayout;
    private TextView commentCntTv;
    private TextView describeTv;
    private PaikewCommentListAdapter adapter;
    private int currentPageIndex = 1;
    private String paikewVideoId;
    private Activity context;
    private InputDialog mInputDialog;
    private long lastClickTime;
    private LifecycleOwner owner;

    public PaiKeCommentPop(@NonNull Context context) {
        super(context);
        this.context = (Activity) context;
        lastClickTime = System.currentTimeMillis();
    }

    public void setPaiKeId(String paikewVideoId) {
        this.paikewVideoId = paikewVideoId;
    }

    public void setLifeCycleOwner(LifecycleOwner owner) {
        this.owner = owner;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_comment_video_paike;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        LogUtil.e("拍客评论 onCreate " + System.currentTimeMillis());
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.list_detail_news);
        refreshLayout = findViewById(R.id.refreshLayout);
        commentCntTv = findViewById(R.id.txt_cnt_detail_news);
        describeTv = findViewById(R.id.txt_detail_news);
        recyclerView.setMotionEventSplittingEnabled(false);
        adapter = new PaikewCommentListAdapter(context, owner);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.img_close_detail_news).setOnClickListener(v -> dismiss());
        findViewById(R.id.edit_pop_list_comment_paikew).setOnClickListener(this);
        findViewById(R.id.news_detail_comment_img).setOnClickListener(this);
        findViewById(R.id.txt_detail_news).setOnClickListener(this);
    }

    /**
     * 显示评论框
     */
    private void showInputDialog() {
        mInputDialog = new InputDialog(context).builder().setOnSendClickListener(this::doComment);
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
        mInputDialog = new InputDialog(context).builder().setOnSendClickListener(content -> doReply(commentId, content));
        mInputDialog.setHint("写评论...");
        mInputDialog.setNeedCheckLogin(false);
        mInputDialog.show();
    }

    @Override
    protected void onShow() {
        super.onShow();
        if (refreshLayout != null) {
            refreshLayout.autoRefresh();
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

    private void refreshData() {
        if (NetUtil.isNetworkConnected(context)) {
            currentPageIndex = 1;
            RxHttp.get(Constant.LIST_COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("type", 1).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(PaikewCommentEntity.ItemPaikewCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.setData(list);
                            currentPageIndex += 1;
                            if (null != commentCntTv) {
                                commentCntTv.setVisibility(VISIBLE);
                                commentCntTv.setText(String.valueOf(list.size()).concat(" 条评论"));
                            }
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(true);
                                refreshLayout.finishRefresh();
                                refreshLayout.setVisibility(VISIBLE);
                            }
                            if (describeTv != null) {
                                describeTv.setVisibility(GONE);
                            }
                        } else {
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(false);
                                refreshLayout.finishRefresh();
                                refreshLayout.setVisibility(GONE);
                            }
                            if (describeTv != null) {
                                describeTv.setVisibility(VISIBLE);
                            }
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                            refreshLayout.setVisibility(GONE);
                        }
                        if (describeTv != null) {
                            describeTv.setVisibility(VISIBLE);
                        }
                        LogUtil.e("评论获取出错 error " + error.getThrowable());
                    });
        } else {
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
                refreshLayout.setVisibility(GONE);
            }
            if (describeTv != null) {
                describeTv.setVisibility(VISIBLE);
            }
            if (describeTv != null) {
                commentCntTv.setVisibility(GONE);
            }
        }
    }

    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.get(Constant.LIST_COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("type", 1).add(Constant.STR_PAGE, currentPageIndex)
                    .asResponseList(PaikewCommentEntity.ItemPaikewCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != list && !list.isEmpty()) {
                            adapter.addData(list);
                            currentPageIndex += 1;
                            if (null != commentCntTv && adapter != null) {
                                commentCntTv.setText(String.valueOf(adapter.getDataSet().size()).concat(" 条评论"));
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

    /**
     * 进行评论
     *
     * @param content 评论的内容
     */
    private void doComment(String content) {
        if (NetUtil.isNetworkConnected(context)) {
            RxHttp.postJson(Constant.COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("content", content)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (data != null) {
                            ToastUtil.showShortToast(data.getMessage());
                        }
                        SharedPreferenceUtil.saveUserCommentContent(content);
                    }, (OnError) error -> {
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(context, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(context.getString(R.string.network_error));
        }
    }

    /**
     * 进行评论
     *
     * @param authorId 等被评论者的 id
     * @param content  评论的内容
     */
    private void doReply(String authorId, String content) {
        if (NetUtil.isNetworkConnected(context)) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.PHONE_BIND);
                IntentUtil.startActivity(context, LoginActivity.class, bundle);
            } else {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    if (TextUtils.isEmpty(userInfo.getMobile())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("from", Constant.PHONE_BIND);
                        IntentUtil.startActivity(context, UpdateMobileActivity.class, bundle);
                        return;
                    }
                    RxHttp.postJson(Constant.REPLY_COMMENT_PAIKEW).add("shoot_id", paikewVideoId).add("comment_id", authorId).add("content", content)
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                            }, (OnError) error -> {
                                ToastUtil.showShortToast(error.getErrorMsg());
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(context, LoginActivity.class);
                                }
                            });
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.TAG_COMMENT_PAIKEW);
                    IntentUtil.startActivity(context, LoginActivity.class, bundle);
                }
            }
        } else {
            ToastUtil.showShortToast(context.getString(R.string.network_error));
        }
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            int id = v.getId();
            if (id == R.id.edit_pop_list_comment_paikew || id == R.id.news_detail_comment_img) {
                if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                    if (null != userInfo) {
                        if (TextUtils.isEmpty(userInfo.getMobile())) {
                            Bundle bundle = new Bundle();
                            bundle.putString("from", Constant.PHONE_BIND);
                            IntentUtil.startActivity(context, UpdateMobileActivity.class, bundle);
                            return;
                        }
                    }
                    showInputDialog();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.TAG_COMMENT_PAIKEW);
                    IntentUtil.startActivity(context, LoginActivity.class, bundle);
                }
            } else if (id == R.id.txt_detail_news) {
                if (describeTv != null) {
                    describeTv.setVisibility(GONE);
                }
                if (refreshLayout != null) {
                    refreshLayout.setVisibility(VISIBLE);
                    refreshLayout.autoRefresh();
                }
            }
        }
        lastClickTime = currentTime;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (!TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("openPaiKewReplyDialog", message.getLabel())) {
                String authorId = message.getContent();
                showInputDialogWithCommentId(authorId);
            }
        }
    }

    @Override
    protected void onDismiss() {
        String commentContent = SharedPreferenceUtil.getUserSaveCommentContent();
        if (!TextUtils.isEmpty(commentContent)) {
            SharedPreferenceUtil.clearUserCommentContent();
        }
        if (mInputDialog != null && mInputDialog.isShow()) {
            mInputDialog.dismiss();
            mInputDialog = null;
        }
        this.context = null;
        LogUtil.e("POP onDismiss " + System.currentTimeMillis());
    }

    @Override
    public void onDestroy() {
        LogUtil.e("POP onDestroy ");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}