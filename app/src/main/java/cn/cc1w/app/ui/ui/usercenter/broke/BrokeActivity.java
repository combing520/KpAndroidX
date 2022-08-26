package cn.cc1w.app.ui.ui.usercenter.broke;

import android.Manifest;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import cn.cc1w.app.ui.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AudioUploadRespondEntity;
import cn.cc1w.app.ui.entity.BrokeAuthEntity;
import cn.cc1w.app.ui.entity.BrokeMessageEntity;
import cn.cc1w.app.ui.entity.BrokeMessageSendEntity;
import cn.cc1w.app.ui.entity.ConnectBrokeRoomEntity;
import cn.cc1w.app.ui.utils.converter.callback.IConvertCallback;
import cn.cc1w.app.ui.utils.pictureSelector.GlideEngine;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.record.AudioRecoderUtils;
import cn.cc1w.app.ui.ui.usercenter.map.LocationActivity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.JsonUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ScreenUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.jzvd.Jzvd;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.cc1w.app.ui.constants.Constant.HOST_SOCKET;

import rxhttp.RxHttp;
import rxhttp.RxHttpFormParam;

/**
 * 爆料
 */
public class BrokeActivity extends CustomActivity implements EasyPermissions.PermissionCallbacks, View.OnTouchListener, AudioRecoderUtils.OnAudioStatusUpdateListener, OnRefreshListener {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_broke)
    RecyclerView recyclerView;
    @BindView(R.id.img_voice_broke)
    ImageView voiceImg;
    @BindView(R.id.img_add_broke)
    ImageView addImg;
    @BindView(R.id.txt_send_broke)
    TextView messageSendBtn;
    @BindView(R.id.grid_bottom_brok)
    GridLayout bottomLayout;
    @BindView(R.id.txt_voice_broke)
    TextView voiceBtn;
    @BindView(R.id.edit_broke)
    EditText edit;
    @BindView(R.id.layout_root)
    LinearLayout rootView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private ImageView recordDialogVoiceImg;
    private TextView recordDialogVoiceTxt;
    private ImageView recordCancelDialogVoiceImg;
    private LinearLayout recordDialogLayout;
    private Drawable normalDialogDrawable;
    private Drawable cancelDialogDrawable;
    private Dialog dialog;
    private static final int CODE_REQUEST_PERMISSION = 1;
    private static final int CODE_REQUEST_AUDIO = 2;
    private static final int CODE_REQUEST_LOCATION = 3;
    private AudioRecoderUtils audioRecoderUtils;
    private static final int TIME_SHORTEST = 1000;
    private long lastTime = System.currentTimeMillis();
    private boolean isRecording = false;
    private static final int DISTANCE_Y_CANCEL = 50;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_WANT_TO_CANCEL = 2;
    private int currentState = STATE_NORMAL;
    private static final String CHARSET = "UTF-8";
    private Gson gson;
    private WebSocket webSocket;
    private static final int TIME_OUT_CONNECT = 15 * 1000;
    private static final int MAX_SIZE_QUEUE = 5;
    private BrokeAdapter adapter;
    private LoadingDialog loading;
    private boolean isConnectionRequestError;
    private static final int TYPE_MESSAGE_TXT = 1;
    private static final int TYPE_MESSAGE_AUDIO = 4;
    private int currentPageIndex = 2;
    private static final int NUM_SELECT_PIC = 9;
    private static final String RECONNECT = "brokeReConnect";
    private static final String LABEL_BROKE = "broke";
    private static final String INSERT_DATA = "insertData";
    private static final String BROKE_REFRESH = "brokeRefresh";
    private static final String CLEAR_EDIT_DATA = "clearEditData";
    private boolean isFirstOpenInputMethod = true;
    private String audioFilePath = "";
    private static final String ERROR_CONNECT = "connect_error";
    private static final String FAILURE_CONNECT = "connect_failure";
    private static final String SUCCESS_CONNECT = "connect_success";
    private static final String DATA_RECEIVE = "receive_data";
    private final PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
    private final ImageEngine imageEngine = GlideEngine.createGlideEngine();
    private int chooseType = 1;
    private static final String LABEL_EVENT = "sendMessage";
    private static final String TYPE_VIDEO_EVENT = "video";
    private static final String TYPE_PIC_EVENT = "pic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broke);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setRedThemeInfo();
        gson = new Gson();
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initDrawable();
        initNavigation();
        initLoading();
        registerListener();
        initList();
        initWebSocketInfo();
        initBottomInfo();
        BrokeUtils.hideKeyBord(this);
    }

    private void setRedThemeInfo() {
        ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).init();
    }

    /**
     * 初始化底部信息
     */
    private void initBottomInfo() {
        initInputMethodInfo();
    }

    /**
     * 监听输入法打开和关闭
     */
    private void initInputMethodInfo() {
        ScreenUtil screenUtil = new ScreenUtil(this);
        screenUtil.observeInputlayout(rootView, new ScreenUtil.OnInputActionListener() {
            @Override
            public void onOpen() {
                if (null != recyclerView) {
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
                bottomLayout.setVisibility(View.GONE);
                if (isFirstOpenInputMethod) {
                    isFirstOpenInputMethod = false;
                }
            }

            @Override
            public void onClose() {
            }
        });
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化list相关信息
     */
    private void initList() {
        adapter = new BrokeAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setMotionEventSplittingEnabled(false);
    }

    private void initDrawable() {
        normalDialogDrawable = ContextCompat.getDrawable(this, R.drawable.bg_transport_half_raduis);
        cancelDialogDrawable = ContextCompat.getDrawable(this, R.drawable.bg_transport_half_red_raduis);
    }

    /**
     * 注册监听事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void registerListener() {
        audioRecoderUtils = new AudioRecoderUtils(this);
        voiceBtn.setOnTouchListener(this);
        audioRecoderUtils.setOnAudioStatusUpdateListener(this);
    }

    /**
     * 初始化 导航头
     */
    private void initNavigation() {
        titleTv.setText("爆料");
    }

    /**
     * 连接webSocket
     */
    private void initWebSocketInfo() {
        if (NetUtil.isNetworkConnected(this)) {
            try {
                WsListener listener = new WsListener();
                WebSocketFactory factory = new WebSocketFactory();
                factory.setVerifyHostname(false);
                webSocket = factory.createSocket(HOST_SOCKET, TIME_OUT_CONNECT).setFrameQueueSize(MAX_SIZE_QUEUE).setMissingCloseFrameAllowed(false).addListener(listener).sendContinuation().connectAsynchronously();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 显示或者隐藏底部容器
     */
    private void showOrHideBottomLayout() {
        if (bottomLayout.getVisibility() == View.VISIBLE) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
        }
        BrokeUtils.hideKeyBord(this);
    }

    /**
     * 拍摄
     */
    private void shoot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissionList = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
            if (EasyPermissions.hasPermissions(this, permissionList)) {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    IntentUtil.startActivity(this, LoginActivity.class, null);
                } else {
                    IntentUtil.startActivity(this, ShootActivity.class, null);
                }
            } else {
                EasyPermissions.requestPermissions(this, "是否允许开屏新闻获取获取相机、相册,音频录制，权限用于拍摄和获取本地图片", CODE_REQUEST_PERMISSION, permissionList);
            }
        } else {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, LoginActivity.class, null);
            } else {
                IntentUtil.startActivity(this, ShootActivity.class, null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST || requestCode == PictureConfig.REQUEST_CAMERA) {
                if (data != null && data.getExtras() != null) {
                    Bundle bundle = data.getExtras();
                    Uri uri = (Uri) bundle.get(MediaStore.EXTRA_OUTPUT);
                    LogUtil.e("realPath = " + uri.getPath());
                    if (uri.getPath() != null && !TextUtils.isEmpty(uri.getPath())) {
                        if (chooseType == 1) {
                            sendPic2Server(uri.getPath());
                        } else if (chooseType == 2) {
                            sendVideo2Server(uri.getPath());
                        }
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            LogUtil.e("onActivityResult PictureSelector Cancel");
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, LoginActivity.class, null);
            return;
        }
        if (NetUtil.isNetworkConnected(this)) {
            String messageContent = edit.getText().toString();
            if (TextUtils.isEmpty(messageContent)) {
                ToastUtil.showShortToast("消息不能为空");
            } else {
                BrokeUtils.extracted(messageContent, gson, webSocket);
            }
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 发送图片到 服务器
     */
    private void sendPic2Server(String picPath) {
        if (NetUtil.isNetworkConnected(this)) {
            BrokeUtils.doSendPic2Server(picPath, this, loading, this, gson, webSocket);
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 上传位置
     */
    private void sendUserPos(String longitude, String latitude, String address, String mapPicPath) {
        if (NetUtil.isNetworkConnected(this)) {
            BrokeUtils.doUserPosSend(longitude, latitude, address, mapPicPath, this, loading, this, gson, webSocket);
        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 发送 视频 到服务器
     */
    private void sendVideo2Server(String videoPath) {
        BrokeUtils.doVideoSend2Server(videoPath, this, loading, this, gson, webSocket);
    }

    /**
     * 修改输入的方式 语音或者 文字输入
     */
    private void switchInputMode() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, LoginActivity.class, null);
            return;
        }
        if (voiceBtn.getVisibility() == View.VISIBLE) {
            voiceBtn.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        } else {
            voiceBtn.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
        }
    }

    /**
     * 拍照
     */
    private void pickPicture() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, LoginActivity.class, null);
        } else {
            BrokeUtils.doPicturePick(this, selectorStyle, imageEngine, NUM_SELECT_PIC, loading, this, gson, webSocket);
        }
    }

    /**
     * 显示 录音 dialog
     */
    @SuppressLint("InflateParams")
    private void showRecordDialog() {
        dialog = new Dialog(this, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_record, null);
        if ((null != dialogView) && (null != dialog)) {
            dialog.setContentView(dialogView);
            recordDialogVoiceImg = dialogView.findViewById(R.id.img_dialog_record);
            recordDialogVoiceTxt = dialogView.findViewById(R.id.txt_dialog_record);
            recordCancelDialogVoiceImg = dialogView.findViewById(R.id.img_cancel_dialog_record);
            recordDialogLayout = dialogView.findViewById(R.id.container_dialog_record);
            recordDialogLayout.setBackground(normalDialogDrawable);
            dialog.show();
            AnimationDrawable drawable = (AnimationDrawable) recordDialogVoiceImg.getDrawable();
            drawable.start();
        }
    }

    /**
     * 关闭 录制 dialog
     */
    private void hideRecordDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 取消 录音
     */
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > voiceBtn.getWidth()) {
            return true;
        }
        return (y < -DISTANCE_Y_CANCEL || y > voiceBtn.getHeight() + DISTANCE_Y_CANCEL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel()) && !TextUtils.isEmpty(message.getContent())) {
            if (TextUtils.equals(LABEL_EVENT, message.getLabel())) {
                if (TextUtils.equals(TYPE_VIDEO_EVENT, message.getFrom())) {
                    if (!isFinishing()) {
                        sendVideo2Server(message.getContent());
                    }
                } else if (TextUtils.equals(TYPE_PIC_EVENT, message.getFrom())) {
                    if (!isFinishing()) {
                        sendPic2Server(message.getContent());
                    }
                }
            } else if (TextUtils.equals(LABEL_BROKE, message.getLabel())) {
                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                    if (!TextUtils.equals(INSERT_DATA, message.getContent())) {
                        int size = adapter.getItemCount();
                        if (null != refreshLayout && recyclerView != null) {
                            refreshLayout.finishRefresh();
                            recyclerView.scrollToPosition(size);
                        }
                    } else {
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }
                }
            } else if (TextUtils.equals(BROKE_REFRESH, message.getLabel())) {
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            } else if (TextUtils.equals(CLEAR_EDIT_DATA, message.getLabel())) {
                edit.setText("");
            } else if (TextUtils.equals(RECONNECT, message.getLabel())) {
                if (null != webSocket && !isFinishing()) {
                    initWebSocketInfo();
                }
            } else if (TextUtils.equals("location", message.getLabel())) {
                LogUtil.e("lon = " + message.getLongitude() + " lat = " + message.getLatitude() + "  address = " + message.getAddress() + " picPath = " + message.getMapPicPath());
                if (!isFinishing()) {
                    sendUserPos(message.getLongitude(), message.getLatitude(), message.getAddress(), message.getMapPicPath());
                }
            } else if (TextUtils.equals(SUCCESS_CONNECT, message.getLabel())) {
                isConnectionRequestError = false;
                doAuth();
            } else if (TextUtils.equals(FAILURE_CONNECT, message.getLabel())) {
                isConnectionRequestError = true;
                if (adapter != null) {
                    adapter.reConnect();
                }
            } else if (TextUtils.equals(ERROR_CONNECT, message.getLabel())) {
                isConnectionRequestError = true;
                if (adapter != null) {
                    adapter.reConnect();
                }
            } else if (TextUtils.equals(DATA_RECEIVE, message.getLabel())) {
                if (!isFinishing()) {
                    if (!TextUtils.isEmpty(message.getContent())) {
                        LogUtil.e("接收到服务器发送的 二进制 消息 " + message);
                        BrokeAuthEntity brokeAuthEntity = JsonUtil.getObject(message.getContent(), BrokeAuthEntity.class);
                        if (null != brokeAuthEntity) {
                            if (brokeAuthEntity.getCode() == Constant.CODE_REQUEST_SUCCESS) {
                                final List<ItemBrokeEntity> list = brokeAuthEntity.getData();
                                if (null != list && !list.isEmpty()) {
                                    if (brokeAuthEntity.isMore()) {
                                        List<ItemBrokeEntity> currentDataSet = adapter.getDataSet();
                                        list.addAll(currentDataSet);
                                        adapter.setData(list);
                                        currentPageIndex += 1;
                                    } else {
                                        adapter.addDataSet(list);
                                    }
                                } else {
                                    if (brokeAuthEntity.isMore()) {
                                        adapter.setListRefreshComplete();
                                    }
                                }
                            }
                        } else {
                            BrokeMessageEntity brokeMessageEntity = JsonUtil.getObject(message.getContent(), BrokeMessageEntity.class);
                            if (null != brokeMessageEntity && brokeMessageEntity.getCode() == Constant.CODE_REQUEST_SUCCESS) {
                                ItemBrokeEntity item = brokeMessageEntity.getData();
                                adapter.addData(item);
                                if (TextUtils.equals(String.valueOf(TYPE_MESSAGE_TXT), item.getType())) {
                                    adapter.clearEditData();
                                }
                            }
                        }
                        BrokeUtils.hideKeyBord(this);
                    } else {
                        adapter.setListRefreshComplete();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_PERMISSION) {
            if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                IntentUtil.startActivity(this, LoginActivity.class, null);
            } else {
                IntentUtil.startActivity(this, ShootActivity.class, null);
            }
        } else if (requestCode == CODE_REQUEST_AUDIO) {
            if (null != recordDialogVoiceImg) {
                recordDialogVoiceImg.clearAnimation();
            }
            if (null != voiceBtn) {
                voiceBtn.setText("按住说话");
            }
            hideRecordDialog();
            isRecording = false;
        } else if (requestCode == CODE_REQUEST_LOCATION) {
            IntentUtil.startActivity(this, LocationActivity.class, null);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CODE_REQUEST_LOCATION) {
            ToastUtil.showShortToast("地理位置权限拒绝。无法发送位置");
        } else if (requestCode == CODE_REQUEST_PERMISSION) {
            ToastUtil.showShortToast("摄像头权限拒绝，无法获取摄像头信息");
        } else if (requestCode == CODE_REQUEST_AUDIO) {
            hideRecordDialog();
            isRecording = false;
            ToastUtil.showShortToast("音频录制权限拒绝。无法录制音频");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final String[] permissionList = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, permissionList)) {
            EasyPermissions.requestPermissions(BrokeActivity.this, "权限申请\n\n" + "使用麦克风或录音\n" + "为了能够正常的使用录音功能，请允许开屏新闻使用您的录音权限", CODE_REQUEST_AUDIO, permissionList);
            return false;
        } else {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                if (!Constant.STATE_USE_BROKE && !TextUtils.isEmpty(Constant.TYPE_MOBILE) && (Constant.TYPE_MOBILE.contains("OPPO") || (Constant.TYPE_MOBILE.contains("oppo")) || (Constant.TYPE_MOBILE.contains("vivo")) || (Constant.TYPE_MOBILE.contains("VIVO")))) {
                    LogUtil.e("oppo 或者 vivo 手机");
                    audioRecoderUtils.startRecord();
                    hideRecordDialog();
                } else {
                    isRecording = true;
                    lastTime = System.currentTimeMillis();
                    audioRecoderUtils.startRecord();
                    voiceBtn.setText("松开结束");
                    showRecordDialog();
                }
            } else if (action == MotionEvent.ACTION_MOVE) { // 滑动
                if (isRecording) {
                    if (wantToCancel(x, y)) { // 取消
                        recordCancelDialogVoiceImg.setVisibility(View.VISIBLE);
                        recordDialogVoiceImg.setVisibility(View.GONE);
                        currentState = STATE_WANT_TO_CANCEL;
                        recordDialogLayout.setBackground(cancelDialogDrawable);
                        recordDialogVoiceTxt.setText("松开手指,取消发送");
                    } else {
                        recordDialogVoiceImg.setVisibility(View.VISIBLE);
                        recordCancelDialogVoiceImg.setVisibility(View.GONE);
                        currentState = STATE_NORMAL;
                        recordDialogLayout.setBackground(normalDialogDrawable);
                        recordDialogVoiceTxt.setText("手指上滑,取消发送");
                    }
                }
            } else if (action == MotionEvent.ACTION_UP) { // 手指释放
                isRecording = false;
                voiceBtn.setText("按住说话");
                hideRecordDialog();
                long time = System.currentTimeMillis();
                LogUtil.e("时间差 = " + (time - lastTime));
                if (currentState == STATE_NORMAL) {
                    if ((time - lastTime) >= TIME_SHORTEST) {
                        audioRecoderUtils.stopRecord();
                    } else {
                        audioRecoderUtils.cancelRecord();
                        ToastUtil.showShortToast("时间太短了");
                    }
                } else { // 被取消了
                    audioRecoderUtils.cancelRecord();
                    ToastUtil.showShortToast("录音取消");
                }
                if (null != recordDialogVoiceImg) {
                    recordDialogVoiceImg.clearAnimation();
                }
            }
        }
        return true;
    }

    @Override
    public void onUpdate(double db, long time) {
        if (!Constant.STATE_USE_BROKE && time > 0) {
            SharedPreferenceUtil.setBrokeAudioUseState(true);
            Constant.STATE_USE_BROKE = true;
        }
    }

    @Override
    public void onStop(String filePath) {
        LogUtil.e("filePath = " + filePath);
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
            return;
        }
        audioFilePath = filePath;
        BrokeUtils.doAudioConvert(audioFilePath, this, loading, this, gson, webSocket, callback);
    }

    /**
     * 音频格式转换
     */
    private final IConvertCallback callback = new IConvertCallback() {
        @Override
        public void onRequestSuccess(File convertedFile) {
            if (!isFinishing() && null != convertedFile && !TextUtils.isEmpty(convertedFile.getPath())) {
                if (NetUtil.isNetworkConnected(BrokeActivity.this)) {
                    if (null != loading) {
                        loading.show();
                    }
                    RxHttpFormParam http = RxHttp.postForm(Constant.FILE_SINGLE_UPLOAD_QUKAN);
                    http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString());
                    http.addFile("file", new File(convertedFile.getAbsolutePath()));
                    http.asSingleUpload(AudioUploadRespondEntity.DataBean.class)
                            .as(RxLife.asOnMain(BrokeActivity.this))
                            .subscribe(audioResponse -> {
                                if (!isFinishing() && audioResponse != null) {
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
                            });
                } else {
                    ToastUtil.showShortToast(getString(R.string.network_error));
                }
            }
        }

        @Override
        public void onFailure(Exception error) {
            LogUtil.e("转码失败 = " + error.getMessage());
            if (!isFinishing() && !TextUtils.isEmpty(audioFilePath)) {
                if (NetUtil.isNetworkConnected(BrokeActivity.this)) {
                    if (null != loading) {
                        loading.show();
                    }
                    RxHttpFormParam http = RxHttp.postForm(Constant.FILE_SINGLE_UPLOAD_QUKAN);
                    http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString());
                    http.addFile("file", new File(audioFilePath));
                    http.asSingleUpload(AudioUploadRespondEntity.DataBean.class)
                            .as(RxLife.asOnMain(BrokeActivity.this))
                            .subscribe(audioResponse -> {
                                if (!isFinishing() && audioResponse != null) {
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
                            }, (OnError) errorInfo -> {
                                if (null != loading && loading.isShow()) {
                                    loading.close();
                                }
                                ToastUtil.showShortToast(errorInfo.getErrorMsg());
                            });
                } else {
                    ToastUtil.showShortToast(getString(R.string.network_error));
                }
            }
        }
    };

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        BrokeUtils.doRefresh(refreshLayout, currentPageIndex, this, gson, webSocket);
    }

    class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
            super.onBinaryMessage(websocket, binary);
