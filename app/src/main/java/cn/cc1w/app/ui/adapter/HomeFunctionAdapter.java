package cn.cc1w.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
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

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.FunctionEntity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PaikewActivity;
import cn.cc1w.app.ui.ui.home.television.TelevisionStationActivity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;

/**
 * 首页用户中心 Adapter
 * @author kpinfo
 */
public class HomeFunctionAdapter extends RecyclerView.Adapter<HomeFunctionAdapter.ViewHolder> {
    private final List<FunctionEntity.ItemFunctionEntity> dataSet = new ArrayList<>();
    private final Context context;

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public HomeFunctionAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置数据
     *
     * @param list 数据集合
     */
    public void setData(List<FunctionEntity.ItemFunctionEntity> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    private HomeFunctionLongClickListener listener;

    /**
     * 添加长按事件
     *
     * @param listener 事件监听
     */
    public void addFunctionLongClickListener(HomeFunctionLongClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取数据集合
     */
    public List<FunctionEntity.ItemFunctionEntity> getDataSet() {
        return dataSet;
    }

    public FunctionEntity.ItemFunctionEntity getItem(int pos) {
        return dataSet.get(pos);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        FunctionEntity.ItemFunctionEntity entity = getItem(position);
        if (null != entity) {
            holder.titleTv.setText(TextUtils.isEmpty(entity.getName()) ? "" : entity.getName());
            AppUtil.loadAppsImg(entity.getPic_path(), holder.functionLogo);
            holder.itemView.setOnClickListener(v -> {
                String type = entity.getIn_type();
                if (null != context && !TextUtils.isEmpty(type)) {
                    Intent intent = new Intent();
                    if (TextUtils.equals("proto", type)) {
                        if (TextUtils.equals("broke", entity.getAction())) {
                            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                                intent.setClass(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("from", Constant.RECORD_PAIKEW);
                                context.startActivity(intent);
                                return;
                            }
                            if (null != SharedPreferenceUtil.getUserInfo()) {
                                if (TextUtils.isEmpty(SharedPreferenceUtil.getUserInfo().getMobile())) {
                                    intent.setClass(context, UpdateMobileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("number", "");
                                    context.startActivity(intent);
                                } else {
                                    intent.setClass(context, BrokeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        }
                        else if (TextUtils.equals("paikew", entity.getAction())) {
                            intent.setClass(context, PaikewActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else if (TextUtils.equals("TV", entity.getAction())) {
                            intent.setClass(context, TelevisionStationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    } else if (TextUtils.equals(Constant.TAG_URL, type)) {
                        if (!TextUtils.isEmpty(entity.getUrl())) {
                            intent.setClass(context, UrlDetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constant.TAG_URL, entity.getUrl());
                            intent.putExtra(Constant.TAG_TITLE, entity.getName());
                            intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(entity.getId()) ? "" : entity.getId());
                            intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(dataSet.get(position).getSummary()) ? "" : dataSet.get(position).getSummary());
                            context.startActivity(intent);
                        }
                    }
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                if (null != listener) {
                    listener.onItemLongClickListener(v, position);
                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final ImageView functionLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.txt_item_function);
            functionLogo = itemView.findViewById(R.id.img_item_function);
        }
    }

    public interface HomeFunctionLongClickListener {
        boolean onItemLongClickListener(View view, int position);
    }
}