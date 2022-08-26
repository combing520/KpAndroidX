package cn.cc1w.app.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.ItemShare;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * @author kpinfo
 * on 2020-12-15
 */
public class ShareInfoAdapter extends RecyclerView.Adapter<ShareInfoAdapter.ViewHolder> {
    private final List<ItemShare> mDataSet = new ArrayList<>();
    private OnItemClickListener mListener;
    private long mLastClickTime;

    public ShareInfoAdapter() {
        mLastClickTime = System.currentTimeMillis();
    }

    public void setData(List<ItemShare> list) {
        if (null != list && !list.isEmpty()) {
            mDataSet.clear();
            mDataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    public ItemShare getItem(int pos) {
        return mDataSet.get(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemShare item = getItem(position);
        AppUtil.loadRes(item.getResId(), holder.icon);
        holder.nameTv.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - mLastClickTime >= Constant.MIN_TIME_INTERVAL) {
                    mListener.onItemClick(v, position);
                }
                mLastClickTime = currentTime;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView nameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.share_item_img);
            nameTv = itemView.findViewById(R.id.share_item_txt);
        }
    }
}