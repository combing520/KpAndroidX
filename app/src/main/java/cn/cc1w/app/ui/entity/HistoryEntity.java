package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 历史浏览记录 实体类
 * @author kpinfo
 */
public class HistoryEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemHistoryEntity> data;

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

    public List<ItemHistoryEntity> getData() {
        return data;
    }

    public void setData(List<ItemHistoryEntity> data) {
        this.data = data;
    }

    public static class ItemHistoryEntity {
        private String id;
        private String news_id;
        private String title;
        private String summary;
        private String pic_path;
        private String pic_path_s;
        private String create_time;
        private String app_id;
        private String app_name;
        private String app_logo_pic_path;
        private String show_type;
        private String in_type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNews_id() {
            return news_id;
        }

        public void setNews_id(String news_id) {
            this.news_id = news_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getPic_path() {
            return pic_path;
        }

        public void setPic_path(String pic_path) {
            this.pic_path = pic_path;
        }

        public String getPic_path_s() {
            return pic_path_s;
        }

        public void setPic_path_s(String pic_path_s) {
            this.pic_path_s = pic_path_s;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getApp_logo_pic_path() {
            return app_logo_pic_path;
        }

        public void setApp_logo_pic_path(String app_logo_pic_path) {
            this.app_logo_pic_path = app_logo_pic_path;
        }

        public String getShow_type() {
            return show_type;
        }

        public void setShow_type(String show_type) {
            this.show_type = show_type;
        }

        public String getIn_type() {
            return in_type;
        }

        public void setIn_type(String in_type) {
            this.in_type = in_type;
        }
    }
}
