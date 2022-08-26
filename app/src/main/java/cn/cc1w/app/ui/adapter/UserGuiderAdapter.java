package cn.cc1w.app.ui.adapter;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户 引导 Adapter
 * @author kpinfo
 */
public class UserGuiderAdapter extends PagerAdapter {
    private  List<View> mViewList = new ArrayList<>();

    public UserGuiderAdapter(List<View> mViewList) {
        this.mViewList = mViewList;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView(mViewList.get(position));
    }
}