package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 贴片广告实体类
 * @author kpinfo
 */
public class PostAdEntity {
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

    public static class DataBean {
        private String id;
        private String title;
        private String pic_path;
        private String url;
        private String type;
        private String position;
        private String position_text;
        private String type_text;

        private String desc;

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


        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}