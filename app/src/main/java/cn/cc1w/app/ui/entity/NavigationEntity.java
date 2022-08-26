package cn.cc1w.app.ui.entity;

/**
 * 地图导航实体类
 */
public class NavigationEntity {
    //   startLon     起始位置的 经度
    private String startLon;
    //  startLat     起始位置的纬度
    private String startLat;
    // startAddress 起始位置的 地址
    private String startAddress;
    // endLon       结束位置的 经度
    private String endLon;
    // endLat       结束位置的 纬度
    private String endLat;
    // endAddress   结束位置的地址
    private String endAddress;

    public String getStartLon() {
        return startLon;
    }

    public void setStartLon(String startLon) {
        this.startLon = startLon;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndLon() {
        return endLon;
    }

    public void setEndLon(String endLon) {
        this.endLon = endLon;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }
}