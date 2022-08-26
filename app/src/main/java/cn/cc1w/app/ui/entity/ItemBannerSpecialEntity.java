package cn.cc1w.app.ui.entity;

/**
 * 专题中的 条目内容
 * @author kpinfo
 */
public class ItemBannerSpecialEntity {
    private String title;
    private String imgUrl;
    public ItemBannerSpecialEntity(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

}
