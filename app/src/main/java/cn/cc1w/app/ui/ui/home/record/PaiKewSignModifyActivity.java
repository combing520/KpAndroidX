package cn.cc1w.app.ui.ui.home.record;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.PaiKewUserInfoEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;

/**
 * 拍客签名修改
 *
 * @author kpinfo
 */
public class PaiKewSignModifyActivity extends CustomActivity implements View.OnClickListener {
    @BindView(R.id.txt_header_not_label)
    TextView saveBtn;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_update_cnt_paikew)
    TextView cntTv;
    @BindView(R.id.edit_update_username_paikew)
    EditText userSignEdit;
    private LoadingDialog loading;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pai_kew_sign_modify);
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

    /**
     * 初始化Navigation
     */
    private void initNavigation() {
        titleTv.setText("签名");
        saveBtn.setVisibility(View.VISIBLE);
        saveBtn.setText("保存");
        saveBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTxtLabel));
        saveBtn.setClickable(false);
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_header_not_label})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_header_not_label) {
            saveUserSign();
        }
    }

    /**
     * 保存签名
     */
    private void saveUserSign() {
        String userSignStr = userSignEdit.getText().toString().trim();
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.TAG_UPDATE_PAIKEW).add("tag", userSignStr)
                    .asResponse(PaiKewUserInfoEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        ToastUtil.showShortToast("修改成功");
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (data != null) {
                            EventBus.getDefault().post(new EventMessage("updateUserSign", data.getTag()));
                            finish();
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

    @OnTextChanged(R.id.edit_update_username_paikew)
    public void onTextChanged(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            cntTv.setText("0/40");
            saveBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTxtLabel));
            saveBtn.setClickable(false);
        } else {
            if (text.length() >= 10) {
                cntTv.setText(renderColorfulStr(String.valueOf(text.length()).concat("/40"), 0, 2));
            } else {
                cntTv.setText(renderColorfulStr(String.valueOf(text.length()).concat("/40"), 0, 1));
            }
            saveBtn.setTextColor(ContextCompat.getColor(this, R.color.color_home_red));
            saveBtn.setClickable(true);
        }
    }

    /**
     * render 带有颜色的 spannableStringBuilder
     *
     * @param targetStr 对应的文字
     * @param endPos    结束位置
     */
    private SpannableStringBuilder renderColorfulStr(String targetStr, int startPos, int endPos) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(targetStr);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}