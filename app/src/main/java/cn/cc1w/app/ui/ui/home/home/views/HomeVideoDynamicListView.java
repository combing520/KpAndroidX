package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.HomeVideoDynamicSubAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.home.home.MoreSmallVideoActivity;

/**
 * @author kpinfo
 * @date 2018/10/23
 * 首页小视频
 */
public class HomeVideoDynamicListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.VideoDynamicNewsHolder holder;
    private long lastTime;

    public HomeVideoDynamicListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.VideoDynamicNewsHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.titleTv.setText(entity.getType_name());
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(RecyclerView.HORIZONTAL);
            holder.recyclerView.setLayoutManager(manager);
            manager.setAutoMeasureEnabled(true);
            HomeVideoDynamicSubAdapter adapter = new HomeVideoDynamicSubAdapter();
            holder.recyclerView.setAdapter(adapter);
            if (holder.recyclerView.getAdapter() != null) {
                ((HomeVideoDynamicSubAdapter) holder.recyclerView.getAdapter()).setData(entity.getNews());
            }
            holder.btnMore.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String id = entity.getNews().get(0).getId();
                    if (!TextUtils.isEmpty(id)) {
                        Intent intent = new Intent();
                        intent.setClass(context, MoreSmallVideoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.TAG_ID, id);
                        context.startActivity(intent);
                    }
                }
                lastTime = currentTime;
            });
        }
    }
}