package cn.cc1w.app.ui.entity;

/**
 * 更多专题的 实体类
 */
public class ItemSpecialMoreEntity {
    private String type; // 类型
    private String time; // 时间
    private String path; // 当前的 图片的url
    private String describe;// 描述

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }


    public ItemSpecialMoreEntity(String type, String time, String path, String describe) {
        this.type = type;
        this.time = time;
        this.path = path;
        this.describe = describe;
    }
}
