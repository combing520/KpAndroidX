package cn.cc1w.app.ui.adapter.live;

import cn.cc1w.app.ui.R;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.ui.detail.live.HostDoubleView;
import cn.cc1w.app.ui.ui.detail.live.HostOtherView;
import cn.cc1w.app.ui.ui.detail.live.HostSingleView;
import cn.cc1w.app.ui.ui.detail.live.HostTripleView;
import cn.cc1w.app.ui.ui.detail.live.HostTxtView;
import cn.cc1w.app.ui.ui.detail.live.HostVideoView;
import cn.cc1w.app.ui.widget.video.LiveItemVideoPlayer;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 主播厅 消息
 *
 * @author kpinfo
 */

public class HostRoomMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<LiveHostEntity.LiveHostItemEntity> list = new ArrayList<>();
    private final LayoutInflater mInflater;
    private int lastSelectPos = 0;

    // 直播   图片显示样式
    //      1张：单独占一行
    //      2、4张 ：2张一行
    //      其它：9宫格
    // 纯文本
    private static final int TYPE_NEWS_TXT = 100;
    // 单图
    private static final int TYPE_IMG_SINGLE = 200;
    // 网格类型的图片  每行2个，或者4个
    private static final int TYPE_IMG_DOUBLE_GIRD = 300;
    // 网格类型的图片，每行3个
    private static final int TYPE_IMG_TRIPLE_TOTAL = 400;
    // 视频类型
    public static final int TYPE_VIDEO = 500;
    // 其他类型
    private static final int TYPE_OTHER = 600;
    private final Context context;

    public HostRoomMessageAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据
     *
     * @param data 数据源
     */
    public void setData(List<LiveHostEntity.LiveHostItemEntity> data) {
        if (null != data && !data.isEmpty()) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 加载数据
     */
    public void addData(List<LiveHostEntity.LiveHostItemEntity> data) {
        if (null != data && !data.isEmpty()) {
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加条目
     */
    public void addItem(int pos, LiveHostEntity.LiveHostItemEntity entity) {
        list.add(pos, entity);
        notifyDataSetChanged();
    }

    /**
     * 获取对应位置条目
     */
    public LiveHostEntity.LiveHostItemEntity getItem(int pos) {
        return list.get(pos);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_NEWS_TXT) {
            viewHolder = new HostTxtHolder(mInflater.inflate(R.layout.item_txt_live_host_recycle, parent, false));
        } else if (viewType == TYPE_IMG_SINGLE) {
            viewHolder = new HostSingleImgHolder(mInflater.inflate(R.layout.item_img_single_live_host_recycle, parent, false));
        } else if (viewType == TYPE_IMG_DOUBLE_GIRD) {
            viewHolder = new HostDoubleGirdHolder(mInflater.inflate(R.layout.item_img_grid_double_live_host_recycle, parent, false));
        } else if (viewType == TYPE_IMG_TRIPLE_TOTAL) {
            viewHolder = new HostTripleGirdHolder(mInflater.inflate(R.layout.item_img_grid_triple_live_host_recycle, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            viewHolder = new HostVideoHolder(mInflater.inflate(R.layout.item_video_live_host_recycle, parent, false));
        } else {
            viewHolder = new HostOtherHolder(mInflater.inflate(R.layout.item_txt_live_host_recycle, parent, false));
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        LiveHostEntity.LiveHostItemEntity entity = list.get(position);
        if (null != entity) {
            if (entity.getRes_type() == null) {
                return TYPE_NEWS_TXT;
            }
            if (TextUtils.equals("1", entity.getRes_type())) {
                List<LiveHostEntity.LiveHostItemEntity.JsonBean> jsonList = entity.getJson();
                if (null == jsonList || jsonList.isEmpty()) {
                    return TYPE_NEWS_TXT;
                }
                if (jsonList.size() == 1) {
                    return TYPE_IMG_SINGLE;
                } else if (jsonList.size() == 2 || jsonList.size() == 4) {
                    return TYPE_IMG_DOUBLE_GIRD;
                } else {  // 三图
                    return TYPE_IMG_TRIPLE_TOTAL;
                }
            } else if (TextUtils.equals("2", entity.getRes_type())) {
                return TYPE_VIDEO;
            } else {
                return TYPE_OTHER;
            }
        }
        return TYPE_OTHER;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        LiveHostEntity.LiveHostItemEntity entity = getItem(position);
        if (holder instanceof HostTxtHolder) {
            HostTxtView hostTxtView = new HostTxtView(context, position, holder, entity);
            hostTxtView.initView();
        } else if (holder instanceof HostSingleImgHolder) {
            HostSingleView singleView = new HostSingleView(context, position, holder, entity);
            singleView.initView();
        } else if (holder instanceof HostDoubleGirdHolder) {
            HostDoubleView doubleView = new HostDoubleView(context, position, holder, entity);
            doubleView.initView();
        } else if (holder instanceof HostTripleGirdHolder) {
            HostTripleView tripleView = new HostTripleView(context, position, holder, entity);
            tripleView.initView();
        } else if (holder instanceof HostVideoHolder) {
            HostVideoView hostVideoView = new HostVideoView(context, position, holder, entity);
            hostVideoView.initView();
        } else {
            HostOtherView hostOtherView = new HostOtherView(context, position, holder, entity);
            hostOtherView.initView();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 获取上次选中的 位置
     */
    public int getLastSelectPos() {
        return lastSelectPos;
    }

    /**
     * 设置上次选中的位置
     *
     * @param selectPos 上次选中的位置
     */
    public void setLastSelectPos(int selectPos) {
        lastSelectPos = selectPos;
    }

    /**
     * 纯文本
     */
    public static class HostTxtHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public TextView userNameTv;
        public TextView titleTv;
        public TextView describeTv;

        HostTxtHolder(View view) {
            super(view);
            timeTv = view.findViewById(R.id.txt_time_txt_live_host);
            avatarImg = view.findViewById(R.id.img_avatar_txt_live_host);
            userNameTv = view.findViewById(R.id.txt_name_txt_live_host);
            titleTv = view.findViewById(R.id.txt_title_txt_live_host);
            describeTv = view.findViewById(R.id.txt_describer_txt_live_host);
        }
    }

    /**
     * 单图
     */
    public static class HostSingleImgHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public TextView userNameTv;
        public TextView titleTv;
        public TextView describeTv;
        public ImageView postImg;

        HostSingleImgHolder(View view) {
            super(view);
            timeTv = view.findViewById(R.id.txt_time_img_single_live_host);
            avatarImg = view.findViewById(R.id.img_avatar_img_single_live_host);
            userNameTv = view.findViewById(R.id.txt_name_img_single_live_host);
            titleTv = view.findViewById(R.id.txt_title_img_single_live_host);
            describeTv = view.findViewById(R.id.txt_describer_img_single_live_host);
            postImg = view.findViewById(R.id.img_post_img_single_live_host);
        }
    }

    /**
     * 网格 2个的
     */
    public static class HostDoubleGirdHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public TextView userNameTv;
        public TextView titleTv;
        public TextView describeTv;
        public RecyclerView recyclerView;

        HostDoubleGirdHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.txt_time_img_grid_double_live_host);
            avatarImg = itemView.findViewById(R.id.img_avatar_img_grid_double_live_host);
            userNameTv = itemView.findViewById(R.id.txt_name_img_grid_double_live_host);
            titleTv = itemView.findViewById(R.id.txt_title_img_grid_double_live_host);
            describeTv = itemView.findViewById(R.id.txt_describer_img_grid_double_live_host);
            recyclerView = itemView.findViewById(R.id.txt_gallery_grid_double_live_host);
        }
    }

    /**
     * 网格 3个的
     */
    public static class HostTripleGirdHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public TextView userNameTv;
        public TextView titleTv;
        public TextView describeTv;
        public RecyclerView recyclerView;

        HostTripleGirdHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.txt_time_img_grid_triple_live_host);
            avatarImg = itemView.findViewById(R.id.img_avatar_img_grid_triple_live_host);
            userNameTv = itemView.findViewById(R.id.txt_name_img_grid_triple_live_host);
            titleTv = itemView.findViewById(R.id.txt_title_img_grid_triple_live_host);
            describeTv = itemView.findViewById(R.id.txt_describer_img_grid_triple_live_host);
            recyclerView = itemView.findViewById(R.id.txt_gallery_grid_triple_live_host);
        }
    }

    /**
     * 视频
     */
    public static class HostVideoHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public TextView userNameTv;
        public TextView titleTv;
        public TextView describeTv;
        public LiveItemVideoPlayer videoPlayer;
        public RelativeLayout container;

        HostVideoHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.txt_time_video_live_host);
            avatarImg = itemView.findViewById(R.id.img_avatar_video_live_host);
            userNameTv = itemView.findViewById(R.id.txt_name_video_live_host);
            titleTv = itemView.findViewById(R.id.txt_title_video_live_host);
            describeTv = itemView.findViewById(R.id.txt_describer_video_live_host);
            videoPlayer = itemView.findViewById(R.id.video_post_video_live_host);
            container = itemView.findViewById(R.id.relate_video_live_host);
        }
    }

    /**
     * 其他
     */
    public static class HostOtherHolder extends RecyclerView.ViewHolder {
        public TextView timeTv;
        public RoundAngleImageView avatarImg;
        public TextView userNameTv;
        public TextView titleTv;
        public TextView describeTv;

        HostOtherHolder(View view) {
            super(view);
            timeTv = view.findViewById(R.id.txt_time_txt_live_host);
            avatarImg = view.findViewById(R.id.img_avatar_txt_live_host);
            userNameTv = view.findViewById(R.id.txt_name_txt_live_host);
            titleTv = view.findViewById(R.id.txt_title_txt_live_host);
            describeTv = view.findViewById(R.id.txt_describer_txt_live_host);
        }
    }
}
