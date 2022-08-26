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

import cn.cc1w.app.ui.entity.AppsDetailEntity;
import cn.cc1w.app.ui.ui.home.apps.fragment.AppsDetailFragment;

/**
 * 应用号详情  Adapter
 * @author kpinfo
 */
public class AppsDetailsPagerAdapter extends FragmentPagerAdapter {
    private final List<AppsDetailEntity.DataBean.ListBean> mDataSet = new ArrayList<>();
    private final FragmentManager fragmentManager;
    /**
     * 初始化
     * @param fm FragmentManager
     * @param dataSet 数据源
     */
    public AppsDetailsPagerAdapter(FragmentManager fm, List<AppsDetailEntity.DataBean.ListBean> dataSet) {
        super(fm);
        this.mDataSet.clear();
        this.mDataSet.addAll(dataSet);
        fragmentManager = fm;
    }

    @NonNull
    @Override
    public AppsDetailFragment getItem(int position) {
        return AppsDetailFragment.newInstance(mDataSet.get(position).getName(),mDataSet.get(position).getId());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDataSet.get(position).getName();
    }

    @Override
    public int getCount() {
        return mDataSet.size();
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