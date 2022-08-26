package cn.cc1w.app.ui.ui.usercenter.wallet.bank;

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
import cn.cc1w.app.ui.adapter.BankListAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.entity.ItemCardInfoEntity;
import cn.cc1w.app.ui.R;
/**
 * 银行卡列表
 * @author kpinfo
 */
public class BankListActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.list_bank)
    RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
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

    /**
     * 初始化 list
     */
    private void initList() {
        BankListAdapter adapter = new BankListAdapter();
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter.setData(initData());
        list.setAdapter(adapter);
    }


    private List<ItemCardInfoEntity> initData() {
        List<ItemCardInfoEntity> list = new ArrayList<>();

        list.add(new ItemCardInfoEntity("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535783082989&di=b0fbae40d96fdab0fb79658d4286aaf4&imgtype=0&src=http%3A%2F%2Fimg.weixinyidu.com%2F160320%2F699ce00b.jpg", "中国建设银行", "信用卡", "", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535783082989&di=b0fbae40d96fdab0fb79658d4286aaf4&imgtype=0&src=http%3A%2F%2Fimg.weixinyidu.com%2F160320%2F699ce00b.jpg"));
        list.add(new ItemCardInfoEntity("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535782977654&di=9a230817543b2b04c505b390afa8dca9&imgtype=0&src=http%3A%2F%2Fpic2.16pic.com%2F00%2F24%2F26%2F16pic_2426848_b.jpg", "中国工商银行", "储蓄卡", "", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535782977654&di=9a230817543b2b04c505b390afa8dca9&imgtype=0&src=http%3A%2F%2Fpic2.16pic.com%2F00%2F24%2F26%2F16pic_2426848_b.jpg"));
        list.add(new ItemCardInfoEntity("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535783160882&di=6d4320afb06c57cf196a4fba0b8900de&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fdesign%2F00%2F07%2F85%2F23%2F59316ca9789ee.png", "中国农业银行", "储蓄卡", "", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535783160882&di=6d4320afb06c57cf196a4fba0b8900de&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fdesign%2F00%2F07%2F85%2F23%2F59316ca9789ee.png"));

        return list;
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText(getResources().getString(R.string.list_bank));
    }

    @OnClick({R.id.img_back_header_not_title, R.id.ll_bank_add})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.ll_bank_add) {
            Intent intent = new Intent();
            intent.setClass(this, BankListAddActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
