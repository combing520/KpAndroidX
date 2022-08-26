package cn.cc1w.app.ui.ui.pay;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.entity.WxPayEntity;

/**
 * 微信支付
 * @author kpinfo
 */
public class WeChatPayActivity extends CustomActivity {
    private Unbinder unbinder;
    private WxPayEntity wxPayEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat_pay);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化 data
     */
    private void initData() {
        Gson gson = new Gson();
        String wxPayJson = getIntent().getStringExtra("wxPayJson");
        wxPayEntity = gson.fromJson(wxPayJson, WxPayEntity.class);
        if (null != wxPayEntity) {
            initPayInfo();
        }
    }

    /**
     * 初始化支付信息
     */
    private void initPayInfo() {
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
        PayReq req = new PayReq();
        req.appId = TextUtils.isEmpty(wxPayEntity.getAppid()) ? "" : wxPayEntity.getAppid();
        req.partnerId = TextUtils.isEmpty(wxPayEntity.getPartnerid()) ? "" : wxPayEntity.getPartnerid();
        req.prepayId = TextUtils.isEmpty(wxPayEntity.getPrepayid()) ? "" : wxPayEntity.getPrepayid();
        req.nonceStr = TextUtils.isEmpty(wxPayEntity.getNoncestr()) ? "" : wxPayEntity.getNoncestr();
        req.timeStamp = String.valueOf(wxPayEntity.getTimestamp());
        req.packageValue = wxPayEntity.getPackageX();
        req.sign = TextUtils.isEmpty(wxPayEntity.getSign()) ? "" : wxPayEntity.getSign();
        api.sendReq(req);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        if (null != message && !TextUtils.isEmpty(message.getLabel())) {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}