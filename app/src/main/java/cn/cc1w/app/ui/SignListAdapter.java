package cn.cc1w.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.SignInfoEntity;

/**
 * @author kpinfo
 * @date 2018/11/28
 * 签到列表
 */
public class SignListAdapter extends RecyclerView.Adapter<SignListAdapter.ViewHolder> {
    private final List<SignInfoEntity.SignInfo.SigninBean> dataSet = new ArrayList<>();
    private final Drawable signedDrawable;
    private final Drawable unSignDrawable;
    private int currentSignPos = 0;

    public SignListAdapter(Context context) {
        signedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_container_ball_select);
        unSignDrawable = ContextCompat.getDrawable(context, R.drawable.bg_container_ball_normal);
    }

    /**
     * 设置列表数据
     *
     * @param list 数据源
     */
    public void setData(List<SignInfoEntity.SignInfo.SigninBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 设置当前签到的日期
     *
     * @param pos 签到的日期
     */
    public void setSignPos(int pos) {
        currentSignPos = pos;
        notifyDataSetChanged();
    }

    /**
     * 获取当前签到的位置（天数）
     *
     * @return 签到的天数
     */
    public int getSignPos() {
        return currentSignPos;
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public SignInfoEntity.SignInfo.SigninBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sign_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        SignInfoEntity.SignInfo.SigninBean entity = getItem(position);
        if (null != entity) {
            holder.scoreTv.setText(TextUtils.isEmpty(entity.getCredit()) ? "0" : ("+".concat(entity.getCredit())));
            if (position < currentSignPos) {
                holder.scoreTv.setBackground(signedDrawable);
                holder.scoreTv.setTextColor(Color.parseColor("#E8382F"));
            } else {
                holder.scoreTv.setBackground(unSignDrawable);
                holder.scoreTv.setTextColor(Color.parseColor("#666666"));
            }
            holder.describeTv.setText(TextUtils.isEmpty(entity.getRemark()) ? "" : entity.getRemark());
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView scoreTv;
        private final TextView describeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            scoreTv = itemView.findViewById(R.id.txt_score_sign_integral);
            describeTv = itemView.findViewById(R.id.txt_score_describe_integral);
        }
    }
}