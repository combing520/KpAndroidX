package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.HomePaikewPicAndVideoAdapter;
import cn.cc1w.app.ui.entity.HomeNewsEntity;


/**
 *
 * @author kpinfo
 * @date 2018/10/23
 * 首页拍客视频
 */
public class HomePaikewVideoListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.PaikewVideoHolder holder;

    public HomePaikewVideoListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.PaikewVideoHolder) h;
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.paikewVideoRecycleView.setLayoutManager(manager);
            HomePaikewPicAndVideoAdapter adapter = new HomePaikewPicAndVideoAdapter(context);
            adapter.setData(entity.getNews());
            holder.paikewVideoRecycleView.setAdapter(adapter);
        }
    }
}