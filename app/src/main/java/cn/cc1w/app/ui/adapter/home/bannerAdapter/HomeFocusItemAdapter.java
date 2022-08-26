package cn.cc1w.app.ui.adapter.home.bannerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 首页 关注的 Banner Adapter
 *
 * @author kpinfo
 */
public class HomeFocusItemAdapter extends BannerAdapter<HomeNewsEntity.ItemHomeNewsEntity.NewsBean, HomeFocusItemAdapter.BannerViewHolder> {

    public HomeFocusItemAdapter(List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> datas) {
        super(datas);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_banner_home_focus, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, HomeNewsEntity.ItemHomeNewsEntity.NewsBean data, int position, int size) {
        AppUtil.loadBannerImg(data.getPic_path(), holder.imageView);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.homeFocusBanner);
        }
    }
}