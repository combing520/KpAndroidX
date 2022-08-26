package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 * @author kpinfo
 * on 2020-09-02
 */
public class EmailBannerView {
    private final Context activity;
    private int index;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.EmailHolder holder;
    private long lastTime;
    private static final long MIN_CLICK_INTERVAL = 1000;

    public EmailBannerView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        index = i;
        this.entity = entity;
        holder = (HomeNewsAdapter.EmailHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            AppUtil.loadEmailImg(entity.getNews().get(0).getPic_path(), holder.bannerImg);
            holder.bannerImg.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= MIN_CLICK_INTERVAL / 2) {
                    String url = entity.getNews().get(0).getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.TAG_URL, url);
                        bundle.putString(Constant.TAG_TITLE, entity.getNews().get(0).getTitle());
                        bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(entity.getNews().get(0).getId()) ? "" : entity.getNews().get(0).getId());
                        bundle.putString(Constant.TAG_SUMMARY, TextUtils.isEmpty(entity.getNews().get(0).getSummary()) ? "" : entity.getNews().get(0).getSummary());
                        IntentUtil.startActivity(activity, UrlDetailActivity.class, bundle);
                    }
                }
                lastTime = currentTime;
            });
        }
    }
}