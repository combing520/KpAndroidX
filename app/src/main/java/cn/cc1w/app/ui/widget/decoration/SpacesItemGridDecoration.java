package cn.cc1w.app.ui.widget.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by think on 2017/12/18.
 * RecycleView 设置条目 的边距
 */

public class SpacesItemGridDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int count = 2;

    private boolean hasLeftMargin = false;
    public SpacesItemGridDecoration(int space, int count,boolean hasLeftMargin) {
        this.space = space;
        this.count = count;
        this.hasLeftMargin = hasLeftMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items

        if(!hasLeftMargin){
            if (parent.getChildPosition(view) % count != 0)
                outRect.left = 0;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

    }
}
