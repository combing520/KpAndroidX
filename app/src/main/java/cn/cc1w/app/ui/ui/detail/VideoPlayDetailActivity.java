package cn.cc1w.app.ui.ui.detail;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.video.CoverVideoPlayer;

/**
 * 视频详情播放
 *
 * @author kpinfo
 */
public class VideoPlayDetailActivity extends CustomActivity {
    @BindView(R.id.player_video_detail_play)
    CoverVideoPlayer videoPlayer;
    private Unbinder unbinder;
    private String videoPostUrl;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        iniData();
    }

    /**
     * 初始化数据
     */
    private void iniData() {
        String videoUrl = getIntent().getStringExtra("videoUrl");
        videoPostUrl = getIntent().getStringExtra("videoPostUrl");
        if (!TextUtils.isEmpty(videoUrl)) {
            initVideoPlayer(videoUrl);
        }
    }

    /**
     * 播放视频
     */
    private void initVideoPlayer(String videoUrl) {
        orientationUtils = new OrientationUtils(this, videoPlayer);
        orientationUtils.setEnable(false);
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
        if (NetUtil.isNetworkConnected(this)) {
            AppUtil.loadBigImg(videoPostUrl, videoPlayer.getThumbImageView());
            GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
            gsyVideoOption.setThumbImageView(videoPlayer.getThumbImageView())
                    .setIsTouchWiget(true).setRotateViewAuto(false).setLockLand(false)
                    .setAutoFullWithSize(true).setShowFullAnimation(false).setNeedLockFull(true)
                    .setUrl(videoUrl).setVideoTitle("")
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
                            if (null != videoPlayer) {
                                videoPlayer.getTitleTextView().setVisibility(View.GONE);
                                videoPlayer.getBackButton().setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects) {
                            super.onAutoComplete(url, objects);
                            if (null != videoPlayer) {
                                videoPlayer.getTitleTextView().setVisibility(View.GONE);
                                videoPlayer.getBackButton().setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onEnterFullscreen(String url, Object... objects) {
                            super.onEnterFullscreen(url, objects);
                            if (null != videoPlayer) {
                                videoPlayer.getTitleTextView().setVisibility(View.GONE);
                                videoPlayer.getBackButton().setVisibility(View.GONE);
                            }
                        }
                    }).setLockClickListener((view, lock) -> {
                if (orientationUtils != null) {
                    orientationUtils.setEnable(!lock);
                }
            }).build(videoPlayer);
            videoPlayer.getFullscreenButton().setOnClickListener(v -> {
                orientationUtils.resolveByClick();
                videoPlayer.startWindowFullscreen(VideoPlayDetailActivity.this, true, true);
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

    @OnClick({R.id.img_back_video_detail_play})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_video_detail_play) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (null != videoPlayer) {
            videoPlayer.getCurrentPlayer().onVideoPause();
        }
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        if (null != videoPlayer) {
            videoPlayer.getCurrentPlayer().onVideoResume(false);
        }
        super.onResume();
        isPause = false;
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
