package cn.cc1w.app.ui.ui.home.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.details_apps.AppsDetailNewsAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.NewsEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.KeybordUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 搜索
 *
 * @author kpinfo
 */
public class SearchActivity extends CustomActivity implements TagFlowLayout.OnTagClickListener, TextView.OnEditorActionListener, OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.txt_search)
    TextView searchBtn;
    @BindView(R.id.edit_search)
    EditText searchEdit;
    @BindView(R.id.ll_keyword_search)
    LinearLayout keyWordLayout;
    private Unbinder unbinder;
    @BindView(R.id.layout_tag_search)
    TagFlowLayout tagLayout;
    @BindView(R.id.list_recommend_search)
    RecyclerView recommendList;
    @BindView(R.id.blank_search)
    BlankView blankView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private AppsDetailNewsAdapter adapter;
    private TagAdapter tagAdapter;
    private LoadingDialog loading;
    private Drawable selectDrawable;
    private int currentPageIndex = 1;
    private String searchContentStr;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        tagLayout.setOnTagClickListener(this);
        lastTime = System.currentTimeMillis();
        initDrawable();
        initBlankView();
        initList();
        initLoading();
        initSearchEdit();
        requestRecommendList();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(R.mipmap.search_empty, getString(R.string.hint_search_none));
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化Edit
     */
    private void initSearchEdit() {
        searchEdit.setOnEditorActionListener(this);
    }

    /**
     * 初始化List
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
     * 初始化 drawable
     */
    private void initDrawable() {
        selectDrawable = ContextCompat.getDrawable(this, R.drawable.bg_container_keyword_hl);
    }

    /**
     * 请求推荐列表
     */
    @SuppressLint("InflateParams")
    private void requestRecommendList() {
        if (NetUtil.isNetworkConnected(this)) {
            LogUtil.e("requestRecommendList !!!!    url = " + (Constant.KEY_SEARCH_NEW));
            //  改成新版本的 2021-07-14
            RxHttp.get(Constant.KEY_SEARCH_NEW)
                    .asResponseList(String.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && null != list && !list.isEmpty()) {
                            tagAdapter = new TagAdapter<String>(list) {
                                @Override
                                public View getView(FlowLayout parent, int position, String str) {
                                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_tag, null);
                                    TextView tv = view.findViewById(R.id.tv_title_tag);
                                    if (null != tv) {
                                        if (position == 0) {
                                            tv.setBackground(selectDrawable);
                                            tv.setTextColor(Color.WHITE);
                                        }
                                        tv.setText(str);
                                        return tv;
                                    }
                                    return null;
                                }
                            };
                            tagLayout.setAdapter(tagAdapter);
                            keyWordLayout.setVisibility(View.VISIBLE);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * Tag 点击
     *
     * @param view     点击的View
     * @param position 点击的位置
     * @param parent   点击条目的父级
     * @return 是否点击
     */
    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
        String item = (String) tagAdapter.getItem(position);
        searchContentStr = item;
        searchEdit.setText(searchContentStr);
        if (!TextUtils.isEmpty(item)) {
            doSearch(item, true);
        }
        return true;
    }

    /**
     * 进行搜索
     *
     * @param searchContent 搜索内容
     */
    private void doSearch(String searchContent, boolean showLoading) {
        hideKeyBord();
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
            if (null != refreshLayout) {
                refreshLayout.finishRefresh();
            }
        } else {
            currentPageIndex = 1;
            if (null != loading && showLoading) {
                loading.show();
            }
            RxHttp.postJson(Constant.SEARCH_FEED_NEW).add("searchkey", searchContent).add(Constant.STR_INDEX_PAGE, String.valueOf(currentPageIndex))
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (!isFinishing()) {
                            if (null != dataSet && !dataSet.isEmpty()) {
                                keyWordLayout.setVisibility(View.GONE);
                                blankView.setVisibility(View.GONE);
                                adapter.setData(dataSet);
                                currentPageIndex += 1;
                                if (null != recommendList) {
                                    recommendList.setVisibility(View.VISIBLE);
                                }
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(true);
                                }
                            } else {
                                blankView.setVisibility(View.VISIBLE);
                                keyWordLayout.setVisibility(View.GONE);
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(false);
                                }
                            }
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    /**
     * 加载更多数据
     *
     * @param searchContent 搜索内容
     */
    private void loadMoreData(String searchContent) {
        hideKeyBord();
        if (!NetUtil.isNetworkConnected(this)) {
            if (null != refreshLayout) {
                refreshLayout.finishLoadMore();
            }
            ToastUtil.showShortToast(getString(R.string.network_error));
        } else {
            RxHttp.postJson(Constant.SEARCH_FEED_NEW).add("searchkey", searchContent).add(Constant.STR_INDEX_PAGE, String.valueOf(currentPageIndex))
                    .asResponseList(NewsEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(dataSet -> {
                        if (!isFinishing()) {
                            if (null != dataSet && !dataSet.isEmpty()) {
                                recommendList.setVisibility(View.VISIBLE);
                                keyWordLayout.setVisibility(View.GONE);
                                adapter.addData(dataSet);
                                currentPageIndex += 1;
                                refreshLayout.setEnableLoadMore(true);
                            } else {
                                if (null != refreshLayout) {
                                    refreshLayout.setEnableLoadMore(false);
                                }
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

    /**
     * 关闭输入法
     */
    private void hideKeyBord() {
        KeybordUtil.closeInputMethod(this);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (!TextUtils.isEmpty(searchContentStr)) {
            doSearch(searchContentStr, false);
        } else {
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadMoreData(searchContentStr);
    }

    @OnClick({R.id.img_back_search, R.id.txt_search})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_search) {
            finish();
        } else if (id == R.id.txt_search) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                String searchContent = searchEdit.getText().toString();
                if (!TextUtils.isEmpty(searchContent)) {
                    keyWordLayout.setVisibility(View.GONE);
                    recommendList.setVisibility(View.VISIBLE);
                    searchContentStr = searchContent;
                    doSearch(searchContent, true);
                } else {
                    ToastUtil.showShortToast("请输入搜索内容");
                }
            }
            lastTime = currentTime;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            searchContentStr = TextUtils.isEmpty(searchEdit.getText().toString()) ? "" : searchEdit.getText().toString();
            if (!TextUtils.isEmpty(searchContentStr)) {
                doSearch(searchContentStr, true);
            } else {
                ToastUtil.showShortToast("搜索内容不能为空");
            }
            hideKeyBord();
            return true;
        }
        return false;
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