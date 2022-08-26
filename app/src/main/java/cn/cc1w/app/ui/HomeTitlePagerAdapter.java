package cn.cc1w.app.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.base.BaseFragment;
import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.ui.fragment.HomeFragment;
import cn.cc1w.app.ui.ui.home.news.UrlChildFragment;

/**
 * 首页 HomeAdapter
 *
 * @author kpinfo
 */
public class HomeTitlePagerAdapter extends FragmentPagerAdapter {
    private List<HomeChannelEntity.ItemHomeChannelEntity> mChannels = new ArrayList<>();
    private final FragmentManager fragmentManager;
    private BaseFragment currentFragment;

    public HomeTitlePagerAdapter(FragmentManager fm, List<HomeChannelEntity.ItemHomeChannelEntity> channels) {
        super(fm);
        if (null != channels && !channels.isEmpty()) {
            mChannels.clear();
            mChannels.addAll(channels);
            notifyDataSetChanged();
        }
        fragmentManager = fm;
    }

    public void updateChannel(List<HomeChannelEntity.ItemHomeChannelEntity> channels) {
        this.mChannels = channels;
        notifyDataSetChanged();
    }

    public List<HomeChannelEntity.ItemHomeChannelEntity> getList() {
        return mChannels;
    }

    @Override
    public BaseFragment getItem(int position) {
        if (!TextUtils.isEmpty(mChannels.get(position).getIn_type()) && mChannels.get(position).getIn_type().equals("url")
                && !TextUtils.isEmpty(mChannels.get(position).getUrl())) {
            return UrlChildFragment.newInstance(mChannels.get(position).getUrl());
        } else {
            return HomeFragment.newInstance(mChannels.get(position).getId(), mChannels.get(position).isIs_index());
        }
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        currentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public BaseFragment getCurrentFragment() {
        return currentFragment;
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
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

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