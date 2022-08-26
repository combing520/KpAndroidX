package cn.cc1w.app.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.entity.HomeNewsEntity;

/**
 * @author kpinfo
 * on 2021-01-27
 */
public class VerticalScrollLayout extends ViewFlipper {

    private ListAdapter mAdapter;
    private boolean isSetAnimDuration = false;
    private int interval = 2000;
    /**
     * 动画时间
     */
    private int animDuration = 500;

    private Context mContext;

    public VerticalScrollLayout(Context context) {
        this(context, null);
        mContext = context;
    }

    public VerticalScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        mContext = context;
    }

    private void init(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalScrollLayout);
        animDuration = array.getInt(R.styleable.VerticalScrollLayout_vsl_animDuration, animDuration);
        isSetAnimDuration = array.getBoolean(R.styleable.VerticalScrollLayout_vsl_isCusDuration, false);
        interval = array.getInt(R.styleable.VerticalScrollLayout_vsl_sleepTime, interval);
        array.recycle();
        setFlipInterval(interval);
        Animation animIn = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scroll_in);
        Animation animOut = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scroll_out);
        if (isSetAnimDuration) {
            animIn.setDuration(animDuration);
            animOut.setDuration(animDuration);
        }
        setInAnimation(animIn);
        setOutAnimation(animOut);
    }

    private DataSetObserver mDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            setupChildren();
        }

        @Override
        public void onInvalidated() {
            setupChildren();
        }

    };

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataObserver);
        }
        setupChildren();
    }

    private void setupChildren() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        removeAllViews();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View child = mAdapter.getView(i, null, this);
            if (child == null) {
                throw new NullPointerException("View can't be null");
            } else {
                addView(child);
            }
        }
        startFlipping();
    }


    public void setViews(final List<HomeNewsEntity.ItemHomeNewsEntity.NewsBean> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        removeAllViews();
        int size = datas.size();
        for (int i = 0; i < size; i += 2) {
            final int position = i;
            //根布局
            LinearLayout item = (LinearLayout) LayoutInflater.from(this.mContext).inflate(R.layout.item_view2, null);
            //控件赋值
            ((TextView) item.findViewById(R.id.tv1)).setText(datas.get(position).getTitle());
            if (position + 1 < size) {
                ((TextView) item.findViewById(R.id.tv2)).setText(datas.get(position + 1).getTitle());
            } else {
                item.findViewById(R.id.rl2).setVisibility(View.GONE);
            }
            addView(item);
        }
        startFlipping();
    }


}
