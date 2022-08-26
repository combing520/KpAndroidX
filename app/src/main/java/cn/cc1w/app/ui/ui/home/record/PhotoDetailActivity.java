package cn.cc1w.app.ui.ui.home.record;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rxjava.rxlife.RxLife;

import net.arvin.itemdecorationhelper.ItemDecorationFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadCircleView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.PhotoDetailAdapter;
import cn.cc1w.app.ui.adapter.paikew.PaikewCommentListAdapter;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PaiKewPhotoDetailEntity;
import cn.cc1w.app.ui.entity.PaikewCommentEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.entity.VideoAndPicPriseEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.detail.ShowWebViewGalleryDetailActivity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.ThumbUpView.ThumbUpView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.widget.input.InputDialog;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;
import rxhttp.RxHttpNoBodyParam;

/**
 * 照片详情
 *
 * @author kpinfo
 */
public class PhotoDetailActivity extends CustomActivity implements OnItemClickListener, BlankViewClickListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.img_photo_detail)
    ImageView photoImg;
    @BindView(R.id.list_photo_detail)
    RecyclerView recyclerView;
    @BindView(R.id.img_avatar_photo_detail)
    ImageView avatarImg;
    @BindView(R.id.txt_username_photo_detail)
    TextView userNameTv;
    @BindView(R.id.txt_title_photo_detail)
    TextView photoTitleTv;
    @BindView(R.id.btn_focus_add_photo_detail)
    ImageView followBtn;
    @BindView(R.id.scrollView_photo_detail)
    NestedScrollView scrollView;
    @BindView(R.id.blankView_photo_detail)
    BlankView blankView;
    @BindView(R.id.commentAdapter_photo_detail)
    RecyclerView commentList;
    @BindView(R.id.ll_bottom_photo_detail)
    LinearLayout bottomLayout;
    @BindView(R.id.ll_promotion_detail_photo_paikew)
    LinearLayout promotionLayout;
    @BindView(R.id.txt_promotion_detail_photo_paikew)
    TextView promotionTv;
    @BindView(R.id.img_collection_photo_detail)
    ThumbUpView collectionImg;
    @BindView(R.id.cnt_collection_photo_detail)
    TextView collectionCntTv;
    @BindView(R.id.cnt_message_photo_detail)
    TextView messageCntTv;
    @BindView(R.id.footer_photo_detail)
    LoadCircleView footerView;
    @BindView(R.id.view_spit_detail_photo)
    View spitView;
    @BindView(R.id.grid_photo_detail)
    GridLayout gridLayout;
    private int worksId;
    private LoadingDialog loading;
    private PhotoDetailAdapter adapter;
    private int currentPageIndex = 1;
    private long lastTime;
    private int paikewId;
    private String userPaikewId;
    private int followStatus = 2;// 关注选项（1-关注，2-未关注）
    private static final int FOLLOWED = 1; // 已关注
    private static final int UNFOLLOW = 2; //  未关注
    private PaikewCommentListAdapter commentAdapter;
    private String promotionUr;
    // 点赞选中状态
    private static final int PRAISE_SELECT = 1;
    // 点赞选中状态 未选择
    private static final int PRAISE_SELECT_NONE = 2;
    private int selectState = -1;
    private long enterTime;
    private long lastPriseTime;
    private long lastClickPaikewShareBtnTime;
    private String picPath;
    private PaiKewPhotoDetailEntity.DataBean paikewPhotoDetailBean;
    private boolean isLoadMoreDataProcess = false;
    private final int pageSize = 20;
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    private InputDialog mInputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initPriseBtn();
        initNavigation();
        initLoading();
        initBlankView();
        initList();
        requestData();
    }

    /**
     * 初始化 点赞按钮
     */
    private void initPriseBtn() {
        collectionImg.setUnLikeType(ThumbUpView.LikeType.broken);
        collectionImg.setCracksColor(Color.parseColor("#b3b3b3"));
        collectionImg.setEdgeColor(Color.parseColor("#b3b3b3"));
        collectionImg.setFillColor(Color.parseColor("#c81f1d"));
        collectionImg.setBgColor(Color.parseColor("#b3b3b3"));
        collectionImg.setEnabled(false);
        collectionImg.setClickable(false);
    }

    /**
     * 初始化 空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, "你所查看的图片不存在,点击重试", "点击重试");
        blankView.setOnBlankViewClickListener(this);
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
        adapter = new PhotoDetailAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        RecyclerView.ItemDecoration itemDecoration = new ItemDecorationFactory.DividerBuilder()
                .dividerHeight(AppUtil.dip2px(this,2))
                .dividerColor(ContextCompat.getColor(this,R.color.colorWhite))
                .showLastDivider(false).build(recyclerView);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(adapter);
        commentAdapter = new PaikewCommentListAdapter(this,this);
        commentList.setLayoutManager(new LinearLayoutManager(this));

        commentList.setAdapter(commentAdapter);
        try {
            commentList.setNestedScrollingEnabled(false);
            commentList.setHasFixedSize(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.setOnItemClickListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
        commentList.setMotionEventSplittingEnabled(false);
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                if (!isLoadMoreDataProcess) {
                    loadMoreComment();
                }
            }
        });
    }

    /**
     * 初始化 导航头
     */
    private void initNavigation() {
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        titleTv.setText("照片详情");
        paikewId = getIntent().getIntExtra(Constant.TAG_ID, 0);
        lastTime = System.currentTimeMillis();
        enterTime = lastTime;
        lastPriseTime = lastTime;
        lastClickPaikewShareBtnTime = System.currentTimeMillis();
    }

    /**
     * 显示评论框
     */
    private void showInputDialog() {
        mInputDialog = new InputDialog(this).builder().setOnSendClickListener(this::comment);
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
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttpNoBodyParam params = RxHttp.get(Constant.DETAIL_PHOTO_PAIKEW);
            params.add(Constant.TAG_ID, paikewId);
            if (!TextUtils.isEmpty(Constant.CW_UID_PAIKEW)) {
                params.add(Constant.STR_CW_UID_SYSTEM, Constant.CW_UID_PAIKEW);
            }
            if (null != loading) {
                loading.show();
            }
            params.asResponse(PaiKewPhotoDetailEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            requestCommentList();
                            if (data != null) {
                                paikewPhotoDetailBean = data;
                                initData(paikewPhotoDetailBean);
                                blankView.setClickable(false);
                                scrollView.setVisibility(View.VISIBLE);
                                bottomLayout.setVisibility(View.VISIBLE);
                                blankView.setVisibility(View.GONE);
                            } else {
                                blankView.setClickable(true);
                                scrollView.setVisibility(View.GONE);
                                bottomLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            blankView.setClickable(true);
                            scrollView.setVisibility(View.GONE);
                            bottomLayout.setVisibility(View.GONE);
                            blankView.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            if (!isFinishing()) {
                scrollView.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
                blankView.setClickable(true);
            }
        }
    }

    /**
     * 请求评论列表
     */
    private void requestCommentList() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.LIST_COMMENT_PAIKEW).add("shoot_id", paikewId).add("type", 2).add(Constant.STR_PAGE, currentPageIndex).add("size", pageSize)
                    .asResponseList(PaikewCommentEntity.ItemPaikewCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                commentAdapter.setData(list);
                                currentPageIndex += 1;
                                if (list.size() == pageSize) {
                                    footerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 初始化数据
     *
     * @param dataBean 数据源
     */
    private void initData(PaiKewPhotoDetailEntity.DataBean dataBean) {
        photoTitleTv.setText(TextUtils.isEmpty(dataBean.getTitle()) ? "" : dataBean.getTitle());
        userNameTv.setText(TextUtils.isEmpty(dataBean.getNickname()) ? "" : dataBean.getNickname());
        AppUtil.loadAvatarImg(dataBean.getHead_pic_path(), avatarImg);
        userPaikewId = dataBean.getUid();
        worksId = dataBean.getId();
        // 如果 当前作品的 作者 ID 和当前操作员的 uid 一样的话，就隐藏 关注按钮
        if (TextUtils.equals(String.valueOf(userPaikewId), Constant.CW_UID_PAIKEW)) {
            followBtn.setVisibility(View.GONE);
        }
        if (dataBean.getPhotos_count() > 1) {
            recyclerView.setVisibility(View.VISIBLE);
            photoImg.setVisibility(View.GONE);
            adapter.setData(dataBean.getPhotos_path());
        } else if (dataBean.getPhotos_count() == 1) {
            recyclerView.setVisibility(View.GONE);
            photoImg.setVisibility(View.VISIBLE);
            picPath = dataBean.getPhotos_path().get(0);
            AppUtil.loadBigImg(picPath, photoImg);
        }
        selectState = dataBean.getPraise_select();
//        priseCnt = dataBean.getPraise_number();
        if (selectState == PRAISE_SELECT) {
            collectionImg.like();
        }
        // 点赞数
        collectionCntTv.setText(String.valueOf(dataBean.getPraise_number()));
        // 评论数量
        messageCntTv.setText(String.valueOf(dataBean.getComment_number()));
        setUserFocusStatus(dataBean.getFollow_select());
        if (dataBean.getIs_promotion() == 1) { // 是否推广（1-是，0-否）
            if (!TextUtils.isEmpty(dataBean.getPromotion_name()) && !TextUtils.isEmpty(dataBean.getPromotion_url())) {
                promotionLayout.setVisibility(View.VISIBLE);
                promotionTv.setText(dataBean.getPromotion_name());
                promotionUr = dataBean.getPromotion_url();
            }
        }
        gridLayout.setVisibility(View.VISIBLE);
        spitView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置用户的 关注状态
     */
    private void setUserFocusStatus(int followStatus) {
        this.followStatus = followStatus;
        // 如果当前用户就是自己则不显示关注和取关的按钮
        if (!TextUtils.equals(String.valueOf(paikewId), Constant.CW_UID_PAIKEW)) {
            if (this.followStatus == FOLLOWED) {
                AppUtil.loadRes(R.mipmap.ic_focus_cancel_paikew, followBtn);
            } else {
                AppUtil.loadRes(R.mipmap.ic_focus_add_paikew, followBtn);
            }
        }
    }

    /**
     * 进行拍客的分享
     */
    private void doPaikewShare() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickPaikewShareBtnTime >= Constant.MIN_TIME_INTERVAL) {
            if (null != paikewPhotoDetailBean) {
                if (!TextUtils.isEmpty(paikewPhotoDetailBean.getShare_url())) {
                    ShareEntity shareEntity = new ShareEntity();
                    shareEntity.setType("paikew");
                    shareEntity.setUrl(paikewPhotoDetailBean.getShare_url());
                    shareEntity.setTitle(TextUtils.isEmpty(paikewPhotoDetailBean.getTitle()) ? Constant.TILE_SHARE : paikewPhotoDetailBean.getTitle());
                    shareEntity.setRedirect_url("");
                    shareEntity.setSummary(TextUtils.isEmpty(paikewPhotoDetailBean.getNickname()) ? Constant.SUMMARY_SHARE : paikewPhotoDetailBean.getNickname());
                    shareEntity.setNewsId(String.valueOf(paikewPhotoDetailBean.getId()));
                    shareEntity.setPaikewShare(true);
                    shareEntity.setPaikewerUserName(TextUtils.isEmpty(paikewPhotoDetailBean.getNickname()) ? "" : paikewPhotoDetailBean.getNickname());
                    shareEntity.setPicUrl(paikewPhotoDetailBean.getCover());
                    shareEntity.setPaikewType("pic");
                    Gson gson = new Gson();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TAG_SHARE_CONTENT, gson.toJson(shareEntity));
                    IntentUtil.startActivity(this, ShareActivity.class, bundle);
                } else {
                    ToastUtil.showShortToast("暂时无法分享");
                }
            } else {
                ToastUtil.showShortToast("无法获取分享信息");
            }
        }
        lastClickPaikewShareBtnTime = currentTime;
    }

    /**
     * 点赞
     */
    private void doPrise() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPriseTime >= Constant.MIN_TIME_INTERVAL) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                Bundle bundle = new Bundle();
                bundle.putString("from", Constant.PHONE_BIND);
                IntentUtil.startActivity(this, LoginActivity.class, bundle);
                return;
            }
            // 先判断是否绑定了手机号
            UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
            if (null != userInfo) {
                if (TextUtils.isEmpty(userInfo.getMobile())) { // 没有绑定过手机号码
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.PHONE_BIND);
                    IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
                    return;
                }
            }
            if (NetUtil.isNetworkConnected(this)) {
                RxHttp.postJson(Constant.PRISE_PAIKEW).add("shoot_id", paikewId).add("status", getVideoPriseStatus(selectState))
                        .asResponse(VideoAndPicPriseEntity.DataBean.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (!isFinishing() && data != null) {
                                if (selectState == PRAISE_SELECT) { // 当前是被赞状态； 需要进行取消点赞
                                    selectState = PRAISE_SELECT_NONE;
                                    collectionImg.unLike();
                                    collectionCntTv.setText(String.valueOf(data.getPraise_number()));

                                } else { // 当前处于未被点赞状态； 需要去进行点赞
                                    selectState = PRAISE_SELECT;
                                    collectionCntTv.setText(String.valueOf(data.getPraise_number()));
                                    collectionImg.like();
                                }
                            }
                        }, (OnError) error -> {
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
        lastPriseTime = currentTime;
    }

    /**
     * 进行评论
     */
    private void doComment() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            Bundle bundle = new Bundle();
            bundle.putString("from", Constant.PHONE_BIND);
            IntentUtil.startActivity(this, LoginActivity.class, bundle);
        } else {
            if (NetUtil.isNetworkConnected(this)) {
                UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                if (null != userInfo) {
                    if (TextUtils.isEmpty(userInfo.getMobile())) { // 没有绑定过手机号码
                        Bundle bundle = new Bundle();
                        bundle.putString("from", Constant.PHONE_BIND);
                        IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
                        return;
                    }
                }
                showInputDialog();
            } else {
                ToastUtil.showShortToast(getString(R.string.network_error));
            }
        }
    }

    /**
     * 获取到 视频点赞的状态
     */
    private int getVideoPriseStatus(int status) {
        if (status == PRAISE_SELECT) {
            status = PRAISE_SELECT_NONE;
        } else {
            status = PRAISE_SELECT;
        }
        return status;
    }

    /**
     * 评论
     */
    private void comment(String content) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.COMMENT_PAIKEW).add("shoot_id", worksId).add("content", content)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing()) {
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
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

    /**
     * 关注 当前的作者
     */
    private void followAuthor() {
        if (NetUtil.isNetworkConnected(this)) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, LoginActivity.class, null);
            } else {
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.FOLLOW_PAIKEW).add("f_uid", userPaikewId).add("status", getStatus(followStatus))
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (!isFinishing()) {
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                                if (followStatus == FOLLOWED) {
                                    AppUtil.loadRes(R.mipmap.ic_focus_add_paikew, followBtn);
                                } else if (followStatus == UNFOLLOW) {
                                    AppUtil.loadRes(R.mipmap.ic_focus_cancel_paikew, followBtn);
                                }
                                followStatus = getStatus(followStatus);
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
            }
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    private int getStatus(int status) {
        return (status == FOLLOWED) ? UNFOLLOW : FOLLOWED;
    }

    /**
     * 查看拍客信息
     */
    private void showPaiKewInfo() {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_ID, String.valueOf(userPaikewId));
        IntentUtil.startActivity(this, UserPaikewActivity.class, bundle);
    }

    /**
     * 进行评论
     */
    private void doReply(String authorId, String content) {
        if (NetUtil.isNetworkConnected(this)) {
            LogUtil.e("authorId = " + authorId + " shoot_id = " + worksId);
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.REPLY_COMMENT_PAIKEW).add("shoot_id", worksId).add("comment_id", authorId).add("content", content)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
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
    public void onItemClick(View targetView, int pos) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setClass(context, ShowWebViewGalleryDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("picList", adapter.getDataSet());
                intent.putExtra("selectPos", pos);
                Pair pair = new Pair<>(recyclerView, "img");
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair);
                ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            }
        }
        lastTime = currentTime;
    }

    /**
     * 添加拍客停留时间
     */
    private void addPaikewRemainAction() {
        if (NetUtil.isNetworkConnected(this)) {
            long currentTime = System.currentTimeMillis();
            RxHttp.postJson(Constant.REMAIN_PAIKEW).add("shoot_id", paikewId).add("cw_site_time", (currentTime - enterTime))
                    .asMsgResponse(MsgResonse.class)
                    .subscribe();
        }
    }

    /**
     * 加载更多评论
     */
    private void loadMoreComment() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != footerView && footerView.getVisibility() == View.GONE) {
                footerView.setVisibility(View.VISIBLE);
            }
            isLoadMoreDataProcess = true;
            RxHttpNoBodyParam param = RxHttp.get(Constant.LIST_COMMENT_PAIKEW);
            param.add("shoot_id", paikewId);
            param.add("type", 2);
            param.add(Constant.STR_PAGE, currentPageIndex);
            param.add("size", pageSize);
            param.asResponseList(PaikewCommentEntity.ItemPaikewCommentEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                commentAdapter.addData(list);
                                currentPageIndex += 1;
                                if (list.size() == pageSize) {
                                    footerView.setVisibility(View.VISIBLE);
                                } else {
                                    if (null != footerView && footerView.getVisibility() == View.VISIBLE) {
                                        footerView.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                if (null != footerView && footerView.getVisibility() == View.VISIBLE) {
                                    footerView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }, (OnError) error -> {
                        if (!isFinishing() && null != footerView && footerView.getVisibility() == View.VISIBLE) {
                            footerView.setVisibility(View.GONE);
                        }
                        isLoadMoreDataProcess = false;
                    });
        } else {
            if (null != footerView && footerView.getVisibility() == View.VISIBLE) {
                footerView.setVisibility(View.GONE);
            }
            isLoadMoreDataProcess = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("focusStateChange", message.getLabel())) {
                setUserFocusStatus(Integer.parseInt(message.getContent()));
            } else if (TextUtils.equals("openPaiKewReplyDialog", message.getLabel())) {
                String authorId = message.getContent();
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", Constant.PHONE_BIND);
                    IntentUtil.startActivity(this, LoginActivity.class, bundle);
                } else {
                    if (NetUtil.isNetworkConnected(this)) {
                        UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
                        if (null != userInfo) {
                            if (TextUtils.isEmpty(userInfo.getMobile())) { // 没有绑定过手机号码
                                Bundle bundle = new Bundle();
                                bundle.putString("from", Constant.PHONE_BIND);
                                IntentUtil.startActivity(this, UpdateMobileActivity.class, bundle);
                                return;
                            }
                        }
                        if (!TextUtils.isEmpty(authorId)) {
                            showInputDialogWithCommentId(authorId);
                        }
                    } else {
                        ToastUtil.showShortToast(getString(R.string.network_error));
                    }
                }
            }
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_focus_add_photo_detail, R.id.ll_like_photo_detail, R.id.ll_message_photo_detail, R.id.ll_share_photo_detail,
            R.id.img_avatar_photo_detail, R.id.commend_photo_detail, R.id.img_comment, R.id.ll_promotion_detail_photo_paikew, R.id.img_photo_detail, R.id.edit_commend_photo_detail
    })
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_focus_add_photo_detail) {
            followAuthor();
        } else if (id == R.id.ll_like_photo_detail) {
            doPrise();
        } else if (id == R.id.ll_message_photo_detail || id == R.id.commend_photo_detail || id == R.id.img_comment || id == R.id.edit_commend_photo_detail) {
            doComment();
        } else if (id == R.id.ll_share_photo_detail) {
            doPaikewShare();
        } else if (id == R.id.img_avatar_photo_detail) {
            showPaiKewInfo();
        } else if (id == R.id.ll_promotion_detail_photo_paikew) {
            Bundle bundle = new Bundle();
            bundle.putString("url", promotionUr);
            IntentUtil.startActivity(this, PromotionDetailActivity.class, bundle);
        } else if (id == R.id.img_photo_detail) {
            if (!TextUtils.isEmpty(picPath)) {
                ArrayList<String> dataSet = new ArrayList<>();
                dataSet.add(picPath);
                Intent intent = new Intent();
                intent.setClass(this, ShowWebViewGalleryDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("picList", dataSet);
                intent.putExtra("selectPos", 0);
                 Pair pair = new Pair<>(recyclerView, "img");
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair);
                ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            }
        }
    }

    @Override
    public void onBlankViewClickListener(View view) {
        blankView.setClickable(false);
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        addPaikewRemainAction();
        unbinder.unbind();
    }
}
