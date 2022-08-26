package cn.cc1w.app.ui.ui.usercenter.integral.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.UserIntegralAdapter;
import cn.cc1w.app.ui.entity.ItemUserIntegralEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemGridDecoration;
import cn.cc1w.app.ui.R;
import cn.kpinfo.log.KpLog;

/**
 * 我的积分
 * @author kpinfo
 */
public class IntegralFragment extends Fragment {
    private View decorView;
    private Unbinder unbinder;
    @BindView(R.id.list_integral)
    RecyclerView list;
    private UserIntegralAdapter adapter;
    private Context context;
    public IntegralFragment() {
    }

    public static IntegralFragment newInstance() {
        IntegralFragment fragment = new IntegralFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new UserIntegralAdapter();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == decorView) {
            decorView = inflater.inflate(R.layout.fragment_integral, container, false);
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
    }

    /**
     * 初始化list
     */
    private void initList() {
        list.setLayoutManager(new GridLayoutManager(context, 2));
        list.addItemDecoration(new SpacesItemGridDecoration(AppUtil.dip2px(context, 8), 2,false));
        adapter.setData(initData());
        list.setAdapter(adapter);
    }

    private List<ItemUserIntegralEntity> initData() {
        List<ItemUserIntegralEntity> list = new ArrayList<>();
        list.add(new ItemUserIntegralEntity("话费20元", "", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533815049291&di=d43b8723ad7e507ee6b0ce77cef78202&imgtype=0&src=http%3A%2F%2Fpic33.nipic.com%2F20130920%2F12413197_102353020000_2.jpg"));
        list.add(new ItemUserIntegralEntity("xx酒店80元下午茶餐券", "", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533815049289&di=527602ace6b17d89fdeb021aebb930c2&imgtype=0&src=http%3A%2F%2Fpic129.nipic.com%2Ffile%2F20170516%2F10091525_112158027030_2.jpg"));
        list.add(new ItemUserIntegralEntity("话费20元", "", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534141447841&di=672ed404df0ea8be217f0e2981954aae&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F013a81595234bba8012193a3007fc4.jpg"));
        list.add(new ItemUserIntegralEntity("xx10元西点券", "", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533815049287&di=dd42f9df7ca35b3a64225e9f872b66d8&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130302%2F11096375_213509235353_2.jpg"));

        return list;
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
        unbinder.unbind();
    }
}