package cn.cc1w.app.ui.kpush;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;


import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.PushEntity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.xg_push.NewsGroupPushListActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.ccwb.push.mixpush.MixPushMessage;
import cn.ccwb.push.mixpush.MixPushPlatform;
import cn.ccwb.push.mixpush.MixPushReceiver;

/**
 * @author kpinfo
 */
public class MyMixPushReceiver extends MixPushReceiver {
    @Override
    public void onRegisterSucceed(Context context, MixPushPlatform mixPushPlatform) {
        LogUtil.print("tongchao", "registerPush  onRegisterSucceed  id = " + mixPushPlatform.getRegId() + "   name = " + mixPushPlatform.getPlatformName());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MixPushMessage message) {
        if (!TextUtils.isEmpty(message.getPayload())) {
            PushEntity pushEntity = JsonUtil.getObject(message.getPayload(), PushEntity.class);
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
        }
    }
}
