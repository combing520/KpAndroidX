package app.cloud.ccwb.cn.linlibrary.gray;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import app.cloud.ccwb.cn.linlibrary.R;

public class GrayLinearLayout extends LinearLayout {

    private boolean isGray;

    private Paint paint = new Paint();

    public GrayLinearLayout(Context context) {
        super(context);
        init(context, null);
    }

    public GrayLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GrayLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(null, paint, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        canvas.restore();
    }


    public void setGray(boolean isShowGray) {
        this.isGray = isShowGray;
        invalidate();
    }
}
