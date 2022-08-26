package cn.cc1w.app.ui.adapter.album;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luck.picture.lib.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.AlbumEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 图集详情
 *
 * @author kpinfo
 */
public class AlbumDetailAdapter extends PagerAdapter {
    private List<AlbumEntity.DataBean> imageList = new ArrayList<>();
    private final LayoutInflater inflater;

    /**
     * 初始化
     */
    public AlbumDetailAdapter(Context context, List<AlbumEntity.DataBean> imageList) {
        this.imageList = imageList;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 获取图集中图片数量
     */
    @Override
    public int getCount() {
        if (imageList != null && imageList.size() > 0) {
            return imageList.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {
        View itemView = inflater.inflate(R.layout.item_album_detail_pager, null);
        assert itemView != null;
        PhotoView imageView = itemView.findViewById(R.id.img_album_detail_pager);
        AppUtil.loadBannerImgNoHolder(container.getContext(), imageList.get(position).getPic_path(), imageView);
        (container).addView(itemView, 0);
        return itemView;
    }

    @Override
    public boolean isViewFromObject(@NotNull View arg0, @NotNull Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return POSITION_NONE;
    }
}