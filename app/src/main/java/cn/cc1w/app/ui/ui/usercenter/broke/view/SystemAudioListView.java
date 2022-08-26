package cn.cc1w.app.ui.ui.usercenter.broke.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

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
 * 系统音频的播放
 */
public class SystemAudioListView {
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.SystemAudioHolder holder;
    private final int mMaxWidth;
    private final int mMinWidth;

    public SystemAudioListView(Context context, RecyclerView.ViewHolder h, ItemBrokeEntity item, List<ItemBrokeEntity> dataSet) {
        holder = (BrokeAdapter.SystemAudioHolder) h;
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
            lp.width = (mMinWidth + (mMaxWidth - mMinWidth) / 60 * entity.getPlay_time());

            // 关闭其他地方的语音， 播放当前的语音
            holder.container.setOnClickListener(v -> {
                // 如果 用户的动画正在播放，则关闭
                if (null != Constant.IMG_AUDIO_PATH_PLAYING_USER && null != Constant.ANIMATION_BROKE_USER) {
                    LogUtil.e("用户动画正在播放  = " + (Constant.ANIMATION_BROKE_USER.isRunning()));
//                        if (Constant.ANIMATION_BROKE_USER.isRunning()) {
                    Constant.ANIMATION_BROKE_USER.stop();
//                        }
                    Constant.IMG_AUDIO_PATH_PLAYING_USER.setBackgroundResource(R.drawable.ic_voice_play3_broke_right);
                    Constant.ANIMATION_BROKE_USER = null;
                    Constant.IMG_AUDIO_PATH_PLAYING_USER = null;

//                    LogUtil.e("用户图片 = null ?" + (Constant.IMG_AUDIO_PATH_PLAYING_USER == null) + "  用户动画 = null ? " + (Constant.ANIMATION_BROKE_USER == null));
                }

                if (Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM != null && Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM == holder.animationView && VoiceplayerUtil.isPlaying()) {
                    VoiceplayerUtil.release();
                    Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.setBackgroundResource(R.drawable.ic_voice_play3_broke_left);
                    Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM = null;
                    return;
                }
                if (Constant.ANIMATION_BROKE_SYSTEM != null && Constant.ANIMATION_BROKE_SYSTEM.isRunning()) {
                    Constant.ANIMATION_BROKE_SYSTEM.stop();
                    Constant.ANIMATION_BROKE_SYSTEM = null;
                }
                if (Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM != null) {
                    Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.setBackgroundResource(R.drawable.ic_voice_play3_broke_left);
                    Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM = null;
                }

                Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM = holder.animationView;
                Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.setBackgroundResource(R.drawable.voice_play_send_anim);

                VoiceplayerUtil.play(entity.getAudio_path(), mp -> {
                    VoiceplayerUtil.release();
                    Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.setBackgroundResource(R.drawable.ic_voice_play3_broke_left);
                });

                if (null != Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM && null != Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.getBackground()) {
                    Constant.ANIMATION_BROKE_SYSTEM = (AnimationDrawable) Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.getBackground();
                    Constant.ANIMATION_BROKE_SYSTEM.start();
                }
            });
        }
    }
}