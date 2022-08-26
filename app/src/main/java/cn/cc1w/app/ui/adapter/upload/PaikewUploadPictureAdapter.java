package cn.cc1w.app.ui.adapter.upload;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luck.picture.lib.entity.LocalMedia;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 拍客上传的 adapter
 * @author kpinfo
 */
public class PaikewUploadPictureAdapter extends RecyclerView.Adapter<PaikewUploadPictureAdapter.ViewHolder> {
    private final List<LocalMedia> dataSet = new ArrayList<>();
    private static final String PATH = "file:///android_asset/ic_pic_paikew_upload.png";
    private OnItemClickListener listener;

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<LocalMedia> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            if (list.size() < Constant.CNT_MAX_PIC_UPLOAD) {
                LocalMedia media = new LocalMedia();
                media.setPath(PATH);
                addItem(media);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 添加条目
     *
     * @param item 待添加的条目
     */
    public void addItem(LocalMedia item) {
        dataSet.add(item);
        notifyDataSetChanged();
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public LocalMedia getItem(int pos) {
        return dataSet.get(pos);
    }

    /**
     * 获取数据集合
     *
     * @return 数据集合
     */
    public List<LocalMedia> getDataSet() {
        return dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic_upload_paikew_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        LocalMedia item = getItem(position);
        if (TextUtils.equals(PATH, item.getPath())) {
            holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        AppUtil.loadNewsImg(item.getPath(), holder.img);
        holder.itemView.setOnClickListener(v -> {
            if (null != listener) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_pic_upload_paikew);
        }
    }
}