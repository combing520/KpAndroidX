package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.youth.banner.Banner;
import com.youth.banner.listener.OnPageChangeListener;
import java.util.List;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.adapter.home.bannerAdapter.HomeUrlItemAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.home.home.MoreActiveListActivity;

/**
 * @author kpinfo
 * @date 2018/10/23
 * 首页普通新闻
 */
public class HomeUrlListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.UrlHolder holder;
    private int currentPos;
    private long lastTime;

    public HomeUrlListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.UrlHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            initBanner(holder.banner, entity.getNews());
            holder.moreImageView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    HomeNewsEntity.ItemHomeNewsEntity.NewsBean newsBean = entity.getNews().get(currentPos);
                    if (null != newsBean) {
                        String id = newsBean.getId();
                        if (!TextUtils.isEmpty(id)) {
                            Intent intent = new Intent();
                            intent.setClass(activity, MoreActiveListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constant.TAG_ID, id);
                            activity.startActivity(intent);
                        }
                    }
                }
                lastTime = currentTime;
            });
        } else {
            holder.banner.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化 Banner相关信息
     */
    private void initBanner(Banner banner, final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> list) {
        banner.setAdapter(new HomeUrlItemAdapter(list));
        banner.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPos = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        banner.setOnBannerListener((data, position) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                String url = list.get(position).getUrl();
                String title = list.get(position).getTitle();
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, UrlDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.TAG_URL, url);
                    intent.putExtra(Constant.TAG_TITLE, TextUtils.isEmpty(title) ? "" : title);
                    intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(list.get(position).getId()) ? "" : list.get(position).getId());
                    intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(list.get(position).getSummary()) ? "" : list.get(position).getSummary());
                    activity.startActivity(intent);
                }
            }
            lastTime = currentTime;
        });
    }
}