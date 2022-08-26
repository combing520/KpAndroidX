package cn.cc1w.app.ui.ui.home.apps;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import org.greenrobot.eventbus.EventBus;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.AppsDetailsPagerAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsDetailEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 应用号 介绍
 *
 * @author kpinfo
 */
public class AppDetailActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_title_header_news_focus)
    TextView titleTv;
    @BindView(R.id.tab_apps_detail)
    SlidingTabLayout tabLayout;
    @BindView(R.id.img_logo_apps_detail)
    RoundAngleImageView appsLogoImg;
    @BindView(R.id.txt_title_apps_detail)
    TextView appsTitleTv;
    @BindView(R.id.txt_describe_apps_detail)
    TextView appsDescribeTv;
    @BindView(R.id.txt_fans_cnt_apps_detail)
    TextView appsFansCntTv;
    @BindView(R.id.txt_article_cnt_apps_detail)
    TextView appsArticleCntTv;
    @BindView(R.id.viewpager_app_detail)
    ViewPager viewPager;
    @BindView(R.id.img_bg_top_apps_detail)
    ImageView topBgImg;
    @BindView(R.id.btn_add_apps_detail)
    TextView focusBtn;
    @BindView(R.id.txt_layout_tab_apps_detail)
    TextView tabLayoutTxt;
    @BindView(R.id.tv_hint_app_detail)
    TextView hintTv;
    private String group_id;
    private LoadingDialog loading;
    private AppsDetailsPagerAdapter appsDetailsPagerAdapter;
    private String appsId;
    private Drawable selectDrawable;
    private Drawable normalDrawable;
    private boolean isAppSelect;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(false).init();
        unbinder = ButterKnife.bind(this);
        initWindowInfo();
        initNavigation();
        initDrawable();
        initLoading();
        requestData();
    }

    /**
     * 初始化 drawable 信息
     */
    private void initDrawable() {
        selectDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_choose);
        normalDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_focus_normal_detail_apps);
        focusBtn.setBackground(normalDrawable);
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.DETAIL_APPS)
                    .add("cw_id", appsId)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponse(AppsDetailEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(appsDetailEntity -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (appsDetailEntity != null && !isFinishing()) {
                            setAppsInfo(appsDetailEntity);
                            if (null != appsDetailEntity.getList() && !appsDetailEntity.getList().isEmpty()) {
                                appsDetailsPagerAdapter = new AppsDetailsPagerAdapter(getSupportFragmentManager(), appsDetailEntity.getList());
                                viewPager.setAdapter(appsDetailsPagerAdapter);
                                tabLayout.setViewPager(viewPager);
                                if (appsDetailEntity.getList().size() == 1) {
                                    tabLayout.setVisibility(View.GONE);
                                    tabLayoutTxt.setVisibility(View.VISIBLE);
                                    tabLayoutTxt.setText(appsDetailEntity.getList().get(0).getName());
                                } else {
                                    tabLayoutTxt.setVisibility(View.GONE);
                                    tabLayoutTxt.setText("");
                                    tabLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                tabLayout.setVisibility(View.GONE);
                                tabLayoutTxt.setVisibility(View.GONE);
                                viewPager.setVisibility(View.GONE);
                                hintTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        }
    }

    /**
     * 设置应用号信息
     *
     * @param appsDetailEntity 应用号信息
     */
    private void setAppsInfo(AppsDetailEntity.DataBean appsDetailEntity) {
        AppUtil.loadNewsGroupImg(appsDetailEntity.getLogo_pic_path(), appsLogoImg);
        appsTitleTv.setText(appsDetailEntity.getName());
        appsDescribeTv.setText(appsDetailEntity.getSummary());
        appsFansCntTv.setText(String.valueOf(appsDetailEntity.getUser_num()).concat("粉丝"));
        appsArticleCntTv.setText(String.valueOf(appsDetailEntity.getNews_num()).concat("文章"));
        AppUtil.loadBannerImg(appsDetailEntity.getBg_pic_path(), topBgImg);
        if (appsDetailEntity.isAttention()) {
            focusBtn.setBackground(selectDrawable);
            isAppSelect = true;
        } else {
            focusBtn.setBackground(normalDrawable);
            isAppSelect = false;
        }
    }

    /**
     * 初始化 Window 信息
     */
    private void initWindowInfo() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    /**
     * 初始化 顶部的导航栏
     */
    private void initNavigation() {
        titleTv.setText(TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_TITLE)) ? "" : getIntent().getStringExtra(Constant.TAG_TITLE));
        appsId = getIntent().getStringExtra(Constant.TAG_ID);
        group_id = TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_GROUP_ID)) ? "" : getIntent().getStringExtra(Constant.TAG_GROUP_ID);
    }

    @OnClick({R.id.img_back_header_news_focus, R.id.btn_add_apps_detail})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_news_focus) {
            finish();
        } else if (id == R.id.btn_add_apps_detail) {
            addOrCancelFocusApps(appsId);
        }
    }

    /**
     * 关注/取关 应用号
     */
    private void addOrCancelFocusApps(final String appsId) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime <= Constant.MIN_TIME_INTERVAL) {
                return;
            }
            if (!isAppSelect) {
                RxHttp.postJson(Constant.ADD_APPS).add("cw_app_id", appsId)
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (!isFinishing()) {
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                                EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, group_id));
                                focusBtn.setBackground(selectDrawable);
                                isAppSelect = true;
                            }
                        }, (OnError) error -> {
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(this, LoginActivity.class);
                            }
                        });
                lastTime = currentTime;
            } else {
                RxHttp.postJson(Constant.CANCEL_APPS).add("cw_app_id", appsId)
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (!isFinishing()) {
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                                EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, group_id));
                                focusBtn.setBackground(normalDrawable);
                                isAppSelect = false;
                            }
                        }, (OnError) error -> {
                            ToastUtil.showShortToast(error.getErrorMsg());
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(this, LoginActivity.class);
                            }
                        });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}