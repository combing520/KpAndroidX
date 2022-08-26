package cn.cc1w.app.ui.ui.home.record;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.viewpager.LinViewpager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.PaikewPagerAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.home.record.fragment.HotPicRankFragment;
import cn.cc1w.app.ui.ui.home.record.fragment.HotTopicRankFragment;
import cn.cc1w.app.ui.ui.home.record.fragment.HotVideoRankFragment;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 话题排名
 * @author kpinfo
 */
public class TopicRankActivity extends CustomActivity implements TabLayout.OnTabSelectedListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.tab_rank_topic)
    TabLayout tabLayout;
    @BindView(R.id.img_top_topic_rank)
    ImageView topImg;
    @BindView(R.id.viewpager_rank_topic)
    LinViewpager viewpager;
    private Unbinder unbinder;
    private static final String[] TITLE = new String[]{"话题", "视频", "照片"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_rank);
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initTabLayout();
        initFragment();
    }

    /**
     * 初始化 fragment
     */
    private void initFragment() {
        int selectPos = getIntent().getIntExtra("selectPos", 0);
        FragmentManager manager = getSupportFragmentManager();
        HotTopicRankFragment hotTopicRankFragment = HotTopicRankFragment.newInstance();
        HotVideoRankFragment hotVideoRankFragment = HotVideoRankFragment.newInstance();
        HotPicRankFragment hotPicRankFragment = HotPicRankFragment.newInstance();
        List<Fragment> list = new ArrayList<>();

        list.add(hotTopicRankFragment);
        list.add(hotVideoRankFragment);
        list.add(hotPicRankFragment);

        PaikewPagerAdapter adapter = new PaikewPagerAdapter(manager, list, Arrays.asList(TITLE));
        viewpager.setIsCanScroll(true);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(selectPos);
    }

    private void initNavigation() {
        titleTv.setText("排行");
        AppUtil.loadRes(R.mipmap.ic_top_topic, topImg);
    }

    /**
     * 初始化 导航
     */
    private void initTabLayout() {
        for (String str : TITLE) {
            tabLayout.addTab(tabLayout.newTab().setText(str));
        }
        tabLayout.addOnTabSelectedListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }
}