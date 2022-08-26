package cn.cc1w.app.ui.kpush;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.mpush.api.Constants;

import org.json.JSONObject;

import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.MyMPushEntity;
import cn.cc1w.app.ui.entity.PushEntity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.xg_push.NewsGroupPushListActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.ccwb.push.mpush.MPush;
import cn.ccwb.push.mpush.MPushService;
import cn.ccwb.push.utils.NotificationUtils;

/**
 * @author kpinfo ---- 小米推送才会进入
 */
public class MyMpushReceiver extends BroadcastReceiver {
    private NotificationUtils notificationUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MPushService.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            byte[] bytes = intent.getByteArrayExtra(MPushService.EXTRA_PUSH_MESSAGE);
            int messageId = intent.getIntExtra(MPushService.EXTRA_PUSH_MESSAGE_ID, 0);
            String message = new String(bytes, Constants.UTF_8);
            LogUtil.print("tongchao", "收到新的通知：" + message);
            if (messageId > 0) {
                MPush.I.ack(messageId);
            }
            if (TextUtils.isEmpty(message)) {
                return;
            }
            MyMPushEntity item = JsonUtil.getObject(message, MyMPushEntity.class);
            if (null != item && null != item.getContent() && null != item.getContent().getPayload()) {
                LogUtil.print("tongchao", "toString ---->>> " + item.toString());
                Intent clickIT = new Intent(MPushService.ACTION_NOTIFICATION_OPENED);
                clickIT.addCategory(BuildConfig.APPLICATION_ID);
                clickIT.setPackage(BuildConfig.APPLICATION_ID);
                clickIT.putExtra("my_extra", item.getContent().getPayload());
                PendingIntent clickPI = PendingIntent.getBroadcast(context, 0, clickIT, 0);
                if (notificationUtils == null) {
                    notificationUtils = new NotificationUtils(context);
                }
                notificationUtils.setContentIntent(clickPI)
                        .sendNotificationCompat(2, item.getContent().getTitle(), item.getContent().getContent(), R.mipmap.ic_launcher);
            }

        } else if (MPushService.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            if (notificationUtils == null) {
                notificationUtils = new NotificationUtils(context);
            }
            notificationUtils.clearNotification();
            PushEntity pushEntity = (PushEntity) intent.getSerializableExtra("my_extra");
            LogUtil.print("tongchao", "通知被点击了， extras=" + pushEntity.toString());
            if (null != pushEntity && !TextUtils.isEmpty(pushEntity.getPushType())) {
                Bundle bundle = new Bundle();
                // 新闻详情
                if (Constant.TAG_TYPE_DETAIL_NEWS_PUSH.equals(pushEntity.getPushType())) {
                    bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(pushEntity.getPushId()) ? "" : pushEntity.getPushId());
                    IntentUtil.startActivityWithPushInfo(context, NewsDetailNewActivity.class, bundle);
                    // 组新闻
                } else if (Constant.TAG_NEWS_GROUP.equals(pushEntity.getPushType())) {
                    bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(pushEntity.getPushId()) ? "" : pushEntity.getPushId());
                    IntentUtil.startActivityWithPushInfo(context, NewsGroupPushListActivity.class, bundle);
                    // 外链
                } else if (Constant.TAG_URL.equals(pushEntity.getPushType())) {
                    bundle.putString(Constant.TAG_URL, TextUtils.isEmpty(pushEntity.getPushUrl()) ? "" : pushEntity.getPushUrl());
                    IntentUtil.startActivityWithPushInfo(context, UrlDetailActivity.class, bundle);
                }
            }
        } else if (MPushService.ACTION_KICK_USER.equals(intent.getAction())) {
            LogUtil.print("tongchao", "用户被踢下线了");
        } else if (MPushService.ACTION_BIND_USER.equals(intent.getAction())) {
            LogUtil.print("tongchao", "绑定用户:"
                    + intent.getStringExtra(MPushService.EXTRA_USER_ID)
                    + (intent.getBooleanExtra(MPushService.EXTRA_BIND_RET, false) ? "成功" : "失败"));
        } else if (MPushService.ACTION_UNBIND_USER.equals(intent.getAction())) {
            LogUtil.print("tongchao", "解绑用户:"
                    + (intent.getBooleanExtra(MPushService.EXTRA_BIND_RET, false)
                    ? "成功"
                    : "失败"));
        } else if (MPushService.ACTION_CONNECTIVITY_CHANGE.equals(intent.getAction())) {
            LogUtil.print("tongchao", intent.getBooleanExtra(MPushService.EXTRA_CONNECT_STATE, false)
                    ? "MPUSH连接建立成功"
                    : "MPUSH连接断开");
        } else if (MPushService.ACTION_HANDSHAKE_OK.equals(intent.getAction())) {
            LogUtil.print("tongchao", "MPUSH握手成功, 心跳:" + intent.getIntExtra(MPushService.EXTRA_HEARTBEAT, 0));
        }
    }

    private NotificationDO fromJson(String message) {
        try {
            JSONObject messageDO = new JSONObject(message);
            if (messageDO != null) {
                JSONObject jo = new JSONObject(messageDO.optString("content"));
                NotificationDO ndo = new NotificationDO();
                ndo.setContent(jo.optString("content"));
                ndo.setTitle(jo.optString("title"));
                ndo.setTicker(jo.optString("ticker"));
                ndo.setNid(jo.optInt("nid", 1));
                ndo.setExtras(jo.optJSONObject("extras"));
                return ndo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
