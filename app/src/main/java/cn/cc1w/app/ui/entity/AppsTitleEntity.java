package cn.cc1w.app.ui.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * 应用号 title实体类
 * @author kpinfo
 */
public class AppsTitleEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemAppsTitleEntity> data;

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

    public List<ItemAppsTitleEntity> getData() {
        return data;
    }

    public void setData(List<ItemAppsTitleEntity> data) {
        this.data = data;
    }

    @Table(name="appsTitleTab")
    public static class ItemAppsTitleEntity {
        @Column(name = "id", isId = true, autoGen = false,property = "NOT NULL")
        private String id;
        @Column(name = "name")
        private String name;
        @Column(name = "icon_checked")
        private String icon_checked;
        @Column(name = "icon_unchecked")
        private String icon_unchecked;
        @Column(name = "bg_path")
        private String bg_path;

        public ItemAppsTitleEntity(){

        }
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

        public String getIcon_checked() {
            return icon_checked;
        }

        public void setIcon_checked(String icon_checked) {
            this.icon_checked = icon_checked;
        }

        public String getIcon_unchecked() {
            return icon_unchecked;
        }

        public void setIcon_unchecked(String icon_unchecked) {
            this.icon_unchecked = icon_unchecked;
        }

        public String getBg_path() {
            return bg_path;
        }

        public void setBg_path(String bg_path) {
            this.bg_path = bg_path;
        }
    }
}