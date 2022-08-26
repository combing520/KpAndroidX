package cn.cc1w.app.ui.ui.usercenter.usercenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;

/**
 * @author kpinfo
 */
public class AccountUnsubscribeSuccessActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView navigationTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_unsubscribe_success);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
    }

    private void initNavigation() {
        navigationTitleTv.setText("注销成功");
    }

    @OnClick(R.id.img_back_header_not_title)
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    private void clearUserInfoCache(){
        if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            SharedPreferenceUtil.saveUserInfo(new UserInfoResultEntity.UserInfo());
            EventBus.getDefault().post(new EventMessage(Constant.TAG_LOGOUT, Constant.TAG_LOGOUT));
            Constant.CW_AUTHORIZATION = "";
            Constant.CW_USERNAME = "";
            Constant.CW_AVATAR = "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearUserInfoCache();
        unbinder.unbind();
    }
}