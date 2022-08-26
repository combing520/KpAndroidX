package cn.cc1w.app.ui.widget.video;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import cn.cc1w.app.ui.R;

/**
 * 带封面的 VideoPlayer
 */
public class CoverVideoPlayer extends StandardGSYVideoPlayer {
    private ImageView mCoverImage;
    private String mCoverOriginUrl;
    private SeekBar seekBar;
    private int mCoverOriginId = 0;
    private int mDefaultRes;

    public CoverVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CoverVideoPlayer(Context context) {
        super(context);
    }

    public CoverVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = findViewById(R.id.thumbImage);
        seekBar = findViewById(R.id.progress);
        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover;
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
                                .placeholder(res)).load(url).into(mCoverImage);
    }

    public void loadCoverImageBy(int id, int res) {
        mCoverOriginId = id;
        mDefaultRes = res;
        mCoverImage.setImageResource(id);
    }

    @Override
    public int getEnlargeImageRes() {
        return R.drawable.custom_enlarge;
    }

    @Override
    public int getShrinkImageRes() {
        return R.drawable.custom_shrink;
    }

    @Override
    public ImageView getThumbImageView() {
        return mCoverImage;
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        CoverVideoPlayer coverVideoPlayer = (CoverVideoPlayer) gsyBaseVideoPlayer;
        if (mCoverOriginUrl != null) {
            coverVideoPlayer.loadCoverImage(mCoverOriginUrl, mDefaultRes);
        } else if (mCoverOriginId != 0) {
            coverVideoPlayer.loadCoverImageBy(mCoverOriginId, mDefaultRes);
        }
        return gsyBaseVideoPlayer;
    }


    @Override
    public GSYBaseVideoPlayer showSmallVideo(Point size, boolean actionBar, boolean statusBar) {
        //下面这里替换成你自己的强制转化
        CoverVideoPlayer coverVideoPlayer = (CoverVideoPlayer) super.showSmallVideo(size, actionBar, statusBar);
        coverVideoPlayer.mStartButton.setVisibility(GONE);
        coverVideoPlayer.mStartButton = null;
        return coverVideoPlayer;
    }

    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        CoverVideoPlayer landLayoutVideo = (CoverVideoPlayer) gsyVideoPlayer;
        landLayoutVideo.dismissProgressDialog();
        landLayoutVideo.dismissVolumeDialog();
        landLayoutVideo.dismissBrightnessDialog();
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
    }

    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        CoverVideoPlayer sf = (CoverVideoPlayer) from;
        CoverVideoPlayer st = (CoverVideoPlayer) to;
        st.mShowFullAnimation = sf.mShowFullAnimation;
    }

    /**
     * 退出window层播放全屏效果
     */
    @SuppressWarnings("ResourceType")
    @Override
    protected void clearFullscreenLayout() {
        if (!mFullAnimEnd) {
            return;
        }
        mIfCurrentIsFullscreen = false;
        int delay = 0;
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (mOrientationUtils != null) {
            delay = mOrientationUtils.backToProtVideo();
            mOrientationUtils.setEnable(false);
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener();
                mOrientationUtils = null;
            }
        }
        if (!mShowFullAnimation) {
            delay = 0;
        }
        final ViewGroup vp = (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        final View oldF = vp.findViewById(getFullId());
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            CoverVideoPlayer gsyVideoPlayer = (CoverVideoPlayer) oldF;
            gsyVideoPlayer.mIfCurrentIsFullscreen = false;
        }
        if (delay == 0) {
            backToNormal();
        } else {
            postDelayed(this::backToNormal, delay);
        }
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

    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
                mThumbImageViewLayout.setVisibility(INVISIBLE);
            }
        }
    }

    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽 ********************/

    protected boolean byStartedClick;

    @Override
    protected void onClickUiToggle(MotionEvent e) {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        byStartedClick = true;
        super.onClickUiToggle(e);

    }

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

    /**
     * 获取 seekBar 当前的播放百分比
     *
     * @return seekBar 的播放百分比
     */
    public int getSeekBarPos() {
        if (seekBar != null) {
            return seekBar.getProgress();
        } else {
            return 0;
        }
    }

    /**
     * 定义结束后的显示
     */
    @Override
    protected void changeUiToCompleteClear() {
        super.changeUiToCompleteClear();
        setTextAndProgress(0, true);
    }

    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
        setTextAndProgress(0, true);
    }
}
