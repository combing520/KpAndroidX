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

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.entity.IntegralRankEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 积分排名
 *
 * @author kpinfo
 */
public class IntegralRankAdapter extends RecyclerView.Adapter<IntegralRankAdapter.ViewHolder> {
    private static final int SCORE_FIRST = 1;
    private static final int SCORE_SECOND = 2;
    private static final int SCORE_THIRD = 3;
    private final List<IntegralRankEntity.DataBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<IntegralRankEntity.DataBean> list) {
        dataSet.clear();
        dataSet.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param list 数据源
     */
    public void addData(List<IntegralRankEntity.DataBean> list) {
        dataSet.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 获取对应位置的条目
     *
     * @param pos 对应的位置
     * @return 对应的条目
     */
    public IntegralRankEntity.DataBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_integral_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        IntegralRankEntity.DataBean entity = getItem(position);
        if (null != entity) {
            if (entity.isIs_header()) {
                holder.spitView.setVisibility(View.VISIBLE);
            } else {
                holder.spitView.setVisibility(View.GONE);
            }
            if (entity.getRanking() <= 3) {
                holder.prizeImg.setVisibility(View.VISIBLE);
                holder.rankTv.setVisibility(View.GONE);
                if (entity.getRanking() == SCORE_FIRST) {
                    AppUtil.loadRes(R.mipmap.ic_rank_first, holder.prizeImg);
                } else if (entity.getRanking() == SCORE_SECOND) {
                    AppUtil.loadRes(R.mipmap.ic_rank_second, holder.prizeImg);
                } else if (entity.getRanking() == SCORE_THIRD) {
                    AppUtil.loadRes(R.mipmap.ic_rank_third, holder.prizeImg);
                }
            } else {
                holder.prizeImg.setVisibility(View.GONE);
                holder.rankTv.setVisibility(View.VISIBLE);
                holder.rankTv.setText(String.valueOf(entity.getRanking()));
            }
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.userAvatarImg);
            holder.totalScoreTv.setText(TextUtils.isEmpty(entity.getCurrcredit()) ? "" : entity.getCurrcredit());
            holder.usernameTv.setText(TextUtils.isEmpty(entity.getNickname()) ? "" : entity.getNickname());
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rankTv;
        private final ImageView prizeImg;
        private final RoundAngleImageView userAvatarImg;
        private final TextView usernameTv;
        private final TextView totalScoreTv;
        private final View spitView;

        public ViewHolder(View itemView) {
            super(itemView);
            rankTv = itemView.findViewById(R.id.txt_place_rank_integral);
            prizeImg = itemView.findViewById(R.id.img_prize_rank_integral);
            userAvatarImg = itemView.findViewById(R.id.img_avatar_rank_integral);
            usernameTv = itemView.findViewById(R.id.txt_username_rank_integral);
            totalScoreTv = itemView.findViewById(R.id.txt_score_total_rank_integral);
            spitView = itemView.findViewById(R.id.view_spit_rank_integral);
        }
    }
}
