package cn.cc1w.app.ui.widget;

import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_LOW_MEMORY;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_NO_VIDEO_TRACK;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SRC_DST_SAME_FILE_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.rxjava.rxlife.RxLife;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.qrscan.Intents;
import app.cloud.ccwb.cn.linlibrary.qrscan.util.CodeUtils;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppConfigEntity;
import cn.cc1w.app.ui.utils.RecordSettings;
import cn.cc1w.app.ui.utils.pictureSelector.GlideEngine;
import cn.cc1w.app.ui.utils.pictureSelector.MeSandboxFileEngine;
import cn.cc1w.app.ui.entity.ResourceUploadResponseEntity;
import cn.cc1w.app.ui.entity.ScanResultEntity;
import cn.cc1w.app.ui.ui.detail.NewsDetailNewActivity;
import cn.cc1w.app.ui.ui.detail.UrlDetailActivity;
import cn.cc1w.app.ui.ui.qr.ScanResultActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.ImageFilePath;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.utils.UriUtils;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;
import rxhttp.RxHttpFormParam;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

public class MWebView extends WebView {
    private final Context mContext;
    private LoadingDialog loading;
    private LoadingDialog loading2;
    private ValueCallback<Uri[]> mFilePathCallback;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private static final int REQUEST_CODE_SCAN = 0X01;
    private static final int CODE_PARSE = 2;
    private String callback;
    private final String[] permissionList2 = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,};
    private String filePath = "";
    private String contentDisposition = "";
    private String fileType = "";
    public static final int CODE_PIC_SINGLE_REQUEST = 1;
    public static final int CODE_PIC_MULTIPLE_REQUEST = 2;
    public static final int CODE_VIDEO_REQUEST = 3;
    public static final int CODE_ALL_REQUEST = 4;
    public static final int CODE_VIDEO_2_REQUEST = 5;
    public static final int CODE_REQUEST_PERMISSION = 10086;
    private static final String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (null != msg) {
                if (msg.arg1 == CODE_PARSE) {
                    if (null != msg.obj) {
                        String parseResult = (String) msg.obj;
                        judgeScanResult(parseResult);
                    } else {
                        ToastUtil.showShortToast(mContext.getResources().getString(R.string.hit_code_incognizant));
                    }
                }
            }
        }
    };

    public MWebView(Context context) {
        super(context);
        mContext = context;
        init();
        initLoading();
    }

    public MWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
        initLoading();
    }

    public MWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        mContext = context;
        init();
        initLoading();
    }

    @Override
    public void loadUrl(String url) {
        AppConfigEntity.AppConfigDetail config = SharedPreferenceUtil.getAppConfigInfo();
        if (config != null && config.isAllow_jump_url()) {
            boolean result = false;
            if (whiteList == null) {
                whiteList = config.getApp_white_list();
            }
            for (String s : whiteList) {
                if (url.startsWith(s)) {
                    result = true;
                    break;
                }
            }
            if (result) {
                super.loadUrl(url);
            } else {
                ToastUtil.showLongToast("不支持的网址");
                if (canGoBack()) {
                    goBack();
                }
            }
        } else {
            super.loadUrl(url);
        }
    }

    public void setFilePath(String path) {
        filePath = path;
    }

    public String getFilePath() {
        return filePath;
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(mContext);
        loading2 = new LoadingDialog(mContext);
        loading2.setLoadStyle(1);
        loading2.setInterceptBack(false);
        loading2.setLoadingText("上传中");
    }

    private void init() {
        clearCache(true);
        clearHistory();
        WebSettings ws = getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        String ua = AppUtil.setWebViewUserAgent(ws.getUserAgentString());
        ws.setUserAgentString(ua);
        ws.setDomStorageEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setSupportZoom(true);
        ws.setTextSize(WebSettings.TextSize.NORMAL);
        ws.setAppCacheMaxSize(Long.MAX_VALUE);
        ws.setAllowFileAccess(true);
        ws.setUseWideViewPort(true);
        ws.setAppCacheEnabled(true);
        ws.setAllowContentAccess(true);
        ws.setBuiltInZoomControls(true);
        ws.setGeolocationEnabled(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setPluginState(WebSettings.PluginState.ON_DEMAND);
        ws.setAppCachePath(getContext().getDir("appcache", 0).getPath());
        ws.setDatabasePath(getContext().getDir("databases", 0).getPath());
        ws.setGeolocationDatabasePath(getContext().getDir("geolocation", 0).getPath());
        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);
        addJavascriptInterface(new JavaScriptManage(mContext), "Android");
        CookieSyncManager.createInstance(mContext);
        CookieSyncManager.getInstance().sync();
        setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            if (!TextUtils.isEmpty(url)) {
                if (url.contains(File.separator) && url.contains(".")) {
                    showDownLoadConfirmDialog(url, contentDisposition, mimetype);
                } else {
                    showDownLoadConfirmDialog(url, contentDisposition, mimetype);
                }
            }
        });
    }

    /**
     * 显示下载 Dialog
     */
    private void showDownLoadConfirmDialog(String filePath, String contentDisposition, String fileType) {
        if (NetUtil.isNetworkConnected(mContext)) {
            this.filePath = filePath;
            this.contentDisposition = contentDisposition;
            this.fileType = fileType;
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("温馨提示");
            alert.setMessage("确认下载该文件?");
            alert.setCancelable(true);
            alert.setPositiveButton("确定", (dialog, which) -> {
                if (!EasyPermissions.hasPermissions(mContext, permissionList2)) {
                    EasyPermissions.requestPermissions((Activity) mContext, "开屏新闻需要获取SD卡读写权限用于读写缓存", CODE_REQUEST_PERMISSION, permissionList2);
                } else {
                    ToastUtil.showShortToast("下载开始");
                    if (!TextUtils.isEmpty(filePath) && filePath.startsWith("http")) {
                        downLoadFile(filePath, TextUtils.isEmpty(contentDisposition) ? "" : contentDisposition, TextUtils.isEmpty(fileType) ? "" : fileType);
                    }
                }
                dialog.dismiss();
            });
            alert.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
            alert.setCancelable(false);
            alert.create();
            alert.show();
        } else {
            ToastUtil.showShortToast(mContext.getResources().getString(R.string.network_error));
        }
    }

    public void downLoadFile() {
        downLoadFile(filePath, TextUtils.isEmpty(contentDisposition) ? "" : contentDisposition, TextUtils.isEmpty(fileType) ? "" : fileType);
    }

    private void downLoadFile(String filePath, String contentDisposition, String fileType) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(filePath));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverMetered(false);
            request.setVisibleInDownloadsUi(false);
            request.setAllowedOverRoaming(true);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            String fileSuffix = "";
            String fileName = "";
            if (filePath.contains(File.separator)) {
                fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            } else {
                fileName = URLUtil.guessFileName(filePath, TextUtils.isEmpty(fileName) ? "" : contentDisposition, TextUtils.isEmpty(fileSuffix) ? "" : fileType);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            final DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            if (null != downloadManager) {
                downloadManager.enqueue(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> whiteList;
    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d("当前网页 地址  = " + url);
            if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                if (isUrl(url)) {
                    if (url.contains(".")) {
                        String suffer = url.substring(url.lastIndexOf(".") + 1);
                        LogUtil.d("suffer =  " + suffer);
                        if (!Constant.LIST_FILE_DOWNLOAD.isEmpty()) {
                            if (Constant.LIST_FILE_DOWNLOAD.contains(suffer)) {
                                LogUtil.d("包含,需要进行下载 ！！！ " + ".");
                                String filename = url.substring(url.lastIndexOf(File.separator) + 1);
                                File file = new File(mContext.getCacheDir().getAbsolutePath(), filename);
                                LogUtil.d(" fileName = " + filename);
                                if (file.exists() && file.isFile()) {
                                    ToastUtil.showShortToast("该文件已下载");
                                } else {
                                    showDownLoadConfirmDialog(url, url, suffer);
                                }
                            } else {
                                LogUtil.d("不包含 ！！！");
                                if (isUrlCanCloseDirect(url)) {
                                    return false;
                                } else {
                                    doUrlLoad(view, url);
                                }
                            }
                        } else {
                            if (isUrlCanCloseDirect(url)) {
                                return false;
                            } else {
                                doUrlLoad(view, url);
                            }
                        }
                    } else {
                        if (isUrlCanCloseDirect(url)) {
                            return false;
                        } else {
                            doUrlLoad(view, url);
                        }
                    }
                }
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (null != loading && !loading.isShow()) {
                loading.show();
            }
            LogUtil.e("onPageStarted  url = " + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (null != loading && loading.isShow()) {
                loading.close();
            }
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError sslError) {
            handler.proceed();
        }
    };

    private void doUrlLoad(WebView view, String url) {
        Activity act = (Activity) mContext;
        if (act != null) {
            if (act instanceof NewsDetailNewActivity) {
                goToUrlDetail(url);
            } else if (act instanceof UrlDetailActivity) {
                if (view != null && !TextUtils.isEmpty(url)) {
                    view.loadUrl(url);
                }
            }
        } else {
            goToUrlDetail(url);
        }
    }

    private void goToUrlDetail(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_URL, url);
        IntentUtil.startActivity(mContext, UrlDetailActivity.class, bundle);
    }

    private boolean isUrlCanCloseDirect(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        } else {
            return url.contains("m.yizhibo.com") || url.contains("www.xinhuanet.com") ||
                    url.contains("www.people.com.cn") || url.contains("www.thepaper.cn") ||
                    url.contains("www.12371.cn") || url.contains("www.qq.com") ||
                    url.contains("news.sohu.com") || url.contains("cpc.people.com.cn") || url.contains("news.sina.com.cn");
        }
    }

    private OnPageFinishListener onPageFinishListener;

    public void setOnPageFinishListener(OnPageFinishListener listener) {
        this.onPageFinishListener = listener;
    }

    public interface OnPageFinishListener {
        void onFinish(String title);

        void onProgressChanged(int newProgress);
    }

    private final WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!TextUtils.isEmpty(title)) {
                if (onPageFinishListener != null) {
                    onPageFinishListener.onFinish(title);
                }
            }
        }

        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
            showCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            hideCustomView();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (onPageFinishListener != null) {
                onPageFinishListener.onProgressChanged(newProgress);
            }
            if (newProgress >= 70) {
                if (null != loading && loading.isShow()) {
                    loading.close();
                }
            }
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
            mFilePathCallback = filePathCallback;
            if (null != fileChooserParams.getAcceptTypes() && null != fileChooserParams.getAcceptTypes()) {
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
                if (null != acceptTypes) {
                    if (("image/*").equals(acceptTypes[0])) { // 图片
                        LogUtil.e("onShowFileChooser " + " 获取图片类型 type = " + fileChooserParams.getMode());
                        if (fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) { //多 图片
                            LogUtil.e("多图片");
                            if (!EasyPermissions.hasPermissions(mContext, permissionList)) {
                                EasyPermissions.requestPermissions(((Activity) mContext), "开屏新闻申请以下权限\n\n" +
                                        "手机存储和相机\n" + "拍照或上传图片，以及视频录制\n" +
                                        "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", CODE_PIC_MULTIPLE_REQUEST, permissionList);
                            } else {
                                openMultiplePic();
                            }
                        } else {
                            LogUtil.e("单图");
                            if (!EasyPermissions.hasPermissions((Activity) mContext, permissionList)) {
                                EasyPermissions.requestPermissions((Activity) mContext, "开屏新闻申请以下权限\n\n" +
                                        "手机存储和相机\n" + "拍照或上传图片，以及视频录制\n" +
                                        "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", CODE_PIC_SINGLE_REQUEST, permissionList);
                            } else {
                                openInputFileAppImage();
                            }
                        }
                    }
                    // 视频
                    else if (acceptTypes[0].equals("video/*") || webView.getUrl().startsWith("https://ida.webank.com/") || acceptTypes[0]
                            .equals("video/webank")) {
                        if (!EasyPermissions.hasPermissions((Activity) mContext, permissionList)) {
                            EasyPermissions.requestPermissions((Activity) mContext, "开屏新闻申请以下权限\n" +
                                    "手机存储和相机\n" + "拍照或上传图片，以及视频录制\n" + "权限申请\n" +
                                    "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", CODE_VIDEO_REQUEST, permissionList);
                        } else {
                            openInputFileVideo();
                        }
                    } else if (("*/*").equals(acceptTypes[0])) { // 所有文件
                        if (!EasyPermissions.hasPermissions((Activity) mContext, permissionList)) {
                            EasyPermissions.requestPermissions((Activity) mContext, "开屏新闻申请以下权限\n" +
                                    "手机存储和相机\n" + "拍照或上传图片，以及视频录制\n" + "权限申请\n" +
                                    "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", CODE_ALL_REQUEST, permissionList);
                        } else {
                            pickAllTypeFile();
                        }
                    } else {
                        if (!EasyPermissions.hasPermissions((Activity) mContext, permissionList)) {
                            EasyPermissions.requestPermissions((Activity) mContext, "开屏新闻申请以下权限\n" +
                                    "手机存储和相机\n" + "拍照或上传图片，以及视频录制\n" + "权限申请\n" +
                                    "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", CODE_ALL_REQUEST, permissionList);
                        } else {
                            pickAllTypeFile();
                        }
                    }
                } else {
                    if (!EasyPermissions.hasPermissions((Activity) mContext, permissionList)) {
                        EasyPermissions.requestPermissions((Activity) mContext, "开屏新闻申请以下权限\n" +
                                "手机存储和相机\n" + "拍照或上传图片，以及视频录制\n" + "权限申请\n" +
                                "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", CODE_ALL_REQUEST, permissionList);
                    } else {
                        pickAllTypeFile();
                    }
                }
            } else {
                LogUtil.e("为空 ");
                if (!EasyPermissions.hasPermissions((Activity) mContext, permissionList)) {
                    EasyPermissions.requestPermissions((Activity) mContext, "开屏新闻申请以下权限\n" +
                            "手机存储和相机\n" + "拍照或上传图片，以及视频录制\n" + "权限申请\n" +
                            "为了能够正常的使用拍照、图片、视频录制服务，请允许开屏新闻使用您的手机存储和相机权限", CODE_ALL_REQUEST, permissionList);
                } else {
                    pickAllTypeFile();
                }
            }
            return true;
        }

        @Override
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            ((Activity) mContext).startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            ToastUtil.showLongToast(message);
            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            LogUtil.e("CommonQQX5WebActivity onConsoleMessage 打印信息:" + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }
    };
    private View customView;
    private FrameLayout fullscreenContainer;
    private final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private IX5WebChromeClient.CustomViewCallback customViewCallback;

    static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ContextCompat.getColor(ctx,android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void showCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }
        try {
            ((Activity) mContext).getWindow().getDecorView();
            FrameLayout decor = (FrameLayout) ((Activity) mContext).getWindow().getDecorView();
            fullscreenContainer = new FullscreenHolder(mContext);
            fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
            customView = view;
            customViewCallback = callback;
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ((Activity) mContext).getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void hideCustomView() {
        if (customView == null) {
            return;
        }
        try {
            FrameLayout decor = (FrameLayout) ((Activity) mContext).getWindow().getDecorView();
            decor.removeView(fullscreenContainer);
            fullscreenContainer = null;
            customView = null;
            customViewCallback.onCustomViewHidden();
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            final WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) mContext).getWindow().setAttributes(attrs);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openInputFileAppImage() {
        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        ImageEngine imageEngine = GlideEngine.createGlideEngine();
        PictureSelector.create(mContext).openGallery(SelectMimeType.ofImage()).setSelectorUIStyle(selectorStyle)
                .setImageEngine(imageEngine).isQuickCapture(true).setMaxSelectNum(1).setMinSelectNum(0)
                .setCompressEngine((CompressFileEngine) (context, source, call) -> Luban.with(context).load(source).ignoreBy(100)
                        .setCompressListener(new OnNewCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String source, File compressFile) {
                                if (call != null) {
                                    call.onCallback(source, compressFile.getAbsolutePath());
                                }
                            }

                            @Override
                            public void onError(String source, Throwable e) {
                                if (call != null) {
                                    call.onCallback(source, null);
                                }
                            }
                        }).launch())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> selectList) {
                        if (mFilePathCallback != null) {
                            if (null != selectList && !selectList.isEmpty()) {
                                Uri[] results;
                                String picPath = TextUtils.isEmpty(selectList.get(0).getCompressPath()) ? selectList.get(0).getPath() : selectList.get(0).getCompressPath();
                                if (!TextUtils.isEmpty(picPath)) {
                                    File file = new File(picPath);
                                    Uri uri = Uri.fromFile(file);
                                    if (null != uri) {
                                        results = new Uri[]{uri};
                                        try {
                                            mFilePathCallback.onReceiveValue(results);
                                            mFilePathCallback = null;
                                        } catch (Exception e) {
                                            LogUtil.e("UrlDetailActivity 上传照片出错 =  ");
                                        }
                                    } else {
                                        mFilePathCallback = null;
                                    }
                                }
                            } else {
                                try {
                                    mFilePathCallback.onReceiveValue(null);
                                    mFilePathCallback = null;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtil.e("UrlDetailActivity 上传照片出错 =  ");
                                    mFilePathCallback = null;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    // 选择多图片
    public void openMultiplePic() {
        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        ImageEngine imageEngine = GlideEngine.createGlideEngine();
        PictureSelector.create(mContext).openGallery(SelectMimeType.ofImage()).setSelectorUIStyle(selectorStyle)
                .setImageEngine(imageEngine).isQuickCapture(true).setMaxSelectNum(9).setMinSelectNum(0)
                .setCompressEngine((CompressFileEngine) (context, source, call) -> Luban.with(context).load(source).ignoreBy(100)
                        .setCompressListener(new OnNewCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String source, File compressFile) {
                                if (call != null) {
                                    call.onCallback(source, compressFile.getAbsolutePath());
                                }
                            }

                            @Override
                            public void onError(String source, Throwable e) {
                                if (call != null) {
                                    call.onCallback(source, null);
                                }
                            }
                        }).launch())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> selectList) {
                        if (selectList != null && !selectList.isEmpty()) {
                            try {
                                Uri[] uris = new Uri[selectList.size()];
                                for (int i = 0; i < selectList.size(); i++) {
                                    String path;
                                    path = TextUtils.isEmpty(selectList.get(i).getCompressPath()) ? selectList.get(i).getPath() : selectList.get(i).getCompressPath();
                                    File f = new File(path);
                                    uris[i] = Uri.fromFile(f);
                                }
                                mFilePathCallback.onReceiveValue(uris);
                                mFilePathCallback = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                mFilePathCallback.onReceiveValue(null);
                                mFilePathCallback = null;
                            } catch (Exception e) {
                                LogUtil.e("UrlDetailActivity 上传多图片出错 =  ");
                                mFilePathCallback = null;
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    public void openInputFileVideo() {
        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        ImageEngine imageEngine = GlideEngine.createGlideEngine();
        PictureSelector.create(mContext).openGallery(SelectMimeType.ofVideo()).setSelectorUIStyle(selectorStyle)
                .setImageEngine(imageEngine).isQuickCapture(true).setMaxSelectNum(1).setMinSelectNum(0)
                .setRecordVideoMaxSecond(Constant.TIME_MAX_VIDEO_UPDATE_WEB)
                .setRecordVideoMinSecond(Constant.TIME_MIN_VIDEO_UPDATE_WEB)
                .setFilterVideoMaxSecond(Constant.TIME_MAX_VIDEO_UPDATE_WEB)
                .setFilterVideoMinSecond(Constant.TIME_MIN_VIDEO_UPDATE_WEB)
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> selectList) {
                        if (mFilePathCallback != null) {
                            Uri[] results;
                            if (null != selectList && !selectList.isEmpty()) {
                                String videoPath = selectList.get(0).getPath();
                                LogUtil.e("视频地址 = " + videoPath);
                                if (!TextUtils.isEmpty(videoPath)) {
                                    File f = new File(videoPath);
                                    Uri uri = Uri.fromFile(f);
                                    if (null != uri) {
                                        results = new Uri[]{uri};
                                        try {
                                            mFilePathCallback.onReceiveValue(results);
                                            mFilePathCallback = null;
                                        } catch (Exception e) {
                                            LogUtil.e("UrlDetailActivity 上传视频出错 =  " + e.getMessage());
                                            e.printStackTrace();
                                            mFilePathCallback = null;
                                        }
                                    } else {
                                        mFilePathCallback = null;
                                    }
                                }
                            } else {
                                try {
                                    mFilePathCallback.onReceiveValue(null);
                                    mFilePathCallback = null;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    // 选择所有文件
    public void pickAllTypeFile() {
        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        ImageEngine imageEngine = GlideEngine.createGlideEngine();
        PictureSelector.create(mContext).openGallery(SelectMimeType.ofAll()).setSelectorUIStyle(selectorStyle).setImageEngine(imageEngine)
                .isQuickCapture(true).setMaxSelectNum(1).setMinSelectNum(0)
                .setRecordVideoMaxSecond(Constant.TIME_MAX_VIDEO_UPDATE_WEB)
                .setRecordVideoMinSecond(Constant.TIME_MIN_VIDEO_UPDATE_WEB)
                .setSelectMaxDurationSecond(Constant.TIME_MAX_VIDEO_UPDATE_WEB + 1)
                .setSelectMinDurationSecond(Constant.TIME_MIN_VIDEO_UPDATE_WEB)
                .setFilterVideoMaxSecond(Constant.TIME_MAX_VIDEO_UPDATE_WEB + 1)
                .setFilterVideoMinSecond(Constant.TIME_MIN_VIDEO_UPDATE_WEB)
                .setCompressEngine((CompressFileEngine) (context, source, call) -> Luban.with(context).load(source).ignoreBy(100)
                        .setCompressListener(new OnNewCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String source, File compressFile) {
                                if (call != null) {
                                    call.onCallback(source, compressFile.getAbsolutePath());
                                }
                            }

                            @Override
                            public void onError(String source, Throwable e) {
                                if (call != null) {
                                    call.onCallback(source, null);
                                }
                            }
                        }).launch())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> selectList) {
                        if (mFilePathCallback != null) {
                            Uri[] results;
                            if (null != selectList && !selectList.isEmpty()) {
                                String filePath = selectList.get(0).getPath();
                                if (!TextUtils.isEmpty(filePath)) {
                                    File f = new File(filePath);
                                    Uri uri = Uri.fromFile(f);
                                    if (null != uri) {
                                        results = new Uri[]{uri};
                                        LogUtil.e("所有文件 Uri = " + uri);
                                        try {
                                            mFilePathCallback.onReceiveValue(results);
                                            mFilePathCallback = null;
                                        } catch (Exception e) {
                                            LogUtil.e("UrlDetailActivity 上传所有文件出错 =  " + e.getMessage());
                                            e.printStackTrace();
                                            mFilePathCallback = null;
                                        }
                                    } else {
                                        LogUtil.e("所有文件 uri 为空  ");
                                        mFilePathCallback = null;
                                    }
                                }
                            } else {
                                try {
                                    mFilePathCallback.onReceiveValue(null);
                                    mFilePathCallback = null;
                                } catch (Exception e) {
                                    //                    LogUtil.e("UrlDetailActivity 上传空照片出错 =  " + e.getMessage());
                                    e.printStackTrace();
                                    mFilePathCallback = null;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    public void openInputFileVideo2(int minTimeLength, int maxTimeLength) {
        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        ImageEngine imageEngine = GlideEngine.createGlideEngine();
        PictureSelector.create(mContext).openGallery(SelectMimeType.ofVideo()).setSelectorUIStyle(selectorStyle)
                .setImageEngine(imageEngine)
                .isQuickCapture(true).setMaxSelectNum(1).setMinSelectNum(0)
                .setRecordVideoMaxSecond(maxTimeLength)
                .setFilterVideoMaxSecond(maxTimeLength)
                .setFilterVideoMinSecond(minTimeLength)
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> selectList) {
                        if (NetUtil.isNetworkConnected(mContext)) {
                            if (null != selectList && !selectList.isEmpty()) {
                                String videoPath = selectList.get(0).getPath();
                                LogUtil.e("视频地址 = " + videoPath);
                                if (!TextUtils.isEmpty(videoPath) && mContext.getExternalCacheDir() != null) {
                                    PLShortVideoTranscoder
                                            shortVideoTranscoder = new PLShortVideoTranscoder(mContext, videoPath,
                                            mContext.getExternalCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis()
                                                    + ".mp4");
                                    MediaMetadataRetriever retr = new MediaMetadataRetriever();
                                    retr.setDataSource(videoPath);
                                    String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
                                    String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
                                    String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
                                    PLMediaFile mediaFile = new PLMediaFile(videoPath);
                                    int videoBitrate = mediaFile.getVideoBitrate();
                                    int transcodingBitrateLevel = 5;
                                    if (videoBitrate < getEncodingBitrateLevel(transcodingBitrateLevel)) { // 直接上传
                                        doVideoUpload2QuKanServer(videoPath);
                                    } else {
                                        if (null != loading) {
                                            loading.setLoadingText("压缩中");
                                            loading.show();
                                        }
                                        shortVideoTranscoder.transcode(Integer.parseInt(width), Integer.parseInt(height), getEncodingBitrateLevel(transcodingBitrateLevel), 0, new PLVideoSaveListener() {
                                            @Override
                                            public void onSaveVideoSuccess(String compressVideoPath) {
                                                ((Activity) mContext).runOnUiThread(() -> {
                                                    if (null != loading && loading.isShow()) {
                                                        loading.close();
                                                    }
                                                    if (!TextUtils.isEmpty(compressVideoPath)) {
                                                        doVideoUpload2QuKanServer(compressVideoPath);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onSaveVideoFailed(int errorCode) {
                                                ((Activity) mContext).runOnUiThread(() -> {
                                                    if (errorCode == ERROR_NO_VIDEO_TRACK) {
                                                        ToastUtil.showShortToast("该文件没有视频信息！");
                                                    } else if (errorCode == ERROR_SRC_DST_SAME_FILE_PATH) {
                                                        ToastUtil.showShortToast("源文件路径和目标路径不能相同！");
                                                    } else if (errorCode == ERROR_LOW_MEMORY) {
                                                        ToastUtil.showShortToast("手机内存不足，转码失败！");
                                                    } else {
                                                        ToastUtil.showShortToast("转码失败");
                                                    }
                                                    if (null != loading && loading.isShow()) {
                                                        loading.close();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onSaveVideoCanceled() {
                                                ((Activity) mContext).runOnUiThread(() -> {
                                                    ToastUtil.showShortToast("转码取消");
                                                    if (null != loading && loading.isShow()) {
                                                        loading.close();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onProgressUpdate(float v) {
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            ToastUtil.showShortToast(mContext.getResources().getString(R.string.network_error));
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    /**
     * 字符串是否为 url
     */
    private boolean isUrl(String url) {
        boolean result = false;
        if (!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))) {
            result = true;
        }
        return result;
    }

    /**
     * 判断扫描结果
     */
    private void judgeScanResult(String parseResult) {
        if (parseResult.contains("cw_type")) {
            requestScanInfo(parseResult);
        } else {
            if (isUrl(parseResult)) {
                addScanRecord(parseResult);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.TAG_URL, parseResult);
                IntentUtil.startActivity(mContext, UrlDetailActivity.class, bundle);
            } else {
                addScanRecord(parseResult);
                Bundle bundle = new Bundle();
                bundle.putString("result", parseResult);
                IntentUtil.startActivity(mContext, ScanResultActivity.class, bundle);
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
            RxHttp.postJson(Constant.RECORD_SCAN).add("cw_content", content)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponse(JsonObject.class)
                    .observeOn(AndroidSchedulers.mainThread()).subscribe();
        }
    }

    /**
     * 请求扫码结果
     *
     * @param content 扫码结果
     */
    private void requestScanInfo(final String content) {
        if (NetUtil.isNetworkConnected(mContext)) {
            if (null != loading) {
                loading.show();
            }
            try {
                String content1 = content.replace("\n", "");
                RxHttp.postJson(Constant.URL_SCAN).add("cw_content", content1)
                        .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                        .asMessageResponse(ScanResultEntity.DataBean.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
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
                                            AppUtil.makeLongSnackMsg(this, data.getMsg());
                                        }
                                    } else {
                                        ToastUtil.showShortToast(data.getMsg());
                                    }
                                } else {
                                    AppUtil.makeLongSnackMsg(this, data.getMsg());
                                }
                            }
                        }, (OnError) error -> {
                            if (null != loading && loading.isShow()) {
                                loading.close();
                            }
                            if (!((Activity) mContext).isFinishing()) {
                                if (null != loading && loading.isShow()) {
                                    loading.close();
                                }
                                AppUtil.makeLongSnackMsg(this, error.getErrorMsg());
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            if (result != null) {
                String imagePath = ImageFilePath.getPath(mContext, result);
                if (!TextUtils.isEmpty(imagePath)) {
                    result = Uri.parse("file:///" + imagePath);
                }
            }
            try {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            } catch (Exception e) {
                e.printStackTrace();
                mUploadMessage = null;
            }
        } else if (requestCode == REQUEST_CODE_SCAN) {
            if (data != null) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                if (TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(mContext.getResources().getString(R.string.hit_code_incognizant));
                } else {
                    judgeScanResult(result);
                }
            }
        }
    }

    public void setCallback(String _callback) {
        callback = _callback;
    }

    public void setFilePathCallback(ValueCallback<Uri[]> _callback) {
        mFilePathCallback = _callback;
    }

    public ValueCallback<Uri[]> getFilePathCallback() {
        return mFilePathCallback;
    }

    /**
     * 上传视频到 趣看服务器
     */
    private void doVideoUpload2QuKanServer(String videoPath) {
        if (NetUtil.isNetworkConnected(mContext)) {
            if (null != loading2 && !loading2.isShow()) {
                loading2.setLoadingText("上传中");
                loading2.show();
            }
            RxHttpFormParam http = RxHttp.postForm(Constant.UPLOAD_VIDEO_QUKAN);
            http.add("appid", Constant.ID_APP).add("sign", AppUtil.generateSignString()).add("appKey", Constant.APPKEY_QUKAN).add("memberId", Constant.ID_USER_QUKAN);
            http.addFile("file", new File(videoPath));
            http.asSingleUpload(ResourceUploadResponseEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (null != loading2 && loading2.isShow()) {
                            loading2.close();
                        }
                        ToastUtil.showShortToast("上传成功");
                        if (data != null) {
                            Gson gson = new Gson();
                            String respondStr = gson.toJson(data);
                            if (!TextUtils.isEmpty(respondStr) && !TextUtils.isEmpty(callback)) {
                                LogUtil.e("respondStr = " + respondStr);
                                LogUtil.e("callback = " + callback + "   ");
                                String callStr = "javascript:" + callback + "(" + respondStr + ")";
                                LogUtil.e("callStr = " + callStr);
                                loadUrl("javascript:" + callback + "(" + respondStr + ")");
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading2 && loading2.isShow()) {
                            loading2.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        } else {
            ToastUtil.showShortToast(mContext.getResources().getString(R.string.network_error));
        }
    }

    /**
     * 设置压缩质量
     */
    private int getEncodingBitrateLevel(int position) {
        return RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[position];
    }

    /**
     * 解析 图库信息
     */
    public void parsePhoto(Intent data) {
        final String path = UriUtils.getImagePath(mContext, data);
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showShortToast(mContext.getResources().getString(R.string.hit_code_incognizant));
        } else {  //异步解析
            asyncThread(() -> {
                final String result = CodeUtils.parseCode(path);
                //                LogUtil.e("本地图片解析结果 = " + result);
                Message message = Message.obtain();
                message.obj = result;
                message.arg1 = CODE_PARSE;
                handler.sendMessage(message);
            });
        }
    }

    private void asyncThread(Runnable runnable) {
        new Thread(runnable).start();
    }
}