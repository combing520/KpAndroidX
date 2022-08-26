package cn.cc1w.app.ui.base;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.kpinfo.log.KpLog;

/**
 * @author tongchao
 */
public abstract class BaseFragment extends Fragment {
    private Unbinder unbinder;
    protected View fragmentView;
    protected Context mContext;
    private LoadingDialog loading;
    protected boolean isVisible;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.fragmentView = inflater.inflate(this.getContentResId(), container, false);
        return this.fragmentView;
    }

    protected int getContentResId() {
        return 0;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initContentView();
    }

    private void initContentView() {
        unbinder = ButterKnife.bind(this, requireView());
        onQueryArguments();
        onFindView(getView());
        onBindListener();
        onApplyData();
    }

    /**
     * 取得传递的参数
     */
    protected void onQueryArguments() {

    }

    /**
     * 初始化控件、获取内部控件
     */
    protected void onFindView(View rootView) {

    }

    /**
     * 设置监听事件
     */
    protected void onBindListener() {

    }

    /**
     * 加载数据
     */
    protected void onApplyData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        KpLog.getInstance().onAppViewScreenIn(getActivity(), getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        KpLog.getInstance().onAppViewScreenOut(getActivity(), getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected void showToast(String str) {
        ToastUtil.showLongToast(str);
    }

    public abstract void frResume();

    public abstract void frPause();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            frResume();
        } else {
            isVisible = false;
            frPause();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    protected void showLoading() {
        if (loading == null) {
            loading = AppUtil.getLoading(getActivity());
        }
        if (loading != null && !loading.isShow()) {
            loading.show();
        }
    }

    protected void hideLoading() {
        if (loading != null && loading.isShow()) {
            loading.close();
        }
    }

    protected boolean isLoadingShow() {
        if (loading != null && loading.isShow()) {
            return true;
        } else {
            return false;
        }
    }
}
