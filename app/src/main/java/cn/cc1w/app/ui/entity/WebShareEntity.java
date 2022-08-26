package cn.cc1w.app.ui.entity;

/**
 * 网页调用 分享的实体类
 * @author kpinfo
 */
public class WebShareEntity {
    private String picPath;// 缩略图地址
    private String redirect_url; // 用户刷新的 url
    private String summary;// 文字描述
    private String title; // title
    private String type;// 分享的内容类型   pic | web | video|wxMini
    private String functions; //  分享的平台 wxFriend:微信好友；wxCircle:微信朋友圈； qq: qq好友; sina :新浪微博 wxMini:微信小程序 【以 , 隔开】
    private String url; // 分享的内容对应的url
    private String wxMiniPath; // 分享的小程序的 页面路径
    private String wxUserName;// 微信小程序的原始id 【小程序原始ID获取方法：登录小程序管理后台-设置-基本设置-帐号信息】
    private String wxMiniImg; // 小程序消息封面图片，小于128k
    private String webpageUrl;//兼容低版本的网页链接

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWxMiniPath() {
        return wxMiniPath;
    }

    public void setWxMiniPath(String wxMiniPath) {
        this.wxMiniPath = wxMiniPath;
    }

    public String getWxUserName() {
        return wxUserName;
    }

    public void setWxUserName(String wxUserName) {
        this.wxUserName = wxUserName;
    }

    public String getWxMiniImg() {
        return wxMiniImg;
    }

    public void setWxMiniImg(String wxMiniImg) {
        this.wxMiniImg = wxMiniImg;
    }

    public String getWebpageUrl() {
        return webpageUrl;
    }

    public void setWebpageUrl(String webpageUrl) {
        this.webpageUrl = webpageUrl;
    }

    public String getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = functions;
    }

    @Override
    public String toString() {
        return "WebShareEntity{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", picPath='" + picPath + '\'' +
                ", redirect_url='" + redirect_url + '\'' +
                ", url='" + url + '\'' +
                ", functions='" + functions + '\'' +
                ", wxMiniPath='" + wxMiniPath + '\'' +
                ", wxUserName='" + wxUserName + '\'' +
                ", wxMiniImg='" + wxMiniImg + '\'' +
                ", webpageUrl='" + webpageUrl + '\'' +
                '}';
    }
}