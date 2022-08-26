package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnPageChangeListener;
import java.util.List;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.adapter.details_apps.bannerAdapter.TeamGroupItemAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.home.home.MoreActiveListActivity;
import cn.cc1w.app.ui.ui.home.home.MoreAlbumListActivity;
import cn.cc1w.app.ui.ui.home.home.MoreLiveActivity;
import cn.cc1w.app.ui.ui.home.home.MoreNormalNewListActivity;
import cn.cc1w.app.ui.ui.home.home.MoreSpecialListActivity;
import cn.cc1w.app.ui.ui.home.home.MoreVideoGroupListActivity;
import cn.cc1w.app.ui.ui.home.home.MoreVideoListActivity;
import cn.cc1w.app.ui.utils.LogUtil;

/**
 * 组新闻 View
 * @author kpinfo
 */
public class TeamGroupListView {
    private final Context context;
    private final NewsEntity.DataBean entity;
    private final AppsDetailNewsAdapter.TeamGroupHolder holder;
    private int currentPos;
    private long lastTime ;

    public TeamGroupListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean entity) {
        context = ac;
        this.entity = entity;
        holder = (AppsDetailNewsAdapter.TeamGroupHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        LogUtil.e("组新闻  ");
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            initBanner(holder.banner, entity.getNews(), holder.groupNameTv);
            holder.moreLayout.setOnClickListener(v -> {
                NewsEntity.DataBean.ItemNewsEntity newsBean = entity.getNews().get(currentPos);
                if (null != newsBean) {
                    String id = newsBean.getId();
                    String type = entity.getNews().get(currentPos).getIn_type();
                    if (!TextUtils.isEmpty(id)) {
                        Intent intent = new Intent();
                        if (Constant.TYPE_NEWS_NORMAL.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, MoreNormalNewListActivity.class);
                            intent.putExtra(Constant.TAG_ID, id);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_VIDEO_NORMAL.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, MoreVideoListActivity.class);
                            intent.putExtra(Constant.TAG_ID, id);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_VIDEO_GROUP.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, MoreVideoGroupListActivity.class);
                            intent.putExtra(Constant.TAG_ID, id);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_PHOTO.equals(type) || Constant.TYPE_PHOTO_GROUP.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, MoreAlbumListActivity.class);
                            intent.putExtra(Constant.TAG_ID, id);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_LIVE.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, MoreLiveActivity.class);
                            intent.putExtra(Constant.TAG_ID, id);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_URL.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, MoreActiveListActivity.class);
                            intent.putExtra(Constant.TAG_ID, id);
                            context.startActivity(intent);
                        } else if (Constant.TYPE_TOPIC.equals(type)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(context, MoreSpecialListActivity.class);
                            intent.putExtra(Constant.TAG_ID, id);
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    /**
     * 初始化 Banner相关信息
     */
    private void initBanner(Banner banner, final List<NewsEntity.DataBean.ItemNewsEntity> list, TextView titleTv) {
        TeamGroupItemAdapter mBannerAdapter = new TeamGroupItemAdapter(list);
        banner.setAdapter(mBannerAdapter).setIndicator(new CircleIndicator(context));
        banner.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPos = position;
                titleTv.setText(list.get(position).getType_name());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        titleTv.setText(list.get(0).getType_name());
        banner.setOnBannerListener((data, position) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                String newsId = list.get(position).getNews_id();
                String type = list.get(position).getIn_type();
                if (!TextUtils.isEmpty(newsId)) {
                    Intent intent = new Intent();
                    if (Constant.TYPE_NEWS_NORMAL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, NewsDetailNewActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_VIDEO_NORMAL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_VIDEO_GROUP.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoGroupDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_PHOTO.equals(type) || Constant.TYPE_PHOTO_GROUP.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, AlbumDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_LIVE.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, LiveDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    } else if (Constant.TYPE_URL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, UrlDetailActivity.class);
                        intent.putExtra(Constant.TAG_URL, entity.getNews().get(currentPos).getUrl());
                        intent.putExtra(Constant.TAG_TITLE, entity.getNews().get(currentPos).getTitle());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(entity.getNews().get(currentPos).getId()) ? "" : entity.getNews().get(currentPos).getId());
                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(entity.getNews().get(currentPos).getSummary()) ? "" : entity.getNews().get(currentPos).getSummary());
                        context.startActivity(intent);
                    } else if (Constant.TYPE_TOPIC.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    }
                }
            }
            lastTime = currentTime;
        });
    }
}
