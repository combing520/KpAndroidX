package cn.cc1w.app.ui.entity;

/**
 * 积分兑换 实体类
 * @author kpinfo
 */
public class IntegralRecordEntity {
    // 原因 描述
    private String describe;
    // 时间
    private String time;
    // 话费的金额
    private String money;

    public IntegralRecordEntity(String describe, String time, String money) {
        this.describe = describe;
        this.time = time;
        this.money = money;
    }

    public String getDescribe() {
        return describe;
    }

    public String getTime() {
        return time;
    }

    public String getMoney() {
        return money;
    }
}