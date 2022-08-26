package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.HomeNormalGroupSubAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;

/**
 *
 * @author kpinfo
 * @date 2018/10/23
 * 首页普通新闻
 */
public class HomeNewsGroupListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.NewsGroupHolder holder;
    private long lastTime;

    public HomeNewsGroupListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        this.context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.NewsGroupHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            HomeNormalGroupSubAdapter adapter = new HomeNormalGroupSubAdapter();
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(RecyclerView.HORIZONTAL);
            holder.recyclerView.setLayoutManager(manager);
            adapter.setData(entity.getNews());
            holder.recyclerView.setAdapter(adapter);
            holder.btnMore.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    String newsGroupId = entity.getNews().get(0).getNews_groud_id();
                    if (!TextUtils.isEmpty(newsGroupId)) {
                        Intent intent = new Intent();
                        intent.setClass(context, MoreNewsGroupListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.TAG_ID, newsGroupId);
                        context.startActivity(intent);
                    }
                }
                lastTime = currentTime;
            });
        }
    }
}