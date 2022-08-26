package cn.cc1w.app.ui.entity;


import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 直播
 * @author kpinfo
 */
public class ItemLiveHostEntity {
    private int code;
    private String message;
    private boolean success;
    private boolean more;
    private String id;
    private String method;
    private LiveHostEntity.LiveHostItemEntity data;
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public LiveHostEntity.LiveHostItemEntity getData() {
        return data;
    }
    public void setData(LiveHostEntity.LiveHostItemEntity data) {
        this.data = data;
    }
    public boolean isMore() {
        return more;
    }
    public void setMore(boolean more) {
        this.more = more;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
}
