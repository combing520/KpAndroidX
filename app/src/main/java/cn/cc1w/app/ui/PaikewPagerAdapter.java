package cn.cc1w.app.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 动新闻 条目 Adapter
 *
 * @author kpinfo
 */
public class PaikewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mChannels = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public PaikewPagerAdapter(FragmentManager fm, List<Fragment> channels, List<String> list) {
        super(fm);
        if (null != channels && !channels.isEmpty()) {
            mChannels.clear();
            mChannels.addAll(channels);
            notifyDataSetChanged();
            this.titles = list;
        }
    }

    public void updateChannel(List<Fragment> channels) {
        this.mChannels = channels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mChannels.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}