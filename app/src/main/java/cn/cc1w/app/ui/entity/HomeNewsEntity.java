package cn.cc1w.app.ui.entity;

import java.io.Serializable;
import java.util.List;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 首页的新闻实体类
 */
public class HomeNewsEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemHomeNewsEntity> data;

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

    public List<ItemHomeNewsEntity> getData() {
        return data;
    }

    public void setData(List<ItemHomeNewsEntity> data) {
        this.data = data;
    }

    @Table(name = "homeNewsList")
    public static class ItemHomeNewsEntity implements Serializable {
        @Column(name = "type_name")
        private String type_name;
        @Column(name = "show_type")
        private String show_type;
        @Column(name = "in_type")
        private String in_type;
        @Column(name = "id", isId = true, autoGen = false, property = "NOT NULL")
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

        public ItemHomeNewsEntity() {

        }

        public static class NewsBean implements Serializable {
            private String id;
            private String news_id;
            private String title;
            private String summary;
            private String pic_path;
            private String pic_path_s;
            private String url;
            private String create_time;
            private String channel_id;
            private String click_num;
            private String good_num;
            private String comment_num;
            private String app_id;
            private String app_name;
            private String app_logo_pic_path;
            private String logo_pic_path;
            private String app_summary;
            private String type_name;
            private String show_type;
            private String in_type;
            private String module;
            private String icon_checked;
            private String icon_unchecked;
            private String bg_path;
            private String name;
            private int app_news_display_model;

            private String news_groud_id;
            private String action;
            private boolean attention;

            private String group_id;

            private int resId;
            private String special_type;

            public String getSpecial_type() {
                return special_type;
            }

            public void setSpecial_type(String special_type) {
                this.special_type = special_type;
            }

            public String getNews_groud_id() {
                return news_groud_id;
            }

            public void setNews_groud_id(String news_groud_id) {
                this.news_groud_id = news_groud_id;
            }

            private List<ItemNewsBean> news;

            private List<String> photos;

            public String getGood_num() {
                return good_num;
            }

            public void setGood_num(String good_num) {
                this.good_num = good_num;
            }

            public List<String> getPhotos() {
                return photos;
            }

            public void setPhotos(List<String> photos) {
                this.photos = photos;
            }

            public int getApp_news_display_model() {
                return app_news_display_model;
            }

            public void setApp_news_display_model(int app_news_display_model) {
                this.app_news_display_model = app_news_display_model;
            }

            public String getGroup_id() {
                return group_id;
            }

            public void setGroup_id(String group_id) {
                this.group_id = group_id;
            }

            public String getLogo_pic_path() {
                return logo_pic_path;
            }

            public void setLogo_pic_path(String logo_pic_path) {
                this.logo_pic_path = logo_pic_path;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public boolean isAttention() {
                return attention;
            }

            public void setAttention(boolean attention) {
                this.attention = attention;
            }

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

            public String getModule() {
                return module;
            }

            public void setModule(String module) {
                this.module = module;
            }

            public String getIcon_checked() {
                return icon_checked;
            }

            public void setIcon_checked(String icon_checked) {
                this.icon_checked = icon_checked;
            }

            public String getIcon_unchecked() {
                return icon_unchecked;
            }

            public void setIcon_unchecked(String icon_unchecked) {
                this.icon_unchecked = icon_unchecked;
            }

            public String getBg_path() {
                return bg_path;
            }

            public void setBg_path(String bg_path) {
                this.bg_path = bg_path;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ItemNewsBean> getNews() {
                return news;
            }

            public void setNews(List<ItemNewsBean> news) {
                this.news = news;
            }

            public int getResId() {
                return resId;
            }

            public void setResId(int resId) {
                this.resId = resId;
            }

            public static class ItemNewsBean {
                private String news_id;
                private String news_title;
                private String news_summary;
                private String show_type;
                private String in_type;

                private String news_url;

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
            }

            @Override
            public String toString() {
                return "NewsBean{" +
                        "create_time='" + create_time + '\'' +
                        ", channel_id='" + channel_id + '\'' +
                        ", click_num='" + click_num + '\'' +
                        ", comment_num='" + comment_num + '\'' +
                        ", app_id='" + app_id + '\'' +
                        ", app_name='" + app_name + '\'' +
                        ", app_logo_pic_path='" + app_logo_pic_path + '\'' +
                        ", logo_pic_path='" + logo_pic_path + '\'' +
                        ", app_summary='" + app_summary + '\'' +
                        ", type_name='" + type_name + '\'' +
                        ", show_type='" + show_type + '\'' +
                        ", in_type='" + in_type + '\'' +
                        ", module='" + module + '\'' +
                        ", icon_checked='" + icon_checked + '\'' +
                        ", icon_unchecked='" + icon_unchecked + '\'' +
                        ", bg_path='" + bg_path + '\'' +
                        ", name='" + name + '\'' +
                        ", action='" + action + '\'' +
                        ", attention=" + attention +
                        ", group_id='" + group_id + '\'' +
                        ", resId=" + resId +
                        '}';
            }
        }

    }
}
