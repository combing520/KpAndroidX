package cn.cc1w.app.ui.entity.entity_public_use_js;

/**
 * 给 公共参数 js 使用的 用户定位信息
 * @author kpinfo
 */
public class UserLocationEntity {
    // 经度
    private String cw_longitude;
    // 纬度
    private String cw_latitude;
    // 详细地址
    private String cw_address;
    // 省/直辖市
    private String cw_province;
    // 城市
    private String cw_city;
    // 区域和县
    private String cw_district;

    public String getCw_longitude() {
        return cw_longitude;
    }

    public void setCw_longitude(String cw_longitude) {
        this.cw_longitude = cw_longitude;
    }

    public String getCw_latitude() {
        return cw_latitude;
    }

    public void setCw_latitude(String cw_latitude) {
        this.cw_latitude = cw_latitude;
    }

    public String getCw_address() {
        return cw_address;
    }

    public void setCw_address(String cw_address) {
        this.cw_address = cw_address;
    }

    public String getCw_province() {
        return cw_province;
    }

    public void setCw_province(String cw_province) {
        this.cw_province = cw_province;
    }

    public String getCw_city() {
        return cw_city;
    }

    public void setCw_city(String cw_city) {
        this.cw_city = cw_city;
    }

    public String getCw_district() {
        return cw_district;
    }

    public void setCw_district(String cw_district) {
        this.cw_district = cw_district;
    }
}