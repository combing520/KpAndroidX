package cn.cc1w.app.ui.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.rxjava.rxlife.RxLife;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.xutils.DbManager;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import app.cloud.ccwb.cn.linlibrary.blankview.interfaces.BlankViewClickListener;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.qrscan.Intents;
import app.cloud.ccwb.cn.linlibrary.qrscan.util.CodeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.FunctionNewAdapter;
import cn.cc1w.app.ui.adapter.HomeFunctionBannerAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.detail.VideoPlayDetailActivity;
import cn.cc1w.app.ui.utils.ThreadUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.FunctionAdvertisement;
import cn.cc1w.app.ui.entity.FunctionEntity;
import cn.cc1w.app.ui.entity.ScanResultEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.home.home.SearchActivity;
import cn.cc1w.app.ui.ui.home.record.PaikewActivity;
import cn.cc1w.app.ui.ui.home.television.TelevisionStationActivity;
import cn.cc1w.app.ui.ui.qr.QrCodeScanActivity;
import cn.cc1w.app.ui.ui.qr.ScanResultActivity;
import cn.cc1w.app.ui.ui.usercenter.broke.BrokeActivity;
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.DatabaseOpenHelper;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.utils.UriUtils;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser.MsgResonse;
import cn.kpinfo.log.KpLog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

import rxhttp.RxHttp;

/**
 * @author kpinfo
 * 功能
 */
public class FunctionFragment extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, EasyPermissions.PermissionCallbacks, BlankViewClickListener {
    private View decorView;
    private Unbinder unbinder;
    @BindView(R.id.function_recycleView)
    RecyclerView mFunctionRecycleView;
    @BindView(R.id.function_banner)
    Banner mBanner;
    @BindView(R.id.function_container)
    ConstraintLayout mBannerContainer;
    @BindView(R.id.function_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.topContainer)
    FrameLayout topContainer;
    @BindView(R.id.scanImg)
    ImageView scanImg;
    @BindView(R.id.searchImg)
    ImageView searchImg;
    @BindView(R.id.logoImg)
    ImageView logoImg;
    @BindView(R.id.blankView)
    BlankView blankView;
    private FunctionNewAdapter mAdapter;
    private static final int CNT_GRID = 4;
    private Context mContext;
    private long lastTime;
    private List<FunctionAdvertisement.DataBean> mDataSet = new ArrayList<>();
    private static final int REQUEST_CODE_SCAN = 0X03;
    private static final int CODE_PARSE = 4;
    private static final int CODE_REQUEST_PERMISSION_SCAN = 100;
    private LoadingDialog loading;
    private boolean isBlankViewCanClick = true;
    private boolean hasBannerData = false;
    private boolean hasFunctionData = false;
    private static final String TYPE_PIC_AD = "1";
    private static final String TYPE_GIF_AD = "2";
    private static final String TYPE_VIDEO_AD = "3";
    private HomeFunctionBannerAdapter homeFunctionBannerAdapter;
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

    public FunctionFragment() {
    }

    public static FunctionFragment newInstance() {
        FunctionFragment fragment = new FunctionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (decorView == null) {
            decorView = inflater.inflate(R.layout.fragment_functionragment, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        initBlankView();
        setRedThemeInfo();
        initLoading();
        initBanner();
        initRecycleView();
        initRefreshLayout();
    }

    private void initBlankView() {
        blankView.setBlankView(R.mipmap.news_empty, "暂无推荐~ 点击重试", getString(R.string.try_again));
        blankView.setOnBlankViewClickListener(this);
    }

    private void setRedThemeInfo() {
        if (Constant.IS_SHOW_RED_MODE) {
            topContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_red_theme));
            searchImg.setImageResource(R.mipmap.search_red);
            scanImg.setImageResource(R.mipmap.saoyisao_red);
            logoImg.setImageResource(R.mipmap.kaipinglogo2);
        }
    }

    private void initLoading() {
        loading = AppUtil.getLoading(mContext);
    }

    private void initBanner() {
        homeFunctionBannerAdapter = new HomeFunctionBannerAdapter(mDataSet);
        mBanner.setAdapter(homeFunctionBannerAdapter).setIndicator(new CircleIndicator(mContext));
    }

    private void initRecycleView() {
        mAdapter = new FunctionNewAdapter();
        mFunctionRecycleView.setLayoutManager(new GridLayoutManager(mContext, CNT_GRID));
        mAdapter.setOnFunctionClickListener(this);
        mFunctionRecycleView.setAdapter(mAdapter);
    }

