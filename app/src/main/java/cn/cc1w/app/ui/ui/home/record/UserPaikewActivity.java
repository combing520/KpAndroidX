package cn.cc1w.app.ui.ui.home.record;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PaiKewUserInfoEntity;
import cn.cc1w.app.ui.ui.home.record.fragment.UserPaikewPhotoFragment;
import cn.cc1w.app.ui.ui.home.record.fragment.UserPaikewVideoFragment;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 用户的拍客  （可以是当前的用户；也可以是被查看的其他用户的）  【没有 喜欢 栏目】
 *
 * @author kpinfo
 */
public class UserPaikewActivity extends CustomActivity implements TabLayout.OnTabSelectedListener {
    private Unbinder unbinder;
    @BindView(R.id.img_avatar_photograph)
    RoundAngleImageView userAvatarImg;
    @BindView(R.id.tab_record_photograph)
    TabLayout tabLayout;
    @BindView(R.id.txt_username_photograph)
    TextView userNameTv;
    @BindView(R.id.txt_describe_photograph)
    TextView describeTv;
    @BindView(R.id.txt_cnt_follow_photograph)
    TextView focusCntTv;
    @BindView(R.id.txt_cnt_fans_photograph)
    TextView fansCntTv;
    @BindView(R.id.txt_cnt_prise_photograph)
    TextView priseCntTv;
    @BindView(R.id.txt_focus_photograph)
    ImageView followImg;
    private UserPaikewVideoFragment videoFragment;
    private UserPaikewPhotoFragment photoFragment;
    private LoadingDialog loading;
    private FragmentManager manager;
    private Fragment currentFragment;
    private static final int POS_VIDEO = 0;// 视频
    private static final int POS_PHOTO = 1;// 照片
    public String uid;
    private int follow_select = 2; //  关注选项（1-关注，2-未关注）
    //关注
    private static final int FOLLOW_SELECT = 1;
    //未关注
    private static final int FOLLOW_UNSELECT = 2;
    private int primaryFollowSelect = 0; // 最初的 选中壮体啊
    private static final String[] TITLE = new String[]{"我的视频", "我的照片"};
    private final Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_paikew);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initLoading();
        initFragment();
        initData();
        initTab();
        requestUserInfo();
    }

    private void initData() {
        uid = getIntent().getStringExtra(Constant.TAG_ID);
        Constant.UID_PAIKEW_OTHER = uid;
        LogUtil.e("uid = " + uid);
        if (!TextUtils.isEmpty(Constant.CW_UID_PAIKEW)) {
            if (TextUtils.equals(Constant.CW_UID_PAIKEW, uid)) {
                followImg.setVisibility(View.GONE);
            }
        } else {
            followImg.setVisibility(View.GONE);
        }
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
        if (null != loading) {
            loading.setLoadingText(null);
        }
    }

    /**
     * 请求用户信息
     */
    private void requestUserInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.USER_INFO_PAIKEW).add(Constant.STR_CW_UID_SYSTEM, uid)
                    .asResponse(PaiKewUserInfoEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing() && data != null) {
                            userNameTv.setText(TextUtils.isEmpty(data.getNickname()) ? "" : data.getNickname());
                            focusCntTv.setText(String.valueOf(data.getFollow_number()));
                            fansCntTv.setText(String.valueOf(data.getFans_number()));
                            priseCntTv.setText(String.valueOf(data.getPraise_number()));

                            AppUtil.loadAvatarImg(data.getHead_pic_path(), userAvatarImg);
                            //  页面刷新
                            EventBus.getDefault().post(new EventMessage("userPaiKewId", String.valueOf(uid)));
                            follow_select = data.getFollow_select();
                            primaryFollowSelect = data.getFollow_select();
                            if (follow_select == FOLLOW_SELECT) {  //关注选项（1-关注，2-未关注）
                                AppUtil.loadRes(R.mipmap.ic_focus_cancel_paikew, followImg);
                            } else {
                                AppUtil.loadRes(R.mipmap.ic_focus_add_paikew, followImg);
                            }
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });
        } else {
            handler.postDelayed(() -> EventBus.getDefault().post(new EventMessage("networkError", "networkError")), 0);
        }
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        videoFragment = UserPaikewVideoFragment.newInstance();
        photoFragment = UserPaikewPhotoFragment.newInstance();
        switchFragment(videoFragment);
        tabLayout.addOnTabSelectedListener(this);
    }

    /**
     * 初始化 tabs
     */
    private void initTab() {
        for (String title : TITLE) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
    }

    /**
     * 切换 fragment
     *
     * @param targetFragment 目标 fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (currentFragment != targetFragment) {
            if (!targetFragment.isAdded()) {
                if (null != currentFragment) {
                    manager.beginTransaction().hide(currentFragment).commit();
                }
                manager.beginTransaction().add(R.id.container_photograph, targetFragment).commit();
            } else {
                manager.beginTransaction().hide(currentFragment).show(targetFragment).commit();
            }
            currentFragment = targetFragment;
        }
    }

    @OnClick({R.id.ll_focus_photograph, R.id.ll_fans_photograph, R.id.txt_focus_photograph, R.id.img_back_header_not_title})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int id = view.getId();
        if (id == R.id.ll_focus_photograph) { //关注
            intent.setClass(this, FocusActivity.class);
            intent.putExtra(Constant.TAG_ID, uid);
            startActivity(intent);
        } else if (id == R.id.ll_fans_photograph) {// 粉丝
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                intent.setClass(this, LoginActivity.class);
            } else {
                intent.putExtra(Constant.STR_CW_UID_SYSTEM, uid);
                intent.setClass(this, FansActivity.class);
            }
            startActivity(intent);
        } else if (id == R.id.txt_focus_photograph) { // 关注
            doFollowAuthor();
        } else if (id == R.id.img_back_header_not_title) {
            finish();
        }
    }

    /**
     * 关注当前的作者
     */
    private void doFollowAuthor() {
        if (NetUtil.isNetworkConnected(this)) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, LoginActivity.class, null);
            } else {

                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.FOLLOW_PAIKEW).add("f_uid", uid).add("status", getStatus(follow_select))
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
                                if (follow_select == FOLLOW_SELECT) {
                                    AppUtil.loadRes(R.mipmap.ic_focus_add_paikew, followImg);
                                } else if (follow_select == FOLLOW_UNSELECT) {
                                    AppUtil.loadRes(R.mipmap.ic_focus_cancel_paikew, followImg);
                                }
                                follow_select = getStatus(follow_select);
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
        return (status == FOLLOW_SELECT) ? FOLLOW_UNSELECT : FOLLOW_SELECT;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        if (pos == POS_VIDEO) { // 视频
            switchFragment(videoFragment);
        } else if (pos == POS_PHOTO) { // 照片
            switchFragment(photoFragment);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (TextUtils.equals("updateUserInfo", message.getLabel())) {
            requestUserInfo();
        } else if (TextUtils.equals("updateAvatar", message.getLabel())) {
            AppUtil.loadAvatarImg(message.getContent(), userAvatarImg);
        } else if (TextUtils.equals("updateName", message.getLabel())) {
            userNameTv.setText(TextUtils.isEmpty(message.getContent()) ? "" : message.getContent());
        }
    }

    @Override
    protected void onDestroy() {
        if (primaryFollowSelect != follow_select) {
            EventBus.getDefault().post(new EventMessage("focusStateChange", String.valueOf(follow_select)));
        }
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}