package cn.cc1w.app.ui.adapter.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

import cn.cc1w.app.ui.R;

/**
 * 滤镜
 *
 * @author kpinfo
 */
public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {
    private final PLBuiltinFilter[] filters;
    private final Context context;
    private FilterClickListener listener;
    private static final String[] CHINESE = new String[]{
            "无", "回忆", "宝橘", "糖果", "初雪", "黑白", "梦幻", "典雅", "朦胧", "时尚",
            "愉悦", "灰调", "哈瓦娜", "欢欣", "秋意", "法式风情", "温柔", "柔雾", "标准", "拍立得",
            "蜜粉", "打印", "紫藤", "在路上", "绯红", "复古", "阳光", "美味", "土耳其", "华尔兹", "夕阳",};

    public FilterListAdapter(Context context, PLBuiltinFilter[] filters) {
        this.context = context;
        this.filters = filters;
    }

    public PLBuiltinFilter getItem(int pos) {
        return filters[pos];
    }

    public void setOnFilterClickListener(FilterClickListener listener) {
        this.listener = listener;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        if (position == 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open("none.png"));
                if (null != bitmap) {
                    Glide.with(context).load(bitmap).into(holder.img);
                    holder.tv.setText("无");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            final PLBuiltinFilter filter = filters[position];
            holder.tv.setText(getChineseByEnglish(position));
            try {
                InputStream is = context.getAssets().open(filter.getAssetFilePath());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Glide.with(context).load(bitmap).into(holder.img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != v) {
                listener.onFilterClickListener(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters == null ? 0 : (filters.length);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item_filter);
            tv = itemView.findViewById(R.id.txt_item_filter);
        }
    }

    /**
     * 通过对应的 英文查找到对应的 中文
     */
    private String getChineseByEnglish(int pos) {
        return CHINESE[pos];
    }

    /**
     * 滤镜条目被点击
     */
    public interface FilterClickListener {
        void onFilterClickListener(int pos, View v);
    }
}