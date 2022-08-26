package cn.cc1w.app.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 动新闻 条目 Adapter
 * @author kpinfo
 */
public class RankPagerAdapter extends FragmentPagerAdapter {
    private final List<String> mTitles = new ArrayList<>();
    private final List<Fragment> mChannels = new ArrayList<>();

    public RankPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> channels) {
        super(fm);
        if (null != titles && null != channels) {
            mTitles.clear();
            mChannels.clear();
            mTitles.addAll(titles);
            mChannels.addAll(channels);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mChannels.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }
}