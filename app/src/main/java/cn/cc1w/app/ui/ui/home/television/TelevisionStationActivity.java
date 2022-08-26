package cn.cc1w.app.ui.ui.home.television;

import android.content.res.Configuration;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.viewpager.LinViewpager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.TelevisionPageAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.TelevisionClassifyEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.MyTabLayout;
import cn.cc1w.app.ui.widget.video.LiveVideoPlayer;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 电视台
 *
 * @author kpinfo
 */
public class TelevisionStationActivity extends CustomActivity implements ViewPager.OnPageChangeListener {
    private Unbinder unbinder;
    @BindView(R.id.video_player_television)
    LiveVideoPlayer player;
    @BindView(R.id.tab_television)
    MyTabLayout tabLayout;
    @BindView(R.id.viewpager_television)
    LinViewpager viewpager;
    @BindView(R.id.img_television)
    ImageView logo;
    @BindView(R.id.txt_television)
    TextView titleTv;
    @BindView(R.id.txt_header_not_title)
    TextView navigationTitleTv;
    private LoadingDialog loading;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private String currentPlayUrl = "";
    private static final int SIZE_SCROLL_MIN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_television_station);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        navigationTitleTv.setText("电视台");
        initLoading();
        viewpager.addOnPageChangeListener(this);
        requestTelevisionClassify();
        initPlayer();
    }

    /**
     * 请求电视台列表
     */
    private void requestTelevisionClassify() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.postJson(Constant.CLASSIFY_STATION_TELEVISON)
                    .asResponseList(TelevisionClassifyEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            bindData2UI(list);
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        }
    }

    /**
     * 绑定数据
     *
     * @param data 数据源
     */
    private void bindData2UI(List<TelevisionClassifyEntity.DataBean> data) {
        FragmentManager manager = getSupportFragmentManager();
        TelevisionPageAdapter adapter = new TelevisionPageAdapter(manager, data);
        viewpager.setAdapter(adapter);
        viewpager.setIsCanScroll(true);
        tabLayout.setupWithViewPager(viewpager);
        if (data.size() > SIZE_SCROLL_MIN) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
    }

    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        player.getTitleTextView().setVisibility(View.GONE);
        player.getBackButton().setVisibility(View.GONE);
        orientationUtils = new OrientationUtils(this, player);
        orientationUtils.setEnable(false);
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(player.getThumbImageView())
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setCacheWithPlay(false)
                .setUrl(currentPlayUrl)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        orientationUtils.setEnable(true);
                        isPlay = true;
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener((view, lock) -> {
            if (orientationUtils != null) {
                orientationUtils.setEnable(!lock);
            }
        }).build(player);
        player.getFullscreenButton().setOnClickListener(v -> {
            orientationUtils.resolveByClick();
            player.startWindowFullscreen(TelevisionStationActivity.this, true, true);
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPlay && !isPause) {
            player.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    @Override
    protected void onPause() {
        if (null != player) {
            player.onVideoPause();
        }
        isPause = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (null != player) {
            player.onVideoResume();
        }
        isPause = false;
        super.onResume();
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
                if (TextUtils.equals("updateVideo", message.getLabel()) && !TextUtils.equals(currentPlayUrl, message.getContent())) {
                    player.setUp(message.getContent(), true, "");
                    player.startPlayLogic();
                    AppUtil.loadNewsImg(message.getPostPath(), logo);
                    titleTv.setText(message.getFrom());
                }
                currentPlayUrl = message.getContent();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (isPlay) {
            player.getCurrentPlayer().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        GSYVideoManager.releaseAllVideos();
        unbinder.unbind();
    }
}