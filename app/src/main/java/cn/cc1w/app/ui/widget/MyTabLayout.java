package cn.cc1w.app.ui.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * Created by kpinfo on 2018/11/19.
 */

public class MyTabLayout extends TabLayout {


    public MyTabLayout(Context context) {
        super(context);
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        super.addTab(tab, position, setSelected);
        setTab(tab);
    }

    private void setTab(Tab tab) {

        try {
            Field field = Tab.class.getDeclaredField("mView");
            field.setAccessible(true);
            LinearLayout view = (LinearLayout) field.get(tab);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            view.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
