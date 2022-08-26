package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cn.cc1w.app.ui.adapter.home.HomeFunctionAdapter;
import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.R;
/**
 *
 * @author kpinfo
 * @date 2018/10/23
 * 首页普通新闻
 */
public class HomeFunctionListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.FunctionHolder holder;
    private final HomeFunctionAdapter adapter;
    // 功能区 一行显示的条目
    private static final int CELL = 4;

    public HomeFunctionListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.FunctionHolder) h;
        adapter = new HomeFunctionAdapter();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            holder.functionList.setVisibility(View.VISIBLE);
            GridLayoutManager manager = new GridLayoutManager(activity, CELL);
            holder.functionList.setLayoutManager(manager);
            adapter.setData(entity.getNews());
            HomeNewsEntity.ItemHomeNewsEntity.NewsBean item = new HomeNewsEntity.ItemHomeNewsEntity.NewsBean();
            item.setName("全部");
            item.setResId(R.mipmap.ic_menu_all);
            adapter.addItem(entity.getNews().size(), item);
            holder.functionList.setAdapter(adapter);
        } else {
            holder.functionList.setVisibility(View.GONE);
        }
    }
}