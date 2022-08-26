package cn.cc1w.app.ui.ui.qr;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.LogUtil;

/**
 * @author kpinfo
 */
public class ScanResultActivity extends CustomActivity {
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    @BindView(R.id.txt_result_scan)
    TextView scanResultTv;
    private Unbinder unbinder;
    private String scanResultStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        
        init();
    }

    private void init() {
        unbinder = ButterKnife.bind(this);
        initData();
        initNavigation();
    }

    private void initData() {
        scanResultStr = TextUtils.isEmpty(getIntent().getStringExtra("result")) ? "" : getIntent().getStringExtra("result");

        LogUtil.e("扫描到的结果为 :" + scanResultStr);
    }

    private void initNavigation() {
        titleTv.setText("扫描结果");
        scanResultTv.setText(scanResultStr);
    }

    @OnClick({R.id.img_back_header_not_title})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_header_not_title) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}