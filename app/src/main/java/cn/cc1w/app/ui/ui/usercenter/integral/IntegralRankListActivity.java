package cn.cc1w.app.ui.ui.usercenter.integral;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.RankPagerAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.usercenter.integral.fragment.MonthRankFragment;
import cn.cc1w.app.ui.ui.usercenter.integral.fragment.TotalRankFragment;
import cn.cc1w.app.ui.ui.usercenter.integral.fragment.WeekRankFragment;

/**
 * 积分排行榜
 * @author kpinfo
 */
public class IntegralRankListActivity extends CustomActivity {
    @BindView(R.id.viewpager_integral)
    ViewPager viewPager;
    @BindView(R.id.tabLayout_integral)
    TabLayout tabLayout;
    @BindView(R.id.txt_integral_week_rank)
    TextView weekIntegralTv;
    @BindView(R.id.txt_integral_month_rank)
    TextView monthIntegralTv;
    @BindView(R.id.txt_integral_total_rank)
    TextView totalIntegralTv;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_rank_list);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initData();
        initViewPagerInfo();
    }


    /**
     * 初始化 viewPager
     */
    private void initViewPagerInfo() {
        WeekRankFragment weekRankFragment = WeekRankFragment.newInstance();
        MonthRankFragment monthRankFragment = MonthRankFragment.newInstance();
        TotalRankFragment totalRankFragment = TotalRankFragment.newInstance();
        List<Fragment> list = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        list.add(weekRankFragment);
        list.add(monthRankFragment);
        list.add(totalRankFragment);
        titles.add("周排行榜");
        titles.add("月排行榜");
        titles.add("总分排行榜");
        RankPagerAdapter adapter = new RankPagerAdapter(getSupportFragmentManager(), titles, list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size() - 1);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String weekIntegral = getIntent().getStringExtra("weekIntegral");
        String monthIntegral = getIntent().getStringExtra("monthIntegral");
        String totalIntegral = getIntent().getStringExtra("totalIntegral");
        weekIntegralTv.setText(TextUtils.isEmpty(weekIntegral) ? "0" : weekIntegral);
        monthIntegralTv.setText(TextUtils.isEmpty(monthIntegral) ? "0" : monthIntegral);
        totalIntegralTv.setText(TextUtils.isEmpty(totalIntegral) ? "0" : totalIntegral);
    }

    @OnClick({R.id.img_back_integral_rank})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_integral_rank) {
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}