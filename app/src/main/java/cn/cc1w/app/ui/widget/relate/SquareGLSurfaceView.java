package cn.cc1w.app.ui.widget.relate;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by kpinfo on 2019/2/18.
 */

public class SquareGLSurfaceView extends GLSurfaceView {
    private static final String TAG = "SquareGLSurfaceView";

    public SquareGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, width);
    }
}
