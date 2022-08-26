package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 *
 * 获取 新闻详情的相关信息
 * @author kpinfo
 */
public class NewsDetailRelatedEntity {
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
        private List<ItemNewsDetailRelateEntity> related;
        private List<ItemNewsDetailRelateEntity> recommend;

        public List<ItemNewsDetailRelateEntity> getRelated() {
            return related;
        }

        public void setRelated(List<ItemNewsDetailRelateEntity> related) {
            this.related = related;
        }

        public List<ItemNewsDetailRelateEntity> getRecommend() {
            return recommend;
        }

        public void setRecommend(List<ItemNewsDetailRelateEntity> recommend) {
            this.recommend = recommend;
        }

    }
}
