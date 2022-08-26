package cn.cc1w.app.ui.ui.usercenter.wallet.income;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.usercenter.wallet.income.fragment.EarnPhotoFragment;
import cn.cc1w.app.ui.ui.usercenter.wallet.income.fragment.EarningVideoFragment;
import cn.cc1w.app.ui.R;

/**
 * 收入
 *
 * @author kpinfo
 */
public class IncomeActivity extends CustomActivity implements TabLayout.OnTabSelectedListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_header_not_label)
    TextView labelTv;
    @BindView(R.id.tab_income)
    TabLayout tabLayout;
    private EarningVideoFragment videoFragment;
    private EarnPhotoFragment photoFragment;
    private FragmentManager manager;
    private Fragment currentFragment;
    private static final int POS_VIDEO = 0;
    private static final int POS_PHOTO = 1;

    private static final String[] TITLE = new String[]{
            "收益视频",
            "收益照片"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initTabs();
        initFragment();
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        videoFragment = EarningVideoFragment.newInstance();
        photoFragment = EarnPhotoFragment.newInstance();

        switchFragment(videoFragment);

        tabLayout.addOnTabSelectedListener(this);
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
                manager.beginTransaction().add(R.id.container_income, targetFragment).commit();
            } else {
                manager.beginTransaction().hide(currentFragment).show(targetFragment).commit();
            }
            currentFragment = targetFragment;
        }
    }


    private void initTabs() {
        for (int i = 0; i < TITLE.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(TITLE[i]));
        }
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText("我的收入");
        labelTv.setText("收入说明");
        labelTv.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_header_not_label})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_header_not_label) { //收入寿命
            Intent intent = new Intent();
            intent.setClass(this, IncomeRulerDeclareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        if (pos == POS_VIDEO) {
            switchFragment(videoFragment);
        } else if (pos == POS_PHOTO) {
            switchFragment(photoFragment);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
