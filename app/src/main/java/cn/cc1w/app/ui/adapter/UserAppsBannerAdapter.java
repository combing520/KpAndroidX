package cn.cc1w.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.ScrollAppsEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * @author kpinfo
 */
public class UserAppsBannerAdapter extends BannerAdapter<ScrollAppsEntity.ItemScrollAppsEntity, UserAppsBannerAdapter.BannerViewHolder> {

    public UserAppsBannerAdapter(List<ScrollAppsEntity.ItemScrollAppsEntity> datas) {
        super(datas);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_radius_none, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, ScrollAppsEntity.ItemScrollAppsEntity data, int position, int size) {
        AppUtil.loadBannerImg(data.getBg_pic_path(), holder.imageView);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemBannerImage);
        }
    }
}