package cn.cc1w.app.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.AppsTitleEntity;
import cn.cc1w.app.ui.ui.home.apps.fragment.AppListFragment;

/**
 * APPs 的页面的Adapter
 *
 * @author kpinfo
 */
public class AppsPageAdapter extends FragmentPagerAdapter {
    private final List<AppsTitleEntity.ItemAppsTitleEntity> mChannels = new ArrayList<>();
    private final FragmentManager fragmentManager;

    /**
     * 初始化
     *
     * @param fm   FragmentManager
     * @param list 数据源
     */
    public AppsPageAdapter(FragmentManager fm, List<AppsTitleEntity.ItemAppsTitleEntity> list) {
        super(fm);
        if (null != list && !list.isEmpty()) {
            mChannels.clear();
            mChannels.addAll(list);
            notifyDataSetChanged();
        }
        fragmentManager = fm;
    }

    @NonNull
    @Override
    public AppListFragment getItem(int position) {
        return AppListFragment.newInstance(mChannels.get(position).getName(), mChannels.get(position).getId());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).getName();
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment f = (Fragment) super.instantiateItem(container, position);
        String fragmentTag = f.getTag();
        if (f != getItem(position)) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(f);
            f = getItem(position);
            ft.add(container.getId(), f, fragmentTag);
            ft.attach(f);
            ft.commitAllowingStateLoss();
        }
        return f;
    }
}