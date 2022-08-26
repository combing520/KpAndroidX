package cn.cc1w.app.ui.ui.home.apps;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;

import com.rxjava.rxlife.RxLife;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.AppsPageAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppsTitleEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 添加我的最爱
 *
 * @author kpinfo
 */
public class FavoriteAppsAddActivity extends CustomActivity implements TabLayout.OnTabSelectedListener {
    private Unbinder unbinder;
    @BindView(R.id.tab_apps_add)
    TabLayout tabLayout;
    @BindView(R.id.viewPager_apps_add)
    ViewPager viewPager;
    private int currentPos = 0;
    private String appsTypeId;
    private String topTitleStr;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_apps_add);
        init();
    }

    private void init() {
        appsTypeId = TextUtils.isEmpty(getIntent().getStringExtra(Constant.TAG_ID)) ? "" : getIntent().getStringExtra(Constant.TAG_ID);
        topTitleStr = TextUtils.isEmpty(getIntent().getStringExtra("topTitle")) ? "" : getIntent().getStringExtra("topTitle");
        unbinder = ButterKnife.bind(this);
        initLoading();
        initTab();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化 Tab
     */
    private void initTab() {
        requestTopTitle();
        tabLayout.addOnTabSelectedListener(this);
    }

    /**
     * 初始化 Tab信息
     *
     * @param list tab数据源
     */
    private void initTabInfo(List<AppsTitleEntity.ItemAppsTitleEntity> list) {
        if (null != list && !list.isEmpty()) {
            if (!TextUtils.isEmpty(appsTypeId)) {
                for (int i = 0; i < list.size(); i++) {
                    if (TextUtils.equals(appsTypeId, list.get(i).getId())) {
                        currentPos = i;
                    }
                }
            }
            // 如果是传递的title
            else {
                if (!TextUtils.isEmpty(topTitleStr)) {
                    for (int i = 0; i < list.size(); i++) {
                        if (TextUtils.equals(topTitleStr, list.get(i).getName())) {
                            currentPos = i;
                        }
                    }
                }
            }
//            viewPager.setOffscreenPageLimit(list.size() - 1);
            AppsPageAdapter adapter = new AppsPageAdapter(getSupportFragmentManager(), list);
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(currentPos, false);
            if (list.size() > Constant.CNT_SCROLL_MIN) {
                tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
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

    /**
     * 请求顶部 title信息
     */
    private void requestTopTitle() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.TITLE_APPS_LIST)
                    .add(Constant.STR_CW_MACHINE_ID, Constant.CW_MACHINE_ID)
                    .asResponseList(AppsTitleEntity.ItemAppsTitleEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        initTabInfo(list);
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        }
    }

    @OnClick({R.id.img_back_apps_add, R.id.img_search_apps_add})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_apps_add) {
            finish();
        } else if (id == R.id.img_search_apps_add) {
            Intent intent = new Intent();
            intent.setClass(this, AppsSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}