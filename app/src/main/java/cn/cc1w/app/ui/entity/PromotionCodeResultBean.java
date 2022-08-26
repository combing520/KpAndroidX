package cn.cc1w.app.ui.entity;

/**
 * @author kpinfo
 * on 2020-11-20
 */
public class PromotionCodeResultBean {
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

    public static class DataBean {
        private String app_download_url;
        private String code;
        private int user_num;
        private boolean is_code;

        public String getApp_download_url() {
            return app_download_url;
        }

        public void setApp_download_url(String app_download_url) {
            this.app_download_url = app_download_url;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getUser_num() {
            return user_num;
        }

        public void setUser_num(int user_num) {
            this.user_num = user_num;
        }

        public boolean isIs_code() {
            return is_code;
        }

        public void setIs_code(boolean is_code) {
            this.is_code = is_code;
        }
    }
}