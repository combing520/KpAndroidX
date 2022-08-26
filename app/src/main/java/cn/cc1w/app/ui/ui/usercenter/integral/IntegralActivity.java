package cn.cc1w.app.ui.ui.usercenter.integral;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.SignListAdapter;
import cn.cc1w.app.ui.adapter.IntegralAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.SignInfoEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 积分
 *
 * @author kpinfo
 */
public class IntegralActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.btn_sign_integral)
    TextView signBtn;
    @BindView(R.id.list_integral)
    RecyclerView integralList;
    @BindView(R.id.scrollView_integral)
    NestedScrollView scrollView;
    @BindView(R.id.txt_total_integral)
    TextView totalIntegralTv;
    @BindView(R.id.txt_today_integral)
    TextView todayIntegralTv;
    @BindView(R.id.txt_detail_integral)
    TextView integralDetailTv;
    @BindView(R.id.list_sign_integral)
    RecyclerView signList;
    private IntegralAdapter adapter;
    private SignListAdapter signListAdapter;
    private LoadingDialog loading;
    private Drawable signedBtnDrawable;
    private Drawable unSignBtnDrawable;
    private int totalIntegral;
    private int weekIntegral;
    private int monthIntegral;
    private int todayIntegral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        adapter = new IntegralAdapter();
        signedBtnDrawable = ContextCompat.getDrawable(this,R.drawable.bg_container_red_big);
        unSignBtnDrawable = ContextCompat.getDrawable(this,R.drawable.bg_container_gray_big);
        setSignBtnInfo();
        initList();
        initLoading();
        requestData();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化 list 相关信息
     */
    private void initList() {
        integralList.setLayoutManager(new LinearLayoutManager(this));
        integralList.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(this, 1), 1));
        integralList.setAdapter(adapter);
        signListAdapter = new SignListAdapter(this);
        GridLayoutManager manager = new GridLayoutManager(this, 6);
        signList.setLayoutManager(manager);
        signList.setAdapter(signListAdapter);
    }

    /**
     * 设置签到按钮是否可点击
     *
     * @param clickable 是否可点击
     */
    private void setSignBtnClickable(boolean clickable) {
        if (clickable) {
            signBtn.setBackground(signedBtnDrawable);
            signBtn.setTextColor(Color.WHITE);
        } else {
            signBtn.setBackground(unSignBtnDrawable);
            signBtn.setTextColor(Color.GRAY);
        }
        signBtn.setClickable(clickable);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.INFO_SIGN_INTEGRAL)
                    .asResponse(SignInfoEntity.SignInfo.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(signInfo -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && signInfo != null) {
                            scrollView.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(signInfo.getCredit_day())) {
                                todayIntegral = Integer.parseInt(signInfo.getCredit_day());
                                todayIntegralTv.setText("今日已领取 : " + todayIntegral);
                            }
                            if (!TextUtils.isEmpty(signInfo.getCredit_week())) {
                                weekIntegral = Integer.parseInt(signInfo.getCredit_week());
                            }
                            if (!TextUtils.isEmpty(signInfo.getCredit_mon())) {
                                monthIntegral = Integer.parseInt(signInfo.getCredit_mon());
                            }
                            if (!TextUtils.isEmpty(signInfo.getCredit_total())) {
                                totalIntegral = Integer.parseInt(signInfo.getCredit_total());
                                totalIntegralTv.setText("我的积分 : " + totalIntegral);
                            }
                            if (signInfo.isDay_signin()) {
                                setSignBtnClickable(false);
                                signBtn.setText("已签到");
                            }
                            List<SignInfoEntity.SignInfo.CreditRecordBean> integralList = signInfo.getCredit_record();
                            adapter.setData(integralList);
                            List<SignInfoEntity.SignInfo.SigninBean> signList = signInfo.getSignin();
                            signListAdapter.setData(signList);
                            if (!TextUtils.isEmpty(signInfo.getSignin_number()) && Integer.parseInt(signInfo.getSignin_number()) > 0) {
                                int daySign = Integer.parseInt(signInfo.getSignin_number());
                                signListAdapter.setSignPos(daySign);
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
        }
    }

    /**
     * 设置 签到按钮 样式
     */
    private void setSignBtnInfo() {
        setSignBtnClickable(true);
    }

    @OnClick({R.id.img_back_integral, R.id.btn_convert_integral, R.id.btn_sign_integral, R.id.txt_detail_integral})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_integral) {
            finish();
        } else if (id == R.id.btn_convert_integral) {
            Bundle bundle = new Bundle();
            bundle.putString("weekIntegral", String.valueOf(weekIntegral));
            bundle.putString("monthIntegral", String.valueOf(monthIntegral));
            bundle.putString("totalIntegral", String.valueOf(totalIntegral));
            IntentUtil.startActivity(this, IntegralRankListActivity.class, bundle);
        } else if (id == R.id.btn_sign_integral) {
            doSign();
        } else if (id == R.id.txt_detail_integral) {
            IntentUtil.startActivity(this, IntegralRulerActivity.class, null);
        }
    }

    /**
     * 进行签到
     */
    private void doSign() {
        if (NetUtil.isNetworkConnected(this)) {
            signBtn.setClickable(false);
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.SIGN)
                    .asMsgResponse(MsgResonse.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && totalIntegralTv != null && todayIntegralTv != null && signBtn != null) {
                            if(data != null){
                                ToastUtil.showShortToast(data.getMessage());
                            }
                            int primaryPos = signListAdapter.getSignPos();
                            signListAdapter.setSignPos(primaryPos + 1);
                            setSignBtnClickable(false);
                            SignInfoEntity.SignInfo.SigninBean integralEntity = signListAdapter.getItem(primaryPos + 1);
                            if (null != integralEntity) {
                                totalIntegral += Integer.parseInt(integralEntity.getCredit());
                                todayIntegral += Integer.parseInt(integralEntity.getCredit());
                                totalIntegralTv.setText("我的积分 : " + totalIntegral);
                                todayIntegralTv.setText("今日已领取 : " + todayIntegral);
                                signBtn.setText("已签到");
                                EventBus.getDefault().post(new EventMessage("updateUserIntegral", String.valueOf(totalIntegral) + "积分"));
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && signBtn != null) {
                            signBtn.setClickable(true);
                        }
                        if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
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
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}