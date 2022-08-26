package cn.cc1w.app.ui.javascript;

import android.app.Activity;

/**
 * JavaScriptCallBackBase
 * @author kpinfo
 */
public class JavaScriptCallBackBase implements JavaScriptCallBack {
    @Override
    public void setCanRefresh(boolean tag) {
    }

    @Override
    public void autoRefresh() {
    }

    @Override
    public void setHeadBarVisible(boolean visible) {
    }

    @Override
    public void fileUploadFinish(String json, String type) {
    }

    @Override
    public void fileUploadError(String json, String type) {
    }

    @Override
    public void startAppRecordCallBack() {
    }

    @Override
    public void endAppRecordCallBack() {
    }

    @Override
    public void imageQRCode(String pic) {
    }

    @Override
    public void setWebviewWindows(String type) {

    }

    @Override
    public void cwHeadBarConfig(String json) {

    }

    @Override
    public void commonBack(Activity activity) {
        activity.finish();
    }

    @Override
    public void commonShare(String json) {

    }
}