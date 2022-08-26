package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 评论 的实体类
 * @author kpinfo
 */
public class ItemCommentEntity {
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
        private String content;
        private String create_time;
        private String user_nickname;
        private String user_headpic;
        private int good_num;
        private boolean praise;
        private String news_id;
        private String news_title;
        private String news_summary;
        private String news_pic_path;
        private String show_type;
        private String in_type;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public String getCreate_time() {
            return create_time;
        }
        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
        public String getUser_nickname() {
            return user_nickname;
        }
        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }
        public String getUser_headpic() {
            return user_headpic;
        }
        public void setUser_headpic(String user_headpic) {
            this.user_headpic = user_headpic;
        }
        public int getGood_num() {
            return good_num;
        }
        public void setGood_num(int good_num) {
            this.good_num = good_num;
        }
        public boolean isPraise() {
            return praise;
        }
        public void setPraise(boolean praise) {
            this.praise = praise;
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
        public String getNews_pic_path() {
            return news_pic_path;
        }
        public void setNews_pic_path(String news_pic_path) {
            this.news_pic_path = news_pic_path;
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
