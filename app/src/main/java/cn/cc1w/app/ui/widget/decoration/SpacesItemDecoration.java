package cn.cc1w.app.ui.widget.decoration;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by think on 2017/12/18.
 * RecycleView 设置条目 的边距
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private  int count = 3;
    public SpacesItemDecoration(int space,int count) {
        this.space = space;
        this.count = count;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
//        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildPosition(view) % count == 0)
            outRect.left = 0;
    }
}