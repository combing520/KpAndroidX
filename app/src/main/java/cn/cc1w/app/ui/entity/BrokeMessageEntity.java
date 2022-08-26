package cn.cc1w.app.ui.entity;

/**
 * 爆料消息实体类
 * @author kpinfo
 */
public class BrokeMessageEntity {
    private int code;
    private String message;
    private boolean success;
    private ItemBrokeEntity data;
    private boolean more;
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
