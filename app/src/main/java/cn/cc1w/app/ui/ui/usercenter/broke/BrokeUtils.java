package cn.cc1w.app.ui.ui.usercenter.broke;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.neovisionaries.ws.client.WebSocket;
import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AudioUploadRespondEntity;
import cn.cc1w.app.ui.entity.BrokeAuthEntity;
import cn.cc1w.app.ui.entity.BrokeMessageEntity;
import cn.cc1w.app.ui.entity.BrokeMessageSendEntity;
import cn.cc1w.app.ui.entity.ConnectBrokeRoomEntity;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.entity.MultipleFileUploadRespondEntity;
import cn.cc1w.app.ui.entity.SignFileUploadResponEntity;
import cn.cc1w.app.ui.entity.VideoUploadResponEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.KeybordUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.utils.converter.AndroidAudioConverter;
import cn.cc1w.app.ui.utils.converter.callback.IConvertCallback;
import cn.cc1w.app.ui.utils.converter.model.AudioFormat;
import cn.cc1w.app.ui.utils.pictureSelector.MeSandboxFileEngine;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;
import rxhttp.RxHttpFormParam;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

public class BrokeUtils {
    private static final String CHARSET = "UTF-8";
    private static final int TYPE_MESSAGE_TXT = 1;
    private static final int TYPE_MESSAGE_PIC = 2;
    private static final int TYPE_MESSAGE_VIDEO = 3;
    private static final int TYPE_MESSAGE_AUDIO = 4;
    private static final int TYPE_MESSAGE_LOCATION = 5;

