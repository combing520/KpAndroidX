package cn.cc1w.app.ui.entity.entity_public_use_js;

/**
 * 设备基础信息 [给 网页端使用]
 * @author kpinfo
 */
public class DeviceInfoEntity {
    // 设备宽度
    private String cw_device_width;
    // 设备高度
    private String cw_device_height;
    // 设备名称
    private String cw_device;
    // 设备机器码
    private String cw_machine_id;
    // app版本号
    private String cw_verstion;
    public String getCw_device_width() {
        return cw_device_width;
    }
    public void setCw_device_width(String cw_device_width) {
        this.cw_device_width = cw_device_width;
    }
    public String getCw_device_height() {
        return cw_device_height;
    }
    public void setCw_device_height(String cw_device_height) {
        this.cw_device_height = cw_device_height;
    }
    public String getCw_device() {
        return cw_device;
    }
    public void setCw_device(String cw_device) {
        this.cw_device = cw_device;
    }
    public String getCw_machine_id() {
        return cw_machine_id;
    }
    public void setCw_machine_id(String cw_machine_id) {
        this.cw_machine_id = cw_machine_id;
    }
    public String getCw_verstion() {
        return cw_verstion;
    }
    public void setCw_verstion(String cw_verstion) {
        this.cw_verstion = cw_verstion;
    }
}