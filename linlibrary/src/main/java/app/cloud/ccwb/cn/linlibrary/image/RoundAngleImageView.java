package app.cloud.ccwb.cn.linlibrary.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;

import app.cloud.ccwb.cn.linlibrary.R;

/**
 * 圆角 ImageView
 * @author kpinfo
 */
public class RoundAngleImageView extends AppCompatImageView {
    /**
     * 默认的 圆角图片的圆角
     */
    private static final int CORNER_SIZE = 10;
    private Paint paint;
    private Paint paint2;
    /**
     * 默认的圆角宽度
     */
    private int roundWidth = CORNER_SIZE;
    /**
     * 默认圆角的高度
     */
    private int roundHeight = CORNER_SIZE;

    public RoundAngleImageView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
            roundWidth = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundWidth, roundWidth);
            roundHeight = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundHeight, roundHeight);
            a.recycle();
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            roundWidth = (int) (roundWidth * density);
            roundHeight = (int) (roundHeight * density);
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() != null){
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas1 = new Canvas(bitmap);
            super.onDraw(canvas1);
            paint2.reset();
            paint2.setAntiAlias(true);
            paint2.setDither(true);
            drawLeftUp(canvas1);
            drawLeftDown(canvas1);
            drawRightUp(canvas1);
            drawRightDown(canvas1);

            canvas.drawBitmap(bitmap, 0, 0, paint2);
            bitmap.recycle();
        }else{
            super.onDraw(canvas);
        }
    }

    private void drawLeftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        path.arcTo(new RectF(0, 0, roundWidth * 2, roundHeight * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLeftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - roundHeight);
        path.lineTo(0, getHeight());
        path.lineTo(roundWidth, getHeight());
        path.arcTo(new RectF(0, getHeight() - roundHeight * 2, roundWidth * 2, getHeight()), 90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - roundWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - roundHeight);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, getHeight() - roundHeight * 2, getWidth(), getHeight()), -0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - roundWidth, 0);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, 0, getWidth(), roundHeight * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }
}