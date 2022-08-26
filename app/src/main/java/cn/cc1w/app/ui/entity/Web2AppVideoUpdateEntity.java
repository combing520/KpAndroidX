package cn.cc1w.app.ui.entity;

/**
 * 网页上传 视频到APP
 */
public class Web2AppVideoUpdateEntity {
    private String minLength; // 最小时长
    private String maxLength; // 最大时长
    private String callBackMethod; // 上传视频成功后 webView回调的方法

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getCallBackMethod() {
        return callBackMethod;
    }

    public void setCallBackMethod(String callBackMethod) {
        this.callBackMethod = callBackMethod;
    }
}
