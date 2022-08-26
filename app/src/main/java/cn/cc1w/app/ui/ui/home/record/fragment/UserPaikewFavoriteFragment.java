package cn.cc1w.app.ui.ui.home.record.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

import app.cloud.ccwb.cn.linlibrary.blankview.BlankView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.EarnPhotoAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.ItemPhotoRecordEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import cn.kpinfo.log.KpLog;
import rxhttp.RxHttp;

/**
 * TA的拍客视频或者照片 /或者我 拍的拍客视频或者照
 *
 * @author kpinfo
 */
public class UserPaikewFavoriteFragment extends Fragment {
    @BindView(R.id.list_earn_like)
    RecyclerView recyclerView;
    @BindView(R.id.blankView_earn_like)
    BlankView blankView;
    private Context context;
    private View decorView;
    private Unbinder unbinder;
    private String paikewUid;
    private EarnPhotoAdapter adapter;

    public static UserPaikewFavoriteFragment newInstance() {
        return new UserPaikewFavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_user_paikew_favorite, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initList();
        initBlankView();
        requestPhotoList();
    }

    /**
     * 初始化空白页
     */
    private void initBlankView() {
        blankView.setBlankView(getString(R.string.str_like_paikew_none));
    }

    /**
     * 初始化List
     */
    private void initList() {
        adapter = new EarnPhotoAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context, 2), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.setMotionEventSplittingEnabled(false);
    }

    /**
     * 请求照片数据信息
     */
    private void requestPhotoList() {
        if (NetUtil.isNetworkConnected(context)) {
            if (null != adapter && adapter.getList().isEmpty()) {
                HashMap<String, Object> map = new HashMap<>(2);
                int currentPageIndex = 1;
                RxHttp.get(Constant.LIST_FAVORITE_USER_PAIKEW).add(Constant.STR_PAGE, String.valueOf(currentPageIndex)).add(Constant.STR_CW_UID_SYSTEM, TextUtils.isEmpty(paikewUid) ? Constant.CW_UID_PAIKEW : paikewUid)
                        .asResponseList(ItemPhotoRecordEntity.DataBean.class)
                        .as(RxLife.asOnMain(this))
                        .subscribe(list -> {
                            if (recyclerView != null && blankView != null) {
                                if (null != list && !list.isEmpty()) {
                                    adapter.setData(list);
                                    blankView.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    blankView.setVisibility(View.VISIBLE);
                                }
                            }
                        }, (OnError) error -> {
                            if ( recyclerView != null && blankView != null) {
                                recyclerView.setVisibility(View.GONE);
                                blankView.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message) {
            if (!TextUtils.isEmpty(message.getLabel()) && TextUtils.equals("userPaiKewId", message.getLabel())) {
                paikewUid = message.getContent();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        KpLog.getInstance().onAppViewScreenIn(context, getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        KpLog.getInstance().onAppViewScreenOut(context, getClass().getSimpleName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}