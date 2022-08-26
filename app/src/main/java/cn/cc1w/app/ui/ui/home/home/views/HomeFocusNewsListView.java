package cn.cc1w.app.ui.ui.home.home.views;

import java.util.List;

import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnPageChangeListener;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cn.cc1w.app.ui.adapter.home.bannerAdapter.HomeFocusItemAdapter;
import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;

/**
 *
 * @author kpinfo
 * @date 2018/10/23
 * 首页普通新闻
 */
public class HomeFocusNewsListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.NewsFocusHolder holder;
    private long lastTime;

    public HomeFocusNewsListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        this.context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.NewsFocusHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            initBanner(holder.banner, entity.getNews());
        } else {
            holder.banner.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化 Banner相关信息
     */
    private void initBanner(Banner banner, final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> list) {
        holder.titleTv.setText(list.get(0).getTitle());
        banner.setAdapter(new HomeFocusItemAdapter(list)).setIndicator(new CircleIndicator(context));
        banner.setOnBannerListener((data, position) -> {
            long currentTime = System.currentTimeMillis();
            if(currentTime -lastTime >= Constant.MIN_TIME_INTERVAL){
                IntentUtil.startActivity2DetailWithEntity(context, list.get(position));
            }
            lastTime = currentTime;
            LogUtil.d("OnBannerClick  position = "+ position );
        });
        banner.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                holder.titleTv.setText(list.get(position).getTitle());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}