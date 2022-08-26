package cn.cc1w.app.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeNewsEntity;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.home.apps.AppDetailActivity;

/**
 * @author kpinfo
 * @date 2018/5/3
 */
public class IntentUtil {

    /**
     * 跳转到对于的 页面
     *
     * @param context 上下午对象
     * @param cls     跳转的 目标Activity
     * @param bundle  带的数据
     */
    public static void startActivity(Context context, Class cls, Bundle bundle) {
        if (null != context) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, cls);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到对于的 页面
     *
     * @param context 上下午对象
     * @param cls     跳转的 目标Activity
     * @param bundle  带的数据
     */
    public static void gotoActivity(Context context, Class cls, Bundle bundle) {
        if (null != context) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, cls);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到对于的 页面
     *
     * @param context 上下午对象
     * @param cls     跳转的 目标Activity
     */
    public static void startActivity(Context context, Class cls) {
        if (null != context) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, cls);
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到对应 页面 （点击推送 通知栏 ，进行跳转）
     *
     * @param context 上下午对象
     * @param cls     跳转的 目标Activity
     * @param bundle  带的数据
     */
    public synchronized static void startActivityWithPushInfo(Context context, Class cls, Bundle bundle) {
        if (null != context) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setClass(context, cls);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        }
    }

    /**
     * 首页跳转详情相关
     *
     * @param context 上下文对象
     * @param entity  首页新闻 实体类
     */
    public synchronized static void startActivity2DetailWithEntity(Context context, HomeNewsEntity.ItemHomeNewsEntity entity) {
        if (null != context && null != entity && null != entity.getNews() && !entity.getNews().isEmpty()) {
            final HomeNewsEntity.ItemHomeNewsEntity.NewsBean itemNews = entity.getNews().get(0);
            startActivity2DetailWithEntity(context, itemNews);
        }
    }

    /**
     * 首页跳转详情相关
     *
     * @param context  上下文对象
     * @param itemNews 首页新闻 实体类
     */
    public synchronized static void startActivity2DetailWithEntity(Context context, HomeNewsEntity.ItemHomeNewsEntity.NewsBean itemNews) {
        if (null != itemNews) {
            String newsId = itemNews.getNews_id();
            if (!TextUtils.isEmpty(newsId)) {
                String type = itemNews.getIn_type();
                if (!TextUtils.isEmpty(newsId) && !TextUtils.isEmpty(type)) {
                    Intent intent = new Intent();
                    LogUtil.d("type = " + type);
                    // 普通新闻
                    if (Constant.TYPE_NEWS_NORMAL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, NewsDetailNewActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 视频
                    } else if (Constant.TYPE_VIDEO_NORMAL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 视频组
                    } else if (Constant.TYPE_VIDEO_GROUP.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoGroupDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 图集
                    } else if (Constant.TYPE_PHOTO.equals(type) || Constant.TYPE_PHOTO_GROUP.equals(type)) {// 图集组

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, AlbumDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 直播
                    } else if (Constant.TYPE_LIVE.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, LiveDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 外链
                    } else if (Constant.TYPE_URL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, UrlDetailActivity.class);
                        intent.putExtra(Constant.TAG_URL, itemNews.getUrl());
                        intent.putExtra(Constant.TAG_TITLE, itemNews.getTitle());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(itemNews.getId()) ? "" : itemNews.getId());
                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(itemNews.getSummary()) ? "" : itemNews.getSummary());
                        context.startActivity(intent);
                        // 专题
                    } else if (Constant.TYPE_TOPIC.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        //特殊类型
                    } else if (Constant.TYPE_SPECIAL.equals(type)) {
                        String jumpType = itemNews.getSpecial_type();
                        if (!TextUtils.isEmpty(jumpType) && jumpType.contains("_")) {
                            if (jumpType.startsWith("app")) {
                                String[] result = jumpType.split("_");
                                LogUtil.d("app result = " + result.length);
                                if (result.length == 2 && !TextUtils.isEmpty(result[1])) {
                                    LogUtil.d("app result = " + result[1]);
                                    intent.setClass(context, AppDetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Constant.TAG_ID, result[1]);
                                    intent.putExtra(Constant.TAG_TITLE, itemNews.getTitle());
                                    intent.putExtra("group_id", itemNews.getGroup_id());
                                    context.startActivity(intent);
                                }
                            } else if (jumpType.startsWith("url")) {
                                String[] result = jumpType.split("_");
                                LogUtil.d("url result = " + result.length);
                                if (result.length == 2 && !TextUtils.isEmpty(result[1]) && result[1].startsWith("http")) {
                                    LogUtil.d("url result = " + result[1]);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Constant.TAG_URL, result[1]);
                                    IntentUtil.startActivity(context, UrlDetailActivity.class, bundle);
                                }
                            }
                        }
                        // 普通新闻组详情
                    } else if (Constant.TYPE_NEWS_NORMAL_GROUP.equals(type)) {
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                    }
                }
            }
        }
    }

    /**
     * 首页跳转详情相关
     *
     * @param context  上下文对象
     * @param itemNews 首页新闻 实体类
     */
    public synchronized static void startActivity2DetailWithEntity2(Context context, NewsEntity.DataBean.ItemNewsEntity itemNews) {
        if (null != itemNews) {
            String newsId = itemNews.getNews_id();
            if (!TextUtils.isEmpty(newsId)) {
                String type = itemNews.getIn_type();
                if (!TextUtils.isEmpty(newsId) && !TextUtils.isEmpty(type)) {
                    Intent intent = new Intent();
                    // 普通新闻
                    if (Constant.TYPE_NEWS_NORMAL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, NewsDetailNewActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 视频
                    } else if (Constant.TYPE_VIDEO_NORMAL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 视频组
                    } else if (Constant.TYPE_VIDEO_GROUP.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, VideoGroupDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 图集
                    } else if (Constant.TYPE_PHOTO.equals(type) || Constant.TYPE_PHOTO_GROUP.equals(type)) {// 图集组

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, AlbumDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 直播
                    } else if (Constant.TYPE_LIVE.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, LiveDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 外链
                    } else if (Constant.TYPE_URL.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, UrlDetailActivity.class);
                        intent.putExtra(Constant.TAG_URL, itemNews.getUrl());
                        intent.putExtra(Constant.TAG_TITLE, itemNews.getTitle());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(itemNews.getId()) ? "" : itemNews.getId());
                        intent.putExtra(Constant.TAG_SUMMARY,
                                TextUtils.isEmpty(itemNews.getSummary()) ? "" : itemNews.getSummary());
                        context.startActivity(intent);
                        // 专题
                    } else if (Constant.TYPE_TOPIC.equals(type)) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        // 普通新闻组详情
                    } else if (Constant.TYPE_NEWS_NORMAL_GROUP.equals(type)) {
                        intent.setClass(context, SpecialDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.TAG_ID, newsId);
                        context.startActivity(intent);
                        //特殊类型
                    } else if (Constant.TYPE_SPECIAL.equals(type)) {
                        String jumpType = itemNews.getSpecial_type();
                        if (!TextUtils.isEmpty(jumpType) && jumpType.contains("_")) {
                            if (jumpType.startsWith("app")) {
                                String[] result = jumpType.split("_");
                                LogUtil.d("app result = " + result.length);
                                if (result.length == 2 && !TextUtils.isEmpty(result[1])) {
                                    LogUtil.d("app result = " + result[1]);
                                    intent.setClass(context, AppDetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Constant.TAG_ID, result[1]);
                                    intent.putExtra(Constant.TAG_TITLE, itemNews.getTitle());
                                    intent.putExtra("group_id", itemNews.getGroup_id());
                                    context.startActivity(intent);
                                }
                            } else if (jumpType.startsWith("url")) {
                                String[] result = jumpType.split("_");
                                LogUtil.d("url result = " + result.length);
                                if (result.length == 2 && !TextUtils.isEmpty(result[1]) && result[1].startsWith("http")) {
                                    LogUtil.d("url result = " + result[1]);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Constant.TAG_URL, result[1]);
                                    IntentUtil.startActivity(context, UrlDetailActivity.class, bundle);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}