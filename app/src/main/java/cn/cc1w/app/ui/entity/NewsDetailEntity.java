package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 普通新闻详情 实体类
 */
public class NewsDetailEntity {
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
        private String news_id;
        private String title;
        private String summary;
        private String pic_path;
        private String url;
        private String click_num;
        private String comment_num;
        private String create_time;
        private String collection_num;
        private String content;
        private String app_id;
        private String app_name;
        private String app_logo_pic_path;
        private String app_summary;
        private boolean app_attention;
        private String type_name;
        private String show_type;
        private String in_type;
        private boolean is_praise;

        private String is_show;
        private boolean is_collection;
        private List<RelatedBean> related;

        private String pic_path_s;

        private String group_id;
        private String good_num;
        private AudioBean audio;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getCollection_num() {
            return collection_num;
        }

        public void setCollection_num(String collection_num) {
            this.collection_num = collection_num;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public boolean isApp_attention() {
            return app_attention;
        }

        public void setApp_attention(boolean app_attention) {
            this.app_attention = app_attention;
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

        public boolean isIs_praise() {
            return is_praise;
        }

        public void setIs_praise(boolean is_praise) {
            this.is_praise = is_praise;
        }

        public boolean isIs_collection() {
            return is_collection;
        }

        public void setIs_collection(boolean is_collection) {
            this.is_collection = is_collection;
        }

        public List<RelatedBean> getRelated() {
            return related;
        }

        public void setRelated(List<RelatedBean> related) {
            this.related = related;
        }

        public String getGood_num() {
            return good_num;
        }

        public void setGood_num(String good_num) {
            this.good_num = good_num;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getPic_path_s() {
            return pic_path_s;
        }

        public void setPic_path_s(String pic_path_s) {
            this.pic_path_s = pic_path_s;
        }

        public String getIs_show() {
            return is_show;
        }

        public void setIs_show(String is_show) {
            this.is_show = is_show;
        }

        public AudioBean getAudio() {
            return audio;
        }

        public void setAudio(AudioBean audio) {
            this.audio = audio;
        }

        public static class AudioBean {
            private String id;
            private Long size;
            private String time;
            private String url;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Long getSize() {
                return size;
            }

            public void setSize(Long size) {
                this.size = size;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class RelatedBean {
            /**
             * id : 201806072117573A28D5
             * news_id : 201806072117573A28D5
             * title : 2018高考表情
             * summary : 2018高考第一天结束了，右腿扫街新鲜出炉
             * pic_path : xx.jpg
             * url :
             * click_num :
             * comment_num :
             * create_time : 2018-06-07
             */

            private String id;
            private String news_id;
            private String title;
            private String summary;
            private String pic_path;
            private String url;
            private String click_num;
            private String comment_num;
            private String create_time;

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

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
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

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }
        }
    }
}
