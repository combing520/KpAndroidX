package cn.cc1w.app.ui.ui.usercenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.qrscan.util.CodeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.PromotionCodeResultBean;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.utils.videocut.ThreadPool2;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * @author kpinfo
 */
public class PromotionCodeActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.img_qr_code_promotion)
    ImageView qrCodeImg;
    @BindView(R.id.edit_code_promotion)
    EditText contentEdit;
    @BindView(R.id.number_promotion)
    TextView codeTv;
    @BindView(R.id.tv_img_num_promotion)
    TextView promotionNumberTv;
    @BindView(R.id.container)
    LinearLayout promotionLayout;
    @BindView(R.id.tv_hint_promotion)
    TextView promotionHintTv;
    @BindView(R.id.container_tools_qr)
    LinearLayout promotionOperationLayout;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_code);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).fitsSystemWindows(true).init();
        initNavigation();
        initLoading();
        getUserPromotionCode();
    }

    private void initNavigation() {
        titleTv.setText("我的推广码");
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 获取推广码
     */
    private void getUserPromotionCode() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.CODE_INVITATION)
                    .asResponse(PromotionCodeResultBean.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && data != null) {
                            if (!TextUtils.isEmpty(data.getApp_download_url())) {
                                createQrCode(data.getApp_download_url(), data.getCode());
                                promotionNumberTv.setText("已推广人数:" + data.getUser_num());
                                promotionLayout.setVisibility(View.VISIBLE);
                                if (data.isIs_code()) {
                                    promotionHintTv.setVisibility(View.VISIBLE);
                                    promotionOperationLayout.setVisibility(View.GONE);
                                } else {
                                    promotionHintTv.setVisibility(View.GONE);
                                    promotionOperationLayout.setVisibility(View.VISIBLE);
                                }
                            }
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
            ToastUtil.showShortToast(getResources().getString(R.string.network_error));
        }
    }

    /**
     * 添加邀请码
     */
    private void addInvitationCode() {
        String code = contentEdit.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showShortToast("请输入邀请人的邀请码");
        } else {
            if (NetUtil.isNetworkConnected(this)) {
                if (null != loading) {
                    loading.show();
                }
                RxHttp.postJson(Constant.CODE_INVITATION_ADD).add("code", code)
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (!isFinishing()) {
                                if(data != null){
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                                promotionHintTv.setVisibility(View.VISIBLE);
                                promotionOperationLayout.setVisibility(View.GONE);
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
                ToastUtil.showShortToast(getResources().getString(R.string.network_error));
            }
        }
    }

    /**
     * 创建二维码
     */
    private void createQrCode(String content, String showText) {
        if (!TextUtils.isEmpty(content)) {
            ThreadPool2.getInstance().execute(() -> {
                Bitmap bitmap = CodeUtils.createQRCode(content, AppUtil.dip2px(this, 183), null);
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        Glide.with(PromotionCodeActivity.this).load(bitmap).into(qrCodeImg);
                        codeTv.setText(showText);
                    }
                });
            });
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_create_promotion})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_create_promotion) {
            addInvitationCode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}