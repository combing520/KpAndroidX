package cn.cc1w.app.ui.ui.usercenter.integral.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import cn.cc1w.app.ui.adapter.IntegralRecordAdapter;
import cn.cc1w.app.ui.entity.IntegralRecordEntity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.widget.decoration.SpacesItemDecoration;
import cn.cc1w.app.ui.R;
import cn.kpinfo.log.KpLog;

/**
 *
 * 积分兑换记录
 * @author kpinfo
 */
public class IntegralRecordFragment extends Fragment {
    private View decorView;
    private Unbinder unbinder;
    @BindView(R.id.list_record_integral)
    RecyclerView list;
    private IntegralRecordAdapter adapter;
    private Context context;

    public IntegralRecordFragment() {
    }

    public static IntegralRecordFragment newInstance() {
        IntegralRecordFragment fragment = new IntegralRecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new IntegralRecordAdapter();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null == decorView){
            decorView = inflater.inflate(R.layout.fragment_integral_record, container, false);
        }
        unbinder = ButterKnife.bind(this,decorView);
        return decorView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initList();
    }

    private void initList() {
        list.setLayoutManager(new LinearLayoutManager(context));
        list.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(context,1),1));
        adapter.setData(initData());
        list.setAdapter(adapter);
    }

    private List<IntegralRecordEntity> initData() {
        List<IntegralRecordEntity> list = new ArrayList<>();
        list.add(new IntegralRecordEntity("花费20元","2018-8-27 15:37:30","-2000"));
        list.add(new IntegralRecordEntity("xx酒店80元下午茶餐券","2018-8-27 15:37:30","-600"));
        list.add(new IntegralRecordEntity("花费20元","2018-8-27 15:37:30","-2000"));
        list.add(new IntegralRecordEntity("xx酒店80元下午茶餐券","2018-8-27 15:37:30","-600"));

        return  list;
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