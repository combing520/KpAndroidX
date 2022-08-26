package cn.cc1w.app.ui.manager;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * CustomLinearLayoutManager
 * @author kpinfo
 */
public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;
    private boolean canVerticalScroll  = true;
    public CustomLinearLayoutManager(Context context) {
        super(context);
    }
    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }
    public void setVerticalScrollEnble(boolean canVerticalScroll){
        this.canVerticalScroll = canVerticalScroll;
    }
    /**
     * 是否可以垂直滑动
     * canScrollHorizontally（禁止横向滑动）
     *
     * @return 是否能横行滑动
     */
    @Override
    public boolean canScrollHorizontally() {
        return isScrollEnabled && super.canScrollVertically();
    }

    /**
     * 是否可以垂直滑动
     * canScrollVertically（禁止竖向滑动）
     *
     * @return 是否可以垂直滑动
     */
    @Override
    public boolean canScrollVertically() {
        return canVerticalScroll && super.canScrollVertically();
    }
}
