package cn.cc1w.app.ui.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.ToastUtil;

/**
 * Created by kpinfo on 2019/3/5.
 * <p>
 * 微信支付
 */

public class WXPayEntryActivity extends CustomActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }


    /**
     * 名称	描述	解决方案
     * 0	成功	展示成功页面
     * -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
     * -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {

        LogUtil.e("支付情况  code =  " + resp.errCode +
                " type =  " + resp.getType() +
                "  transaction = " + resp.transaction +
                " errStr" + resp.errStr

        );
        if (resp != null) {
            if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) { // 支付成功
                if (resp.errCode == 0) {  // 支付成功
                    //  刷新
                    EventBus.getDefault().post(new EventMessage("paySuccess", "paySuccess"));
                } else if (resp.errCode == -1) {  // -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                    ToastUtil.showShortToast("支付参数错误");
                    EventBus.getDefault().post(new EventMessage("payFailure", "payFailure"));
                } else if (resp.errCode == -2) { // -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
                    ToastUtil.showShortToast("支付取消");
                    EventBus.getDefault().post(new EventMessage("payFailure", "payFailure"));
                } else { // 支付失败
                    EventBus.getDefault().post(new EventMessage("payFailure", "payFailure"));
                }
            } else {  // 支付失败
                EventBus.getDefault().post(new EventMessage("payFailure", "payFailure"));
            }
        } else { //  支付失败
            EventBus.getDefault().post(new EventMessage("payFailure", "payFailure"));

        }
        finish();
        //自定义跳转回调地址
        overridePendingTransition(0,0);
    }
}