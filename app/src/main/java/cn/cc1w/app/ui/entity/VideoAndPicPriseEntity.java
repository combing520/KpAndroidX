package cn.cc1w.app.ui.entity;

/**
 * 视频/照片点赞实体类
 * @author kpinfo
 */
public class VideoAndPicPriseEntity {
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
        private int praise_number;

        public int getPraise_number() {
            return praise_number;
        }

        public void setPraise_number(int praise_number) {
            this.praise_number = praise_number;
        }
    }
}
