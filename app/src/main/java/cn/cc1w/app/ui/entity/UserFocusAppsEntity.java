package cn.cc1w.app.ui.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * 用户关注的 应用号的实体类
 * @author kpinfo
 */
public class UserFocusAppsEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemUserFocusAppsEntity> data;

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

    public List<ItemUserFocusAppsEntity> getData() {
        return data;
    }

    public void setData(List<ItemUserFocusAppsEntity> data) {
        this.data = data;
    }

    @Table(name = "itemUserFocusApps")
    public static class ItemUserFocusAppsEntity {
        @Column(name = "id", isId = true, autoGen = false, property = "NOT NULL")
        private String id;
        @Column(name = "name")
        private String name;
        @Column(name = "logo_pic_path")
        private String logo_pic_path;
        @Column(name = "summary")
        private String summary;
        @Column(name = "attention")
        private boolean attention;
        @Column(name = "group_id")
        private String group_id;
        @Column(name = "res")
        private int res;

        public ItemUserFocusAppsEntity(){}
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo_pic_path() {
            return logo_pic_path;
        }

        public void setLogo_pic_path(String logo_pic_path) {
            this.logo_pic_path = logo_pic_path;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public boolean isAttention() {
            return attention;
        }

        public void setAttention(boolean attention) {
            this.attention = attention;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public int getRes() {
            return res;
        }

        public void setRes(int res) {
            this.res = res;
        }


    }
}
