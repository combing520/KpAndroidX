package cn.cc1w.app.ui.entity;

import java.util.List;

import cn.ccwb.cloud.httplibrary.rxhttp.entity.LiveHostEntity;

/**
 * 直播室消息 多条的 单条
 * @author kpinfo
 */
public class ItemLiveRoomMessage {
    private int code;
    private String message;
    private boolean success;
    private String method;
    private List<LiveHostEntity.LiveHostItemEntity> data;
    private String id;
    private boolean more;// 是否有更多
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

    public List<LiveHostEntity.LiveHostItemEntity> getData() {
        return data;
    }

    public void setData(List<LiveHostEntity.LiveHostItemEntity> data) {
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
}
