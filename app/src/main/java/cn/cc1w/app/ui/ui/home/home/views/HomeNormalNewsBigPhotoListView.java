package cn.cc1w.app.ui.ui.home.home.views;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import cn.cc1w.app.ui.adapter.home.HomeNewsAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;

/**
 *
 * @author kpinfo
 * @date 2018/10/23
 * 首页普通新闻
 */
public class HomeNormalNewsBigPhotoListView {
    private final Context context;
    private final HomeNewsEntity.ItemHomeNewsEntity entity;
    private final HomeNewsAdapter.NormalNewsBigPhotoHolder holder;
    private long lastTime;

    public HomeNormalNewsBigPhotoListView(Context ac, RecyclerView.ViewHolder h, HomeNewsEntity.ItemHomeNewsEntity entity) {
        this.context = ac;
        this.entity = entity;
        holder = (HomeNewsAdapter.NormalNewsBigPhotoHolder) h;
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            final HomeNewsEntity.ItemHomeNewsEntity.NewsBean itemNews = entity.getNews().get(0);
            if (null != itemNews) {
                holder.newsTitleTv.setText(itemNews.getTitle());
                AppUtil.loadBannerImg(itemNews.getPic_path(), holder.newsPicImg);
                holder.itemView.setOnClickListener(v -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                        IntentUtil.startActivity2DetailWithEntity(context, itemNews);
                    }
                    lastTime = currentTime;
                });
            }
        }
    }
}