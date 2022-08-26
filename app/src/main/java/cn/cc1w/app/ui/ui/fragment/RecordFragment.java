package cn.cc1w.app.ui.ui.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import app.cloud.ccwb.cn.linlibrary.viewpager.LinViewpager;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cc1w.app.ui.PaikewPagerAdapter;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.BaseFragment;
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
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;

/**
 * 拍客
 *
 * @author kpinfo
 */
public class RecordFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {
    @BindView(R.id.tab_record)
    TabLayout tabLayout;
    @BindView(R.id.viewpager_record)
    LinViewpager viewpager;
    @BindView(R.id.topContainer)
    LinearLayout topContainer;
    @BindView(R.id.btn_paike_record)
    TextView btn;
    @BindView(R.id.img)
    ImageView imageView;
    @BindView(R.id.container)
    LinearLayout container;
    private Fragment currentFragment;
    private FragmentManager manager;
    private PhotoFragment photoFragment;
    private static final int POS_PIC = 0;
    private Context mContext;
    private static final String[] TITLE = new String[]{"照片", "视频", "热门", "我拍"};

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    @Override
    protected int getContentResId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void onBindListener() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onApplyData() {
        setRedThemeInfo();
        initTab();
        initFragment();
    }

    @Override
    public void frResume() {

    }

    @Override
    public void frPause() {

    }

    /**
     * 初始化顶部title
     */
    private void initTab() {
        for (String title : TITLE) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        tabLayout.addOnTabSelectedListener(this);
    }

    /**
     * 初始化 Fragment
     */
    private void initFragment() {
        manager = getChildFragmentManager();
        VideoFragment videoFragment = VideoFragment.newInstance();
        photoFragment = PhotoFragment.newInstance();
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

    private void setRedThemeInfo() {
        if (Constant.IS_SHOW_RED_MODE) {
            topContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_red_theme));
            tabLayout.setTabTextColors(ContextCompat.getColor(mContext, R.color.colorWhite), ContextCompat.getColor(mContext, R.color.color_golden_theme));
            container.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_paike_release2));
            btn.setTextColor(ContextCompat.getColor(mContext, R.color.color_red_theme));
            imageView.setImageResource(R.drawable.ic_xiangji1);
        }
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
                if (null != tabLayout && null != tabLayout.getTabAt(POS_PIC)) {
                    tabLayout.getTabAt(POS_PIC).select();
                }
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @OnClick(R.id.btn_paike_record)
    public void onClick(View view) {
        if (view.getId() == R.id.btn_paike_record) {
            doRecordPaiKeWorks();
        }
    }

    private void doRecordPaiKeWorks() {
        //  判断是否登录过，然后判断是否绑定过手机号
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            Bundle bundle = new Bundle();
            bundle.putString("from", Constant.RECORD_PAIKEW);
            IntentUtil.startActivity(mContext, LoginActivity.class, bundle);
        } else {
            UserInfoResultEntity.UserInfo userInfo = SharedPreferenceUtil.getUserInfo();
            // 已经绑定过手机号了
            if (null != userInfo && !TextUtils.isEmpty(userInfo.getMobile())) {
                IntentUtil.startActivity(mContext, PaikewUploadActivity.class, null);
            } else {
                // 去绑定手机号
                IntentUtil.startActivity(mContext, UpdateMobileActivity.class, null);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}