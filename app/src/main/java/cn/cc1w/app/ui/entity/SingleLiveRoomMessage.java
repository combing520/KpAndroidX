package cn.cc1w.app.ui.entity;

/**
 * 直播室消息 单条的
 * @author kpinfo
 */
public class SingleLiveRoomMessage {
    private int code;
    private String message;
    private boolean success;
    private String method;
    private boolean more;
    private ItemBrokeEntity data;

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ItemBrokeEntity getData() {
        return data;
    }

    public void setData(ItemBrokeEntity data) {
        this.data = data;
    }


    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}