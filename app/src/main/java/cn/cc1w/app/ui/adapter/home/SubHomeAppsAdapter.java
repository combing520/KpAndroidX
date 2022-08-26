package cn.cc1w.app.ui.adapter.home;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;

import org.greenrobot.eventbus.EventBus;


import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.apps.AppDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import rxhttp.RxHttp;

/**
 * 首页中的 应用号的adapter
 *
 * @author kpinfo
 */
public class SubHomeAppsAdapter extends RecyclerView.Adapter<SubHomeAppsAdapter.ViewHolder> {
    private final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> dataSet = new ArrayList<>();
    private final LifecycleOwner owner;

    public SubHomeAppsAdapter(LifecycleOwner owner) {
        this.owner = owner;
    }

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
     * 获取对应位置条目
     */
    public HomeNewsEntity.ItemHomeNewsEntity.NewsBean getItem(int pos) {
        return dataSet.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_apps_home_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HomeNewsEntity.ItemHomeNewsEntity.NewsBean entity = getItem(position);
        AppUtil.loadNewsGroupImg(entity.getLogo_pic_path(), holder.appsLogoImg);
        if (entity.isAttention()) {
            AppUtil.loadRes(R.mipmap.ic_choose, holder.addOrDeleteAppsImg);
        } else {
            AppUtil.loadRes(R.mipmap.ic_add, holder.addOrDeleteAppsImg);
        }
        holder.titleTv.setText(entity.getName());
        holder.describeTv.setText(entity.getSummary());
        holder.list.setLayoutManager(new LinearLayoutManager(AppContext.getAppContext()));
        SubHomeChildAppsAdapter adapter = new SubHomeChildAppsAdapter();
        adapter.setData(entity.getNews());
        holder.list.setAdapter(adapter);
        holder.container.setOnClickListener(v -> {
            Context context = AppContext.getAppContext();
            if (null != context) {
                Intent intent = new Intent();
                intent.setClass(context, AppDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.TAG_ID, entity.getId());
                context.startActivity(intent);
            }
        });
        holder.appsLogoImg.setOnClickListener(v -> {
            if (holder.list.getVisibility() == View.VISIBLE) {
                holder.list.setVisibility(View.GONE);
            } else {
                holder.list.setVisibility(View.VISIBLE);
            }
        });
        holder.addOrDeleteAppsImg.setOnClickListener(v -> {
            if (!entity.isAttention()) {
                RxHttp.postJson(Constant.ADD_APPS).add("cw_app_id", entity.getId())
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(owner))
                        .subscribe(data -> {
                            entity.setAttention(true);
                            notifyDataSetChanged();
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id(), "home"));
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                        }, (OnError) error -> {
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(v.getContext(), LoginActivity.class);
                            }
                            ToastUtil.showShortToast(error.getErrorMsg());
                        });
            } else {
                // 取消关注
                RxHttp.postJson(Constant.APPS_FOCUS_CANCEL).add("cw_app_id", entity.getId())
                        .asMsgResponse(MsgResonse.class)
                        .as(RxLife.asOnMain(owner))
                        .subscribe(data -> {
                            entity.setAttention(false);
                            notifyDataSetChanged();
                            EventBus.getDefault().post(new EventMessage(Constant.STATE_APPS_UPDATE, entity.getGroup_id(), "home"));
                            if (data != null) {
                                ToastUtil.showShortToast(data.getMessage());
                            }
                        }, (OnError) error -> {
                            if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                AppUtil.doUserLogOut();
                                IntentUtil.startActivity(v.getContext(), LoginActivity.class);
                            }
                            ToastUtil.showShortToast(error.getErrorMsg());
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView appsLogoImg;
        private final TextView titleTv;
        private final TextView describeTv;
        private final ImageView addOrDeleteAppsImg;
        private final RecyclerView list;
        private final LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            appsLogoImg = itemView.findViewById(R.id.img_item_logo_sub_apps_home);
            titleTv = itemView.findViewById(R.id.txt_item_title_sub_apps_home);
            describeTv = itemView.findViewById(R.id.txt_item_describe_sub_apps_home);
            addOrDeleteAppsImg = itemView.findViewById(R.id.img_item_add_sub_apps_home);
            list = itemView.findViewById(R.id.list_child_apps_home);
            container = itemView.findViewById(R.id.ll_container_sub_apps_detail);
        }
    }
}