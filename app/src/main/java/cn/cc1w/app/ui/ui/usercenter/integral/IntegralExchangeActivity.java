package cn.cc1w.app.ui.ui.usercenter.integral;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.usercenter.integral.fragment.IntegralFragment;
import cn.cc1w.app.ui.ui.usercenter.integral.fragment.IntegralRecordFragment;

/**
 * 积分兑换
 *
 * @author kpinfo
 */
public class IntegralExchangeActivity extends CustomActivity {
    @BindView(R.id.ll_integral_integralExchange)
    LinearLayout integralLayout;
    @BindView(R.id.ll_record_integralExchange)
    LinearLayout recordLayout;
    private Unbinder unbinder;
    private IntegralFragment integralFragment;
    private IntegralRecordFragment integralRecordFragment;
    private Fragment currentFragment;
    private FragmentManager manager;
    private int currentPos = 0;
    private static final int POS_INTEGRAL = 0;
    private static final int POS_RECORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_exchange);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initTabState();
        initFragment();
        switchFragment(integralFragment);
    }

    /**
     * 初始化 Fragment
     */
    private void initFragment() {
        manager = getSupportFragmentManager();
        integralFragment = IntegralFragment.newInstance();
        integralRecordFragment = IntegralRecordFragment.newInstance();
    }

    /**
     * 初始化 tab栏选中状态
     */
    private void initTabState() {
        integralLayout.setBackgroundColor(getResources().getColor(R.color.bg_tab_select));
        recordLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    /**
     * 切换 Fragment
     *
     * @param targetFragment 切换的 目标Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (currentFragment != targetFragment) {
            if (!targetFragment.isAdded()) {
                if (null != currentFragment) {
                    manager.beginTransaction().hide(currentFragment).commit();
                }
                manager.beginTransaction().add(R.id.content_integralExchange, targetFragment).commit();
            } else {
                manager.beginTransaction().hide(currentFragment).show(targetFragment).commit();
            }
            currentFragment = targetFragment;
        }
    }

    @OnClick({R.id.ll_integral_integralExchange, R.id.ll_record_integralExchange, R.id.img_back_integralExchange})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_integral_integralExchange) {
            setSelectedPage(POS_INTEGRAL);
        } else if (id == R.id.ll_record_integralExchange) {
            setSelectedPage(POS_RECORD);
        } else if (id == R.id.img_back_integralExchange) {
            finish();
        }
    }

    /**
     * 设置选中的 页面
     *
     * @param selectPos 当前选中的位置
     */
    private void setSelectedPage(int selectPos) {
        if (currentPos != selectPos) {
            if (selectPos == POS_INTEGRAL) {
                switchFragment(integralFragment);
            } else if (selectPos == POS_RECORD) {
                switchFragment(integralRecordFragment);
            }
        }
        setSelectTab(selectPos);
        currentPos = selectPos;
    }


    /**
     * 设置当前选中的 tab
     *
     * @param pos 选中的位置
     */
    private void setSelectTab(int pos) {
        if (currentPos != pos) {
            if (pos == POS_INTEGRAL) {
                recordLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
                integralLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_tab_select));
            } else if (pos == POS_RECORD) {
                integralLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
                recordLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_tab_select));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}