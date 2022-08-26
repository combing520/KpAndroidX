package cn.cc1w.app.ui.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.ItemPhotoRecordEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 拍客中的 【照片】Adapter
 * @author kpinfo
 */
public class  RecordPhotoAdapter extends RecyclerView.Adapter<RecordPhotoAdapter.ViewHolder> {
    private final List<ItemPhotoRecordEntity.DataBean> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemPhotoRecordEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 追加数据
     *
     * @param list 数据源
     */
    public void addData(List<ItemPhotoRecordEntity.DataBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public ItemPhotoRecordEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_photo_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemPhotoRecordEntity.DataBean entity = getItem(position);
        AppUtil.loadSplashImg(entity.getCover(), holder.pathImg);
        AppUtil.loadAvatarImg(entity.getHead_pic_path(), holder.avatarImg);
        holder.userNameTv.setText(TextUtils.isEmpty(entity.getNickname()) ? "" : entity.getNickname());
        holder.priseCntTv.setText(String.valueOf(entity.getPraise_number()).concat("赞"));
        holder.container.setOnClickListener(rippleView -> {
            if (null != listener) {
                listener.onItemClick(rippleView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public List<ItemPhotoRecordEntity.DataBean> getList() {
        return dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView pathImg;
        private final RoundAngleImageView avatarImg;
        private final TextView userNameTv;
        private final TextView priseCntTv;
        private final ConstraintLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            pathImg = itemView.findViewById(R.id.img_path_record_photo);
            avatarImg = itemView.findViewById(R.id.img_avatar_record_photo);
            userNameTv = itemView.findViewById(R.id.item_username_record_photo);
            priseCntTv = itemView.findViewById(R.id.item_cnt_parse_record_photo);
            container = itemView.findViewById(R.id.container_record_photo);
        }
    }
}