package cn.cc1w.app.ui.adapter.video_frame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;

/**
 * 视频帧
 *
 * @author kpinfo
 */
public class VideoFrameAdapter extends RecyclerView.Adapter<VideoFrameAdapter.ViewHolder> {
    private final List<PLVideoFrame> dataSet = new ArrayList<>();
    private final Drawable selectDrawableBg;
    private final Drawable normalDrawableBg;
    private final Context context;
    private int selectPos = 0;
    public FrameClickListener listener;

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public VideoFrameAdapter(Context context) {
        this.context = context;
        selectDrawableBg = ContextCompat.getDrawable(context, R.drawable.bg_container_red_rectangle_hollow);
        normalDrawableBg = ContextCompat.getDrawable(context, R.color.colorTransport);
    }

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setDataSet(List<PLVideoFrame> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setOnFrameClickListener(FrameClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置选中位置条目
     *
     * @param pos 选中位置
     */
    public void setSelectItem(int pos) {
        selectPos = pos;
        notifyDataSetChanged();
    }

    /**
     * 获取选中条目的位置
     *
     * @return 对应的位置
     */
    public int getSelectPos() {
        return selectPos;
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public PLVideoFrame getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frame_video_redact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        PLVideoFrame item = getItem(position);
        Bitmap bitmap = item.toBitmap();
        if (null != bitmap) {
            Bitmap bm = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            Glide.with(context).load(bm).into(holder.img);
        }
        if (position == selectPos) {
            holder.container.setBackground(selectDrawableBg);
        } else {
            holder.container.setBackground(normalDrawableBg);
        }
        holder.container.setOnClickListener(v -> {
            if (null != listener) {
                listener.onFrameChooseListener(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_frame);
            container = itemView.findViewById(R.id.ll_container_item_fram);
        }
    }

    public interface FrameClickListener {
        void onFrameChooseListener(int pos, View v);
    }
}