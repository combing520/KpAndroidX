package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 拍客分类
 * @author kpinfo
 */
public class PaikewCategoryEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemPaikewCategoryEntity> data;

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

    public List<ItemPaikewCategoryEntity> getData() {
        return data;
    }

    public void setData(List<ItemPaikewCategoryEntity> data) {
        this.data = data;
    }

    public static class ItemPaikewCategoryEntity {
        private int category_id;
        private String category_name;
        private int participation;
        private String summary;

        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public int getParticipation() {
            return participation;
        }

        public void setParticipation(int participation) {
            this.participation = participation;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }
}