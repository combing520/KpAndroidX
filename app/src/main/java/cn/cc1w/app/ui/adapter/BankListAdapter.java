package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.entity.ItemCardInfoEntity;
import cn.cc1w.app.ui.ui.usercenter.wallet.bank.BankInfoDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;

/**
 * 银行卡 Adapter
 *
 * @author kpinfo
 */
public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.ViewHolder> {
    private final List<ItemCardInfoEntity> dataSet = new ArrayList<>();

    /**
     * 设置数据
     *
     * @param list 数据源
     */
    public void setData(List<ItemCardInfoEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取对应位置条目
     *
     * @param pos 对应位置
     * @return 对应条目
     */
    public ItemCardInfoEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_bank_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        ItemCardInfoEntity entity = getItem(position);
        if (null != entity) {
            AppUtil.loadNetworkImg(entity.getBankIconPath(), holder.iconImg);
            AppUtil.loadNetworkImg(entity.getBankBigImgPath(), holder.bigImg);
            holder.bankNameTv.setText(entity.getBankName());
            holder.bankTypeTv.setText(entity.getBankType());
            holder.itemView.setOnClickListener(v -> {
                Context context = AppContext.getAppContext();
                if (null != context) {
                    Intent intent = new Intent();
                    intent.setClass(context, BankInfoDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView iconImg;
        private final RoundAngleImageView bigImg;
        private final TextView bankNameTv;
        private final TextView bankTypeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            iconImg = itemView.findViewById(R.id.img_item_bank_small_list);
            bigImg = itemView.findViewById(R.id.img_item_bank_list);
            bankNameTv = itemView.findViewById(R.id.txt_item_bank_name);
            bankTypeTv = itemView.findViewById(R.id.txt_item_bank_type);
        }
    }
}