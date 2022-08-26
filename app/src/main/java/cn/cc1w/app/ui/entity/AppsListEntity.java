package cn.cc1w.app.ui.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * 应用号的列表实体类
 */
public class AppsListEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemAppsListEntity> data;

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

    public List<ItemAppsListEntity> getData() {
        return data;
    }

    public void setData(List<ItemAppsListEntity> data) {
        this.data = data;
    }

    @Table(name = "appsList")
    public static class ItemAppsListEntity {
        @Column(name = "id", isId = true, autoGen = false, property = "NOT NULL")
        private String id;
        @Column(name = "name")
        private String name;
        @Column(name = "logo_pic_path")
        private String logo_pic_path;
        @Column(name = "summary")
        private String summary;
        @Column(name = "attention")
        private boolean attention;
        @Column(name = "news_id")
        private String news_id;
        @Column(name = "news_title")
        private String news_title;
        @Column(name = "news_summary")
        private String news_summary;
        @Column(name = "bg_path")
        private String bg_path;
        @Column(name = "group_id")
        private String group_id;
        @Column(name = "show_type")
        private String show_type;
        @Column(name = "in_type")
        private String in_type;
        @Column(name = "news_url")
        private String news_url;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getLogo_pic_path() {
            return logo_pic_path;
        }
        public void setLogo_pic_path(String logo_pic_path) {
            this.logo_pic_path = logo_pic_path;
        }
        public String getSummary() {
            return summary;
        }
        public void setSummary(String summary) {
            this.summary = summary;
        }
        public boolean isAttention() {
            return attention;
        }
        public void setAttention(boolean attention) {
            this.attention = attention;
        }
        public String getNews_id() {
            return news_id;
        }
        public void setNews_id(String news_id) {
            this.news_id = news_id;
        }
        public String getNews_title() {
            return news_title;
        }
        public void setNews_title(String news_title) {
            this.news_title = news_title;
        }
        public String getNews_summary() {
            return news_summary;
        }
        public void setNews_summary(String news_summary) {
            this.news_summary = news_summary;
        }
        public String getBg_path() {
            return bg_path;
        }
        public void setBg_path(String bg_path) {
            this.bg_path = bg_path;
        }
        public String getGroup_id() {
            return group_id;
        }
        public void setGroup_id(String group_id) {
            this.group_id = group_id;
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
        public String getNews_url() {
            return news_url;
        }
        public void setNews_url(String news_url) {
            this.news_url = news_url;
        }

        public ItemAppsListEntity(){

        }
    }
}