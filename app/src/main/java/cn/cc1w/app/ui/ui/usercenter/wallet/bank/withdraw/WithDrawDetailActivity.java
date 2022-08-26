package cn.cc1w.app.ui.ui.usercenter.wallet.bank.withdraw;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;




import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.R;
/**
 * 提现详情
 * @author kpinfo
 */
public class WithDrawDetailActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
    }

    /**
     * 初始化 导航头
     */
    private void initNavigation() {
        titleTv.setText("提现结果");
    }

    @OnClick({R.id.img_back_header_not_title, R.id.btn_complete_detail_withDraw})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.btn_complete_detail_withDraw) {
            completeWithDraw();
        }
    }

    /**
     * 完成提现
     */
    private void completeWithDraw() {
        String label = "closeWithDraw";
        String content = "content";

        EventBus.getDefault().post(new EventMessage(label, content));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
