package cn.cc1w.app.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cc1w.app.ui.entity.TelevisionClassifyEntity;
import cn.cc1w.app.ui.ui.home.television.fragment.TelevisionFragment;

/**
 * 电视台的 adapter
 * @author kpinfo
 */
public class TelevisionPageAdapter extends FragmentPagerAdapter {
    private final List<TelevisionClassifyEntity.DataBean> mChannels = new ArrayList<>();
    /**
     * 初始化
     *
     * @param fm       FragmentManager
     * @param channels 数据集合
     */
    public TelevisionPageAdapter(FragmentManager fm, List<TelevisionClassifyEntity.DataBean> channels) {
        super(fm);
        if (null != channels && !channels.isEmpty()) {
            mChannels.clear();
            mChannels.addAll(channels);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public TelevisionFragment getItem(int position) {
        return TelevisionFragment.newInstance(mChannels.get(position).getId(), position);
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

}