package cn.cc1w.app.ui.ui.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import cn.cc1w.app.ui.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsDetailEntity;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 专题详情
 *
 * @author kpinfo
 */
public class SpecialDetailActivity extends CustomActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.txt_title_detail_special)
    TextView titleTv;
    @BindView(R.id.img_post_detail_special)
    ImageView postImg;
    @BindView(R.id.txt_title_special_detail_special)
    TextView specialTitleTv;
    @BindView(R.id.img_thumbnail_detail_special)
    RoundAngleImageView appsLogoImg;
    @BindView(R.id.txt_type_special_detail_special)
    TextView appsTypeTv;
    @BindView(R.id.txt_time_special_detail_special)
    TextView appsCreateTimeTv;
    @BindView(R.id.txt_summary_special_detail_special)
    TextView appsSummaryTv;
    @BindView(R.id.list_detail_special)
    RecyclerView recommendList;
    @BindView(R.id.ll_topView_special)
    LinearLayout container;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private Unbinder unbinder;
    private AppsDetailNewsAdapter adapter;
    private String newsId;
    private int currentPageIndex = 1;
    private LoadingDialog loading;
    private boolean isFirstLoad = true;
    private String shareUrl;
    private String shareSummary;
    private String shareTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        newsId = getIntent().getStringExtra(Constant.TAG_ID);
        initList();
        initLoading();
        requestSpecialDetailInfo();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化list
     */
    private void initList() {
        recommendList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppsDetailNewsAdapter(this);
        recommendList.setAdapter(adapter);
        recommendList.setMotionEventSplittingEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 请求 专题详情信息
     */
    private void requestSpecialDetailInfo() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            if (!TextUtils.isEmpty(newsId)) {
                if (null != loading && isFirstLoad) {
                    loading.show();
                }
                isFirstLoad = false;
                RxHttp.postJson(Constant.DETAIL_SPECIAL)
                        .add("cw_news_id", newsId)
                        .asResponse(NewsDetailEntity.DataBean.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            if (!isFirstLoad) {
                                requestSpecialListDetail();
                                if (data != null) {
                                    updateSpecialDetailInfo(data);
                                }
                            }
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                        }, (OnError) error -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                        });
            }
        }
    }

    /**
     * 更新详情信息
     *
     * @param data 数据
     */
    private void updateSpecialDetailInfo(NewsDetailEntity.DataBean data) {
        container.setVisibility(View.VISIBLE);
        AppUtil.loadBigImg(data.getPic_path(), postImg);
        AppUtil.loadNewsImg(data.getApp_logo_pic_path(), appsLogoImg);
        titleTv.setText(TextUtils.isEmpty(data.getTitle()) ? "" : data.getTitle());
        specialTitleTv.setText(TextUtils.isEmpty(data.getTitle()) ? "" : data.getTitle());
        appsTypeTv.setText(TextUtils.isEmpty(data.getApp_name()) ? "" : data.getApp_name());
        appsCreateTimeTv.setText(TextUtils.isEmpty(data.getCreate_time()) ? "" : data.getCreate_time());
        appsSummaryTv.setText(TextUtils.isEmpty(data.getSummary()) ? "" : data.getSummary());
        shareSummary = data.getSummary();
        shareUrl = data.getUrl();
        shareTitle = data.getTitle();
    }

    /**
     * 请求专题列表
     */
    private void requestSpecialListDetail() {
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(newsId)) {
            currentPageIndex = 1;
            RxHttp.postJson(Constant.LIST_DETAIL_SPECIAL)
                    .add("cw_news_id", newsId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        if (null != dataSet && !dataSet.isEmpty()) {
                            adapter.setData(dataSet);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    });
        }
    }

    /**
     * 请求专题列表
     */
    private void loadMoreData() {
        if (NetUtil.isNetworkConnected(this) && !TextUtils.isEmpty(newsId)) {
            RxHttp.postJson(Constant.LIST_DETAIL_SPECIAL)
                    .add("cw_news_id", newsId).add("cw_page", String.valueOf(currentPageIndex))
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (null != dataSet && !dataSet.isEmpty()) {
                            adapter.addData(dataSet);
                            currentPageIndex += 1;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            if (null != refreshLayout) {
                                refreshLayout.setEnableLoadMore(false);
                            }
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishLoadMore();
                        }
                    }, (OnError) error -> {
                        if (null != refreshLayout) {
                            refreshLayout.finishLoadMore();
                        }
                    });
        }
    }

    @OnClick({R.id.img_back_detail_special, R.id.img_share_detail_special})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_detail_special) {
            finish();
        } else if (id == R.id.img_share_detail_special) {
            shareSpecial();
        }
    }

    /**
     * 分享专题
     */
    private void shareSpecial() {
        if (!TextUtils.isEmpty(newsId) && !TextUtils.isEmpty(shareTitle) && !TextUtils.isEmpty(shareSummary) && !TextUtils.isEmpty(shareUrl)) {
            Gson gson = new Gson();
            Bundle bundle = new Bundle();
            ShareEntity shareEntity = new ShareEntity();
            shareEntity.setNewsId(newsId);
            shareEntity.setRedirect_url("");
            shareEntity.setSummary(TextUtils.isEmpty(shareSummary) ? Constant.SUMMARY_SHARE : shareSummary);
            shareEntity.setTitle(TextUtils.isEmpty(shareTitle) ? Constant.TILE_SHARE : shareTitle);
            shareEntity.setUrl(TextUtils.isEmpty(shareUrl) ? "" : shareUrl);
            shareEntity.setType(Constant.TYPE_SHARE_NEWS);
            String shareContent = gson.toJson(shareEntity);
            bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
            IntentUtil.startActivity(this, ShareActivity.class, bundle);
        } else {
            ToastUtil.showShortToast("分享参数不全");
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestSpecialListDetail();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading && loading.isShow()) {
            loading.close();
        }
        unbinder.unbind();
    }
}