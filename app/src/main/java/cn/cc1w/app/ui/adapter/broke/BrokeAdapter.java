package cn.cc1w.app.ui.adapter.broke;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.ui.usercenter.broke.view.SystemAudioListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.SystemImageListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.SystemLocationListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.SystemOtherListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.SystemTxtListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.SystemVideoListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.UserAudioListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.UserImageListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.UserLocationListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.UserOtherListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.UserTxtListView;
import cn.cc1w.app.ui.ui.usercenter.broke.view.UserVideoListView;
import cn.cc1w.app.ui.R;

/**
 * 爆料 adapter
 *
 * @author kpinfo
 */
public class BrokeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ItemBrokeEntity> dataSet = new ArrayList<>();
    private final Context context;
    private final LayoutInflater mInflater;
    private static final int TYPE_TXT_USER_BROKE = 100;
    private static final int TYPE_IMG_USER_BROKE = 200;
    private static final int TYPE_VIDEO_USER_BROKE = 300;
    private static final int TYPE_AUDIO_USER_BROKE = 400;
    private static final int TYPE_OTHER_USER_BROKE = 500;
    private static final int TYPE_LOCATION_USER_BROKE = 1100;
    private static final int TYPE_TXT_SYSTEM_BROKE = 600;
    private static final int TYPE_IMG_SYSTEM_BROKE = 700;
    private static final int TYPE_VIDEO_SYSTEM_BROKE = 800;
    private static final int TYPE_AUDIO_SYSTEM_BROKE = 900;
    private static final int TYPE_OTHER_SYSTEM_BROKE = 1000;
    private static final int TYPE_LOCATION_SYSTEM_BROKE = 1200;
    private static final String TYPE_MESSAGE_TXT = "1";
    private static final String TYPE_MESSAGE_PIC = "2";
    private static final String TYPE_MESSAGE_VIDEO = "3";
    private static final String TYPE_MESSAGE_AUDIO = "4";
    private static final String TYPE_MESSAGE_LOCATION = "5";
    private static final String TYPE_MESSAGE_SYSTEM = "left";
    private static final String TYPE_MESSAGE_USER = "right";
    private static final String RECONNECT = "brokeReConnect";
    private static final String LABEL_BROKE = "broke";
    private static final String INSERT_DATA = "insertData";
    private static final String LIST_UPDATE = "updateList";
    private static final String BROKE_REFRESH = "brokeRefresh";
    private static final String ITEM_UPDATE = "updateItem";
    private static final String CLEAR_EDIT_DATA = "clearEditData";

    public BrokeAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据
     */
    public void setData(List<ItemBrokeEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            EventBus.getDefault().post(new EventMessage(LABEL_BROKE, INSERT_DATA));
        }
    }

    /**
     * 添加数据
     */
    public void addDataSet(List<ItemBrokeEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            EventBus.getDefault().post(new EventMessage(LABEL_BROKE, LIST_UPDATE));
        }
    }

    /**
     * 刷新完成
     */
    public void setListRefreshComplete() {
        EventBus.getDefault().post(new EventMessage(BROKE_REFRESH, BROKE_REFRESH));
    }

    /**
     * 重连接
     */
    public void reConnect() {
        EventBus.getDefault().post(new EventMessage(RECONNECT, RECONNECT));
    }

    /**
     * 添加数据
     */
    public void addData(ItemBrokeEntity item) {
        if (null != item) {
            dataSet.add(item);
            EventBus.getDefault().post(new EventMessage(LABEL_BROKE, ITEM_UPDATE));
        }
    }

    /**
     * 清空输入框数字
     */
    public void clearEditData() {
        EventBus.getDefault().post(new EventMessage(CLEAR_EDIT_DATA, CLEAR_EDIT_DATA));
    }

    /**
     * 获取数据源
     */
    public List<ItemBrokeEntity> getDataSet() {
        return dataSet;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_TXT_USER_BROKE) {
            viewHolder = new UserTxtHolder(mInflater.inflate(R.layout.item_txt_broke_user_recycle, parent, false));
        } else if (viewType == TYPE_IMG_USER_BROKE) {
            viewHolder = new UserImgHolder(mInflater.inflate(R.layout.item_img_user_broke_recycle, parent, false));
        } else if (viewType == TYPE_VIDEO_USER_BROKE) {
            viewHolder = new UserVideoHolder(mInflater.inflate(R.layout.item_video_broke_user_recycle, parent, false));
        } else if (viewType == TYPE_AUDIO_USER_BROKE) {
            viewHolder = new UserAudioHolder(mInflater.inflate(R.layout.item_audio_user_broke_recycle, parent, false));
        } else if (viewType == TYPE_OTHER_USER_BROKE) {
            viewHolder = new UserOtherHolder(mInflater.inflate(R.layout.item_other_user_broke_recycle, parent, false));
        } else if (viewType == TYPE_LOCATION_USER_BROKE) {
            viewHolder = new UserLocationHolder(mInflater.inflate(R.layout.item_location_broke_user_recycle, parent, false));
        } else if (viewType == TYPE_IMG_SYSTEM_BROKE) {
            viewHolder = new SystemImgHolder(mInflater.inflate(R.layout.item_img_system_broke_recycle, parent, false));
        } else if (viewType == TYPE_VIDEO_SYSTEM_BROKE) {
            viewHolder = new SystemVideoHolder(mInflater.inflate(R.layout.item_video_broke_system_recycle, parent, false));
        } else if (viewType == TYPE_AUDIO_SYSTEM_BROKE) {
            viewHolder = new SystemAudioHolder(mInflater.inflate(R.layout.item_audio_system_broke_recycle, parent, false));
        } else if (viewType == TYPE_OTHER_SYSTEM_BROKE) {
            viewHolder = new SystemOtherHolder(mInflater.inflate(R.layout.item_other_system_broke_recycle, parent, false));
        } else if (viewType == TYPE_LOCATION_SYSTEM_BROKE) {
            viewHolder = new SystemLocationHolder(mInflater.inflate(R.layout.item_location_broke_system_recycle, parent, false));
        } else {
            viewHolder = new SystemTxtHolder(mInflater.inflate(R.layout.item_txt_broke_system_recycle, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        ItemBrokeEntity item = dataSet.get(position);
        if (holder instanceof UserTxtHolder) {
            UserTxtListView userTxtListView = new UserTxtListView(holder, item);
            userTxtListView.initView();
        } else if (holder instanceof UserImgHolder) {
            UserImageListView userImageListView = new UserImageListView(context, holder, item);
            userImageListView.initView();
        } else if (holder instanceof UserVideoHolder) {
            UserVideoListView userImageListView = new UserVideoListView(context, holder, item);
            userImageListView.initView();
        } else if (holder instanceof UserAudioHolder) {
            UserAudioListView userAudioListView = new UserAudioListView(context, holder, item, dataSet);
            userAudioListView.initView();
        } else if (holder instanceof UserLocationHolder) {
            UserLocationListView userLocationListView = new UserLocationListView(context, holder, item, dataSet);
            userLocationListView.initView();
        } else if (holder instanceof UserOtherHolder) {
            UserOtherListView userOtherListView = new UserOtherListView(holder, item);
            userOtherListView.initView();
        } else if (holder instanceof SystemTxtHolder) {
            SystemTxtListView systemTxtListView = new SystemTxtListView( holder, item);
            systemTxtListView.initView();
        } else if (holder instanceof SystemImgHolder) {
            SystemImageListView systemImageListView = new SystemImageListView(context, holder, item);
            systemImageListView.initView();
        } else if (holder instanceof SystemVideoHolder) {
            SystemVideoListView systemVideoListView = new SystemVideoListView(context, holder, item);
            systemVideoListView.initView();
        } else if (holder instanceof SystemAudioHolder) {
            SystemAudioListView systemAudioListView = new SystemAudioListView(context, holder, item, dataSet);
            systemAudioListView.initView();
        } else if (holder instanceof SystemLocationHolder) {
            SystemLocationListView systemLocationListView = new SystemLocationListView(context, position, holder, item, dataSet);
            systemLocationListView.initView();
        } else {
            SystemOtherListView systemOtherListView = new SystemOtherListView( holder, item);
            systemOtherListView.initView();
        }
    }

    @Override
    public int getItemViewType(int position) {
        ItemBrokeEntity item = dataSet.get(position);
        if (TextUtils.isEmpty(item.getType()) || TextUtils.isEmpty(item.getLocation())) {
            return TYPE_TXT_SYSTEM_BROKE;
        }
        if (TextUtils.equals(TYPE_MESSAGE_SYSTEM, item.getLocation())) {
            if (TextUtils.equals(TYPE_MESSAGE_TXT, item.getType())) {
                return TYPE_TXT_SYSTEM_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_PIC, item.getType())) {
                return TYPE_IMG_SYSTEM_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_VIDEO, item.getType())) {
                return TYPE_VIDEO_SYSTEM_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_AUDIO, item.getType())) {
                return TYPE_AUDIO_SYSTEM_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_LOCATION, item.getType())) {
                return TYPE_LOCATION_SYSTEM_BROKE;
            } else { // 其他类型
                return TYPE_OTHER_SYSTEM_BROKE;
            }
        } else if (TextUtils.equals(TYPE_MESSAGE_USER, item.getLocation())) {
            if (TextUtils.equals(TYPE_MESSAGE_TXT, item.getType())) {
                return TYPE_TXT_USER_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_PIC, item.getType())) {
                return TYPE_IMG_USER_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_VIDEO, item.getType())) {
                return TYPE_VIDEO_USER_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_AUDIO, item.getType())) {
                return TYPE_AUDIO_USER_BROKE;
            } else if (TextUtils.equals(TYPE_MESSAGE_LOCATION, item.getType())) {
                return TYPE_LOCATION_USER_BROKE;
            } else { // 其他类型
                return TYPE_TXT_USER_BROKE;
            }
        } else {
            return TYPE_OTHER_USER_BROKE;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * 文本 [用户]
     */
    public static class UserTxtHolder extends RecyclerView.ViewHolder {
        public TextView describeTv;
        public RoundAngleImageView avatarImg;

        UserTxtHolder(View view) {
            super(view);
            describeTv = view.findViewById(R.id.txt_describe_user_broke);
            avatarImg = view.findViewById(R.id.img_avatar_user_broke);
        }
    }

    /**
     * 图片 [用户]
     */
    public static class UserImgHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView avatarImg;
        public RoundAngleImageView pic;

        UserImgHolder(View view) {
            super(view);
            avatarImg = view.findViewById(R.id.img_avatar_post_broke);
            pic = view.findViewById(R.id.img_item_post_broke);
        }
    }

    /**
     * 用户 视频
     */
    public static class UserVideoHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView avatarImg;
        public RoundAngleImageView videoPostImg;

        UserVideoHolder(View view) {
            super(view);
            avatarImg = view.findViewById(R.id.img_avatar_video_user_broke);
            videoPostImg = view.findViewById(R.id.img_post_video_user_broke);
        }
    }

    /**
     * 用户 音频
     */
    public static class UserAudioHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public LinearLayout container;
        public View animationView;

        UserAudioHolder(View view) {
            super(view);
            timeTv = view.findViewById(R.id.txt_time_voice_user_broke);
            avatarImg = view.findViewById(R.id.img_avatar_voice_user_broke);
            container = view.findViewById(R.id.ll_audio_user_broke);
            animationView = view.findViewById(R.id.img_animation_voice_user_broke);
        }
    }

    /**
     * 用户 其他
     */
    public static class UserOtherHolder extends RecyclerView.ViewHolder {
        public TextView describeTv;
        public RoundAngleImageView avatarImg;

        UserOtherHolder(View view) {
            super(view);
            describeTv = view.findViewById(R.id.txt_describe_other_user_broke);
            avatarImg = view.findViewById(R.id.img_avatar_other_user_broke);
        }
    }

    /**
     * 用户 定位
     */
    public static class UserLocationHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView userAvatarImg;
        public ImageView mapImg;
        public TextView address;

        UserLocationHolder(View view) {
            super(view);
            userAvatarImg = view.findViewById(R.id.img_avatar_location_user_broke);
            mapImg = view.findViewById(R.id.map_location_user_broke);
            address = view.findViewById(R.id.txt_address_location_user_broke);
        }
    }

    /**
     * 系统 文本
     */
    public static class SystemTxtHolder extends RecyclerView.ViewHolder {
        public TextView describeTv;
        public RoundAngleImageView avatarImg;

        SystemTxtHolder(View view) {
            super(view);
            describeTv = view.findViewById(R.id.txt_describe_system_broke);
            avatarImg = view.findViewById(R.id.img_avatar_system_broke);
        }
    }

    /**
     * 图片 [系统]
     */
    public static class SystemImgHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView avatarImg;
        public RoundAngleImageView pic;

        SystemImgHolder(View view) {
            super(view);
            avatarImg = view.findViewById(R.id.img_avatar_post_system_broke);
            pic = view.findViewById(R.id.img_item_post_system_broke);
        }
    }

    /**
     * 系统 视频
     */
    public static class SystemVideoHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView avatarImg;
        public RoundAngleImageView videoPostImg;

        SystemVideoHolder(View view) {
            super(view);
            avatarImg = view.findViewById(R.id.img_avatar_video_system_broke);
            videoPostImg = view.findViewById(R.id.img_post_video_system_broke);
        }
    }

    /**
     * 系统 音频
     */
    public static class SystemAudioHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public View animationView;
        public LinearLayout container;

        SystemAudioHolder(View view) {
            super(view);
            timeTv = view.findViewById(R.id.txt_time_voice_system_broke);
            avatarImg = view.findViewById(R.id.img_avatar_voice_system_broke);
            animationView = view.findViewById(R.id.img_animation_voice_system_broke);
            container = view.findViewById(R.id.ll_audio_system_broke);
        }
    }

    /**
     * 系统 其他
     */
    public static class SystemOtherHolder extends RecyclerView.ViewHolder {
        public TextView describeTv;
        public RoundAngleImageView avatarImg;

        SystemOtherHolder(View view) {
            super(view);
            describeTv = view.findViewById(R.id.txt_describe_other_system_broke);
            avatarImg = view.findViewById(R.id.img_avatar_other_system_broke);
        }
    }

    /**
     * 系统定位
     */
    public static class SystemLocationHolder extends RecyclerView.ViewHolder {
        public RoundAngleImageView userAvatarImg;
        public ImageView mapImg;
        public TextView address;

        SystemLocationHolder(View view) {
            super(view);
            userAvatarImg = view.findViewById(R.id.img_avatar_location_system_broke);
            mapImg = view.findViewById(R.id.map_location_system_broke);
            address = view.findViewById(R.id.txt_address_location_system_broke);
        }
    }
}