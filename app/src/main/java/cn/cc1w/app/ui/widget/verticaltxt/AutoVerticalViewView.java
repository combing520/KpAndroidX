package cn.cc1w.app.ui.widget.verticaltxt;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.AutoVerticalViewDataData;

/**
 * Created by kpinfo on 2018/8/10.
 * <p>
 * 可以自动垂直滚动的文字
 */

public class AutoVerticalViewView extends ViewFlipper {

    private Context context;
    /**
     * 是否开启动画
     */
    private static final boolean isSetAnimDuration = false;
    /**
     * 时间间隔
     */
    private static final int interval = 3000;
    /**
     * 动画时间
     */
    private static final int animDuration = 500;

    public AutoVerticalViewView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AutoVerticalViewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        setFlipInterval(interval);
        Animation animIn = AnimationUtils.loadAnimation(this.context, R.anim.anim_marquee_in);
        if (isSetAnimDuration) {
            animIn.setDuration(animDuration);
        }
        setInAnimation(animIn);
        Animation animOut = AnimationUtils.loadAnimation(this.context, R.anim.anim_marquee_out);
        if (isSetAnimDuration) {
            animOut.setDuration(animDuration);
        }
        setOutAnimation(animOut);
    }

    /**
     * 设置循环滚动的View数组
     *
     * @param
     */
    public void setViews(final List<AutoVerticalViewDataData> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        removeAllViews();

        int size = datas.size();
//        for (int i = 0; i < size; i++) {
//            final int position = i;
//            //根布局
//            LinearLayout item = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.item_view, null);
//            //设置监听
//            item.findViewById(R.id.rl).setOnClickListener(v -> {
//                if (onItemClickListener != null) {
//                    onItemClickListener.onItemClick(position);
//                }
//            });
//            //控件赋值
//            ((TextView) item.findViewById(R.id.tv1)).setText(datas.get(position).getValue());
//            addView(item);
//        }

        for (int i = 0; i < size; i += 2) {
            final int position = i;
            //根布局
            LinearLayout item = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.item_view, null);
            //控件赋值
            ((TextView) item.findViewById(R.id.tv1)).setText(datas.get(position).getValue());
            if (position + 1 < size) {
                ((TextView) item.findViewById(R.id.tv2)).setText(datas.get(position + 1).getValue());
            } else {
                item.findViewById(R.id.r2).setVisibility(View.GONE);
            }
            addView(item);
        }

        startFlipping();
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 设置监听接口
     *
     * @param onItemClickListener 条目点击监听
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * item_view的接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
