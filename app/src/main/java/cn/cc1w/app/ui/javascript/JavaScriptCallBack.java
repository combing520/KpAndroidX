package cn.cc1w.app.ui.javascript;

import android.app.Activity;

/**
 * JavaScriptCallBack
 * @author kpinfo
 */
public interface JavaScriptCallBack {
     void setCanRefresh(boolean tag);
     void autoRefresh();

     void setHeadBarVisible(boolean visible);

     void fileUploadFinish(String json, String type);

     void fileUploadError(String json, String type);

     void startAppRecordCallBack();

     void endAppRecordCallBack();

     void imageQRCode(String pic);

     void setWebviewWindows(String type);

     void cwHeadBarConfig(String json);

     void commonBack(Activity activity);

     void commonShare(String json);
}