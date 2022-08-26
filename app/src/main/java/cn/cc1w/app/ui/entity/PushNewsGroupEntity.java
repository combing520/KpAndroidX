package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 推送的新闻组实体类
 */
public class PushNewsGroupEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemPushNewsGroupEntity> data;

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

    public List<ItemPushNewsGroupEntity> getData() {
        return data;
    }

    public void setData(List<ItemPushNewsGroupEntity> data) {
        this.data = data;
    }

    public static class ItemPushNewsGroupEntity {
        private String type_name;
        private String show_type;
        private String in_type;
        private List<NewsBean> news;

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
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

        public List<NewsBean> getNews() {
            return news;
        }

        public void setNews(List<NewsBean> news) {
            this.news = news;
        }

        public static class NewsBean {
            private String id;
            private String news_id;
            private String title;
            private String summary;
            private String keyword;
            private String pic_path;
            private String pic_path_s;
            private String url;
            private String create_time;
            private String channel_id;
            private String click_num;
            private String comment_num;
            private String app_id;
            private String app_name;
            private String app_logo_pic_path;
            private String app_summary;
            private String group_id;
            private boolean is_show;
            private String type_name;
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

            public String getKeyword() {
                return keyword;
            }

            public void setKeyword(String keyword) {
                this.keyword = keyword;
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

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getChannel_id() {
                return channel_id;
            }

            public void setChannel_id(String channel_id) {
                this.channel_id = channel_id;
            }

            public String getClick_num() {
                return click_num;
            }

            public void setClick_num(String click_num) {
                this.click_num = click_num;
            }

            public String getComment_num() {
                return comment_num;
            }

            public void setComment_num(String comment_num) {
                this.comment_num = comment_num;
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

            public String getApp_summary() {
                return app_summary;
            }

            public void setApp_summary(String app_summary) {
                this.app_summary = app_summary;
            }

            public String getGroup_id() {
                return group_id;
            }

            public void setGroup_id(String group_id) {
                this.group_id = group_id;
            }

            public boolean isIs_show() {
                return is_show;
            }

            public void setIs_show(boolean is_show) {
                this.is_show = is_show;
            }

            public String getType_name() {
                return type_name;
            }

            public void setType_name(String type_name) {
                this.type_name = type_name;
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
}