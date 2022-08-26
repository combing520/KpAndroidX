package cn.cc1w.app.ui.widget.viewpage;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

/**
 * Created by kpinfo on 2018/8/22.
 * 取消滑动 动画的 ViewPager
 */

public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //去除页面切换时的滑动翻页效果
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }
}
