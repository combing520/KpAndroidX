package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnPageChangeListener;
import java.util.List;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * 焦点 View
 *
 * @author kpinfo
 */
public class FocusListView {
    private final Context context;
    private final NewsEntity.DataBean list;
    private final AppsDetailNewsAdapter.FocusNewsHolder holder;
    private long lastTime;

    public FocusListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        context = ac;
//        int index = i;
        list = l;
        holder = (AppsDetailNewsAdapter.FocusNewsHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != list && null != list.getNews() && !list.getNews().isEmpty()) {
            initBanner(holder.banner, list.getNews());
        } else {
            holder.banner.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化 Banner相关信息
     */
    private void initBanner(Banner banner, final List<NewsEntity.DataBean.ItemNewsEntity> list) {
        banner.setAdapter(new BannerImageAdapter<NewsEntity.DataBean.ItemNewsEntity>(list) {
            @Override
            public void onBindView(BannerImageHolder holder, NewsEntity.DataBean.ItemNewsEntity data, int position, int size) {
                AppUtil.loadBannerImg( data.getPic_path(), holder.imageView);
            }
        });
        holder.title.setText(list.get(0).getTitle());
        banner.setIndicator(new CircleIndicator(context));
        banner.setOnBannerListener((data, position) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                if (list.get(position) != null && null != context) {
                    IntentUtil.startActivity2DetailWithEntity2(context, list.get(position));
                }
            }
            lastTime = currentTime;
        });
        banner.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                holder.title.setText(list.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}