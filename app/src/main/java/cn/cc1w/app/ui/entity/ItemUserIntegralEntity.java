package cn.cc1w.app.ui.entity;

/**
 * 我的积分实体类
 */
public class ItemUserIntegralEntity {
    // 描述
    private String describe;
    // 积分数量
    private String integralCnt;
    //礼品图片地址
    private String prizePath;
    public ItemUserIntegralEntity(String describe, String integralCnt, String prizePath) {
        this.describe = describe;
        this.integralCnt = integralCnt;
        this.prizePath = prizePath;
    }

    public String getDescribe() {
        return describe;
    }

    public String getIntegralCnt() {
        return integralCnt;
    }

    public String getPrizePath() {
        return prizePath;
    }
}
