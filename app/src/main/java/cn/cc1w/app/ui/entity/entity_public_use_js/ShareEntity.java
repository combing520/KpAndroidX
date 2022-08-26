package cn.cc1w.app.ui.entity.entity_public_use_js;

/**
 * 用户分享的实体类
 * @author kpinfo
 */
public class ShareEntity {
    // 分享的类型
    private String type;
    // 分享的id
    private String newsId;
    // 分享的标题
    private String title;
    // 分享的摘要
    private String summary;
    // 分享的地址
    private String url;
    // 分享成功后跳转的地址
    private String redirect_url;
    private String paikewerUserName;
    private String paikewType;
    private String picUrl;
    private boolean isPaikewShare;
    private boolean isCollect;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNewsId() {
        return newsId;
    }
    public void setNewsId(String newsId) {
        this.newsId = newsId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public boolean isPaikewShare() {
        return this.isPaikewShare;
    }

    public void setPaikewShare(boolean paikewShare) {
        this.isPaikewShare = paikewShare;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPaikewerUserName() {
        return paikewerUserName;
    }

    public void setPaikewerUserName(String paikewerUserName) {
        this.paikewerUserName = paikewerUserName;
    }

    public String getPaikewType() {
        return paikewType;
    }

    public void setPaikewType(String paikewType) {
        this.paikewType = paikewType;
    }


    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    @Override
    public String toString() {
        return "ShareEntity{" +
                "type='" + type + '\'' +
                ", newsId='" + newsId + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", url='" + url + '\'' +
                ", redirect_url='" + redirect_url + '\'' +
                ", paikewerUserName='" + paikewerUserName + '\'' +
                ", paikewType='" + paikewType + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", isPaikewShare=" + isPaikewShare +
                ", isCollect=" + isCollect +
                '}';
    }
}

