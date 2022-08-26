package cn.cc1w.app.ui.entity;

/**
 * 银行卡 条目实体类
 * @author kpinfo
 */
public class ItemCardInfoEntity {
    private String bankIconPath; // 银行卡的小图标
    private String bankName;//  银行 名称
    private String bankType;// 银行卡类型
    private String bankCardNumber;// 银行卡 卡号
    private String bankBigImgPath;// 银行卡大图

    public ItemCardInfoEntity(String bankIconPath, String bankName, String bankType, String bankCardNumber, String bankBigImgPath) {
        this.bankIconPath = bankIconPath;
        this.bankName = bankName;
        this.bankType = bankType;
        this.bankCardNumber = bankCardNumber;
        this.bankBigImgPath = bankBigImgPath;
    }

    public String getBankIconPath() {
        return bankIconPath;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankType() {
        return bankType;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public String getBankBigImgPath() {
        return bankBigImgPath;
    }
}