    public static void extracted(String messageContent, Gson gson, WebSocket webSocket) {
        BrokeMessageSendEntity brokeMessageSendEntity = new BrokeMessageSendEntity();
        BrokeMessageSendEntity.ArgumentsBean argumentsBean = new BrokeMessageSendEntity.ArgumentsBean();
        BrokeMessageSendEntity.ArgumentsBean.PostBean postBean = new BrokeMessageSendEntity.ArgumentsBean.PostBean();
        postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
        postBean.setCw_type(TYPE_MESSAGE_TXT);
        postBean.setCw_content(messageContent);
        postBean.setCw_pic_path("");
        postBean.setCw_pic_id("");
        postBean.setCw_video_path("");
        postBean.setCw_video_id("");
        postBean.setCw_audio_path("");
        postBean.setCw_audio_id("");
        postBean.setSource_id(Constant.ID_APP);
        argumentsBean.setPost(postBean);
        brokeMessageSendEntity.setArguments(argumentsBean);
        brokeMessageSendEntity.setUrl("/broke/pushMessage");
        String messageInfo = gson.toJson(brokeMessageSendEntity);
        if (null != webSocket) {
            try {
                webSocket.sendBinary(messageInfo.getBytes(CHARSET));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public static void doSendPic2Server(String picPath, Context context, LoadingDialog loading, LifecycleOwner owner, Gson gson, WebSocket webSocket) {
        if (null != loading) {
            loading.show();
        }
        RxHttpFormParam http = RxHttp.postForm(Constant.FILE_SINGLE_UPLOAD_QUKAN);
        http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString()).addFile("file", new File(picPath));
        http.asSingleUpload(SignFileUploadResponEntity.DataBean.class)
                .as(RxLife.asOnMain(owner))
                .subscribe(picResponse -> {
                    if (picResponse != null) {
                        BrokeMessageSendEntity brokeMessageSendEntity = new BrokeMessageSendEntity();
                        BrokeMessageSendEntity.ArgumentsBean argumentsBean = new BrokeMessageSendEntity.ArgumentsBean();
                        BrokeMessageSendEntity.ArgumentsBean.PostBean postBean = new BrokeMessageSendEntity.ArgumentsBean.PostBean();
                        postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
                        postBean.setCw_type(TYPE_MESSAGE_PIC);
                        postBean.setCw_content("");
                        postBean.setCw_pic_id(String.valueOf(picResponse.getId()));
                        postBean.setCw_pic_path(picResponse.getPic_path_n());
                        postBean.setCw_video_path("");
                        postBean.setCw_video_id("");
                        postBean.setCw_play_time("");
                        postBean.setCw_audio_path("");
                        postBean.setCw_audio_id("");
                        postBean.setSource_id(Constant.ID_APP);
                        argumentsBean.setPost(postBean);
                        brokeMessageSendEntity.setArguments(argumentsBean);
                        brokeMessageSendEntity.setUrl("/broke/pushMessage");
                        String messageInfo = gson.toJson(brokeMessageSendEntity);
                        if (null != webSocket) {
                            try {
                                webSocket.sendBinary(messageInfo.getBytes(CHARSET));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(context, LoginActivity.class);
                    }
                });
    }

    public static void doRefresh(@NonNull RefreshLayout refreshLayout, int currentPageIndex, Context context, Gson gson, WebSocket webSocket) {
        if (NetUtil.isNetworkConnected(context)) {
            LogUtil.e("currentPageIndex = " + currentPageIndex);
            ConnectBrokeRoomEntity connectBrokeRoomEntity = new ConnectBrokeRoomEntity();
            connectBrokeRoomEntity.setUrl("/broke/getUserBrokeLists");
            ConnectBrokeRoomEntity.ArgumentsBean argumentsBean = new ConnectBrokeRoomEntity.ArgumentsBean();
            ConnectBrokeRoomEntity.ArgumentsBean.PostBean postBean = new ConnectBrokeRoomEntity.ArgumentsBean.PostBean();
            postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
            postBean.setSource_id(Constant.ID_APP);
            postBean.setCw_page(String.valueOf(currentPageIndex));
            argumentsBean.setPost(postBean);
            connectBrokeRoomEntity.setArguments(argumentsBean);
            String content = gson.toJson(connectBrokeRoomEntity);
            if (null != webSocket) {
                try {
                    webSocket.sendBinary(content.getBytes(CHARSET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            refreshLayout.finishRefresh();
        }
    }

    public static void doUserPosSend(String longitude, String latitude, String address, String mapPicPath, Context context, LoadingDialog loading, LifecycleOwner owner, Gson gson, WebSocket webSocket) {
        if (null != loading) {
            loading.show();
        }
        RxHttpFormParam http = RxHttp.postForm(Constant.FILE_SINGLE_UPLOAD_QUKAN);
        http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString()).addFile("file", new File(mapPicPath));
        http.asSingleUpload(SignFileUploadResponEntity.DataBean.class)
                .as(RxLife.asOnMain(owner))
                .subscribe(picResponse -> {
                    if (picResponse != null) {
                        BrokeMessageSendEntity brokeMessageSendEntity = new BrokeMessageSendEntity();
                        BrokeMessageSendEntity.ArgumentsBean argumentsBean = new BrokeMessageSendEntity.ArgumentsBean();
                        BrokeMessageSendEntity.ArgumentsBean.PostBean postBean = new BrokeMessageSendEntity.ArgumentsBean.PostBean();
                        postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
                        postBean.setCw_type(TYPE_MESSAGE_LOCATION);
                        postBean.setCw_content("");
                        postBean.setCw_pic_id(String.valueOf(picResponse.getId()));
                        postBean.setCw_pic_path(picResponse.getPic_path_n());
                        postBean.setCw_video_path("");
                        postBean.setCw_video_id("");
                        postBean.setCw_latitude(latitude);
                        postBean.setCw_longitude(longitude);
                        postBean.setCw_content(address);
                        postBean.setCw_audio_path("");
                        postBean.setCw_audio_id("");
                        postBean.setSource_id(Constant.ID_APP);
                        argumentsBean.setPost(postBean);
                        brokeMessageSendEntity.setArguments(argumentsBean);
                        brokeMessageSendEntity.setUrl("/broke/pushMessage");
                        String messageInfo = gson.toJson(brokeMessageSendEntity);
                        if (null != webSocket) {
                            try {
                                webSocket.sendBinary(messageInfo.getBytes(CHARSET));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(context, LoginActivity.class);
                    }
                });
    }

    public static void doVideoSend2Server(String videoPath, Context context, LoadingDialog loading, LifecycleOwner owner, Gson gson, WebSocket webSocket) {
        if (NetUtil.isNetworkConnected(context)) {
            if (null != loading) {
                loading.show();
            }
            RxHttpFormParam http = RxHttp.postForm(Constant.VIDEO_UPLOAD_QUKAN);
            http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString())
                    .add("appKey", Constant.APPKEY_QUKAN)
                    .add("memberId", Constant.ID_USER_QUKAN)
                    .addFile("file", new File(videoPath));
            http.asSingleUpload(VideoUploadResponEntity.DataBean.class)
                    .as(RxLife.asOnMain(owner))
                    .subscribe(videoResponse -> {
                        if (videoResponse != null) {
                            BrokeMessageSendEntity brokeMessageSendEntity = new BrokeMessageSendEntity();
                            BrokeMessageSendEntity.ArgumentsBean argumentsBean = new BrokeMessageSendEntity.ArgumentsBean();
                            BrokeMessageSendEntity.ArgumentsBean.PostBean postBean = new BrokeMessageSendEntity.ArgumentsBean.PostBean();
                            postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
                            postBean.setCw_type(TYPE_MESSAGE_VIDEO);
                            postBean.setCw_content("");
                            postBean.setCw_pic_id("");
                            postBean.setCw_pic_path(videoResponse.getPic_path());
                            postBean.setCw_video_path(videoResponse.getUrl());
                            postBean.setCw_video_id(String.valueOf(videoResponse.getId()));
                            postBean.setCw_play_time(videoResponse.getTime());
                            postBean.setCw_audio_path("");
                            postBean.setCw_audio_id("");
                            postBean.setSource_id(Constant.ID_APP);
                            argumentsBean.setPost(postBean);
                            brokeMessageSendEntity.setArguments(argumentsBean);
                            brokeMessageSendEntity.setUrl("/broke/pushMessage");
                            String messageInfo = gson.toJson(brokeMessageSendEntity);
                            if (null != webSocket) {
                                try {
                                    webSocket.sendBinary(messageInfo.getBytes(CHARSET));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(context, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(context.getString(R.string.network_error));
        }
    }

    public static void doPicturePick(Context activity, PictureSelectorStyle selectorStyle, ImageEngine imageEngine, int maxNumber, LoadingDialog loading, LifecycleOwner owner, Gson gson, WebSocket webSocket) {
        PictureSelector.create(activity).openGallery(SelectMimeType.ofImage()).setSelectorUIStyle(selectorStyle)
                .isQuickCapture(true)
                .setMaxSelectNum(maxNumber)
                .setMinSelectNum(0)
                .setImageEngine(imageEngine)
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
                        if (null != selectList && !selectList.isEmpty()) {
                            List<String> picList = new ArrayList<>();
                            for (LocalMedia it : selectList) {
                                if (!TextUtils.isEmpty(it.getCompressPath())) {
                                    picList.add(it.getCompressPath());
                                } else {
                                    picList.add(it.getPath());
                                }
                            }
                            if (!picList.isEmpty()) {
                                sendAlbum2Server(picList, activity, loading, owner, gson, webSocket);
                            }
                        }
                        LogUtil.d("onResult !!! ");
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    /**
     * 上传相册选中的图片到服务器
     */
    private static void sendAlbum2Server(List<String> selectList, Context context, LoadingDialog loading, LifecycleOwner owner, Gson gson, WebSocket webSocket) {
        BrokeUtils.doAlbumSend2Server(selectList, context, loading, owner, gson, webSocket);
    }

    public static void doAlbumSend2Server(List<String> selectList, Context context, LoadingDialog loading, LifecycleOwner owner, Gson gson, WebSocket webSocket) {
        if (NetUtil.isNetworkConnected(context)) {
            if (null != loading) {
                loading.show();
            }
            RxHttpFormParam http = RxHttp.postForm(Constant.FILE_MULTIPLE_UPLOAD_QUKAN);
            http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString());
            for (String filePath : selectList) {
                http.addFile("files", new File(filePath));
            }
            http.asMultipleUpload(MultipleFileUploadRespondEntity.DataBean.class)
                    .as(RxLife.asOnMain(owner))
                    .subscribe(data -> {
                        if (data != null && !data.isEmpty()) {
                            StringBuilder ids = new StringBuilder(); // 图片的地址
                            StringBuilder paths = new StringBuilder();// 图片的地址
                            for (MultipleFileUploadRespondEntity.DataBean item : data) {
                                ids.append(item.getId()).append(",");
                                paths.append(item.getPic_path_n()).append(",");
                                LogUtil.e(" url = " + item.getPic_path_n());
                            }
                            BrokeMessageSendEntity brokeMessageSendEntity = new BrokeMessageSendEntity();
                            BrokeMessageSendEntity.ArgumentsBean argumentsBean = new BrokeMessageSendEntity.ArgumentsBean();
                            BrokeMessageSendEntity.ArgumentsBean.PostBean postBean = new BrokeMessageSendEntity.ArgumentsBean.PostBean();
                            postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
                            postBean.setCw_type(TYPE_MESSAGE_PIC);
                            postBean.setCw_content("");
                            postBean.setCw_pic_id(ids.toString());
                            postBean.setCw_pic_path(paths.toString());
                            postBean.setCw_video_path("");
                            postBean.setCw_video_id("");
                            postBean.setCw_audio_path("");
                            postBean.setCw_audio_id("");
                            postBean.setSource_id(Constant.ID_APP);
                            argumentsBean.setPost(postBean);
                            brokeMessageSendEntity.setArguments(argumentsBean);
                            brokeMessageSendEntity.setUrl("/broke/pushMessage");
                            String messageInfo = gson.toJson(brokeMessageSendEntity);
                            LogUtil.e("图库图片信息 = " + messageInfo);
                            if (null != webSocket) {
                                try {
                                    webSocket.sendBinary(messageInfo.getBytes(CHARSET));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                            AppUtil.doUserLogOut();
                            IntentUtil.startActivity(context, LoginActivity.class);
                        }
                    });
        } else {
            ToastUtil.showShortToast(context.getString(R.string.network_error));
        }
    }

    /**
     * 关闭输入法
     */
    public static void hideKeyBord(Activity context) {
        if (KeybordUtil.isInputMethodActive()) {
            KeybordUtil.closeInputMethod(context);
        }
    }

    public static void doUserAuth(Gson gson, WebSocket webSocket) {
        ConnectBrokeRoomEntity connectBrokeRoomEntity = new ConnectBrokeRoomEntity();
        connectBrokeRoomEntity.setUrl("/broke/auth");
        ConnectBrokeRoomEntity.ArgumentsBean argumentsBean = new ConnectBrokeRoomEntity.ArgumentsBean();
        ConnectBrokeRoomEntity.ArgumentsBean.PostBean postBean = new ConnectBrokeRoomEntity.ArgumentsBean.PostBean();
        postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
        postBean.setSource_id(Constant.ID_APP);
        argumentsBean.setPost(postBean);
        connectBrokeRoomEntity.setArguments(argumentsBean);
        String content = gson.toJson(connectBrokeRoomEntity);
        if (null != webSocket) {
            try {
                webSocket.sendBinary(content.getBytes(CHARSET));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public static void doAudioConvert(String audioFilePath, Context context, LoadingDialog loading, LifecycleOwner owner, Gson gson, WebSocket webSocket, IConvertCallback callback) {
        if (!TextUtils.isEmpty(audioFilePath)) {
            if (Constant.IS_AUDIO_CONVERT_FINISHED) {
                File file = new File(audioFilePath);
                AndroidAudioConverter.with(context).setFile(file).setFormat(AudioFormat.MP3).setCallback(callback).convert();
            } else {
                if (null != loading) {
                    loading.show();
                }
                if (NetUtil.isNetworkConnected(context)) {
                    if (null != loading) {
                        loading.show();
                    }
                    RxHttpFormParam http = RxHttp.postForm(Constant.FILE_SINGLE_UPLOAD_QUKAN);
                    http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString());
                    http.addFile("file", new File(audioFilePath));
                    http.asSingleUpload(AudioUploadRespondEntity.DataBean.class)
                            .as(RxLife.asOnMain(owner))
                            .subscribe(audioResponse -> {
                                if (audioResponse != null) {
                                    BrokeMessageSendEntity brokeMessageSendEntity = new BrokeMessageSendEntity();
                                    BrokeMessageSendEntity.ArgumentsBean argumentsBean = new BrokeMessageSendEntity.ArgumentsBean();
                                    BrokeMessageSendEntity.ArgumentsBean.PostBean postBean = new BrokeMessageSendEntity.ArgumentsBean.PostBean();
                                    postBean.setCwauthorization(Constant.CW_AUTHORIZATION);
                                    postBean.setCw_type(TYPE_MESSAGE_AUDIO);
                                    postBean.setCw_content("");
                                    postBean.setCw_pic_path("");
                                    postBean.setCw_pic_id("");
                                    postBean.setCw_video_path("");
                                    postBean.setCw_video_id("");
                                    postBean.setCw_audio_path(audioResponse.getUrl());
                                    postBean.setCw_audio_id(String.valueOf(audioResponse.getId()));
                                    postBean.setCw_play_time(audioResponse.getTime());
                                    postBean.setSource_id(Constant.ID_APP);
                                    argumentsBean.setPost(postBean);
                                    brokeMessageSendEntity.setArguments(argumentsBean);
                                    brokeMessageSendEntity.setUrl("/broke/pushMessage");
                                    String messageInfo = gson.toJson(brokeMessageSendEntity);
                                    if (null != webSocket) {
                                        try {
                                            webSocket.sendBinary(messageInfo.getBytes(CHARSET));
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (null != loading && loading.isShow()) {
                                    loading.close();
                                }
                            }, (OnError) error -> {
                                if (null != loading && loading.isShow()) {
                                    loading.close();
                                }
                                ToastUtil.showShortToast(error.getErrorMsg());
                                if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                                    AppUtil.doUserLogOut();
                                    IntentUtil.startActivity(context, LoginActivity.class);
                                }
                            });
                } else {
                    ToastUtil.showShortToast(context.getString(R.string.network_error));
                }
            }
        }
    }

}
