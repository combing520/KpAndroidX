package cn.cc1w.app.ui.widget.video;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import cn.cc1w.app.ui.R;

/**
 * Created by kpinfo on 2018/12/7.
 * <p>
 * 直播播放器
 */

public class LiveVideoPlayer extends StandardGSYVideoPlayer {

    private ImageView mCoverImage;

//    private TextView currentTv;
//
//    private TextView totalTv;
//
//    private SeekBar seekBar;

    String mCoverOriginUrl;

    int mDefaultRes;

    public LiveVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public LiveVideoPlayer(Context context) {
        super(context);
    }

    public LiveVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = findViewById(R.id.thumbImage);

//        currentTv = findViewById(R.id.current);
//        seekBar = findViewById(R.id.progress);
//        totalTv = findViewById(R.id.total);

        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover2;
    }

    public void loadCoverImage(String url, int res) {
        mCoverOriginUrl = url;
        mDefaultRes = res;
        Glide.with(getContext().getApplicationContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                                .error(res)
                                .placeholder(res))
                .load(url)
                .into(mCoverImage);
    }


    @Override
    public ImageView getThumbImageView() {

        return mCoverImage;
    }



    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

    }

    /**
     * 显示控制器
     */
    public void showControl() {
//        currentTv.setVisibility(VISIBLE);
//        seekBar.setVisibility(VISIBLE);
//        totalTv.setVisibility(VISIBLE);
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        LiveVideoPlayer sampleCoverVideo = (LiveVideoPlayer) gsyBaseVideoPlayer;
        sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes);
        return gsyBaseVideoPlayer;
    }


    @Override
    public GSYBaseVideoPlayer showSmallVideo(Point size, boolean actionBar, boolean statusBar) {
        //下面这里替换成你自己的强制转化
        LiveVideoPlayer sampleCoverVideo = (LiveVideoPlayer) super.showSmallVideo(size, actionBar, statusBar);

        sampleCoverVideo.mStartButton.setVisibility(GONE);
        sampleCoverVideo.mStartButton = null;

        return sampleCoverVideo;
    }


    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽 ********************/
    @Override
    public void onSurfaceUpdated(Surface surface) {
        super.onSurfaceUpdated(surface);
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
            mThumbImageViewLayout.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return;
        }
        super.setViewShowState(view, visibility);
    }


    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽 ********************/

    protected boolean byStartedClick;

//    @Override
//    protected void onClickUiToggle() {
//        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
//            setViewShowState(mLockScreen, VISIBLE);
//            return;
//        }
//        byStartedClick = true;
//        super.onClickUiToggle();
//
//    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        byStartedClick = false;
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        Debuger.printfLog("Sample changeUiToPreparingShow");
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        Debuger.printfLog("Sample changeUiToPlayingBufferingShow");
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, INVISIBLE);
        }
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        Debuger.printfLog("Sample changeUiToPlayingShow");
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, INVISIBLE);
        }
    }

    @Override
    public void startAfterPrepared() {
        super.startAfterPrepared();
        Debuger.printfLog("Sample startAfterPrepared");
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        byStartedClick = true;
        super.onStartTrackingTouch(seekBar);
    }
}