package cn.cc1w.app.ui.ui.usercenter.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 用户反馈
 *
 * @author kpinfo
 */
public class FeedBackActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.edit_feedback)
    EditText contentEdit;
    @BindView(R.id.btn_send_feedback)
    TextView submitBtn;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoading();
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText(getResources().getString(R.string.feedback));
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_send_feedback})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_send_feedback) {
            feedBack();
        }
    }

    /**
     * 进行反馈
     */
    private void feedBack() {
        String content = contentEdit.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShortToast("反馈内容不能为空");
            return;
        }
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
            return;
        }
        loading.show();
        submitBtn.setClickable(false);
        RxHttp.postJson(Constant.FEED_BACK).add("cw_content", content)
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
                        finish();
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (!isFinishing()) {
                        if (null != submitBtn) {
                            submitBtn.setClickable(true);
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    }
                    if (error != null && error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(this, LoginActivity.class);
                    }
                });
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