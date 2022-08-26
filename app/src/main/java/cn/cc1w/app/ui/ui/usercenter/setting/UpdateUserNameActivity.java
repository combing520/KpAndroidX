package cn.cc1w.app.ui.ui.usercenter.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;

/**
 * 修改用户名称
 *
 * @author kpinfo
 */
public class UpdateUserNameActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.edit_update_username)
    EditText userNameEdit;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initUserName();
        initLoading();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化名称
     */
    private void initUserName() {
        String name = getIntent().getStringExtra("name");
        userNameEdit.setText(TextUtils.isEmpty(name) ? "" : name);
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("昵称");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_submit_username})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_submit_username) {
            updateUserName();
        }
    }

    /**
     * 更新用户名称
     */
    private void updateUserName() {
        if (NetUtil.isNetworkConnected(this)) {
            String userName = userNameEdit.getText().toString();
            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userName.trim())) {
                ToastUtil.showShortToast("昵称长度错误");
            } else {
                loading.show();
                RxHttp.postJson(Constant.NICKNAME_UPDATE).add("cw_nickname", userName)
                        .asResponse(UserInfoResultEntity.UserInfo.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(userInfo -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (!isFinishing()) {
                                if (null != userInfo) {
                                    ToastUtil.showShortToast("更新成功");
                                    Constant.CW_USERNAME = TextUtils.isEmpty(userInfo.getNickname()) ? "" : userInfo.getNickname();
                                    EventBus.getDefault().post(new EventMessage("updateName", userInfo.getNickname()));
                                    finish();
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
        } else {
            ToastUtil.showShortToast(getResources().getString(R.string.network_error));
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