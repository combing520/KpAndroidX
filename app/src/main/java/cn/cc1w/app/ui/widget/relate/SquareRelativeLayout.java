package cn.cc1w.app.ui.widget.relate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by kpinfo on 2019/2/18.
 */

public class SquareRelativeLayout extends RelativeLayout {

    private static final String TAG = "SquareRelativeLayout";

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);


        setMeasuredDimension(width, width);
    }
}