//            if (!isFinishing()) {
//                BrokeUtils.doMessageReceive(binary, adapter, currentPageIndex, BrokeActivity.this);
//            }
            if (!isFinishing()) {
                String message = new String(binary, CHARSET);
                if (!TextUtils.isEmpty(message)) {
                    LogUtil.d("onBinaryMessage message = " + message);
                    BrokeAuthEntity brokeAuthEntity = JsonUtil.getObject(message, BrokeAuthEntity.class);
                    if (null != brokeAuthEntity) {
                        if (brokeAuthEntity.getCode() == Constant.CODE_REQUEST_SUCCESS) {
                            final List<ItemBrokeEntity> list = brokeAuthEntity.getData();
                            if (null != list && !list.isEmpty()) {
                                if (brokeAuthEntity.isMore()) {
                                    List<ItemBrokeEntity> currentDataSet = adapter.getDataSet();
                                    list.addAll(currentDataSet);
                                    adapter.setData(list);
                                    currentPageIndex += 1;
                                } else {
                                    adapter.addDataSet(list);
                                }
                            } else {
                                if (brokeAuthEntity.isMore()) {
                                    adapter.setListRefreshComplete();
                                }
                            }
                        }
                    } else {
                        BrokeMessageEntity brokeMessageEntity = JsonUtil.getObject(message, BrokeMessageEntity.class);
                        if (null != brokeMessageEntity && brokeMessageEntity.getCode() == Constant.CODE_REQUEST_SUCCESS) {
                            ItemBrokeEntity item = brokeMessageEntity.getData();
                            adapter.addData(item);
                            if (TextUtils.equals(String.valueOf(TYPE_MESSAGE_TXT), item.getType())) {
                                adapter.clearEditData();
                            }
                        }
                    }
                    BrokeUtils.hideKeyBord(BrokeActivity.this);
                } else {
                    adapter.setListRefreshComplete();
                }
            }
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            LogUtil.e("连接成功 ");
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            isConnectionRequestError = false;
            doAuth();
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            super.onConnectError(websocket, exception);
            isConnectionRequestError = true;
            if (null != loading && loading.isShow()) {
                loading.close();
            }
            if (!isFinishing() && null != adapter && null != websocket) {
                adapter.reConnect();
            }
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            LogUtil.e("断开连接");
            if (!isFinishing() && null != adapter) {
                adapter.reConnect();
            }
            isConnectionRequestError = true;
        }
    }

    /**
     * 授权
     */
    private void doAuth() {
        if (!isFinishing()) {
            BrokeUtils.doUserAuth(gson, webSocket);
        }
    }

    /**
     * 获取定位
     */
    private void getLocation() {
        if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
            IntentUtil.startActivity(this, LoginActivity.class, null);
        } else {
            final String[] permissionList = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            if (!EasyPermissions.hasPermissions(this, permissionList)) {
                EasyPermissions.requestPermissions(BrokeActivity.this, "权限申请\n\n" + "为了能够正常的使用地图定位服务，请允许开屏新闻使用您的定位权限", CODE_REQUEST_LOCATION, permissionList);
            } else {
                IntentUtil.startActivity(this, LocationActivity.class, null);
            }
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.img_voice_broke, R.id.img_add_broke, R.id.txt_send_broke, R.id.ll_album_broke, R.id.ll_picture_broke, R.id.ll_location_broke})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.img_voice_broke) {
            switchInputMode();
        } else if (id == R.id.img_add_broke) {
            showOrHideBottomLayout();
        } else if (id == R.id.txt_send_broke) {
            sendMessage();
        } else if (id == R.id.ll_album_broke) {
            pickPicture();
        } else if (id == R.id.ll_picture_broke) {
            shoot();
        } else if (id == R.id.ll_location_broke) {
            getLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM && null != Constant.ANIMATION_BROKE_SYSTEM) {
            Constant.ANIMATION_BROKE_SYSTEM.stop();
            Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM.setBackgroundResource(R.drawable.ic_voice_play3_broke_left);
            Constant.ANIMATION_BROKE_SYSTEM = null;
            Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM = null;
        }
        if (null != Constant.IMG_AUDIO_PATH_PLAYING_USER && null != Constant.ANIMATION_BROKE_USER) {
            Constant.ANIMATION_BROKE_USER.stop();
            Constant.IMG_AUDIO_PATH_PLAYING_USER.setBackgroundResource(R.drawable.ic_voice_play3_broke_right);
            Constant.ANIMATION_BROKE_USER = null;
            Constant.IMG_AUDIO_PATH_PLAYING_USER = null;
        }
        if (VoiceplayerUtil.isPlaying()) {
            VoiceplayerUtil.release();
        }
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnectionRequestError && null != webSocket) {
            initWebSocketInfo();
        }
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (null != audioRecoderUtils) {
            audioRecoderUtils.cancelRecord();
        }
        if (null != recordDialogVoiceImg) {
            recordDialogVoiceImg.clearAnimation();
        }
        if (null != dialog && dialog.isShowing()) {
            hideRecordDialog();
            dialog.dismiss();
            dialog = null;
        }
        if (null != webSocket && !isFinishing()) {
            ConnectBrokeRoomEntity connectBrokeRoomEntity = new ConnectBrokeRoomEntity();
            connectBrokeRoomEntity.setUrl("/broke/logout");
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
                webSocket.disconnect();
                webSocket = null;
            }
        }
        Constant.IMG_AUDIO_PATH_PLAYING_USER = null;
        Constant.IMG_AUDIO_PATH_PLAYING_SYSTEM = null;
        if (null != Constant.ANIMATION_BROKE_SYSTEM) {
            Constant.ANIMATION_BROKE_SYSTEM.stop();
            Constant.ANIMATION_BROKE_SYSTEM = null;
        }
        if (null != Constant.ANIMATION_BROKE_USER) {
            Constant.ANIMATION_BROKE_USER.stop();
            Constant.ANIMATION_BROKE_USER = null;
        }
        unbinder.unbind();
        super.onDestroy();
    }
}