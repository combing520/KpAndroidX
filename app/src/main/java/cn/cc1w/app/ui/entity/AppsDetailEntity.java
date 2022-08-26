package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 应用号详情 实体类
 * @author kpinfo
 */
public class AppsDetailEntity {
    private int code;
    private String message;
    private boolean success;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String name;
        private String logo_pic_path;
        private String summary;
        private boolean attention;
        private String bg_pic_path;
        private int user_num;
        private int news_num;
        private List<ListBean> list;

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

        public String getBg_pic_path() {
            return bg_pic_path;
        }

        public void setBg_pic_path(String bg_pic_path) {
            this.bg_pic_path = bg_pic_path;
        }

        public int getUser_num() {
            return user_num;
        }

        public void setUser_num(int user_num) {
            this.user_num = user_num;
        }

        public int getNews_num() {
            return news_num;
        }

        public void setNews_num(int news_num) {
            this.news_num = news_num;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            private String id;
            private String name;

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
        }
    }
}