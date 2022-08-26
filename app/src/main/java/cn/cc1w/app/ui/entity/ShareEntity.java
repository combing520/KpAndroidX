package cn.cc1w.app.ui.entity;

/**
 * 分享的实体类
 * @author kpinfo
 */
public class ShareEntity {
    private String cw_id; // 分享内容的id
    private String cw_content_type;// 分享内容的类型 news: 新闻ad: 广告biz:商品ask:问吧
    private String cw_share_type ;//动作share:分享transform：转发
    private String cw_platform;// 平台类型  微信：wechat,微信朋友圈：wxcircle,新浪微博：sina,QQ：qq,QQ空间：qzone
    private String cw_share_url;//   分享的链接
    private String cw_title;// 分享标题
    private String cw_summary;//分享的摘要
    private String cw_share_pic_path;//分享图片链接

    public String getCw_id() {
        return cw_id;
    }

    public void setCw_id(String cw_id) {
        this.cw_id = cw_id;
    }

    public String getCw_content_type() {
        return cw_content_type;
    }

    public void setCw_content_type(String cw_content_type) {
        this.cw_content_type = cw_content_type;
    }

    public String getCw_share_type() {
        return cw_share_type;
    }

    public void setCw_share_type(String cw_share_type) {
        this.cw_share_type = cw_share_type;
    }

    public String getCw_platform() {
        return cw_platform;
    }

    public void setCw_platform(String cw_platform) {
        this.cw_platform = cw_platform;
    }

    public String getCw_share_url() {
        return cw_share_url;
    }

    public void setCw_share_url(String cw_share_url) {
        this.cw_share_url = cw_share_url;
    }

    public String getCw_title() {
        return cw_title;
    }

    public void setCw_title(String cw_title) {
        this.cw_title = cw_title;
    }

    public String getCw_summary() {
        return cw_summary;
    }

    public void setCw_summary(String cw_summary) {
        this.cw_summary = cw_summary;
    }

    public String getCw_share_pic_path() {
        return cw_share_pic_path;
    }

    public void setCw_share_pic_path(String cw_share_pic_path) {
        this.cw_share_pic_path = cw_share_pic_path;
    }
}