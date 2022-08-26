package cn.cc1w.app.ui.ui.usercenter.broke;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.video.CoverVideoPlayer;
import cn.cc1w.app.ui.R;
/**
 * 爆料 视频播放详情
 * @author kpinfo
 */
public class BrokeVideoPlayDetailActivity extends CustomActivity {
    @BindView(R.id.player_video_broke)
    CoverVideoPlayer videoPlayer;
    private Unbinder unbinder;
    private String videoUrl;
    private String videoPostUrl;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broke_video_play_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarColor(R.color.colorBlack).statusBarDarkFont(false).init();
        initData();
        initVideoPlayer();
    }

    private void initData() {
        videoUrl = getIntent().getStringExtra("videoUrl");
        videoPostUrl = getIntent().getStringExtra("videoPostUrl");
    }

    /**
     * 播放视频
     */
    private void initVideoPlayer() {
        orientationUtils = new OrientationUtils(this, videoPlayer);
        orientationUtils.setEnable(false);
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
        if (NetUtil.isNetworkConnected(this)) {
            AppUtil.loadBigImg(videoPostUrl, videoPlayer.getThumbImageView());
            GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
            gsyVideoOption.setThumbImageView(videoPlayer.getThumbImageView())
                    .setIsTouchWiget(true)
                    .setRotateViewAuto(false)
                    .setLooping(true)
                    .setLockLand(false)
                    .setAutoFullWithSize(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(true)
                    .setUrl(videoUrl)
                    .setVideoTitle("")
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
            }).build(videoPlayer);
            videoPlayer.getFullscreenButton().setOnClickListener(v -> {
                orientationUtils.resolveByClick();
                videoPlayer.startWindowFullscreen(BrokeVideoPlayDetailActivity.this, true, true);
            });
            videoPlayer.startPlayLogic();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (null != videoPlayer) {
            videoPlayer.getCurrentPlayer().onVideoPause();
        }
        isPause = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (null != videoPlayer) {
            videoPlayer.getCurrentPlayer().onVideoResume(false);
        }
        isPause = false;
        super.onResume();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            videoPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        unbinder.unbind();
    }
}