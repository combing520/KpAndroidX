package cn.cc1w.app.ui.adapter.advertisement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.entity_public_use_js.DialogAdEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * @author kpinfo
 */
public class HomeAdvertisementItemAdapter extends BannerAdapter<DialogAdEntity.ItemDialogAdEntity, HomeAdvertisementItemAdapter.BannerViewHolder> {

    public HomeAdvertisementItemAdapter(List<DialogAdEntity.ItemDialogAdEntity> datas) {
        super(datas);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_banner_home_advertisement, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, DialogAdEntity.ItemDialogAdEntity data, int position, int size) {
        AppUtil.loadSplashImg(data.getPic_path(), holder.imageView);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final RoundAngleImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.homeAdvertisementBanner);
        }
    }
}