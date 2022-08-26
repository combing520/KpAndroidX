package cn.cc1w.app.ui.ui.usercenter.usercenter.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.fragment.FastLoginFragment;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.fragment.LoginFragment;
import cn.cc1w.app.ui.R;

/**
 * 登录
 * @author kpinfo
 */
public class LoginActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_login_fast_login)
    TextView fastLoginBtn;
    @BindView(R.id.txt_login_normal_login)
    TextView normalLoginBtn;
    private int currentPos = 0;
    private static final int POS_LOGIN_FAST = 0;
    private static final int POS_LOGIN_NORMAL = 1;
    private Fragment currentFragment;
    private FastLoginFragment fastLoginFragment;
    private LoginFragment loginFragment;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initLoginTab();
        initFragment();
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        manager = getSupportFragmentManager();
        fastLoginFragment = FastLoginFragment.newInstance();
        loginFragment = LoginFragment.newInstance();
        switchFragment(fastLoginFragment);
    }

    /**
     * 初始化 tab信息
     */
    private void initLoginTab() {
        fastLoginBtn.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
        normalLoginBtn.setTextColor(ContextCompat.getColor(this,R.color.colorTxtLabel));
    }

    /**
     * 设置登录tab
     */
    private void setLoginTab(int pos) {
        if (currentPos != pos) {
            if (pos == POS_LOGIN_FAST) {
                fastLoginBtn.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                normalLoginBtn.setTextColor(ContextCompat.getColor(this,R.color.colorTxtLabel));
                switchFragment(fastLoginFragment);
            } else {
                normalLoginBtn.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                fastLoginBtn.setTextColor(ContextCompat.getColor(this,R.color.colorTxtLabel));
                switchFragment(loginFragment);
            }
        }
        currentPos = pos;
    }

    /**
     * 切换 fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (currentFragment != targetFragment) {
            if (!targetFragment.isAdded()) {
                if (null != currentFragment) {
                    manager.beginTransaction().hide(currentFragment).commit();
                }
                manager.beginTransaction().add(R.id.frame_content_login, targetFragment).commit();
            } else {
                manager.beginTransaction().hide(currentFragment).show(targetFragment).commit();
            }
            currentFragment = targetFragment;
        }
    }

    /**
     * 初始化 导航头
     */
    private void initNavigation() {
        titleTv.setText("登录");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_login_fast_login, R.id.txt_login_normal_login})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_login_fast_login) {
            setLoginTab(POS_LOGIN_FAST);
        } else if (id == R.id.txt_login_normal_login) {
            setLoginTab(POS_LOGIN_NORMAL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        unbinder.unbind();
    }
}