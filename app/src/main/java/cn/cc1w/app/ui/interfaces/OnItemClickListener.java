package cn.cc1w.app.ui.interfaces;

import android.view.View;

/**
 * 条目点击事件
 * @author kpinfo
 */

public interface OnItemClickListener {
    /**
     * 条目点击事件
     *
     * @param targetView 目标View
     * @param pos        对应的位置
     */
    void onItemClick(View targetView, int pos);
}
