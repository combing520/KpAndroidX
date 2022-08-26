package cn.cc1w.app.ui.ui.home.apps.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;


/**
 * 图集 View
 * 一屏幕显示 2长半
 *
 * @author kpinfo
 */
public class AlbumListView {
    private final Context activity;
    private final NewsEntity.DataBean list;
    private final AppsDetailNewsAdapter.AlbumHolder holder;
    private long lastTime;

    public AlbumListView(Context ac, int i, RecyclerView.ViewHolder h, NewsEntity.DataBean l) {
        activity = ac;
//        index = i;
        list = l;
        holder = (AppsDetailNewsAdapter.AlbumHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != list && null != list.getNews() && !list.getNews().isEmpty()) {
            NewsEntity.DataBean.ItemNewsEntity itemNews = list.getNews().get(0);
            holder.titleTv.setText(TextUtils.isEmpty(itemNews.getTitle()) ? "" : itemNews.getTitle());
            AppUtil.loadBannerImg(itemNews.getPic_path(), holder.coverIv);
            holder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                    IntentUtil.startActivity2DetailWithEntity2(activity, itemNews);
                }
                lastTime = currentTime;
            });
        }
    }
}