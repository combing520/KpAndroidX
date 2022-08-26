package cn.cc1w.app.ui.entity;


import java.util.List;

/**
 * 专题 条目实体类
 * @author kpinfo
 */
public class ItemSpecialEntity {
    private String type ;
    private String date;
    private List<ItemBannerSpecialEntity>list ;
    public ItemSpecialEntity(String type, List<ItemBannerSpecialEntity> list,String date) {
        this.type = type;
        this.list = list;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public List<ItemBannerSpecialEntity> getList() {
        return list;
    }

    public String getDate() {
        return date;
    }
}
