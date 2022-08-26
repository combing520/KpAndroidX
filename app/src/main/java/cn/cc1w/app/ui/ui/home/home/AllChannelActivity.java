package cn.cc1w.app.ui.ui.home.home;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.home.HomeChannelAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HomeChannelEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.FileUtils;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 所有频道
 * @author kpinfo
 */
public class AllChannelActivity extends CustomActivity implements HomeChannelAdapter.OnHomeChannelLongClickListener {
    private Unbinder unbinder;
    @BindView(R.id.list_all_channel)
    RecyclerView allChannelList;
    @BindView(R.id.list_user_channel)
    RecyclerView userFocusList;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    private HomeChannelAdapter allChannelAdapter;
    private HomeChannelAdapter userFocusAdapter;
    private LoadingDialog loading;
    private boolean isFirstLoad;
    private boolean isFirstLoadUserFocusChannelData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_channel);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initNavigation();
        initList();
        initLoading();
        requestAllChannelList();
        requestUserFocusChannelList();
    }

    /**
     * 初始化loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
    }

    /**
     * 初始化Navigation
     */
    private void initNavigation() {
        titleTv.setText("频道订阅");
    }

    /**
     * 初始化 RecycleView
     */
    private void initList() {
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        allChannelAdapter = new HomeChannelAdapter();
        allChannelList.setLayoutManager(manager);
        allChannelList.addItemDecoration(new GridSpacingItemDecoration(4, AppUtil.dip2px(this, 8), true));
        allChannelList.setAdapter(allChannelAdapter);
        GridLayoutManager manager2 = new GridLayoutManager(this, 4);
        userFocusAdapter = new HomeChannelAdapter();
        userFocusList.setLayoutManager(manager2);
        userFocusList.addItemDecoration(new GridSpacingItemDecoration(4, AppUtil.dip2px(this, 8), true));
        userFocusList.setAdapter(userFocusAdapter);
        allChannelAdapter.addHomeChannelLongClickListener(this);
        userFocusAdapter.addHomeChannelLongClickListener(this);
    }

    /**
     * 请求频道列表数据
     */
    private void requestAllChannelList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.postJson(Constant.CHANNEL_INDEX_TOTAL)
                    .add(Constant.STR_CW_MACHINE_TYPE, Constant.CW_MACHINE_TYPE)
                    .asResponseList(HomeChannelEntity.ItemHomeChannelEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing() && list != null && !list.isEmpty()) {
                            allChannelAdapter.setData(list);
                        }
                    }, (OnError) error -> {
                    });
        }
    }

    /**
     * 请求用户关注的 频道
     */
    private void requestUserFocusChannelList() {
        if (NetUtil.isNetworkConnected(this)) {
            if (null != loading && isFirstLoad) {
                loading.show();
            }
            isFirstLoad = false;
            RxHttp.postJson(Constant.CHANNEL_FOCUS_USER)
                    .asResponseList(HomeChannelEntity.ItemHomeChannelEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                        if (!isFinishing() && list != null && !list.isEmpty()) {
                            userFocusAdapter.setData(list);
                            if (!isFirstLoadUserFocusChannelData) {
                                saveTitle2Cache(new Gson().toJson(list));
                            }
                            isFirstLoadUserFocusChannelData = false;
                        }
                    }, (OnError) error -> {
                        if (null != loading && loading.isShow()) {
                            loading.close();
                        }
                    });
        }
    }

    /**
     * 保存数据到缓存
     *
     * @param result Json数据
     */
    private void saveTitle2Cache(String result) {
        try {
            if (getExternalCacheDir() != null) {
                String title = "homeTitle";
                FileUtils.deleteFile(getExternalCacheDir().getAbsolutePath() + File.separator.concat(title).concat(Constant.SUFFIX_CACHE));
                LogUtil.d("全部保存 delete = " + getExternalCacheDir().getAbsolutePath() + File.separator.concat(title).concat(Constant.SUFFIX_CACHE));
                File file = new File(getExternalCacheDir(), File.separator.concat(title).concat(Constant.SUFFIX_CACHE));
                LogUtil.d("全部频道地址 =  " + file.getAbsolutePath());
                FileUtils.saveContent(result, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.txt_edit_all_channel})
    protected void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.txt_edit_all_channel) {
            editChannel();
        }
    }

    /**
     * 编辑频道
     */
    private void editChannel() {
        if (allChannelAdapter.getDataList().size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("AllChannelList", (ArrayList<? extends Parcelable>) allChannelAdapter.getDataList());
            bundle.putParcelableArrayList("userChannelList", (ArrayList<? extends Parcelable>) userFocusAdapter.getDataList());
            IntentUtil.startActivity(this, ChannelEditActivity.class, bundle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            if (TextUtils.equals("updateUserFocusChannel", message.getLabel())) {
                requestUserFocusChannelList();
                requestAllChannelList();
            } else if (TextUtils.equals("closeLast", message.getLabel())) {
                finish();
            }
        }
    }

    @Override
    public boolean onHomeChannelLongClick(View view, int position) {
        editChannel();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading && loading.isShow()) {
            loading.close();
            loading = null;
        }
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}