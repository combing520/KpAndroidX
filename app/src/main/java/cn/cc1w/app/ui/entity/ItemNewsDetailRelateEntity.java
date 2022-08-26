package cn.cc1w.app.ui.entity;

/**
 * 新闻详情中的 相关新闻 条目实体类
 */
public class ItemNewsDetailRelateEntity {
    private String id;
    private String news_id;
    private String title;
    private String url;
    private String summary;
    private String pic_path;
    private String pic_path_s;
    private String create_time;
    private String click_num;
    private String app_id;
    private String app_name;
    private String app_logo_pic_path;
    private String app_summary;
    private String comment_num;
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

    public String getPic_path_s() {
        return pic_path_s;
    }

    public void setPic_path_s(String pic_path_s) {
        this.pic_path_s = pic_path_s;
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
}
