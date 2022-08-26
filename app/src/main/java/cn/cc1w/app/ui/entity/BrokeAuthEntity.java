package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 爆料 授权成功后的实体类
 * @author kpinfo
 */
public class BrokeAuthEntity {
    private int code;
    private String message;
    private boolean success;
    private boolean more;
    private List<ItemBrokeEntity> data;
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
    public List<ItemBrokeEntity> getData() {
        return data;
    }
    public void setData(List<ItemBrokeEntity> data) {
        this.data = data;
    }
    public boolean isMore() {
        return more;
    }
    public void setMore(boolean more) {
        this.more = more;
    }
}
