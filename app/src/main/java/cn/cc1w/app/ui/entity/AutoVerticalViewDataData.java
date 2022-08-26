package cn.cc1w.app.ui.entity;

/**
 * 自动滚动的文字实体类
 * @author kpinfo
 */
public class AutoVerticalViewDataData {
    private String title;
    private String value;
    private String url;
    public AutoVerticalViewDataData(String title, String value, String url) {
        super();
        this.title = title;
        this.value = value;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}