package cn.cc1w.app.ui.entity;

import java.io.Serializable;

/**
 * @author kpinfo
 * on 2020-07-16
 * 推送实体类
 */
public class PushEntity implements Serializable {
    // 推送类型
    private String pushType;
    // 推送 id
    private String pushId;
    // 推送 url
    private String pushUrl;

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }
    @Override
    public String toString() {
        return "PushEntity{" +
                "pushType='" + pushType + '\'' +
                ", pushId='" + pushId + '\'' +
                ", pushUrl='" + pushUrl + '\'' +
                '}';
    }
}
