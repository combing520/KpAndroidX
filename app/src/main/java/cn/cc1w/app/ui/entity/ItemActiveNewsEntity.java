package cn.cc1w.app.ui.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * 动新闻实体类
 * @author kpinfo
 */
public class ItemActiveNewsEntity {
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
        private String type_name;
        private String show_type;
        private String in_type;
        private List<ActiveNewsEntity> news;
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
        public List<ActiveNewsEntity> getNews() {
            return news;
        }
        public void setNews(List<ActiveNewsEntity> news) {
            this.news = news;
        }

        @Table(name="activeNews")
        public static class ActiveNewsEntity {
            @Column(name = "id", isId = true, autoGen = false,property = "NOT NULL")
            private String id;
            @Column(name = "news_id")
            private String news_id;
            @Column(name = "title")
            private String title;
            @Column(name = "summary")
            private String summary;
            @Column(name = "pic_path")
            private String pic_path;
            @Column(name = "url")
            private String url;
            @Column(name = "create_time")
            private String create_time;
            @Column(name = "click_num")
            private String click_num;
            @Column(name = "comment_num")
            private String comment_num;
            @Column(name = "type_name")
            private String type_name;
            @Column(name = "show_type")
            private String show_type;
            @Column(name = "in_type")
            private String in_type;
            @Column(name = "video_path")
            private String video_path;
            @Column(name = "video_id")
            private String video_id;
            @Column(name = "channel_id")
            private String channel_id;
            @Column(name = "pic_path_s")
            private String pic_path_s;
            @Column(name = "width")
            private String width; // 视频的宽度
            @Column(name = "height")
            private String height; // 视频的高度

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

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
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

            public String getVideo_path() {
                return video_path;
            }

            public void setVideo_path(String video_path) {
                this.video_path = video_path;
            }

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }

            public String getChannel_id() {
                return channel_id;
            }

            public void setChannel_id(String channel_id) {
                this.channel_id = channel_id;
            }

            public String getPic_path_s() {
                return pic_path_s;
            }

            public void setPic_path_s(String pic_path_s) {
                this.pic_path_s = pic_path_s;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }
        }
    }
}