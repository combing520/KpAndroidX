package cn.cc1w.app.ui.ui.fragment;

import static android.app.Activity.RESULT_OK;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.qrscan.Intents;
import app.cloud.ccwb.cn.linlibrary.qrscan.util.CodeUtils;
import app.cloud.ccwb.cn.linlibrary.viewpager.LinViewpager;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cc1w.app.ui.HomeTitlePagerAdapter;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.BaseFragment;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.ThreadUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.entity.ScanResultEntity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.home.home.ChannelManageActivity;
import cn.cc1w.app.ui.ui.home.home.SearchActivity;
import cn.cc1w.app.ui.ui.qr.QrCodeScanActivity;
import cn.cc1w.app.ui.ui.qr.ScanResultActivity;
import cn.cc1w.app.ui.utils.FileUtils;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.utils.UriUtils;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;

/**
 * 首页
 *
 * @author kpinfo
 */
public class ParentHomeFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks, BlankViewClickListener {
    @BindView(R.id.viewpager_parent_home)
    LinViewpager viewpager;
    @BindView(R.id.tab_parent_home)
    SlidingTabLayout tabLayout;
    @BindView(R.id.blank_parent_home)
    BlankView blankView;
    @BindView(R.id.ll_tab_parent_home)
    RelativeLayout channelContainer;
    @BindView(R.id.topContainer)
    FrameLayout topContainer;
    @BindView(R.id.scanImg)
    ImageView scanImg;
    @BindView(R.id.searchImg)
    ImageView searchImg;
    @BindView(R.id.logoImg)
    ImageView logoImg;
    @BindView(R.id.classifyImg)
    ImageView classifyImg;
    private final String title = "homeTitle";
    private boolean isTabsHasData = false;
    private long lastTime;
    private static final int REQUEST_CODE_SCAN = 0X01;
    private static final int CODE_PARSE = 2;
    private static final int CODE_REQUEST_PERMISSION_SCAN = 100;
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    private boolean isBlankViewCanClick = true;
    private HomeTitlePagerAdapter pagerAdapter;
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (null != msg) {
                if (msg.arg1 == CODE_PARSE) {
                    if (null != msg.obj) {
                        String parseResult = (String) msg.obj;
                        if (!TextUtils.isEmpty(parseResult)) {
                            judgeScanResult(parseResult);
                        }
                    } else {
                        ToastUtil.showShortToast(mContext.getString(R.string.hit_code_incognizant));
                    }
                }
            }
        }
    };

    public static ParentHomeFragment newInstance() {
        LogUtil.e("ParentHomeFragment newInstance " + System.currentTimeMillis());
        ParentHomeFragment fragment = new ParentHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentResId() {
        return R.layout.fragment_parent_home;
    }

    @Override
    protected void onQueryArguments() {
        lastTime = System.currentTimeMillis();
        if (getActivity() != null) {
            getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
    }

    @Override
    protected void onFindView(View rootView) {
        blankView.setBlankView(R.mipmap.news_empty, "你所查看的新闻不存在,点击重试", "新闻");
        setRedThemeInfo();
    }

    @Override
    protected void onBindListener() {
        EventBus.getDefault().register(this);
        blankView.setOnBlankViewClickListener(this);
    }

    @Override
    protected void onApplyData() {
        initTab();
    }

    private void setRedThemeInfo() {
        if (Constant.IS_SHOW_RED_MODE) {
            tabLayout.setTextUnselectColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            tabLayout.setTextSelectColor(ContextCompat.getColor(mContext, R.color.color_golden_theme));
            topContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_red_theme));
            channelContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_red_theme));

            searchImg.setImageResource(R.mipmap.search_red);
            scanImg.setImageResource(R.mipmap.saoyisao_red);
            logoImg.setImageResource(R.mipmap.kaipinglogo2);
            classifyImg.setImageResource(R.mipmap.more_column_red);
        }
    }

    /**
     * 先从缓存获取顶部的导航信息
     */
    private void initTab() {
        try {
            if (mContext.getExternalCacheDir() != null) {
                File file = new File(mContext.getExternalCacheDir().getAbsolutePath().concat(File.separator).concat(title).concat(Constant.SUFFIX_CACHE));
                if (file.exists()) {
                    String content = FileUtils.getContent(file);
                    if (!TextUtils.isEmpty(content)) {
                        List<HomeChannelEntity.ItemHomeChannelEntity> list = JsonUtil.getList(content, HomeChannelEntity.ItemHomeChannelEntity.class);
                        if (!list.isEmpty()) {
                            initTabInfo(list);
                            isTabsHasData = true;
                        } else {
                            requestTopTitles();
                            isTabsHasData = false;
                        }
                    } else {
                        LogUtil.e("initTab   没有数据 ！！！ ");
                        requestTopTitles();
                        isTabsHasData = false;
                    }
                } else {
                    LogUtil.e("不存在 ！！！");
                    requestTopTitles();
                    isTabsHasData = false;
                }
            } else {
                requestTopTitles();
                isTabsHasData = false;
            }
        } catch (Exception e) {
            requestTopTitles();
            isTabsHasData = false;
            e.getStackTrace();
        }
    }

    /**
     * 保存数据到缓存
     *
     * @param result Json数据
     */
    private void saveTitle2Cache(String result) {
        try {
            if (mContext.getExternalCacheDir() != null) {
                isTabsHasData = true;
                FileUtils.saveContent(result, new File(mContext.getExternalCacheDir().getAbsolutePath().concat(File.separator).concat(title).concat(Constant.SUFFIX_CACHE)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 Tab信息
     *
     * @param list 数据源
     */
    private void initTabInfo(List<HomeChannelEntity.ItemHomeChannelEntity> list) {
        List<String> titleList = new ArrayList<>();
        if (null != list && !list.isEmpty()) {
            pagerAdapter = new HomeTitlePagerAdapter(getChildFragmentManager(), list);
            viewpager.setAdapter(pagerAdapter);
            viewpager.setIsCanScroll(true);
            for (HomeChannelEntity.ItemHomeChannelEntity entity : list) {
                titleList.add(entity.getName());
            }
        }
        tabLayout.setViewPager(viewpager, titleList.toArray(new String[]{}));
    }

    /**
     * 解析 相册中获取的二维码信息
     *
     * @param data 数据
     */
    private void parsePhoto(Intent data) {
        final String path = UriUtils.getImagePath(mContext, data);
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showShortToast(mContext.getString(R.string.hit_code_incognizant));
        } else {
            asyncThread(() -> {
                final String result = CodeUtils.parseCode(path);
                Message message = Message.obtain();
                message.obj = result;
                message.arg1 = CODE_PARSE;
                handler.sendMessage(message);
            });
        }
    }

    /**
     * 字符串是否为 url
     */
    private boolean isUrl(String url) {
        return url.startsWith("http") || url.startsWith("https") || url.startsWith("www") || url.startsWith("ftp");
    }

    /**
     * 判断扫描结果
     *
     * @param parseResult 扫描结果
     */
    private void judgeScanResult(String parseResult) {
        if (parseResult.contains("cw_type")) { // 春晚的 二维码
            requestScanInfo(parseResult);
        } else {
            if (isUrl(parseResult)) {
                if (parseResult.contains("cwtype=code")) {
                    addInvitationCodeRecord(parseResult);
                } else {
                    addScanRecord(parseResult);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TAG_URL, parseResult);
                    IntentUtil.startActivity(mContext, UrlDetailActivity.class, bundle);
                }
            } else {
                addScanRecord(parseResult);
                Bundle bundle = new Bundle();
                bundle.putString("result", parseResult);
                IntentUtil.startActivity(mContext, ScanResultActivity.class, bundle);
            }
        }
    }

    /**
     * 扫描处理 邀请码
     */
    private void addInvitationCodeRecord(String parseResult) {
        if (parseResult.contains("sign=")) {
            String[] result = parseResult.split("sign=");
            if (result.length > 1) {
                if (NetUtil.isNetworkConnected(mContext)) {
                    showLoading();
                    RxHttp.postJson(Constant.CODE_SCAN)
                            .add("code", result[result.length - 1])
                            .asMsgResponse(MsgResonse.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                hideLoading();
                                if (data != null) {
                                    ToastUtil.showShortToast(data.getMessage());
                                }
                            }, (OnError) error -> {
                                hideLoading();
                                ToastUtil.showLongToast(error.getErrorMsg());
                            });
                } else {
                    hideLoading();
                    ToastUtil.showShortToast(getResources().getString(R.string.network_error));
                }
            }
        }
    }

    /**
     * 添加扫码记录
     *
     * @param content 扫码的内容
     */
    private void addScanRecord(String content) {
        if (NetUtil.isNetworkConnected(mContext)) {
            RxHttp.postJson(Constant.RECORD_SCAN)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .add("cw_content", content)
                    .asResponse(JsonObject.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe();
        }
    }

    /**
     * 请求顶部title
     */
    private void requestTopTitles() {
        LogUtil.e("ParentHomeFragment  requestTopTitles " + isTabsHasData);
        if (NetUtil.isNetworkConnected(mContext)) {
            if (!isTabsHasData) {
                showLoading();
            }
            RxHttp.postJson(Constant.CHANNEL_INDEX)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(HomeChannelEntity.ItemHomeChannelEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        hideLoading();
                        if (data != null && !data.isEmpty()) {
                            isTabsHasData = true;
                            initTabInfo(data);
                            saveTitle2Cache(new Gson().toJson(data));
                            isBlankViewCanClick = false;
                            showBlankView(false);
                        } else {
                            showBlankView(true);
                            isBlankViewCanClick = true;
                        }
                    }, (OnError) error -> {
                        if (!isTabsHasData) {
                            showBlankView(true);
                        }
                        hideLoading();
                    });
        } else {
            if (!isTabsHasData) {
                isBlankViewCanClick = true;
                showBlankView(true);
            }
        }
    }

    /**
     * 显示 BlankView
     *
     * @param isShow 是否显示
     */
    private void showBlankView(boolean isShow) {
        if (blankView != null && channelContainer != null && viewpager != null) {
            if (isShow) {
                if (blankView.getVisibility() == View.GONE) {
                    blankView.setVisibility(View.VISIBLE);
                }
                if (channelContainer.getVisibility() == View.VISIBLE) {
                    channelContainer.setVisibility(View.GONE);
                }
                if (viewpager.getVisibility() == View.VISIBLE) {
                    viewpager.setVisibility(View.GONE);
                }
            } else {
                if (blankView.getVisibility() == View.VISIBLE) {
                    blankView.setVisibility(View.GONE);
                }
                if (channelContainer.getVisibility() == View.GONE) {
                    channelContainer.setVisibility(View.VISIBLE);
                }
                if (viewpager.getVisibility() == View.GONE) {
                    viewpager.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 请求扫码结果
     *
     * @param content 扫码结果
     */
    private void requestScanInfo(final String content) {
        if (NetUtil.isNetworkConnected(mContext)) {
            showLoading();
            try {
                String content1 = content.replace("\n", "");
                RxHttp.postJson(Constant.URL_SCAN)
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .add("cw_content", content1)
                        .asMessageResponse(ScanResultEntity.DataBean.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(data -> {
                            hideLoading();
                            if (data != null) {
                                if (data.getCode() == Constant.CODE_REQUEST_SUCCESS) {
                                    if (data.getData() != null) {
                                        String type = data.getData().getIn_type();
                                        if (!TextUtils.isEmpty(type)) {
                                            if (TextUtils.equals("proto", data.getData().getIn_type())) {
                                                //  : 进行原生的逻辑
                                            } else if (TextUtils.equals("url", type) && !TextUtils.isEmpty(data.getData().getUrl())) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("url", data.getData().getUrl());
                                                IntentUtil.startActivity(mContext, UrlDetailActivity.class, bundle);
                                            }
                                        } else {
                                            showSnack(data.getMsg());
                                        }
                                    } else {
                                        ToastUtil.showShortToast(data.getMsg());
                                    }
                                } else {
                                    showSnack(data.getMsg());
                                }
                            }
                        }, (OnError) error -> {
                            hideLoading();
                            showSnack(error.getErrorMsg());
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShortToast("当前网络不可用");
        }
    }

    private void showSnack(String str) {
        if (!TextUtils.isEmpty(str) && classifyImg != null) {
            AppUtil.makeLongSnackMsg(classifyImg, str);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("updateUserFocusChannel", message.getLabel())) {
                requestTopTitles();
            } else if (TextUtils.equals("showRecommend", message.getLabel())) {
                viewpager.setCurrentItem(0, false);
            } else if (TextUtils.equals(Constant.TAG_RESULT_SCAN, message.getLabel())) {
                Intent intent = message.getIntentData();
                if (null == intent) {
                    ToastUtil.showShortToast(mContext.getString(R.string.hit_code_incognizant));
                } else {
                    parsePhoto(intent);
                }
            }
        }
    }

    @OnClick({R.id.img_scan_home, R.id.home_search, R.id.img_parent_home})
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
            Intent intent = new Intent();
            int id = view.getId();
            if (id == R.id.img_scan_home) {
                checkQrPermission();
            } else if (id == R.id.home_search) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(mContext, SearchActivity.class);
                startActivity(intent);
            } else if (id == R.id.img_parent_home) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(mContext, ChannelManageActivity.class);
                startActivity(intent);
            }
        }
        lastTime = currentTime;
    }

    @SuppressLint("InflateParams")
    private void checkQrPermission() {
        final String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(mContext, permissionList)) {
            EasyPermissions.requestPermissions(this, "开屏新闻申请以下权限：\n" +
                    "手机存储和相机\n" +
                    "二维码扫描或上传\n" +
                    "权限申请\n" +
                    "为了能够正常的使用二维码扫描/拍摄服务，请允许开屏新闻使用您的手机存储和相机权限。", CODE_REQUEST_PERMISSION_SCAN, permissionList);
        } else {
            Intent qrIntent = new Intent();
            qrIntent.setClass(mContext, QrCodeScanActivity.class);
            startActivityForResult(qrIntent, REQUEST_CODE_SCAN);
        }
    }

    private void asyncThread(Runnable runnable) {
//        new Thread(runnable).start();
        ThreadUtil.getInstance().execute(runnable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_SCAN) {
                LogUtil.e("ParentHomeFragment onActivityResult " + data);
                String result = data.getStringExtra(Intents.Scan.RESULT);
                LogUtil.e("扫描结果 = " + result);
                if (TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(mContext.getString(R.string.hit_code_incognizant));
                } else {
                    judgeScanResult(result);
                }
            }
        }
    }

    @Override
    public void onBlankViewClickListener(View view) {
        if (isBlankViewCanClick && NetUtil.isNetworkConnected(mContext)) {
            LogUtil.d("onBlankViewClick");
            isTabsHasData = false;
            isBlankViewCanClick = false;
            requestTopTitles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtil.e("ParentHomeFragment  onPermissionsGranted code =  " + requestCode);
        if (requestCode == CODE_REQUEST_PERMISSION_SCAN) {
            Intent qrIntent = new Intent();
            qrIntent.setClass(mContext, QrCodeScanActivity.class);
            startActivityForResult(qrIntent, REQUEST_CODE_SCAN);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtil.e("权限拒绝 code = " + requestCode);
        if (requestCode == CODE_REQUEST_PERMISSION_SCAN) {
            ToastUtil.showShortToast("二维码扫描需要获取相机权限");
        }
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void frResume() {
        if (pagerAdapter != null && pagerAdapter.getCurrentFragment() != null) {
            pagerAdapter.getCurrentFragment().frResume();
        }
    }

    @Override
    public void frPause() {
        if (pagerAdapter != null && pagerAdapter.getCurrentFragment() != null) {
            pagerAdapter.getCurrentFragment().frPause();
        }
    }
}