package cn.cc1w.app.ui.ui.usercenter.wallet.bank.forgetpwd;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;




import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.SelectAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.entity.ItemSelectEntity;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.R;
/**
 * 忘记密码
 * @author kpinfo
 */
public class ForgetPwdActivity extends CustomActivity {
    private Unbinder unbinder;

    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_forget)
    RecyclerView list;

    private SelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initList();
    }

    private void initList() {
        adapter = new SelectAdapter();
        adapter.setData(initData());

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    private List<ItemSelectEntity> initData() {

        List<ItemSelectEntity> list = new ArrayList<>();

        list.add(new ItemSelectEntity("建行卡", "储蓄卡(4135)", true));
        list.add(new ItemSelectEntity("工行卡", "储蓄卡(4138)", false));
        list.add(new ItemSelectEntity("农行卡", "储蓄卡(4139)", false));
        list.add(new ItemSelectEntity("广大卡号", "储蓄卡(4136)", false));

        return list;
    }


    /**
     * 初始化 导航头
     */
    private void initNavigation() {
        titleTv.setText("忘记支付密码");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_forget_pwd})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_forget_pwd) {
            forgetPwd();
        }
    }

    /**
     * 忘记密码
     */
    private void forgetPwd() {
        int selectPos = adapter.getSelectPos();
        if (selectPos >= 0) {
            LogUtil.e(adapter.getItem(selectPos).getCardNumber());
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, ForgetPayPwdActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
