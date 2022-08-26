// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package cn.cc1w.app.ui.widget.wheel;

import android.os.Handler;
import android.os.Message;


//            WheelView

final class MessageHandler extends Handler {
    public static final int WHAT_INVALIDATE_LOOP_VIEW = 1000;
    public static final int WHAT_SMOOTH_SCROLL = 2000;
    public static final int WHAT_ITEM_SELECTED = 3000;

    final WheelView wheelView;

    MessageHandler(WheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public final void handleMessage(Message msg) {
        if (msg.what == WHAT_INVALIDATE_LOOP_VIEW) {
            wheelView.invalidate();
        } else if (msg.what == WHAT_SMOOTH_SCROLL) {
            wheelView.smoothScroll(WheelView.ACTION.FLING);
        } else if (msg.what == WHAT_ITEM_SELECTED) {
            wheelView.onItemSelected();
        }
    }
}
