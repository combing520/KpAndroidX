package cn.cc1w.app.ui.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * @author kpinfo
 * on 2021-01-15
 */
public class FunctionAdvertisement {
    private int code;
    private String message;
    private boolean success;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }
    @Table(name = "homeServiceAdvertisementList")
    public static class DataBean {
        @Column(name = "id", isId = true, autoGen = false, property = "NOT NULL")
        private String id;
        @Column(name = "title")
        private String title;
        @Column(name = "pic_path")
        private String pic_path;
        @Column(name = "url")
        private String url;
        @Column(name = "desc")
        private String desc;
        @Column(name = "type")
        private String type;
        @Column(name = "position")
        private String position;
        @Column(name = "position_text")
        private String position_text;
        @Column(name = "type_text")
        private String type_text;

        public DataBean(){}
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

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getPosition_text() {
            return position_text;
        }

        public void setPosition_text(String position_text) {
            this.position_text = position_text;
        }

        public String getType_text() {
            return type_text;
        }

        public void setType_text(String type_text) {
            this.type_text = type_text;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", pic_path='" + pic_path + '\'' +
                    ", url='" + url + '\'' +
                    ", desc='" + desc + '\'' +
                    ", type='" + type + '\'' +
                    ", position='" + position + '\'' +
                    ", position_text='" + position_text + '\'' +
                    ", type_text='" + type_text + '\'' +
                    '}';
        }
    }
}