package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 我的通知 实体类
 * @author kpinfo
 */
public class ItemMessageEntity {
    private int code;
    private String message;
    private boolean success;
    private List<DataBean> data;
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
    public List<DataBean> getData() {
        return data;
    }
    public void setData(List<DataBean> data) {
        this.data = data;
    }
    public static class DataBean {
        private String id;
        private String type;
        private String type_text;
        private String content;
        private boolean notice;
        private String create_time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType_text() {
            return type_text;
        }

        public void setType_text(String type_text) {
            this.type_text = type_text;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isNotice() {
            return notice;
        }

        public void setNotice(boolean notice) {
            this.notice = notice;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }
}
