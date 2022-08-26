package cn.cc1w.app.ui.widget.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.jzvd.JzvdStd;

/**
 * 拍客视频处理中，预览视频
 */

public class PaikewPreviewVideoPlayer extends JzvdStd {

    public PaikewPreviewVideoPlayer(Context context) {
        super(context);
    }

    public PaikewPreviewVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == cn.jzvd.R.id.fullscreen) {
            if (state == SCREEN_FULLSCREEN) {
                //click quit fullscreen
                LogUtil.d("拍客条目退出全屏");
            } else {
                //click goto fullscreen
                LogUtil.e("拍客条目进入全屏");
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_layout_live;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }


    @Override
    public void startVideo() {
        super.startVideo();
    }

    //onState 代表了播放器引擎的回调，播放视频各个过程的状态的回调
    @Override
    public void onStateNormal() {
        super.onStateNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
    }

    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        startVideo();
    }

    //changeUiTo 真能能修改ui的方法
    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
    }

    @Override
    public void changeUiToPreparing() {
        super.changeUiToPreparing();
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
    }

    @Override
    public void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
    }

    @Override
    public void changeUiToPauseShow() {
        super.changeUiToPauseShow();
    }

    @Override
    public void changeUiToPauseClear() {
        super.changeUiToPauseClear();
    }

    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();
    }

    @Override
    public void changeUiToError() {
        super.changeUiToError();
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }

}