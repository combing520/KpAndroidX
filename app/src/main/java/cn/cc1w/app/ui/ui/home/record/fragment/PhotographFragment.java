package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PaiKewUserInfoEntity;
import cn.cc1w.app.ui.ui.home.record.FansActivity;
import cn.cc1w.app.ui.ui.home.record.FocusActivity;
import cn.cc1w.app.ui.ui.home.record.PaiKewSignModifyActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.income.fragment.EarnPhotoFragment;
import cn.cc1w.app.ui.ui.usercenter.wallet.income.fragment.EarningVideoFragment;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * 我拍
 *
 * @author kpinfo
 */
public class PhotographFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private Unbinder unbinder;
    private View decorView;
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
    @BindView(R.id.img_describe_photograph)
    ImageView updateUserSignImg;
    @BindView(R.id.container_photograph)
    FrameLayout frameLayout;
    @BindView(R.id.relate_top_photograph)
    RelativeLayout topLayout;
    @BindView(R.id.relate_bottom_photograph)
    RelativeLayout bottomLayout;
    @BindView(R.id.blankView_photograph)
    BlankView bottomBlankView;
    @BindView(R.id.txt_login_photograph)
    TextView loginBtn;
    private EarningVideoFragment videoFragment;
    private EarnPhotoFragment photoFragment;
    private FavoriteFragment favoriteFragment;
    private LoadingDialog loading;
    private FragmentManager manager;
    private Fragment currentFragment;
    private static final int POS_VIDEO = 0;// 视频
    private static final int POS_PHOTO = 1;// 照片
    private static final int POS_FAVORITE = 2;// 喜欢
    private boolean isFirstLoad = true;
    private boolean isViewCreate = false;
    private static final String[] TITLE = new String[]{"我的视频", "我的照片", "喜欢"};
    private final Handler handler = new Handler(Looper.myLooper());
    private Context context;

    public static PhotographFragment newInstance() {
        return new PhotographFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initLoading();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_photograph, container, false);
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
     * 当前页面是否可见
     *
     * @param isVisibleToUser 当前页面的可见情况
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            requestUserInfo();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        if (!isViewCreate) {
            isViewCreate = true;
            initFragment();
            initBlankView();
            initTab();
        }
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        bottomBlankView.setBlankView(R.mipmap.img_unlogin, "您还没有登录,请登录后查看更多!", "登录");
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(context);
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        manager = getChildFragmentManager();
        videoFragment = EarningVideoFragment.newInstance();
        photoFragment = EarnPhotoFragment.newInstance();
        favoriteFragment = FavoriteFragment.newInstance();
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
     * 请求用户信息
     */
    private void requestUserInfo() {
        LogUtil.e("我拍 requestUserInfo ");
        if (NetUtil.isNetworkConnected(context)) {
            if (!isFirstLoad) {
                return;
            }
            isFirstLoad = false;
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.USERINFO_PAIKEW)
                    .asResponse(PaiKewUserInfoEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (null != data && userNameTv != null && focusCntTv != null && fansCntTv != null
                                && priseCntTv != null && userAvatarImg != null && topLayout != null
                                && frameLayout != null && bottomLayout != null) {
                            userNameTv.setText(TextUtils.isEmpty(data.getNickname()) ? "" : data.getNickname());
                            focusCntTv.setText(String.valueOf(data.getFollow_number()));
                            fansCntTv.setText(String.valueOf(data.getFans_number()));
                            priseCntTv.setText(String.valueOf(data.getPraise_number()));
                            AppUtil.loadAvatarImg(data.getHead_pic_path(), userAvatarImg);
                            Constant.CW_UID_PAIKEW = String.valueOf(data.getUid());
                            topLayout.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.VISIBLE);
                            bottomLayout.setVisibility(View.GONE);
                            EventBus.getDefault().post(new EventMessage(Constant.STR_CW_UID_PAIKEW, Constant.CW_UID_PAIKEW));
                            if (!TextUtils.isEmpty(data.getTag())) {
                                updateUserSign(data.getTag());
                            } else {
                                updateUserSign("这个人很懒,没有留下任何签名～");
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (bottomBlankView != null && loginBtn != null && topLayout != null && frameLayout != null && bottomLayout != null) {
                            if (error.getErrorCode() == Constant.CODE_FOR_PHONE_BIND) {// 未绑定手机号 ，去绑定手机号
                                bottomBlankView.setBlankView(R.mipmap.img_unlogin, "请完善手机号");
                                loginBtn.setText("完善手机号");
                                topLayout.setVisibility(View.GONE);
                                frameLayout.setVisibility(View.GONE);
                                bottomLayout.setVisibility(View.VISIBLE);
                            } else if (error.getErrorCode() == Constant.CODE_FOR_LOGIN) { // 未登录，去登录
                                topLayout.setVisibility(View.GONE);
                                frameLayout.setVisibility(View.GONE);
                                bottomLayout.setVisibility(View.VISIBLE);
                                bottomBlankView.setBlankView(R.mipmap.img_unlogin, "您还没有登录,请登录后查看更多!", "登录");
                                loginBtn.setText("登录");
                            } else {
                                topLayout.setVisibility(View.GONE);
                                frameLayout.setVisibility(View.GONE);
                                bottomLayout.setVisibility(View.VISIBLE);
                                bottomBlankView.setBlankView(R.mipmap.img_unlogin, "您还没有登录,请登录后查看更多!", "登录");
                                loginBtn.setText("登录");
                            }
                        }
                    });
        } else {
            handler.postDelayed(() -> EventBus.getDefault().post(new EventMessage("networkError", "networkError")), 0);
        }
    }

    /**
     * 更新签名
     *
     * @param signStr 签名信息
     */
    private void updateUserSign(String signStr) {
        describeTv.setText(signStr);
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

    @OnClick({R.id.ll_focus_photograph, R.id.ll_fans_photograph, R.id.txt_focus_photograph,
            R.id.txt_describe_photograph, R.id.txt_login_photograph, R.id.img_describe_photograph})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int id = view.getId();
        if (id == R.id.ll_focus_photograph) {
            intent.setClass(context, FocusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constant.TAG_ID, Constant.CW_UID_PAIKEW);
            startActivity(intent);
        } else if (id == R.id.ll_fans_photograph) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, LoginActivity.class);
            } else {
                intent.putExtra(Constant.STR_CW_UID_SYSTEM, Constant.CW_UID_PAIKEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, FansActivity.class);
            }
            startActivity(intent);
        } else if (id == R.id.txt_focus_photograph) {
            ToastUtil.showShortToast("关注");
        } else if (id == R.id.txt_describe_photograph || id == R.id.img_describe_photograph) {
            updateUserPaikewSign();
        } else if (id == R.id.txt_login_photograph) {
            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(context, UpdateMobileActivity.class, null);
            } else {
                IntentUtil.startActivity(context, LoginActivity.class, null);
            }
        }
    }

    /**
     * 更新签名
     */
    private void updateUserPaikewSign() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(context, LoginActivity.class, null);
        } else {
            IntentUtil.startActivity(context, PaiKewSignModifyActivity.class, null);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        if (pos == POS_VIDEO) {
            switchFragment(videoFragment);
        } else if (pos == POS_PHOTO) {
            switchFragment(photoFragment);
        } else if (pos == POS_FAVORITE) {
            switchFragment(favoriteFragment);
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
//        LogUtil.e("label = " + message.getLabel() + "  value = " + message.getContent());
        if (TextUtils.equals("updateUserInfo", message.getLabel())) {
            isFirstLoad = true;
            requestUserInfo();
        } else if (TextUtils.equals("updateAvatar", message.getLabel())) {
            AppUtil.loadAvatarImg(message.getContent(), userAvatarImg);
        } else if (TextUtils.equals(Constant.TAG_LOGOUT, message.getLabel())) {
            isFirstLoad = true;
            requestUserInfo();
        }
        // 更新用户名称
        else if (TextUtils.equals("updateName", message.getLabel())) {
            userNameTv.setText(TextUtils.isEmpty(message.getContent()) ? "" : message.getContent());
        } else if (TextUtils.equals("updateUserSign", message.getLabel())) {
            if (!TextUtils.isEmpty(message.getContent())) {
                updateUserSign(message.getContent());
            }
        }
        // 退出登录
        else if (TextUtils.equals("close", message.getLabel())) {
            topLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
            bottomBlankView.setBlankView(R.mipmap.img_unlogin, "您还没有登录,请登录后查看更多!", "登录");
            loginBtn.setText("登录");
            LogUtil.e("退出登录  ");
        }
        // 绑定手机号
        else if (TextUtils.equals("updateMobile", message.getLabel())) {
            isFirstLoad = true;
            requestUserInfo();
        }
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        unbinder.unbind();
    }
}