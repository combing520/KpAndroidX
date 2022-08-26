package app.cloud.ccwb.cn.linlibrary.gray;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import app.cloud.ccwb.cn.linlibrary.R;

public class GrayImageView extends AppCompatImageView {

    private boolean isGray;

    private Paint paint = new Paint();

    public GrayImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GrayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GrayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GrayImageView);
            isGray = a.getBoolean(R.styleable.GrayImageView_isShowGray, isGray);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isGray) {
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
        }
        canvas.saveLayer(null, paint, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setGray(boolean isShowGray) {
        this.isGray = isShowGray;
        invalidate();
    }
}
