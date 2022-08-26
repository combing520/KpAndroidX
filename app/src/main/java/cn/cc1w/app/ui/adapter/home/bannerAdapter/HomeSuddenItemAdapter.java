package cn.cc1w.app.ui.adapter.home.bannerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * @author kpinfo
 */
public class HomeSuddenItemAdapter extends BannerAdapter<HomeNewsEntity.ItemHomeNewsEntity.NewsBean, HomeSuddenItemAdapter.BannerViewHolder> {

    public HomeSuddenItemAdapter(List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> datas) {
        super(datas);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_banner_home_url, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, HomeNewsEntity.ItemHomeNewsEntity.NewsBean data, int position, int size) {
        AppUtil.loadBannerImg(data.getPic_path(), holder.imageView);
        holder.titleTv.setText(data.getTitle());
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView imageView;
        private final TextView titleTv;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.homeUrlImg);
            titleTv = itemView.findViewById(R.id.homeUrlTitle);
        }
    }
}