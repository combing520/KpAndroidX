package cn.cc1w.app.ui.ui.usercenter.broke.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;

import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;


import java.util.List;

import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.ui.usercenter.broke.VoiceplayerUtil;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.R;

/**
 * @author kpinfo
 * @date 2018/11/12
 * <p>
 * 用户音频播放
 */
public class UserAudioListView {
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.UserAudioHolder holder;
    private final int mMaxWidth; // 最大宽度
    private final int mMinWidth; // 最小宽度

    public UserAudioListView(Context context, RecyclerView.ViewHolder h, ItemBrokeEntity item, List<ItemBrokeEntity> dataSet) {
        holder = (BrokeAdapter.UserAudioHolder) h;
        entity = item;
        mMaxWidth = AppUtil.dip2px(context, 200);
        mMinWidth = AppUtil.dip2px(context, 80);
    }


    public void initView() {
        if (null != entity) {
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.avatarImg);
            holder.timeTv.setText((entity.getPlay_time()) + "''");
            // 设置长度
            ViewGroup.LayoutParams lp = holder.container.getLayoutParams();
            lp.width = (int) (mMinWidth + (mMaxWidth - mMinWidth) / 60 * entity.getPlay_time());
            // 关闭其他地方的语音， 播放当前的语音
            holder.container.setOnClickListener(v -> {
                if (null != Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM && null != Constant.ANIMATION_BROKE_SYSTEM) {
                    LogUtil.e("系统动画正在播放  = " + (Constant.ANIMATION_BROKE_SYSTEM.isRunning()));
//                        if (Constant.ANIMATION_BROKE_SYSTEM.isRunning()) {
                    Constant.ANIMATION_BROKE_SYSTEM.stop();
//                        }
                    Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.setBackgroundResource(R.drawable.ic_voice_play3_broke_left);
                    Constant.ANIMATION_BROKE_SYSTEM = null;
                    Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM = null;
                    LogUtil.e("系统图片 = null ?" + (Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM == null) + "  系统动画 = null ? " + (Constant.ANIMATION_BROKE_SYSTEM == null));
                }

                if (Constant.IMG_AUDIO_PATH_PLAYING_USER != null && Constant.IMG_AUDIO_PATH_PLAYING_USER == holder.animationView && VoiceplayerUtil.isPlaying()) {
                    VoiceplayerUtil.release();
                    Constant.IMG_AUDIO_PATH_PLAYING_USER.setBackgroundResource(R.drawable.ic_voice_play3_broke_right);
                    Constant.IMG_AUDIO_PATH_PLAYING_USER = null;
                    return;
                }
                if (Constant.ANIMATION_BROKE_USER != null && Constant.ANIMATION_BROKE_USER.isRunning()) {
                    Constant.ANIMATION_BROKE_USER.stop();
                    Constant.ANIMATION_BROKE_USER = null;
                }
                if (Constant.IMG_AUDIO_PATH_PLAYING_USER != null) {
                    Constant.IMG_AUDIO_PATH_PLAYING_USER.setBackgroundResource(R.drawable.ic_voice_play3_broke_right);
                    Constant.IMG_AUDIO_PATH_PLAYING_USER = null;
                }

                Constant.IMG_AUDIO_PATH_PLAYING_USER = holder.animationView;

                VoiceplayerUtil.play(entity.getAudio_path(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        VoiceplayerUtil.release();
                        Constant.IMG_AUDIO_PATH_PLAYING_USER.setBackgroundResource(R.drawable.ic_voice_play3_broke_right);
                    }
                });

                Constant.IMG_AUDIO_PATH_PLAYING_USER.setBackgroundResource(R.drawable.voice_play_receive_anim);

                if (null != Constant.IMG_AUDIO_PATH_PLAYING_USER && null != Constant.IMG_AUDIO_PATH_PLAYING_USER.getBackground()) {
                    Constant.ANIMATION_BROKE_USER = (AnimationDrawable) Constant.IMG_AUDIO_PATH_PLAYING_USER.getBackground();
                    Constant.ANIMATION_BROKE_USER.start();

                }
            });
        }
    }
}

