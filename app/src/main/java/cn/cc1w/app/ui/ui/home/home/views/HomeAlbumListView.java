package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 *
 * @author kpinfo
 * 首页普通新闻
 */
public class HomeAlbumListView {
    private final Context activity;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.AlbumHolder holder;
    private long lastTime;

    public HomeAlbumListView(Context ac, int i, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        activity = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.AlbumHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            HomeNewsEntity.ItemHomeNewsEntity.NewsBean itemNews = entity.getNews().get(0);
            holder.titleTv.setText(TextUtils.isEmpty(itemNews.getTitle()) ? "" : itemNews.getTitle());
            AppUtil.loadBannerImg(itemNews.getPic_path(), holder.coverIv);
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    IntentUtil.startActivity2DetailWithEntity(activity, itemNews);
                }
                lastTime = currentTime;
            });
        }
    }
}