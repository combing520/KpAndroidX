package cn.cc1w.app.ui.entity;

/**
 * 我的最爱
 *
 * @author kpinfo
 */
public class ItemAppsRecommendEntity {
    private String title; // 显示的title
    private String label; // 显示的标签
    private String path; // 图片


    public ItemAppsRecommendEntity(String title, String label, String path) {
        this.title = title;
        this.label = label;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getLabel() {
        return label;
    }

    public String getPath() {
        return path;
    }

}
