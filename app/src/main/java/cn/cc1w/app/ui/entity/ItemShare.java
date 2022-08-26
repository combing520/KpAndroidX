package cn.cc1w.app.ui.entity;

/**
 * @author kpinfo
 * on 2020-12-15
 */
public class ItemShare {
    private String name;
    private int resId;
    private ShareType type;

    public ItemShare(String name, int resId, ShareType type) {
        this.name = name;
        this.resId = resId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public ShareType getType() {
        return type;
    }

    public void setType(ShareType type) {
        this.type = type;
    }

}
