package app.cloud.ccwb.cn.linlibrary.loading.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Luo_xiasuhuei321@163.com on 2016/11/6.
 * desc:
 */
public class WrongDiaView extends View {
    private final String TAG = this.getClass().getSimpleName();

    private FinishDrawListener listener;

    private Context mContext;
    private int mWidth = 0;
    private Paint mPaint;
    private RectF rectF;

    private int line1_x;
    private int line1_y;

    private int line2_x;
    private int line2_y;

    private int times = 0;
    private boolean drawEveryTime = true;
    private int speed = 1;
    private int count = 0;
//    private int color;

    public WrongDiaView(Context context) {
        this(context, null);
    }

    public WrongDiaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrongDiaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initAttr(context, attrs, defStyleAttr);
        initPaint(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode != MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize >= heightSpecSize ? widthSpecSize : heightSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            mWidth = heightSpecSize;
        } else if (widthSpecMode != MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = SizeUtils.dip2px(mContext, 80);
        }
        setMeasuredDimension(mWidth, mWidth);
        float mPadding = 8;
        rectF = new RectF(mPadding, mPadding, mWidth - mPadding, mWidth - mPadding);
    }

//    private void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {
//        TypedArray a = context.getTheme()
//                .obtainStyledAttributes(attrs, R.styleable.WrongDiaView, defStyleAttr, 0);
//        for (int i = 0; i < a.getIndexCount(); i++) {
//            int attr = a.getIndex(i);
//            if (attr == R.styleable.RightDiaView_speed) {
//                speed = a.getInt(attr, 1);
//            }
//            if (attr == R.styleable.RightDiaView_strokeColor) {
//                color = a.getColor(attr, Color.WHITE);
//            }
//        }
//        a.recycle();
//    }

    private void initPaint(Context context) {
        mContext = context;
        mPaint = new Paint();
        //?????????
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(8);
    }

    int progress = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawEveryTime)
            drawDynamic(canvas);
        else {
            drawStatic(canvas);
            if (listener != null)
                listener.dispatchFinishEvent(this);
        }
    }

    private void drawDynamic(Canvas canvas) {
        if (progress < 100)
            progress += speed;
        //?????????????????????
        canvas.drawArc(rectF, 235, 360 * progress / 100, false, mPaint);

        int line1_start = 3 * mWidth / 10;
        int line2_startX = 7 * mWidth / 10;


        //????????
        if (progress == 100) {
            if ((line1_x + line1_start) <= line2_startX) {
                line1_x += speed;
                line1_y += speed;
            }
            //???????????????
            canvas.drawLine(line1_start, line1_start,
                    line1_start + line1_x, line1_start + line1_y, mPaint);

            if (line1_x == 2 * mWidth / 5) {
                line1_x++;
                line1_y++;
            }

            if (line1_x >= 2 * mWidth / 5 && (line2_startX - line2_y) >= line1_start) {
                line2_x -= speed;
                line2_y += speed;
            }
            //???????????????
            canvas.drawLine(line2_startX, line1_start,
                    line2_startX + line2_x, line1_start + line2_y, mPaint);

            if ((line2_startX - line2_y) < line1_start) {
                //1.????????????????????????????????????
                //2.?????????????????????????????????
                if (count == 0 && times == 0 && listener != null) {
                    listener.dispatchFinishEvent(this);
                    count++;
                }
                times--;
                if (times >= 0) {
                    reDraw();
                    invalidate();
                } else {
                    return;
                }
            }
        }
        invalidate();
    }

    private void drawStatic(Canvas canvas) {
        canvas.drawArc(rectF, 0, 360, false, mPaint);

        int line1_start = 3 * mWidth / 10;
        int line2_startX = 7 * mWidth / 10;

        canvas.drawLine(line1_start, line1_start,
                line1_start + 2 * mWidth / 5, line1_start + 2 * mWidth / 5, mPaint);
        canvas.drawLine(line1_start + 2 * mWidth / 5, line1_start,
                line1_start, line1_start + 2 * mWidth / 5, mPaint);
    }

    private void reDraw() {
        line1_x = 0;
        line2_x = 0;
        line1_y = 0;
        line2_y = 0;
        progress = 0;
    }

    //---------------------------???????????????api-------------------------//

    /**
     * ????????????????????????????????????drawEveryTime = true?????????
     *
     * @param times ???????????????????????????1????????????????????????????????????????????????
     */
    protected void setRepeatTime(int times) {
        if (drawEveryTime)
            this.times = times;
    }

    /**
     * ??????????????????????????????
     */
    protected void setDrawDynamic(boolean drawEveryTime) {
        this.drawEveryTime = drawEveryTime;
    }

    /**
     * ????????????
     */
    protected void setSpeed(int speed) {
        if (speed <= 0 && speed >= 3) {
            throw new IllegalArgumentException("how can u set this speed??  " + speed + "  do not " +
                    "use reflect to use this method!u can see the LoadingDialog class for how to" +
                    "set the speed");
        } else {
            this.speed = speed;
        }
    }

    protected void setDrawColor(int color) {
        mPaint.setColor(color);
    }

    public void setOnDrawFinishListener(FinishDrawListener f) {
        this.listener = f;
    }
}
