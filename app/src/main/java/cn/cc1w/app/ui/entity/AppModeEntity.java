package cn.cc1w.app.ui.entity;

import java.io.Serializable;

/**
 * @author kpinfo
 * on 2021-04-20
 */
public class AppModeEntity implements Serializable {
    private int code;
    private String message;
    private boolean success;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private boolean mourning;
        private boolean red;
        public boolean isMourning() {
            return mourning;
        }

        public void setMourning(boolean mourning) {
            this.mourning = mourning;
        }

        public boolean isRed() {
            return red;
        }

        public void setRed(boolean red) {
            this.red = red;
        }
    }
}