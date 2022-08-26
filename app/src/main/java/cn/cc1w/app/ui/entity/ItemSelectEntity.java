package cn.cc1w.app.ui.entity;


/**
 * 选中的条目实体类
 * @author kpinfo
 */
public class ItemSelectEntity  {
    private String title; // title
    private String cardNumber; // 卡号
    private boolean isSelect; // 是否选中
    public String getTitle() {
        return title;
    }
    public String getCardNumber() {
        return cardNumber;
    }
    public boolean isSelect() {
        return isSelect;
    }
    public void setSelect(boolean select) {
        isSelect = select;
    }
    public ItemSelectEntity(String title, String cardNumber, boolean isSelect) {
        this.title = title;
        this.cardNumber = cardNumber;
        this.isSelect = isSelect;
    }
}
