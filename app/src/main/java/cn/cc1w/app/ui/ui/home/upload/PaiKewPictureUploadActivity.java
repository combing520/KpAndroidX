package cn.cc1w.app.ui.ui.home.upload;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;

import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.rxjava.rxlife.RxLife;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.topicEdit.TObject;
import app.cloud.ccwb.cn.linlibrary.topicEdit.TopicEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.topic.HotTopicAdapter;
import cn.cc1w.app.ui.adapter.upload.PaikewUploadPictureAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HotTopicEntity;
import cn.cc1w.app.ui.entity.MultipleFileUploadRespondEntity;
import cn.cc1w.app.ui.entity.PaikewCategoryEntity;
import cn.cc1w.app.ui.interfaces.OnItemClickListener;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ScreenUtil;
import cn.cc1w.app.ui.utils.SharedPreferenceUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.utils.pictureSelector.GlideEngine;
import cn.cc1w.app.ui.utils.pictureSelector.MeSandboxFileEngine;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.RxHttp;
import rxhttp.RxHttpFormParam;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * 拍客上传 图片
 *
 * @author kpinfo
 */
public class PaiKewPictureUploadActivity extends CustomActivity implements OnItemClickListener, HotTopicAdapter.TagClickListener, TagFlowLayout.OnTagClickListener, TextView.OnEditorActionListener {
    @BindView(R.id.list_picture_upload)
    RecyclerView selectPictureRecycleView;
    @BindView(R.id.list_topic_hot_picture_upload)
    RecyclerView hotTopicRecycleView;
    @BindView(R.id.edit_title_picture_upload)
    TopicEditText describeEdit;
    @BindView(R.id.txt_release_picture_upload)
    TextView releaseBtn;
    @BindView(R.id.txt_cnt_picture_upload)
    TextView txtCntTv;
    @BindView(R.id.txt_topic_picture_upload)
    TextView topicTv;
    @BindView(R.id.flow_layout_pic_upload)
    TagFlowLayout tagFlowLayout;
    @BindView(R.id.layout_root_pic_upload)
    LinearLayout rootView;
    @BindView(R.id.edit_search_pic_upload)
    EditText searchEdit;
    @BindView(R.id.txt_none_search_picture_upload)
    TextView topicSearchHitTv;
    private LoadingDialog loading;
    private Unbinder unbinder;
    private PaikewUploadPictureAdapter adapter;
    private HotTopicAdapter hotTopicAdapter;
    private List<LocalMedia> picList = new ArrayList<>();
    private TagAdapter tagAdapter;
    private int currentCategoryPageSize = 1;
    private int currentMoreCategoryBtnPos = -1;
    private static final int SIZE_CATEGORY_PAIKEW = 10;
    private long lastTime;
    private List<PaikewCategoryEntity.ItemPaikewCategoryEntity> tagDataSet = new ArrayList<>();
    private boolean isFirstOpenInputMethod = true;
    private long enterTime;
    private final PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
    private final ImageEngine imageEngine = GlideEngine.createGlideEngine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pai_kew_picture_upload);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        ImmersionBar.with(this).statusBarColor(R.color.colorWhite).statusBarDarkFont(true).init();
        unbinder = ButterKnife.bind(this);
        overridePendingTransition(0, 0);
        initList();
        initData();
        initLoading();
        initTopicEdit();
        initTagInfo();
        initInputMethodInfo();
        requestCategory();
        requestHotTopic();
    }

    /**
     * 初始化Edit
     */
    private void initTopicEdit() {
        TObject object = new TObject();
        object.setObjectRule("#");
        searchEdit.setOnEditorActionListener(this);
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        picList = getIntent().getParcelableArrayListExtra("picList");
        adapter.setData(picList);
        lastTime = System.currentTimeMillis();
        enterTime = System.currentTimeMillis();
    }

    /**
     * 初始化标签信息
     */
    private void initTagInfo() {
        tagFlowLayout.setOnTagClickListener(this);
    }

    /**
     * 监听输入法打开和关闭
     */
    private void initInputMethodInfo() {
        ScreenUtil screenUtil = new ScreenUtil(this);
        screenUtil.observeInputlayout(rootView, new ScreenUtil.OnInputActionListener() {
            @Override
            public void onOpen() {
                if (isFirstOpenInputMethod) {
                    isFirstOpenInputMethod = false;
                    getSupportSoftInputHeight(PaiKewPictureUploadActivity.this);
                }
                selectPictureRecycleView.setVisibility(View.GONE);
            }

            @Override
            public void onClose() {
                selectPictureRecycleView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 获取软件盘的高度
     *
     * @return 输入法高度
     */
    private int getSupportSoftInputHeight(Activity activity) {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            softInputHeight = softInputHeight - getSoftButtonsBarHeight(activity);
        }
        if (softInputHeight < 0) {
            LogUtil.e("EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        if (softInputHeight > 0) {
            LogUtil.e("输入法的高度 = " + softInputHeight);
            Constant.HEIGHT_INPUT_METHOD_CURRENT = softInputHeight;
            SharedPreferenceUtil.setInputMethodHeight(softInputHeight);
        }
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return 底部虚拟按键栏的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 初始化 列表
     */
    private void initList() {
        adapter = new PaikewUploadPictureAdapter();
        hotTopicAdapter = new HotTopicAdapter();
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        selectPictureRecycleView.addItemDecoration(new GridSpacingItemDecoration(3, AppUtil.dip2px(this, 2), false));


        selectPictureRecycleView.setLayoutManager(manager);
        selectPictureRecycleView.setAdapter(adapter);

        LinearLayoutManager categoryManager = new LinearLayoutManager(this);
        categoryManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager hotTopicManager = new LinearLayoutManager(this);
        hotTopicRecycleView.setLayoutManager(hotTopicManager);
        hotTopicRecycleView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(this, 0.5f), 1));
        hotTopicRecycleView.setAdapter(hotTopicAdapter);
        adapter.setOnItemClickListener(this);
        hotTopicAdapter.setOnItemClickListener(this);
    }

    /**
     * 获取分类
     */
    private void requestCategory() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.CATEGORY_PAIKEW_UPLOAD).add(Constant.STR_PAGE, currentCategoryPageSize).add("size", SIZE_CATEGORY_PAIKEW)
                    .asResponseList(PaikewCategoryEntity.ItemPaikewCategoryEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && list != null && !list.isEmpty()) {
                            tagDataSet = list;
                            if (tagDataSet.size() == SIZE_CATEGORY_PAIKEW) {
                                PaikewCategoryEntity.ItemPaikewCategoryEntity moreCategoryEntity = new PaikewCategoryEntity.ItemPaikewCategoryEntity();
                                moreCategoryEntity.setCategory_name("更多");
                                tagDataSet.add(moreCategoryEntity);
                                currentMoreCategoryBtnPos = tagDataSet.size() - 1;
                                currentCategoryPageSize += 1;

                            } else {
                                currentMoreCategoryBtnPos = tagDataSet.size();
                            }
                            LogUtil.e(" requestCategory moreBtnPos = " + currentMoreCategoryBtnPos);
                            tagAdapter = new TagAdapter<PaikewCategoryEntity.ItemPaikewCategoryEntity>(tagDataSet) {
                                @Override
                                public View getView(FlowLayout parent, int position, PaikewCategoryEntity.ItemPaikewCategoryEntity item) {
                                    View view = LayoutInflater.from(PaiKewPictureUploadActivity.this).inflate(R.layout.layout_category_paikew, tagFlowLayout, false);
                                    TextView tv = view.findViewById(R.id.tv_title_tag);
                                    if (null != tv) {
                                        tv.setText(item.getCategory_name());
                                        return tv;
                                    }
                                    return null;
                                }
                            };
                            tagFlowLayout.setAdapter(tagAdapter);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 加载更多的分类
     */
    private void loadMoreCategory() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.CATEGORY_PAIKEW_UPLOAD).add(Constant.STR_PAGE, currentCategoryPageSize).add("size", SIZE_CATEGORY_PAIKEW)
                    .asResponseList(PaikewCategoryEntity.ItemPaikewCategoryEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            tagDataSet.remove(currentMoreCategoryBtnPos);
                            tagDataSet.addAll(list);
                            if (tagDataSet.size() == SIZE_CATEGORY_PAIKEW) {
                                PaikewCategoryEntity.ItemPaikewCategoryEntity moreCategoryEntity = new PaikewCategoryEntity.ItemPaikewCategoryEntity();
                                moreCategoryEntity.setCategory_name("更多");
                                tagDataSet.add(moreCategoryEntity);
                                currentMoreCategoryBtnPos = tagDataSet.size() - 1;
                                currentCategoryPageSize += 1;
                            } else {
                                currentMoreCategoryBtnPos = tagDataSet.size();
                            }
                            LogUtil.e("loadMoreCategory =  currentMoreCategoryBtnPos " + currentMoreCategoryBtnPos);
                            Set<Integer> selectIds = tagFlowLayout.getSelectedList();
                            tagAdapter = new TagAdapter<PaikewCategoryEntity.ItemPaikewCategoryEntity>(tagDataSet) {
                                @Override
                                public View getView(FlowLayout parent, int position, PaikewCategoryEntity.ItemPaikewCategoryEntity item) {
                                    View view = LayoutInflater.from(PaiKewPictureUploadActivity.this).inflate(R.layout.layout_category_paikew, tagFlowLayout, false);
                                    TextView tv = view.findViewById(R.id.tv_title_tag);
                                    if (null != tv) {
                                        tv.setText(item.getCategory_name());
                                        return tv;
                                    }
                                    return null;
                                }
                            };
                            tagAdapter.setSelectedList(selectIds);
                            tagFlowLayout.setAdapter(tagAdapter);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 获取热门话题
     */
    private void requestHotTopic() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.LIST_TOPIC_HOT).add("size", 20)
                    .asResponseList(HotTopicEntity.ItemHotTopicEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            hotTopicAdapter.setData(list);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 搜索
     */
    private void doSearchTopic(String keyword) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttp.get(Constant.LIST_TOPIC_RECOMMEND)
                    .add("size", 100).add("keywords", keyword).add("page", 1)
                    .asResponseList(HotTopicEntity.ItemHotTopicEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing()) {
                            if (null != list && !list.isEmpty()) {
                                hotTopicRecycleView.setVisibility(View.VISIBLE);
                                topicSearchHitTv.setVisibility(View.GONE);
                                hotTopicAdapter.setData(list);
                            } else {
                                hotTopicRecycleView.setVisibility(View.GONE);
                                topicSearchHitTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        ToastUtil.showShortToast(error.getErrorMsg());
                        if (!isFinishing()) {
                            hotTopicRecycleView.setVisibility(View.GONE);
                            topicSearchHitTv.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    /**
     * 打开图库
     */
    private void openAlbum() {
        PictureSelector.create(this).openGallery(SelectMimeType.ofImage()).setSelectorUIStyle(selectorStyle)
                .setImageEngine(imageEngine)
                .isQuickCapture(true)
                .setMaxSelectNum(Constant.CNT_MAX_PIC_UPLOAD)
                .setMinSelectNum(0)
                .setSelectedData(picList)
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
                            picList = selectList;
                            adapter.setData(picList);
                        }
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d("PictureSelector onCancel !!! ");
                    }
                });
    }

    @OnClick({R.id.img_close_picture_upload, R.id.txt_release_picture_upload, R.id.txt_topic_picture_upload})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_close_picture_upload) {
            finish();
        } else if (id == R.id.txt_release_picture_upload) {  // 上传
            doUploadPicFiles();
        } else if (id == R.id.txt_topic_picture_upload) { // 话题
//
//                Intent intent = new Intent();
//                intent.setClass(this, HotTopicTagActivity.class);
//                startActivityForResult(intent, CODE_TOPIC_HOT);
////                IntentUtil.startActivity(this, HotTopicTagActivity.class, null);
        }
    }

    @OnTextChanged(R.id.edit_title_picture_upload)
    public void onTextChange(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() >= 10) {
                txtCntTv.setText(renderColorfulStr(text.length() + " /40", 0, 2));
            } else {
                txtCntTv.setText(renderColorfulStr(text.length() + " /40", 0, 1));
            }
        } else {
            txtCntTv.setText("0/40");
        }
    }

    /**
     * 上传文件
     */
    private void doUploadPicFiles() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - enterTime >= Constant.MIN_TIME_INTERVAL) {
            Set<Integer> ids = tagFlowLayout.getSelectedList();
            if (null == ids || ids.isEmpty()) {
                ToastUtil.showShortToast("请选择分类");
                return;
            }
            String title = describeEdit.getText().toString().trim();
            if (title.isEmpty()) {
                ToastUtil.showShortToast("标题不能为空");
                return;
            }
            if (NetUtil.isNetworkConnected(this)) {
                if (!picList.isEmpty()) {
                    List<String> picPathList = new ArrayList<>();
                    for (LocalMedia localMedia : picList) {
                        //压缩图片
                        if (!TextUtils.isEmpty(localMedia.getCompressPath())) {
                            picPathList.add(localMedia.getCompressPath());
                        } else {
                            picPathList.add(localMedia.getPath());
                        }
                    }
                    // 发送图片到服务器
                    if (!picPathList.isEmpty()) {
                        sendPicList2QukanServer(picPathList);
                    }
                }
            } else {
                ToastUtil.showShortToast(getString(R.string.network_error));
            }
        }
        enterTime = currentTime;
    }


    /**
     * 将图片文件上传到 趣看服务器中
     *
     * @param picPathList 图片地址几个
     */
    private void sendPicList2QukanServer(List<String> picPathList) {
        if (null != loading) {
            loading.show();
        }
        RxHttpFormParam http = RxHttp.postForm(Constant.FILE_MULTIPLE_UPLOAD_QUKAN);
        http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString());
        for (String filePath : picPathList) {
            http.addFile("files", new File(filePath));
        }
        http.asMultipleUpload(MultipleFileUploadRespondEntity.DataBean.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (!isFinishing() && data != null) {
                        List<String> ids = new ArrayList<>();  // 图片的地址
                        List<String> paths = new ArrayList<>();// 图片的地址
                        for (MultipleFileUploadRespondEntity.DataBean item : data) {
                            ids.add(item.getId());
                            paths.add(item.getPic_path_n());
                            LogUtil.e(" url = " + item.getPic_path_n() + "  id = " + item.getId());
                        }
                        if (!paths.isEmpty()) {
                            updatePic2PaikePicServer(paths, ids);
                        }
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(this, LoginActivity.class);
                    }
                });
    }

    /**
     * 将图片更新到 拍客图片库
     *
     * @param picPathList 待上传的图片
     */
    private void updatePic2PaikePicServer(List<String> picPathList, List<String> ids) {
        String title = "";
        if (describeEdit.getText() != null && !TextUtils.isEmpty(describeEdit.getText().toString())) {
            title = describeEdit.getText().toString().trim();
        }
        StringBuilder stringBuilder = new StringBuilder();
        Set<Integer> tagIds = tagFlowLayout.getSelectedList();
        for (int i = 0; i < tagIds.size(); i++) {
            stringBuilder.append(tagDataSet.get(i).getCategory_id()).append(",");
        }
        RxHttpFormParam http = RxHttp.postForm(Constant.PIC_UPLOAD_PAIKEW);
        http.add("title", TextUtils.isEmpty(title) ? "" : title);
        http.add("category_ids", (stringBuilder.substring(0, stringBuilder.toString().length() - 1)));
        for (int i = 0; i < picPathList.size(); i++) {
            http.add("photos_path[" + i + "]", picPathList.get(i)).add("picture_ids[" + i + "]", ids.get(i));
        }
        http.asResponse(String.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (!isFinishing()) {
                        ToastUtil.showShortToast("上传成功");
                        finish();
                    }

                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    ToastUtil.showShortToast(error.getErrorMsg());
                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(this, LoginActivity.class);
                    }
                });
    }

    // 生成指定长度的随机字符串
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @Override
    public void onItemClick(View targetView, int pos) {
        LocalMedia media = adapter.getItem(pos);
        if (null != media) {
            openAlbum();
        }
    }

    /**
     * render 带有颜色的 spannableStringBuilder
     *
     * @param targetStr 对应的文字
     * @param endPos    结束位置
     */
    private SpannableStringBuilder renderColorfulStr(String targetStr, int startPos, int endPos) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(targetStr);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public void onTagClick(View v, int pos) {
        HotTopicEntity.ItemHotTopicEntity entity = hotTopicAdapter.getItem(pos);
        String topic = entity.getTopic_name();
        if (describeEdit.getText() != null) {
            int editLen = describeEdit.getText().toString().length();
            if (!TextUtils.isEmpty(topic) && (editLen + topic.length() < 37)) {
                //话题对象，可继承此类实现特定的业务逻辑
                TObject object = new TObject();
                //匹配规则
                object.setObjectRule("#");
                object.setObjectText(topic);
                describeEdit.setObject(object);
                if (describeEdit.getText().toString().length() >= 10) {
                    txtCntTv.setText(renderColorfulStr(describeEdit.getText().toString().length() + " /40", 0, 2));
                } else {
                    txtCntTv.setText(renderColorfulStr(describeEdit.getText().toString().length() + " /40", 0, 1));
                }
            }
        }
    }

    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
        Set<Integer> selectSet = tagFlowLayout.getSelectedList();
        if (position == currentMoreCategoryBtnPos) {
            long currentTime = System.currentTimeMillis();
            if (selectSet.contains(position)) {
                tagAdapter.unSelected(position, view);
                selectSet.remove(position);
                tagAdapter.setSelectedList(selectSet);
            }
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                loadMoreCategory();
            }
        }
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((actionId == EditorInfo.IME_ACTION_SEARCH)) {
            String searchContent = searchEdit.getText().toString();
            if (!TextUtils.isEmpty(searchContent)) {
                if (NetUtil.isNetworkConnected(this)) {
                    doSearchTopic(searchContent);
                } else {
                    ToastUtil.showShortToast(getString(R.string.network_error));
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}