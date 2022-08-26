package cn.cc1w.app.ui.ui.detail.gallery;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luck.picture.lib.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 * 普通新闻详情显示的
 * @author kpinfo
 */
public class GalleryFragment extends Fragment {
    @BindView(R.id.img_gallery)
    PhotoView postImg;
    private View decorView;
    private Unbinder unbinder;
    private String picUrl;
    private boolean isFirstLoad = true;
    private boolean isViewCreated;
    private boolean isUiVisible;

    public static GalleryFragment newInstance(String path, int pos, int total) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        args.putInt("pos", pos);
        args.putInt("total", total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            picUrl = getArguments().getString("path");
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_gallery, container, false);
        }
        unbinder = ButterKnife.bind(this, decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();
    }

    /**
     * 当前页面是否可见
     *
     * @param isVisibleToUser 当前页面的可见情况
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUiVisible = true;
            init();
        } else {
            isUiVisible = false;
        }
    }

    /**
     * 请求数据
     */
    private void init() {
        if (isUiVisible && isViewCreated) {
            isUiVisible = false;
            isViewCreated = false;
            if (isFirstLoad) {
                if (!TextUtils.isEmpty(picUrl)) {
                    isFirstLoad = false;
                    AppUtil.loadBigImg(picUrl, postImg);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}