    /**
     * 初始化 refresh信息
     */
    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(
                Color.parseColor("#d7a101"),
                Color.parseColor("#54c745"),
                Color.parseColor("#f16161"),
                Color.BLUE, Color.YELLOW);
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        lastTime = System.currentTimeMillis();
        getCacheData();
        requestData();
    }

    private void getCacheData() {
        getFunctionBannerCache();
        getFunctionCache();
    }

    private void requestData() {
        if (NetUtil.isNetworkConnected(mContext)) {
            getFunctionInfo();
        } else {
            if (null != mRefreshLayout) {
                mRefreshLayout.setRefreshing(false);
            }
            isBlankViewCanClick = true;
            showBlankView(!hasBannerData || !hasFunctionData);
        }
    }

    private void getFunctionBannerCache() {
        try {
            List<FunctionAdvertisement.DataBean> list = DatabaseOpenHelper.getInstance().findAll(FunctionAdvertisement.DataBean.class);
            if (null != list && !list.isEmpty()) {
                mDataSet = list;
                setBannerData(mDataSet);
                mBannerContainer.setVisibility(View.VISIBLE);
                hasBannerData = true;
            } else {
                mBannerContainer.setVisibility(View.GONE);
            }
        } catch (DbException e) {
            e.printStackTrace();
            if (null != mBannerContainer) {
                mBannerContainer.setVisibility(View.GONE);
            }
        }
    }

    private void getFunctionCache() {
        try {
            List<FunctionEntity.ItemFunctionEntity> list = DatabaseOpenHelper.getInstance().findAll(FunctionEntity.ItemFunctionEntity.class);
            if (null != list && !list.isEmpty()) {
                mAdapter.setData(list);
                hasFunctionData = true;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void showLoading() {
        if (null != loading && !loading.isShow()) {
            loading.show();
        }
    }

    private void hideLoading() {
        if (null != loading && loading.isShow()) {
            loading.close();
        }
    }

    /**
     * 获取 广告信息
     */
    private void getBannerInfo() {
        RxHttp.get(Constant.FUNCTION_ADVERTISEMENT)
                .asResponseList(FunctionAdvertisement.DataBean.class)
                .as(RxLife.asOnMain(this))
                .subscribe(list -> {
                    if (list != null && !list.isEmpty()) {
                        hasBannerData = true;
                        mDataSet = list;
                        mBannerContainer.setVisibility(View.VISIBLE);
                        setBannerData(mDataSet);
                        try {
                            DbManager db = DatabaseOpenHelper.getInstance();
                            db.delete(FunctionAdvertisement.DataBean.class);
                            db.saveOrUpdate(mDataSet);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        showBlankView(false);
                        isBlankViewCanClick = false;
                    } else {
                        mBannerContainer.setVisibility(View.GONE);
                        hasBannerData = false;
                    }
                    if (null != mRefreshLayout) {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, (OnError) error -> {
                    if (null != mRefreshLayout) {
                        mRefreshLayout.setRefreshing(false);
                    }
                    hasBannerData = false;
                    if (!hasFunctionData ) {
                        isBlankViewCanClick = true;
                        showBlankView(true);
                    }
                });
    }

    private void setBannerData(List<FunctionAdvertisement.DataBean> data) {
        homeFunctionBannerAdapter.setDatas(data);
        homeFunctionBannerAdapter.notifyDataSetChanged();
        mBanner.setOnBannerListener((data1, position) -> {
            if (null != mDataSet && !mDataSet.isEmpty() && position < mDataSet.size()) {
                FunctionAdvertisement.DataBean postAdEntity = mDataSet.get(position);
                String newsId = postAdEntity.getId();
                String type = postAdEntity.getType();
                String url = postAdEntity.getUrl();
                String title = postAdEntity.getTitle();
                String summary = postAdEntity.getDesc();
                String picPath = postAdEntity.getPic_path();
                if (!TextUtils.isEmpty(type)) {
                    // 图片
                    if (TextUtils.equals(type, TYPE_PIC_AD) || TextUtils.equals(type, TYPE_GIF_AD)) {
                        if (!TextUtils.isEmpty(url)) {
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(mContext, UrlDetailActivity.class);
                            intent.putExtra(Constant.TAG_URL, url);
                            intent.putExtra(Constant.TAG_TITLE, TextUtils.isEmpty(title) ? "" : title);
                            intent.putExtra(Constant.TAG_ID, newsId);
                            intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(summary) ? "" : summary);
                            mContext.startActivity(intent);
                        }
                    } else if (TextUtils.equals(type, TYPE_VIDEO_AD)) {
                        if (!TextUtils.isEmpty(url)) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, VideoPlayDetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("videoUrl", url);
                            intent.putExtra("videoPostUrl", picPath);
                            mContext.startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取功能区信息
     */
    private void getFunctionInfo() {
        RxHttp.postJson(Constant.FUNCTION_ALL)
                .asResponseList(FunctionEntity.ItemFunctionEntity.class)
                .as(RxLife.asOnMain(this))
                .subscribe(list -> {
                    getBannerInfo();
                    if (list != null && !list.isEmpty()) {
                        hasFunctionData = true;
                        mAdapter.setData(list);
                        try {
                            DbManager db = DatabaseOpenHelper.getInstance();
                            db.delete(FunctionEntity.ItemFunctionEntity.class);
                            db.saveOrUpdate(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        hasFunctionData = false;
                    }
                }, (OnError) error -> {
                    getBannerInfo();
                });
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        FunctionEntity.ItemFunctionEntity entity = mAdapter.getItem(pos);
        if (null != entity) {
            String type = entity.getIn_type();
            if (null != mContext && !TextUtils.isEmpty(type)) {
                Intent intent = new Intent();
                if (TextUtils.equals("proto", type)) {
                    if (TextUtils.equals("broke", entity.getAction())) {
                        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                            intent.setClass(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("from", Constant.RECORD_PAIKEW);
                            mContext.startActivity(intent);
                            return;
                        }
                        if (null != SharedPreferenceUtil.getUserInfo()) {
                            if (TextUtils.isEmpty(SharedPreferenceUtil.getUserInfo().getMobile())) {
                                intent.setClass(mContext, UpdateMobileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("number", "");
                            } else {
                                intent.setClass(mContext, BrokeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            mContext.startActivity(intent);
                        }
                    } else if (TextUtils.equals("paikew", entity.getAction())) {
                        intent.setClass(mContext, PaikewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else if (TextUtils.equals("TV", entity.getAction())) {
                        intent.setClass(mContext, TelevisionStationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } else if (TextUtils.equals(Constant.TAG_URL, type)) {
                    if (!TextUtils.isEmpty(entity.getUrl())) {
                        intent.setClass(mContext, UrlDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.TAG_URL, entity.getUrl());
                        intent.putExtra(Constant.TAG_TITLE, entity.getName());
                        intent.putExtra(Constant.TAG_ID, TextUtils.isEmpty(entity.getId()) ? "" : entity.getId());
                        intent.putExtra(Constant.TAG_SUMMARY, TextUtils.isEmpty(entity.getSummary()) ? "" : entity.getSummary());
                        mContext.startActivity(intent);
                    }
                }
            }
        }
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
            qrIntent.putExtra("isFromFunScan", true);
            startActivityForResult(qrIntent, REQUEST_CODE_SCAN);
        }
    }

    @OnClick({R.id.img_scan_home, R.id.home_search})
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
            }
        }
        lastTime = currentTime;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_SCAN) {
                LogUtil.e("Function onActivityResult " + data);
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

    /**
     * 判断扫描结果
     */
    private void judgeScanResult(String parseResult) {
        if (parseResult.contains("cw_type")) {
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
     * 字符串是否为 url
     */
    private boolean isUrl(String url) {
        boolean result = false;
        if (url.startsWith("http") || url.startsWith("https") || url.startsWith("www") || url.startsWith("ftp")) {
            result = true;
        }
        return result;
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
                    .asResponse(JsonObject.class).as(RxLife.asOnMain(this)).subscribe();
        }
    }

    /**
     * 请求扫码结果
     *
     * @param content 扫码结果
     */
    private void requestScanInfo(final String content) {
        if (NetUtil.isNetworkConnected(mContext)) {
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
        } else {
            ToastUtil.showShortToast("当前网络不可用");
        }
    }

    private void showSnack(String str) {
        if (!TextUtils.isEmpty(str) && scanImg != null) {
            AppUtil.makeLongSnackMsg(scanImg, str);
        }
    }

    private void asyncThread(Runnable runnable) {
//        new Thread(runnable).start();
        ThreadUtil.getInstance().execute(runnable);
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
        } else {  //异步解析
            asyncThread(() -> {
                final String result = CodeUtils.parseCode(path);
                Message message = Message.obtain();
                message.obj = result;
                message.arg1 = CODE_PARSE;
                handler.sendMessage(message);
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("functionScan", message.getLabel())) {
                Intent intent = message.getIntentData();
                if (null == intent) {
                    ToastUtil.showShortToast(mContext.getString(R.string.hit_code_incognizant));
                } else {
                    parsePhoto(intent);
                }
            }
        }
    }

    private void showBlankView(boolean isShow) {
        if (topContainer != null && mRefreshLayout != null && blankView != null) {
            if (isShow) {
                topContainer.setVisibility(View.GONE);
                mRefreshLayout.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
            } else {
                topContainer.setVisibility(View.VISIBLE);
                mRefreshLayout.setVisibility(View.VISIBLE);
                blankView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(mContext, getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        KpLog.getInstance().onAppViewScreenOut(mContext, getClass().getSimpleName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onBlankViewClickListener(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= Constant.TIME_INTERVAL) {
            if (NetUtil.isNetworkConnected(mContext) && isBlankViewCanClick) {
                showBlankView(false);
                requestData();
            }
        }
        lastTime = currentTime;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        unbinder.unbind();
        super.onDestroyView();
    }
}