package cn.cc1w.app.ui.entity;

/**
 * Created by kpinfo on 2018/7/2.
 */
public class MenuItemEntity {
    private String name;
    private int icon;
    public MenuItemEntity(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
