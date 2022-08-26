package app.cloud.ccwb.cn.linlibrary.blankview;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import app.cloud.ccwb.cn.linlibrary.R;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;

/**
 * Created by kpinfo on 2018/12/3.
 * 空白页
 */

public class BlankView extends LinearLayout {
    private ImageView img;
    private TextView txt;
    private final Context context;
    private BlankViewClickListener listener;
    private boolean isLoading = false;// 是否正在加载

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setOnBlankViewClickListener(BlankViewClickListener listener) {
        this.listener = listener;
    }


    public BlankView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public BlankView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public BlankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }


    /**
     * 设置 空白页信息
     *
     * @param postAvatarUrl 显示的图片
     * @param describe      显示的文字
     * @param highLightStr  高亮的文字
     */
    public void setBlankView(String postAvatarUrl, String describe, String highLightStr) {
        if (null != img && null != txt) {
            Glide.with(context).load(postAvatarUrl).into(img);
            if (describe.contains(highLightStr)) {
                int startPos = describe.indexOf(highLightStr);
                int endPos = startPos + (highLightStr.length());
                if (endPos <= startPos) {
                    txt.setText(describe);
                } else {
                    txt.setText(renderColorfulStr(describe, startPos, endPos));
                }
            } else {
                txt.setText(describe);
            }
        }
    }

    /**
     * 设置 空白页信息
     *
     * @param resId        显示的图片
     * @param describe     显示的文字
     * @param highLightStr 高亮的文字
     */
    public void setBlankView(int resId, String describe, String highLightStr) {
        if (null != img && null != txt) {
            Glide.with(context).load(resId).into(img);
            if (describe.contains(highLightStr)) {
                int startPos = describe.indexOf(highLightStr);
                int endPos = startPos + (highLightStr.length());
                if (endPos <= startPos) {
                    txt.setText(describe);
                } else {
                    txt.setText(renderColorfulStr(describe, startPos, endPos));
                }
            } else {
                txt.setText(describe);
            }
        }
    }


    /**
     * 设置 空白页信息
     *
     * @param resId    显示的图片
     * @param describe 显示的文字
     */
    public void setBlankView(int resId, String describe) {
        if (null != img && null != txt) {
            Glide.with(context).load(resId).into(img);
            txt.setText(describe);
        }
    }


    /**
     * 设置 空白页信息
     *
     * @param describe 显示的文字
     */
    public void setBlankView(String describe) {
        if (null != txt && null != img) {
            img.setVisibility(GONE);
            txt.setText(describe);
        }
    }


    /**
     * 设置 空白页信息
     *
     * @param describe     显示的文字
     * @param highLightStr 高亮的文字
     */
    public void setBlankView(String describe, String highLightStr) {
        if (null != img && null != txt) {
            int startPos = describe.indexOf(highLightStr);
            int endPos = startPos + (highLightStr.length());

            if (endPos <= startPos) {
                txt.setText(describe);
            } else {
                txt.setText(renderColorfulStr(describe, startPos, endPos));
            }
        }
    }


    /**
     * 禁止 可以点击
     */
    public void forbidClick() {
        isLoading = false;
    }

    /**
     * 允许点击
     */
    public void permitClick() {
        isLoading = true;
    }


    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_blank, this, true);
        if (null != view) {
            img = view.findViewById(R.id.img_blank);
            txt = view.findViewById(R.id.txt_describe_blank);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != listener && !isLoading) {
                        listener.onBlankViewClickListener(v);
                    }
                }
            });
        }
    }

    /**
     * render 带有颜色的 spannableStringBuilder
     *
     * @param targetStr 对应的文字
     * @param endPos    结束位置
     */
    private SpannableStringBuilder renderColorfulStr(String targetStr, int startPos, int endPos) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(targetStr);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
