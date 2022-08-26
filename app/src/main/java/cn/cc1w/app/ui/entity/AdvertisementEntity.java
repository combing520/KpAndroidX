package cn.cc1w.app.ui.entity;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 首页开机广告实体类
 * @author kpinfo
 */
public class AdvertisementEntity {
    private int code;
    private String message;
    private boolean success;
    private AdvertisementBean data;
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
    public AdvertisementBean getData() {
        return data;
    }
    public void setData(AdvertisementBean data) {
        this.data = data;
    }
    @Table(name = "advertisement")
    public static class AdvertisementBean  implements Serializable{
        @Column(name = "id", isId = true, autoGen = false, property = "NOT NULL")
        private String id; //广告id
        @Column(name = "title")
        private String title; //广告标题
        @Column(name = "pic_path")
        private String pic_path; //广告图
        @Column(name = "url")
        private String url;// 广告外链
        @Column(name = "time")
        private int time;// 广告秒数
        @Column(name = "action")
        private String action;
        @Column(name = "in_type")
        private String in_type;
        @Column(name = "in_id")
        private String in_id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getPic_path() {
            return pic_path;
        }
        public void setPic_path(String pic_path) {
            this.pic_path = pic_path;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public int getTime() {
            return time;
        }
        public void setTime(int time) {
            this.time = time;
        }
        public String getAction() {
            return action;
        }
        public void setAction(String action) {
            this.action = action;
        }
        public String getIn_type() {
            return in_type;
        }
        public void setIn_type(String in_type) {
            this.in_type = in_type;
        }
        public String getIn_id() {
            return in_id;
        }
        public void setIn_id(String in_id) {
            this.in_id = in_id;
        }

        @Override
        public String toString() {
            return "AdvertisementBean{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", pic_path='" + pic_path + '\'' +
                    ", url='" + url + '\'' +
                    ", time=" + time +
                    ", action='" + action + '\'' +
                    ", in_type='" + in_type + '\'' +
                    ", in_id='" + in_id + '\'' +
                    '}';
        }
    }
}