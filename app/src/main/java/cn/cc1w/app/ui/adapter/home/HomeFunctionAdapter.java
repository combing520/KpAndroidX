package cn.cc1w.app.ui.adapter.home;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.home.home.UserFunctionActivity;
import cn.cc1w.app.ui.ui.home.television.TelevisionStationActivity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;

/**
 * 首页 功能 Adapter
 *
 * @author kpinfo
 */
public class HomeFunctionAdapter extends RecyclerView.Adapter<HomeFunctionAdapter.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> dataSet = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setData(List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> list) {
        if (null != list && !list.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 给对应位置添加条目
     */
    public void addItem(int pos, HomeNewsEntity.ItemHomeNewsEntity.NewsBean item) {
        dataSet.add(pos, item);
        int startPos = dataSet.size();
        notifyDataSetChanged();
    }

    public HomeNewsEntity.ItemHomeNewsEntity.NewsBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HomeNewsEntity.ItemHomeNewsEntity.NewsBean entity = getItem(position);
        holder.titleTv.setText(entity.getName());
        if (entity.getResId() == 0) {
            AppUtil.loadAppsImg(entity.getPic_path(), holder.functionImg);
        } else {
            AppUtil.loadRes(entity.getResId(), holder.functionImg);
        }
        holder.container.setOnClickListener(rippleView -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                if (TextUtils.equals("全部", entity.getName())) {
                    intent.setClass(context, UserFunctionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return;
                }
                String type = entity.getIn_type();
                if (TextUtils.equals(Constant.TAG_URL, type)) {
                    if (!TextUtils.isEmpty(entity.getUrl())) {
                        intent.setClass(context, UrlDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.TAG_URL, entity.getUrl());
                        intent.putExtra(Constant.TAG_TITLE, entity.getName());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(entity.getId()) ? "" : entity.getId());
                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(entity.getSummary()) ? "" : entity.getSummary());
                        context.startActivity(intent);
                    }
                } else if (TextUtils.equals("proto", type)) {
                    if (TextUtils.equals("TV", entity.getAction())) {
                        intent.setClass(context, TelevisionStationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (TextUtils.equals("broke", entity.getAction())) {
                        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                            intent.setClass(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("from", Constant.RECORD_BROKE);
                            context.startActivity(intent);
                            return;
                        }
                        if (null != SharedPreferenceUtil.getUserInfo()) {
                            LogUtil.e("用户信息 = " + SharedPreferenceUtil.getUserInfo().toString());
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
                    } else if (TextUtils.equals("paikew", entity.getAction())) {
                        EventBus.getDefault().post(new EventMessage("showPaikewTab", "showPaikewTab"));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView functionImg;
        private final TextView titleTv;
        private final RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            functionImg = itemView.findViewById(R.id.img_item_function);
            titleTv = itemView.findViewById(R.id.txt_item_function);
            container = itemView.findViewById(R.id.container_item_function);
        }
    }
}