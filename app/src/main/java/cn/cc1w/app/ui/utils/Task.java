package cn.cc1w.app.ui.utils;

import java.util.TimerTask;

public abstract class Task extends TimerTask {

    public abstract void onRun();

    @Override
    public void run() {
        onRun();
    }
}
