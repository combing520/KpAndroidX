package cn.cc1w.app.ui.ui.share;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.ShareInfoAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemShare;
import cn.cc1w.app.ui.entity.ShareType;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * @author kpinfo
 * 开屏新闻 7.0 分享
 */
public class NewsShareActivity extends CustomActivity implements OnItemClickListener {
    private Unbinder mBind;
    @BindView(R.id.news_share_recycle)
    RecyclerView mShareRecycleView;
    private ShareInfoAdapter mAdapter;
    private static final int CNT_GRID = 4;
    private boolean isSharing;
    private boolean isResume;
    private ShareType mShareType;
    private ShareEntity shareEntity;
    private LoadingDialog mLoading;
    private ShareAction mShareAction;
    private final Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_share);
        init();
    }

    private void init() {
        overridePendingTransition(0, 0);
        mBind = ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        initLoading();
        initRecycleView();
        ImmersionBar.with(this).statusBarColor(R.color.colorHalfTransport).init();
    }

    private void initLoading() {
        mLoading = AppUtil.getLoading(this);
    }

    private void initRecycleView() {
        mAdapter = new ShareInfoAdapter();
        mShareRecycleView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mShareRecycleView.setLayoutManager(new GridLayoutManager(this, CNT_GRID));
    }

    private void initData() {
        initIntentData();
    }

    private void initIntentData() {
        String shareContent = getIntent().getStringExtra(Constant.TAG_SHARE_CONTENT);
        if (!TextUtils.isEmpty(shareContent)) {
            Gson gson = new Gson();
            shareEntity = gson.fromJson(shareContent, ShareEntity.class);
            if (null != shareEntity) {
                initRecycleViewData();
            }
        }
    }

    private void initRecycleViewData() {
        List<ItemShare> list = new ArrayList<>();
        list.add(new ItemShare("微信好友", R.mipmap.wechat, ShareType.WX_FRIEND));
        list.add(new ItemShare("朋友圈", R.mipmap.friends, ShareType.WX_CIRCLE));
        list.add(new ItemShare("新浪微博", R.mipmap.sina, ShareType.WEI_BO));
        list.add(new ItemShare("QQ好友", R.mipmap.qq, ShareType.QQ));
        list.add(new ItemShare("复制链接", R.mipmap.link, ShareType.COPY_URL));
        if (shareEntity.isCollect()) {
            list.add(new ItemShare("收藏", R.mipmap.collect_d, ShareType.COLLECTION));
        } else {
            list.add(new ItemShare("收藏", R.mipmap.collect, ShareType.COLLECTION));
        }
        list.add(new ItemShare("刷新", R.mipmap.refresh, ShareType.REFRESH));
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    private void doShare(ShareType shareType) {
        if (null != shareEntity) {
            if (mShareAction == null) {
                mShareAction = new ShareAction(this);
            }
            mShareAction.setPlatform(
                    shareType == ShareType.WX_FRIEND ? SHARE_MEDIA.WEIXIN :
                            shareType == ShareType.WX_CIRCLE ? SHARE_MEDIA.WEIXIN_CIRCLE :
                                    shareType == ShareType.QQ ? SHARE_MEDIA.QQ : SHARE_MEDIA.SINA);
            mShareAction.setCallback(mShareListener);

            UMWeb umWeb = new UMWeb(shareEntity.getUrl());
            umWeb.setDescription(TextUtils.isEmpty(shareEntity.getSummary()) ? Constant.SUMMARY_SHARE : shareEntity.getSummary());
            umWeb.setTitle(TextUtils.isEmpty(shareEntity.getTitle()) ? Constant.TILE_SHARE : shareEntity.getTitle());

            UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share_app));
            umWeb.setThumb(umImage);
            mShareAction.withMedia(umWeb);
            mShareAction.share();
        }
    }

    private void doCopyUrl() {
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (null != clipboardManager && null != shareEntity) {
            clipboardManager.addPrimaryClipChangedListener(() -> {
                if (clipboardManager.getPrimaryClip() != null && clipboardManager.getPrimaryClip().getItemCount() > 0) {
                    ClipData.Item itemAt = clipboardManager.getPrimaryClip().getItemAt(0);
                    LogUtil.e(itemAt.toString());
                }
            });
            ClipData clipData = ClipData.newPlainText("save", shareEntity.getUrl());
            clipboardManager.setPrimaryClip(clipData);
            ToastUtil.showShortToast("链接复制成功");
        }
    }

    private void doOperate(String name) {
        EventBus.getDefault().post(new EventMessage(name, name));
    }

    private void addShareSuccessRecord() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null == shareEntity || TextUtils.isEmpty(shareEntity.getNewsId())) {
                return;
            }
            RxHttp.postJson(Constant.SUCCESS_SHARE)
                    .add("cw_news_id", shareEntity.getNewsId()).add("cw_type", shareEntity.getType()).add("cw_share_type", "share")
                    .add("cw_platform", mShareType == ShareType.WX_FRIEND ? "wechat" : mShareType == ShareType.WX_CIRCLE ? "wxcircle" : mShareType == ShareType.QQ ? "qq" : "sina")
                    .add("cw_share_url", shareEntity.getUrl()).add("cw_title", shareEntity.getTitle()).add("cw_summary", shareEntity.getSummary())
                    .asResponse(JsonObject.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!TextUtils.isEmpty(shareEntity.getRedirect_url())) {
                            EventBus.getDefault().post(new EventMessage("reload", shareEntity.getRedirect_url()));
                            finish();
                        }
                    }, (OnError) error -> {
                        finish();
                    });
        }
    }

    private final UMShareListener mShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
            if (null != mLoading) {
                mLoading.show();
            }
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (null != shareEntity && !TextUtils.isEmpty(shareEntity.getNewsId()) && (TextUtils.equals("news", shareEntity.getType()))) {
                addShareSuccessRecord();
            }
            if (null != mLoading && mLoading.isShow()) {
                mLoading.close();
            }
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (null != mLoading && mLoading.isShow()) {
                mLoading.close();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (null != mLoading && mLoading.isShow()) {
                mLoading.close();
            }
            finish();
        }
    };

    @Override
    public void onItemClick(View targetView, int pos) {
        ItemShare shareItem = mAdapter.getItem(pos);
        if (shareItem.getType() == ShareType.WX_FRIEND || shareItem.getType() == ShareType.WX_CIRCLE || shareItem.getType() == ShareType.QQ || shareItem.getType() == ShareType.WEI_BO) {
            mShareType = shareItem.getType();
            doShare(shareItem.getType());
        } else if (shareItem.getType() == ShareType.COPY_URL) {
            doCopyUrl();
            finish();
        } else if (shareItem.getType() == ShareType.COLLECTION) {
            doOperate(Constant.TAG_COLLECTION);
            finish();

        } else if (shareItem.getType() == ShareType.REPORT) {
            doOperate(Constant.TAG_REPORT);
            finish();
        } else if (shareItem.getType() == ShareType.REFRESH) {
            doOperate(Constant.TAG_REFRESH);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.share_container)
    public void onClick(View view) {
        if (view.getId() == R.id.share_container) {
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isSharing) {
            isSharing = false;
            handler.postDelayed(() -> {
                if (!isResume) {
                    if (mShareType == ShareType.WX_FRIEND
                            && (null != shareEntity)
                            && (!TextUtils.isEmpty(shareEntity.getNewsId()))
                            && (TextUtils.equals("news", shareEntity.getType()))) {
                        addShareSuccessRecord();
                        if (null != mLoading && mLoading.isShow()) {
                            mLoading.close();
                        }
                        finish();
                    }
                }
            }, 200);
        }
        if (null != mLoading && mLoading.isShow()) {
            mLoading.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSharing = false;
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        mBind.unbind();
        super.onDestroy();
    }
}