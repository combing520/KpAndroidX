package app.cloud.ccwb.cn.linlibrary;

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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class CustomRoundImageView extends ImageView {

    private static final int LEFT_TOP = 0;
    private static final int LEFT_BOTTOM = 1;
    private static final int RIGHT_TOP = 2;
    private static final int RIGHT_BOTTOM = 3;

    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;
    /**
     * My paint.
     */
    private Paint mPaint = null;
    /**
     * The Border width.
     */
    private float borderWidth = 2;
    /**
     * The Border color.
     */
    private int borderColor = Color.parseColor("#8A2BE2");
    /**
     * The Display border.
     */
    private boolean displayBorder;
    /**
     * The Left top radius.
     */
    private float leftTopRadius,
    /**
     * The Right top radius.
     */
    rightTopRadius,
    /**
     * The Left bottom radius.
     */
    leftBottomRadius,
    /**
     * The Right bottom radius.
     */
    rightBottomRadius;

    /**
     * The Display type.
     */
    private DisplayType displayType;

    /**
     * The text paint
     */
    private TextPaint mTextPaint = null;
    /**
     * Text displayed on the label
     */
    private String text;

    /**
     * The enum Display type.
     */
    public enum DisplayType {
        /**
         * Normal display type.
         */
        NORMAL(0),
        /**
         * Circle display type.
         */
        CIRCLE(1),
        /**
         * Round rect display type.
         */
        ROUND_RECT(2);

        DisplayType(int type) {
            this.type = type;
        }

        /**
         * The Type.
         */
        final int type;
    }

    /**
     * The Display type array.
     */
    private static final DisplayType[] displayTypeArray = {
            DisplayType.NORMAL,
            DisplayType.CIRCLE,
            DisplayType.ROUND_RECT
    };

    /**
     * Instantiates a new Round image view.
     *
     * @param context the Context
     */
    public CustomRoundImageView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Round image view.
     *
     * @param context the Context
     * @param attrs   the AttributeSet
     */
    public CustomRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Round image view.
     *
     * @param context      the Context
     * @param attrs        the AttributeSet
     * @param defStyleAttr the default style Attribute
     */
    public CustomRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Init.
     *
     * @param ctx   the Context
     * @param attrs the AttributeSet
     */
    private void init(Context ctx, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        mPaint = new Paint();
        mTextPaint = new TextPaint();

        if (attrs != null) {
            TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

            borderWidth = a.getDimension(R.styleable.RoundImageView_borderWidth, borderWidth);
            borderColor = a.getColor(R.styleable.RoundImageView_borderColor, borderColor);
            displayBorder = a.getBoolean(R.styleable.RoundImageView_displayBorder, displayBorder);

            leftTopRadius = a.getDimension(R.styleable.RoundImageView_leftTopRadius, leftTopRadius);
            rightTopRadius = a.getDimension(R.styleable.RoundImageView_rightTopRadius, rightTopRadius);
            leftBottomRadius = a.getDimension(R.styleable.RoundImageView_leftBottomRadius, leftBottomRadius);
            rightBottomRadius = a.getDimension(R.styleable.RoundImageView_rightBottomRadius, rightBottomRadius);

            float radius = a.getDimension(R.styleable.RoundImageView_radiusSize, 0);
            if (radius > 0) {
                leftTopRadius = leftBottomRadius = rightTopRadius = rightBottomRadius = radius;
            }

            int index = a.getInt(R.styleable.RoundImageView_displayType, -1);

            if (index >= 0) {
                displayType = displayTypeArray[index];
            } else {
                displayType = DisplayType.NORMAL;
            }
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (displayType == DisplayType.CIRCLE) {
            int measureSpec;
            if (widthSize < heightSize) {
                measureSpec = widthMeasureSpec;
            } else {
                measureSpec = heightMeasureSpec;
            }
            if (widthSize <= 0) {
                measureSpec = heightMeasureSpec;
            }
            super.onMeasure(measureSpec, measureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() != null) {
            resetSize(Math.min(getWidth(), getHeight()) / 2f);
            Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            Canvas mCanvas = new Canvas(bm);
            super.onDraw(mCanvas);
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            drawMyContent(mCanvas);
            canvas.drawBitmap(bm, 0, 0, mPaint);
            bm.recycle();
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * Reset Size.
     *
     * @param size the size
     */
    private void resetSize(float size) {
        leftTopRadius = Math.min(leftTopRadius, size);
        rightTopRadius = Math.min(rightTopRadius, size);
        leftBottomRadius = Math.min(leftBottomRadius, size);
        rightBottomRadius = Math.min(rightBottomRadius, size);
        borderWidth = Math.min(borderWidth, size / 2);
    }

    /**
     * Draw my content.
     *
     * @param mCanvas my canvas
     */
    private void drawMyContent(Canvas mCanvas) {

        if (displayType != DisplayType.NORMAL) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            Path path = createPath();
            path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
            mCanvas.drawPath(path, mPaint);
            mPaint.setXfermode(null);
        }

        if (displayBorder) {
            drawBorders(mCanvas);
        }
    }

    /**
     * Draw borders.
     *
     * @param mCanvas my canvas
     */
    private void drawBorders(Canvas mCanvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(borderColor);
        mPaint.setStrokeWidth(borderWidth);
        Path path = createPath();
        mCanvas.drawPath(path, mPaint);
    }

    /**
     * Create path.
     *
     * @return the path
     */
    private Path createPath() {
        Path mPath = new Path();
        float size = borderWidth / 2;
        if (displayType == DisplayType.CIRCLE) {
            mPath.addCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - size, Path.Direction.CW);
        } else if (displayType == DisplayType.ROUND_RECT) {
            RectF rectF = new RectF(0, 0, getWidth(), getHeight());
            rectF.inset(size, size);
            mPath.addRoundRect(rectF,
                    new float[]{
                            leftTopRadius, leftTopRadius,
                            rightTopRadius, rightTopRadius,
                            rightBottomRadius, rightBottomRadius,
                            leftBottomRadius, leftBottomRadius},
                    Path.Direction.CW);
        } else {
            RectF rect = new RectF(0, 0, getWidth(), getHeight());
            rect.inset(size, size);
            mPath.addRect(rect, Path.Direction.CW);
        }
        return mPath;
    }

    /**
     * Sets border color.
     *
     * @param borderColor the border color
     */
    public void setBorderColor(int borderColor) {
        if (this.borderColor != borderColor) {
            this.borderColor = borderColor;
            if (displayBorder) {
                postInvalidate();
            }
        }
    }

    /**
     * Is display border boolean.
     *
     * @return the boolean
     */
    public boolean isDisplayBorder() {
        return displayBorder;
    }

    /**
     * Sets display border.
     *
     * @param displayBorder the display border
     */
    public void setDisplayBorder(boolean displayBorder) {
        if (this.displayBorder != displayBorder) {
            this.displayBorder = displayBorder;
            postInvalidate();
        }
    }

    /**
     * Gets left top radius.
     *
     * @return the left top radius
     */
    public float getLeftTopRadius() {
        return leftTopRadius;
    }

    /**
     * Sets left top radius.
     *
     * @param leftTopRadius the left top radius
     */
    public void setLeftTopRadius(float leftTopRadius) {
        if (this.leftTopRadius != leftTopRadius) {
            this.leftTopRadius = leftTopRadius;
            if (displayType != DisplayType.NORMAL) {
                postInvalidate();
            }
        }
    }

    /**
     * Gets right top radius.
     *
     * @return the right top radius
     */
    public float getRightTopRadius() {
        return rightTopRadius;
    }

    /**
     * Sets right top radius.
     *
     * @param rightTopRadius the right top radius
     */
    public void setRightTopRadius(float rightTopRadius) {
        if (this.rightTopRadius != rightTopRadius) {
            this.rightTopRadius = rightTopRadius;
            if (displayType != DisplayType.NORMAL) {
                postInvalidate();
            }
        }
    }

    /**
     * Gets left bottom radius.
     *
     * @return the left bottom radius
     */
    public float getLeftBottomRadius() {
        return leftBottomRadius;
    }

    /**
     * Sets left bottom radius.
     *
     * @param leftBottomRadius the left bottom radius
     */
    public void setLeftBottomRadius(float leftBottomRadius) {
        if (this.leftBottomRadius != leftBottomRadius) {
            this.leftBottomRadius = leftBottomRadius;
            if (displayType != DisplayType.NORMAL) {
                postInvalidate();
            }
        }
    }

    /**
     * Gets right bottom radius.
     *
     * @return the right bottom radius
     */
    public float getRightBottomRadius() {
        return rightBottomRadius;
    }

    /**
     * Sets right bottom radius.
     *
     * @param rightBottomRadius the right bottom radius
     */
    public void setRightBottomRadius(float rightBottomRadius) {
        if (this.rightBottomRadius != rightBottomRadius) {
            this.rightBottomRadius = rightBottomRadius;
            if (displayType != DisplayType.NORMAL) {
                postInvalidate();
            }
        }
    }

    /**
     * Sets radius.
     *
     * @param leftTopRadius     the left top radius
     * @param rightTopRadius    the right top radius
     * @param leftBottomRadius  the left bottom radius
     * @param rightBottomRadius the right bottom radius
     */
    public void setRadius(float leftTopRadius, float rightTopRadius, float leftBottomRadius, float rightBottomRadius) {
        if (this.leftTopRadius == leftTopRadius
                && this.rightTopRadius == rightTopRadius
                && this.leftBottomRadius == leftBottomRadius
                && this.rightBottomRadius == rightBottomRadius) {
            return;
        }

        this.leftTopRadius = leftTopRadius;
        this.rightTopRadius = rightTopRadius;
        this.leftBottomRadius = leftBottomRadius;
        this.rightBottomRadius = rightBottomRadius;
        if (displayType != DisplayType.NORMAL) {
            postInvalidate();
        }
    }

    /**
     * Sets radius.
     *
     * @param radius the radius
     */
    public void setRadius(float radius) {
        setRadius(radius, radius, radius, radius);
    }

    /**
     * Gets display type.
     *
     * @return the display type
     */
    public DisplayType getDisplayType() {
        return displayType;
    }

    /**
     * Sets display type.
     *
     * @return the display type
     */
    public void setDisplayType(DisplayType _displayType) {
        displayType = _displayType;
        if (displayType != DisplayType.NORMAL) {
            postInvalidate();
        }
    }

}
