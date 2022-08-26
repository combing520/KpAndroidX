package cn.ccwb.cloud.httplibrary.rxhttp.entity;

/**
 * @author kpinfo
 * Html 传递给原生的实体类
 */
public class Html2AppEntity {
    private String title ;
    private String summary;

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

    @Override
    public String toString() {
        return "Html2AppEntity{" +
                "title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}
