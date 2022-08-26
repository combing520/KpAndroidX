package cn.cc1w.app.ui.adapter.gallery;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luck.picture.lib.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 图集详情
 *
 * @author kpinfo
 */
public class GalleryDetailAdapter extends PagerAdapter {
    private final List<String> dataSet;
    private final LayoutInflater inflater;

    public GalleryDetailAdapter(Context context, List<String> imageList) {
        this.dataSet = imageList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (dataSet != null && dataSet.size() > 0) {
            return dataSet.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NotNull View arg0, @NotNull Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((View) object);
    }

    @NotNull
    @Override
    @SuppressLint("InflateParams")
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {
        View itemView = inflater.inflate(R.layout.item_album_detail_pager, null);
        assert itemView != null;
        PhotoView imageView = itemView.findViewById(R.id.img_album_detail_pager);
        AppUtil.loadBannerImgNoHolder(container.getContext(), dataSet.get(position), imageView);
        imageView.setTag(position);
        container.addView(itemView, 0);
        return itemView;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

}