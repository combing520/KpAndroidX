package cn.cc1w.app.ui.ui.home.upload;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.luck.picture.lib.entity.LocalMedia;

import com.rxjava.rxlife.RxLife;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView;
import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.topicEdit.TObject;
import app.cloud.ccwb.cn.linlibrary.topicEdit.TopicEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.topic.HotTopicAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HotTopicEntity;
import cn.cc1w.app.ui.entity.PaikewCategoryEntity;
import cn.cc1w.app.ui.entity.SignFileUploadResponEntity;
import cn.cc1w.app.ui.entity.VideoUploadResponEntity;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import pub.devrel.easypermissions.EasyPermissions;
import rxhttp.RxHttp;
import rxhttp.RxHttpFormParam;
import rxhttp.RxHttpJsonParam;

/**
 * 上传视频
 */
public class PaikewVideoUploadActivity extends CustomActivity implements HotTopicAdapter.TagClickListener, TagFlowLayout.OnTagClickListener, TextView.OnEditorActionListener {
    @BindView(R.id.img_post_video_upload)
    RoundAngleImageView postImg;
    @BindView(R.id.txt_release_video_upload)
    TextView releaseBtn;
    @BindView(R.id.edit_title_video_upload)
    TopicEditText describeEdit;
    @BindView(R.id.txt_cnt_video_upload)
    TextView txtCntTv;
    @BindView(R.id.list_topic_hot_video_upload)
    RecyclerView hotTopicRecycleView;
    @BindView(R.id.flow_layout_video_upload)
    TagFlowLayout tagFlowLayout;
    @BindView(R.id.txt_none_search_video_upload)
    TextView topicSearchHitTv;
    @BindView(R.id.edit_search_video_upload)
    EditText tapSearchEdit;
    private TagAdapter tagAdapter;
    private HotTopicAdapter hotTopicAdapter;
    private LoadingDialog loading;
    private Unbinder unbinder;
    private LocalMedia localMedia;
    private int currentCategoryPageSize = 1;
    private int currentMoreCategoryBtnPos = -1;
    private static final int SIZE_CATEGORY_PAIKEW = 10;
    private long lastTime;
    private List<PaikewCategoryEntity.ItemPaikewCategoryEntity> tagDataSet = new ArrayList<>();
    private long enterTime;
    private String works2UploadCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paikew_video_upload);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initData();
        initLoading();
        initList();
        initTagInfo();
        initTopicEdit();
        requestCategory();
        requestHotTopic();
    }

    /**
     * 初始化list信息
     */
    private void initList() {
        hotTopicAdapter = new HotTopicAdapter();
        LinearLayoutManager hotTopicManager = new LinearLayoutManager(this);
        hotTopicRecycleView.setLayoutManager(hotTopicManager);
        hotTopicRecycleView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(this, 0.5f), 1));
        hotTopicRecycleView.setAdapter(hotTopicAdapter);
        hotTopicAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化标签信息
     */
    private void initTagInfo() {
        tagFlowLayout.setOnTagClickListener(this);
    }

    /**
     * 初始化 顶部的 edit
     */
    private void initTopicEdit() {
        TObject object = new TObject();
        object.setObjectRule("#");
        tapSearchEdit.setOnEditorActionListener(this);
    }

    /**
     * 初始化Loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化
     */
    private void initData() {
        localMedia = getIntent().getParcelableExtra("video");
        String coverPath = getIntent().getStringExtra("coverPath");
        if (!TextUtils.isEmpty(coverPath)) {
            uploadCoverPic(coverPath);
        }
        if (null != localMedia) {
            AppUtil.loadSplashImg(coverPath, postImg);
            LogUtil.e(" 拍客视频上传 上传的视频的 地址 = " + localMedia.getPath());
        }
        lastTime = System.currentTimeMillis();
        enterTime = System.currentTimeMillis();
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
                            tagAdapter = new TagAdapter<PaikewCategoryEntity.ItemPaikewCategoryEntity>(tagDataSet) {
                                @Override
                                public View getView(FlowLayout parent, int position, PaikewCategoryEntity.ItemPaikewCategoryEntity item) {
                                    View view = LayoutInflater.from(PaikewVideoUploadActivity.this).inflate(R.layout.layout_category_paikew, tagFlowLayout, false);
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
                        if (!isFinishing() && list != null && !list.isEmpty()) {
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
                                    View view = LayoutInflater.from(PaikewVideoUploadActivity.this).inflate(R.layout.layout_category_paikew, tagFlowLayout, false);
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
            RxHttp.get(Constant.LIST_TOPIC_RECOMMEND).add("page", 1).add("size", 100).add("keywords", keyword)
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
                        if (!isFinishing()) {
                            hotTopicRecycleView.setVisibility(View.GONE);
                            topicSearchHitTv.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }


    /**
     * 上传 封面图片
     *
     * @param coverPath 封面图片
     */
    private void uploadCoverPic(String coverPath) {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading) {
                loading.show();
            }
            RxHttpFormParam http = RxHttp.postForm(Constant.FILE_SINGLE_UPLOAD_QUKAN);
            http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString());
            http.addFile("file", new File(coverPath));
            http.asSingleUpload(SignFileUploadResponEntity.DataBean.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(data -> {
                        if (!isFinishing() && data != null) {
                            works2UploadCover = TextUtils.isEmpty(data.getPic_path_n()) ? "" : data.getPic_path_n();
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
                            IntentUtil.startActivity(this, LoginActivity.class);
                        }
                    });

        } else {
            ToastUtil.showShortToast(getString(R.string.network_error));
        }
    }

    /**
     * 更新视频
     */
    private void doUploadVideo() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - enterTime >= Constant.MIN_TIME_INTERVAL) {
            Set<Integer> ids = tagFlowLayout.getSelectedList();
            if (null == ids || ids.isEmpty()) {
                ToastUtil.showShortToast("请选择分类");
                return;
            }
            if (describeEdit.getText() != null) {
                String title = describeEdit.getText().toString();
                if (title.isEmpty()) {
                    ToastUtil.showShortToast("标题不能为空");
                    return;
                }
                if (NetUtil.isNetworkConnected(this)) {
                    if (null != loading) {
                        loading.show();
                    }
                    RxHttpFormParam http = RxHttp.postForm(Constant.VIDEO_UPLOAD_QUKAN);
                    http.add("appid", Constant.ID_APP).add("sign", AppUtil.getSignString()).add("appKey", Constant.APPKEY_QUKAN).add("memberId", Constant.ID_USER_QUKAN);
                    http.addFile("file", new File(localMedia.getPath()));
                    http.asSingleUpload(VideoUploadResponEntity.DataBean.class)
                            .as(RxLife.asOnMain(this))
                            .subscribe(data -> {
                                if (!isFinishing() && data != null) {
                                    upLoadVideo2PaikewServer(data.getFile_path(), data.getUrl(), String.valueOf(data.getId()));
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
                } else {
                    ToastUtil.showShortToast(getString(R.string.network_error));
                }
            }
        }
        enterTime = currentTime;
    }

    /**
     * 将视频文件上同步到 拍客服务器中去
     *
     * @param cover     视频的封面图
     * @param videoPath 视频的路径
     */
    private void upLoadVideo2PaikewServer(String cover, String videoPath, String videoId) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<Integer> ids = tagFlowLayout.getSelectedList();
        if (ids.size() != 0) {
            for (int i = 0; i < ids.size(); i++) {
                stringBuilder.append(tagDataSet.get(i).getCategory_id()).append(",");
            }
        }
        String title = "";
        if (describeEdit.getText() != null && !TextUtils.isEmpty(describeEdit.getText().toString())) {
            title = describeEdit.getText().toString();
        }
        RxHttpJsonParam http = RxHttp.postJson(Constant.VIDEO_UPLOAD_PAIKEW);
        http.add("title", title)
                .add("cover", TextUtils.isEmpty(works2UploadCover) ? cover : works2UploadCover).add("video_path", videoPath)
                .add("video_id", videoId).add("category_ids", stringBuilder.substring(0, stringBuilder.toString().length() - 1));
        http.asResponse(String.class)
                .as(RxLife.asOnMain(this))
                .subscribe(data -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (!isFinishing()) {
                        ToastUtil.showShortToast("上传成功");
                        String[] permissionList = new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                        };
                        if (EasyPermissions.hasPermissions(PaikewVideoUploadActivity.this, permissionList)) {
                            try {
                                File file = new File(localMedia.getPath());
                                if (file.exists() && !file.isDirectory()) {
                                    boolean isSuccess = file.delete();
                                    LogUtil.e("上传后 删除成功 = " + isSuccess);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        finish();
                    }
                }, (OnError) error -> {
                    if (null != loading && loading.isShow()) {
                        loading.close();
                    }
                    if (error.getThrowable() != null && error.getThrowable() instanceof AuthException) {
                        AppUtil.doUserLogOut();
                        IntentUtil.startActivity(this, LoginActivity.class);
                    }
                });
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

    @OnClick({R.id.img_close_video_upload, R.id.txt_release_video_upload})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_close_video_upload) {
            finish();
        } else if (id == R.id.txt_release_video_upload) { // 发布
            doUploadVideo();
        }
    }

    @OnTextChanged(R.id.edit_title_video_upload)
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

    @Override
    public void onTagClick(View v, int pos) {
        HotTopicEntity.ItemHotTopicEntity entity = hotTopicAdapter.getItem(pos);
        String topic = entity.getTopic_name();
        int editLen = describeEdit.getText().toString().length();
        if (!TextUtils.isEmpty(topic) && (editLen + topic.length() < 37)) {
            TObject object = new TObject();
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

    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
        Set<Integer> selectSet = tagFlowLayout.getSelectedList();
        if (position == currentMoreCategoryBtnPos) {  //
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
            String searchContent = tapSearchEdit.getText().toString();
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
        try {
            File file = new File(localMedia.getPath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unbinder.unbind();
    }
}