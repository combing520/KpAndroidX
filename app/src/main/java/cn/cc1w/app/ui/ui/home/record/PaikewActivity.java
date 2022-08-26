package cn.cc1w.app.ui.ui.home.record;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.viewpager.LinViewpager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.PaikewPagerAdapter;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;
import cn.cc1w.app.ui.ui.home.record.fragment.HotPaikewWorksFragment;
import cn.cc1w.app.ui.ui.home.record.fragment.PhotoFragment;
import cn.cc1w.app.ui.ui.home.record.fragment.PhotographFragment;
import cn.cc1w.app.ui.ui.home.record.fragment.VideoFragment;
import cn.cc1w.app.ui.ui.home.upload.PaikewUploadActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;

/**
 * 拍客
 *
 * @author kpinfo
 */
public class PaikewActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.tab_record)
    TabLayout tabLayout;
    @BindView(R.id.viewpager_record)
    LinViewpager viewpager;
    private Fragment currentFragment;
    private FragmentManager manager;
    private PhotoFragment photoFragment;
    private static final String[] TITLE = new String[]{"照片", "视频", "热门", "我拍"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recoverData(savedInstanceState);
        setContentView(R.layout.activity_paikew_activity);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTab();
        initFragment();
    }

    /**
     * 初始化顶部title
     */
    private void initTab() {
        for (String title : TITLE) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
    }

    /**
     * 初始化 Fragment
     */
    private void initFragment() {
        manager = getSupportFragmentManager();
        photoFragment = PhotoFragment.newInstance();
        VideoFragment videoFragment = VideoFragment.newInstance();
        HotPaikewWorksFragment hotPaikewWorksFragment = HotPaikewWorksFragment.newInstance();
        PhotographFragment photographFragment = PhotographFragment.newInstance();
        List<Fragment> list = new ArrayList<>();
        list.add(photoFragment);
        list.add(videoFragment);
        list.add(hotPaikewWorksFragment);
        list.add(photographFragment);
        PaikewPagerAdapter adapter = new PaikewPagerAdapter(manager, list, Arrays.asList(TITLE));
        viewpager.setIsCanScroll(true);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(viewpager);
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
                manager.beginTransaction().add(R.id.container_record, targetFragment).commit();
            } else {
                manager.beginTransaction().hide(currentFragment).show(targetFragment).commit();
            }
            currentFragment = targetFragment;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("close", message.getLabel())) {
                switchFragment(photoFragment);
                if (tabLayout != null && null != tabLayout.getTabAt(0)) {
                    tabLayout.getTabAt(0).select();
                }
            }
        }
    }

    private void recoverData(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            LogUtil.e("恢复数据");
            if (!TextUtils.isEmpty(savedInstanceState.getString("avatar"))) {
                Constant.CW_AVATAR = savedInstanceState.getString("avatar");
            }
            if (!TextUtils.isEmpty(savedInstanceState.getString("token"))) {
                Constant.CW_AUTHORIZATION = savedInstanceState.getString("token");
                EventBus.getDefault().post(new EventMessage("updateUserInfo", "updateUserInfo"));
            }
            AppUtil.initPublicParams(this);
        }
    }

    @OnClick(R.id.btn_paike_record)
    public void onClick(View view) {
        if (view.getId() == R.id.btn_paike_record) {
            doRecordPaiKeWorks();
        }
    }

    private void doRecordPaiKeWorks() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            Bundle bundle = new Bundle();
            bundle.putString("from", Constant.RECORD_PAIKEW);
            IntentUtil.startActivity(this, LoginActivity.class, bundle);
        } else {
            UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
            if (null != userInfo && !TextUtils.isEmpty(userInfo.getMobile())) {
                IntentUtil.startActivity(this, PaikewUploadActivity.class, null);
            } else {
                IntentUtil.startActivity(this, UpdateMobileActivity.class, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}