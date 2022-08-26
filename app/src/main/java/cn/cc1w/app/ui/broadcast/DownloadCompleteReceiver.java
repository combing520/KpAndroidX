package cn.cc1w.app.ui.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.greenrobot.eventbus.EventBus;

import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
/**
 * 系统管理器下载完成的通知
 * @author kpinfo
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                EventBus.getDefault().post(new EventMessage(Constant.TAG_DOWNLOAD_COMPLETE, Constant.TAG_DOWNLOAD_COMPLETE));
            }
        }
    }
}