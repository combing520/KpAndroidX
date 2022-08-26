package cn.cc1w.app.ui.ui.xg_push.view;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.push.PushNewsGroupAdapter;
import cn.cc1w.app.ui.entity.PushNewsGroupEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 *
 * @author kpinfo
 * @date 2018/11/16
 * <p>
 * 直播的View[推送]
 */
public class PushLiveView {
    private final PushNewsGroupEntity.ItemPushNewsGroupEntity entity;
    private final PushNewsGroupAdapter.LiveHolder holder;

    public PushLiveView(RecyclerView.ViewHolder holder, PushNewsGroupEntity.ItemPushNewsGroupEntity entity) {
        this.entity = entity;
        this.holder = (PushNewsGroupAdapter.LiveHolder) holder;
    }
    /**
     * 初始化
     */
    public void initView() {
        if (null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            PushNewsGroupEntity.ItemPushNewsGroupEntity.NewsBean item = entity.getNews().get(0);
            holder.titleTv.setText(TextUtils.isEmpty(item.getTitle())?"":item.getTitle());
            holder.timeTv.setText(TextUtils.isEmpty(item.getCreate_time())?"":item.getCreate_time());
            holder.smallTitleTv.setText(TextUtils.isEmpty(item.getTitle())?"":item.getTitle());
            holder.sourceTv.setText(TextUtils.isEmpty(item.getApp_name())?"":item.getApp_name());
            holder.describeTv.setText(TextUtils.isEmpty(item.getSummary())?"":item.getSummary());
            AppUtil.loadBannerImg(item.getPic_path(),holder.postImg);
        }
    }
}