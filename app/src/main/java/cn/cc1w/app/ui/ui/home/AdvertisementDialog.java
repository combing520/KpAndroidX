package cn.cc1w.app.ui.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.advertisement.HomeAdvertisementItemAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.entity_public_use_js.DialogAdEntity;
import cn.cc1w.app.ui.ui.detail.AlbumDetailActivity;
import cn.cc1w.app.ui.ui.detail.LiveDetailActivity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.SpecialDetailActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoDetailActivity;
import cn.cc1w.app.ui.ui.detail.VideoGroupDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PaikewTopicActivity;
import cn.cc1w.app.ui.ui.home.record.PaikewVideoDetailActivity;
import cn.cc1w.app.ui.ui.home.record.PhotoDetailActivity;
import cn.cc1w.app.ui.ui.home.record.UserPaikewActivity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;

/**
 * @author kpinfo
 * 首页弹窗
 */
public class AdvertisementDialog extends CenterPopupView {
    private Banner banner;
    private CircleIndicator indicator;
    private static final String TYPE_NEWS_NORMAL = "news";
    private static final String TYPE_NEWS_NEWSLIST = "newslist";
    private static final String TYPE_NEWS_VIDEO = "video";
    private static final String TYPE_NEWS_VIDEOGROUP = "videogroup";
    private static final String TYPE_NEWS_PHOTO = "photo";
    private static final String TYPE_NEWS_LIVE = "live";
    private static final String TYPE_NEWS_ACTIVITY = "url";
    private static final String TYPE_NEWS_SPECIAL = "topic";
    private static final String TYPE_NEWS_SPEICAL_NORMAL = "normaltopic";
    private static final String TYPE_NEWS_PHOTOGROUP = "photogroup";
    private static final String TYPE_NEWS_VIDEO_PAIKEW = "shootvideo";
    private static final String TYPE_NEWS_PHOTO_PAIKEW = "shootphoto";
    private static final String TYPE_NEWS_TOPIC_PAIKEW = "shoottopic";
    private static final String TYPE_NEWS_USERCETER_PAIKEW = "shootuser";
    private static final String TYPE_NEWS_BROKE = "broke";
    private static final String TYPE_NEWS_PAIKEW = "paikew";
    private List<DialogAdEntity.ItemDialogAdEntity> list;
    private  Context context;

    public AdvertisementDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void setDataSet(@NonNull List<DialogAdEntity.ItemDialogAdEntity> dataSet) {
        list = dataSet;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_advertisement_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initView();
    }

    private void initView() {
        banner = findViewById(R.id.banner_dialog_ad);
        indicator = findViewById(R.id.indicator);
        findViewById(R.id.img_dialog_ad).setOnClickListener(v -> dismiss());
    }

    @Override
    protected void onShow() {
        super.onShow();
        HomeAdvertisementItemAdapter adapter = new HomeAdvertisementItemAdapter(list);
        banner.setAdapter(adapter);
        banner.setIndicator(indicator, false);
        banner.setOnBannerListener((data, position) -> {
            DialogAdEntity.ItemDialogAdEntity item = list.get(position);
            if (null != item) {
                String inType = item.getIn_type();
                Bundle bundle = new Bundle();
                if (TextUtils.equals(Constant.TAG_URL, inType)) {
                    if (!TextUtils.isEmpty(item.getUrl())) {
                        bundle.putString(Constant.TAG_URL, item.getUrl());
                        bundle.putString(Constant.TAG_TITLE, TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
                        bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                        bundle.putString(Constant.TAG_SUMMARY, "");
                        IntentUtil.startActivity(context, UrlDetailActivity.class, bundle);
                    }
                } else if (TextUtils.equals("proto", inType)) {
                    String action = item.getAction();
                    if (TextUtils.isEmpty(action)) {
                        if (!TextUtils.isEmpty(item.getUrl())) {
                            bundle.putString(Constant.TAG_URL, item.getUrl());
                            bundle.putString(Constant.TAG_TITLE, TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
                            bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                            bundle.putString(Constant.TAG_SUMMARY, "");
                            IntentUtil.startActivity(context, UrlDetailActivity.class, bundle);
                        }
                        return;
                    }
                    if (!TextUtils.isEmpty(action)) {
                        if (TYPE_NEWS_NORMAL.equals(action) || TYPE_NEWS_NEWSLIST.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, NewsDetailNewActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_VIDEO.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, VideoDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_VIDEOGROUP.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, VideoGroupDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_PHOTO.equals(action) || TYPE_NEWS_PHOTOGROUP.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, AlbumDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_LIVE.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, LiveDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_ACTIVITY.equals(action)) {
                            if (!TextUtils.isEmpty(item.getUrl())) {
                                bundle.putString(Constant.TAG_URL, item.getUrl());
                                bundle.putString(Constant.TAG_TITLE, TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
                                bundle.putString(Constant.TAG_ID, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                                bundle.putString(Constant.TAG_SUMMARY, "");
                                IntentUtil.startActivity(context, UrlDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_SPECIAL.equals(action) || TYPE_NEWS_SPEICAL_NORMAL.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, SpecialDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_VIDEO_PAIKEW.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, PaikewVideoDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_PHOTO_PAIKEW.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, PhotoDetailActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_TOPIC_PAIKEW.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId()) && TextUtils.isDigitsOnly(item.getId())) {
                                bundle.putInt(Constant.TAG_TOPIC, Integer.parseInt(item.getId()));
                                bundle.putString(Constant.TAG_TITLE, item.getTitle());
                                IntentUtil.startActivity(context, PaikewTopicActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_USERCETER_PAIKEW.equals(action)) {
                            if (!TextUtils.isEmpty(item.getId())) {
                                bundle.putString(Constant.TAG_ID, item.getId());
                                IntentUtil.startActivity(context, UserPaikewActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_BROKE.equals(action)) {
                            if (!TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                                IntentUtil.startActivity(context, BrokeActivity.class, bundle);
                            }
                        } else if (TYPE_NEWS_PAIKEW.equals(action)) {
                            EventBus.getDefault().post(new EventMessage("showPaikewTab", "showPaikewTab"));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (isShow()) {
            dismiss();
        }
        context = null;
        super.onDestroy();
    }
}