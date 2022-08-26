package cn.cc1w.app.ui.ui.home.record;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import com.rxjava.rxlife.RxLife;

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
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.entity.TopicDetailInfoEntity;
import cn.cc1w.app.ui.ui.home.record.fragment.TopicDetailPicFragment;
import cn.cc1w.app.ui.ui.home.record.fragment.TopicDetailVideoFragment;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 拍客中的话题
 *
 * @author kpinfo
 */
public class PaikewTopicActivity extends CustomActivity implements TabLayout.OnTabSelectedListener {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_title_topic)
    TextView topicTitleTv;
    @BindView(R.id.txt_cnt_vote_topic)
    TextView topicClickCntTv;
    @BindView(R.id.txt_describe_topic)
    TextView describeTv;
    @BindView(R.id.tab_topic)
    TabLayout tabLayout;
    @BindView(R.id.viewpager_topic)
    LinViewpager viewpager;
    protected int topicId;
    private String title;
    private Unbinder unbinder;
    private static final String[] TITLE = new String[]{"视频", "照片"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paikew_topic);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initData();
        initNavigation();
        initTabLayout();
        initFragment();
        requestTopicInfo();
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText(TextUtils.isEmpty(title) ? "" : title);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        topicId = getIntent().getIntExtra(Constant.TAG_TOPIC, 0);
        title = TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_TITLE)) ? "" : getIntent().getStringExtra(Constant.TAG_TITLE);
    }

    /**
     * 初始化 Fragment
     */
    private void initFragment() {
        FragmentManager manager = getSupportFragmentManager();
        TopicDetailVideoFragment topicDetailVideoFragment = TopicDetailVideoFragment.newInstance();
        TopicDetailPicFragment topicDetailPicFragment = TopicDetailPicFragment.newInstance();
        List<Fragment> list = new ArrayList<>();
        list.add(topicDetailVideoFragment);
        list.add(topicDetailPicFragment);
        PaikewPagerAdapter adapter = new PaikewPagerAdapter(manager, list, Arrays.asList(TITLE));
        viewpager.setIsCanScroll(true);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(viewpager);
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

    /**
     * 获取话题的基本信息
     */
    private void requestTopicInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.DETAIL_TOPIC).add(Constant.TAG_ID, topicId)
                    .asResponse(TopicDetailInfoEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing() && data != null) {
                            topicTitleTv.setText(TextUtils.isEmpty(data.getTopic_name()) ? "" : data.getTopic_name());
                            topicClickCntTv.setText(String.valueOf(data.getParticipation()));
                            describeTv.setText(TextUtils.isEmpty(data.getSummary()) ? "" : data.getSummary());
                        }
                    }, (OnError) error -> {
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.TOPIC_ID = 0;
        unbinder.unbind();
    }
}