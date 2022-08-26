package cn.cc1w.app.ui.widget.input;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by wangyujie04 on 2016/3/9.
 */
public class SoftKeyboardRelativeLayout extends RelativeLayout {

    private int FULL_SCREEN_HEIGHT;

    private OnSoftKeyboardListener onSoftKeyboardListener;

    public SoftKeyboardRelativeLayout(Context context) {
        super(context);
    }

    public SoftKeyboardRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoftKeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh == 0) {
            FULL_SCREEN_HEIGHT = h;
        } else {
            if (onSoftKeyboardListener != null && oldh > 0) {
                if (h < oldh && oldh >= FULL_SCREEN_HEIGHT) {
                    onSoftKeyboardListener.onKeyboardShown();
                } else if (h > oldh && h >= FULL_SCREEN_HEIGHT) {
                    onSoftKeyboardListener.onKeyboardHidden();
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnSoftKeyboardListener(OnSoftKeyboardListener onSoftKeyboardListener2) {
        this.onSoftKeyboardListener = onSoftKeyboardListener2;
    }

    public interface OnSoftKeyboardListener {
        void onKeyboardShown();

        void onKeyboardHidden();
    }

}
