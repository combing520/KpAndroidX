package cn.cc1w.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.FunctionAdvertisement;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * @author kpinfo
 */
public class HomeFunctionBannerAdapter extends BannerAdapter<FunctionAdvertisement.DataBean, HomeFunctionBannerAdapter.BannerViewHolder> {
    public HomeFunctionBannerAdapter(List<FunctionAdvertisement.DataBean> datas) {
        super(datas);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_radius, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, FunctionAdvertisement.DataBean data, int position, int size) {
        AppUtil.loadBannerImg(data.getPic_path(), holder.imageView);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemBannerRadius);
        }
    }
}