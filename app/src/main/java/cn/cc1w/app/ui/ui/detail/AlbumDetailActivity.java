package cn.cc1w.app.ui.ui.detail;

import android.annotation.SuppressLint;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.rxjava.rxlife.RxLife;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.viewpager.LinViewpager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.album.AlbumDetailAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AlbumEntity;
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity;
import cn.cc1w.app.ui.ui.share.ShareActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 图集详情
 *
 * @author kpinfo
 */
public class AlbumDetailActivity extends CustomActivity implements BlankViewClickListener {
    private Unbinder unbinder;
    private String albumId;
    @BindView(R.id.viewPager_detail_apps)
    LinViewpager viewPager;
    @BindView(R.id.blankView_detail_apps)
    BlankView blankView;
    @BindView(R.id.txt_title_detail_apps)
    TextView titleTv;
    @BindView(R.id.txt_pos_current_detail_apps)
    TextView currentPosTv;
    @BindView(R.id.txt_cnt_detail_apps)
    TextView totalPageTv;
    @BindView(R.id.txt_describe_detail_apps)
    TextView describeTv;
    private LoadingDialog loading;
    private List<AlbumEntity.DataBean> list = new ArrayList<>();
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarDarkFont(false).transparentStatusBar().init();
        albumId = getIntent().getStringExtra(Constant.TAG_ID);
        lastTime = System.currentTimeMillis();
        viewPager.setIsCanScroll(true);
        initLoading();
        initBlankView();
        requestData();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setOnBlankViewClickListener(this);
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.DETAIL_ALBUM)
                    .add("cw_news_id", albumId)
                    .asResponseList(AlbumEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        list = data;
                        if (null != list && !list.isEmpty()) {
                            AlbumEntity.DataBean item = list.get(0);
                            blankView.setVisibility(View.GONE);
                            viewPager.setVisibility(View.VISIBLE);
                            AlbumDetailAdapter adapter = new AlbumDetailAdapter(AlbumDetailActivity.this, list);
                            viewPager.setAdapter(adapter);
                            titleTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
                            currentPosTv.setText(String.valueOf(1));
                            totalPageTv.setText(" / " + (list.size()));
                            describeTv.setText(TextUtils.isEmpty(item.getSummary()) ? "" : item.getSummary());
                            viewPager.addOnPageChangeListener(pageChangeListener);
                        } else {
                            viewPager.setVisibility(View.GONE);
                            blankView.setBlankView(R.mipmap.news_empty, getString(R.string.news_none));
                            blankView.setVisibility(View.VISIBLE);
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            viewPager.setVisibility(View.GONE);
                            blankView.setBlankView(R.mipmap.error_page, getString(R.string.network_error));
                            blankView.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            if (!isFinishing()) {
                viewPager.setVisibility(View.GONE);
                blankView.setBlankView(R.mipmap.error_page, getString(R.string.network_error));
                blankView.setVisibility(View.VISIBLE);
            }
        }
    }

    private final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageSelected(int position) {
            AlbumEntity.DataBean item = list.get(position);
            if (null != item && !isFinishing()) {
                titleTv.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
                currentPosTv.setText(String.valueOf(position + 1));
                totalPageTv.setText(" / " + (list.size()));
                describeTv.setText(TextUtils.isEmpty(item.getSummary()) ? "" : item.getSummary());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @OnClick({R.id.img_back_detail_apps, R.id.img_share_detail_album})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_detail_apps) {
            finish();
        } else if (id == R.id.img_share_detail_album) {
            doShare();
        }
    }

    /**
     * 分享图集详情
     */
    private void doShare() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            if (null != list && !TextUtils.isEmpty(albumId) && !list.isEmpty()) {
                AlbumEntity.DataBean item = list.get(0);
                Gson gson = new Gson();
                Bundle bundle = new Bundle();
                ShareEntity shareEntity = new ShareEntity();
                shareEntity.setNewsId(TextUtils.isEmpty(item.getNews_id()) ? "" : item.getNews_id());
                shareEntity.setRedirect_url("");
                shareEntity.setSummary(TextUtils.isEmpty(item.getSummary()) ? Constant.SUMMARY_SHARE : item.getSummary());
                shareEntity.setTitle(TextUtils.isEmpty(item.getTitle()) ? Constant.TILE_SHARE : item.getTitle());
                shareEntity.setUrl(TextUtils.isEmpty(item.getUrl()) ? "" : item.getUrl());
                shareEntity.setType(Constant.TYPE_SHARE_NEWS);
                String shareContent = gson.toJson(shareEntity);
                bundle.putString(Constant.TAG_SHARE_CONTENT, shareContent);
                IntentUtil.startActivity(this, ShareActivity.class, bundle);

            } else {
                ToastUtil.showShortToast("分享参数不全");
            }
        }
        lastTime = currentTime;
    }

    @Override
    public void onBlankViewClickListener(View view) {
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}
