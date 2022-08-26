package cn.cc1w.app.ui.interfaces;

import androidx.recyclerview.widget.RecyclerView;

/**
 * RecycleView 拖拽的 接口
 * @author kpinfo
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(RecyclerView.ViewHolder holder, int fromPosition, int targetPosition);

    void onItemSelect(RecyclerView.ViewHolder holder);

    void onItemClear(RecyclerView.ViewHolder holder);

    void onItemDismiss(RecyclerView.ViewHolder holder);
}
