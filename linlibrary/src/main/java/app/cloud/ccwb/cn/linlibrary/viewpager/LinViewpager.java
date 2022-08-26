package app.cloud.ccwb.cn.linlibrary.viewpager;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kpinfo on 2018/12/5.
 */

public class LinViewpager extends ViewPager {

    private boolean isCanScroll;

    public LinViewpager(Context context) {
        super(context);
    }

    public LinViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            //允许滑动则应该调用父类的方法
            try {
                return super.onTouchEvent(ev);
            } catch (Exception e) {
                return false;
            }
        } else {
            //禁止滑动则不做任何操作，直接返回true即可
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (isCanScroll) {
                return super.onInterceptTouchEvent(ev);
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    //设置是否允许滑动，true是可以滑动，false是禁止滑动
    public void setIsCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

}
