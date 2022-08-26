package cn.cc1w.app.ui.entity;

/**
 * 单文件上传 结果
 */
public class SignFileUploadResponEntity {
    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String res_type;
        private String title;
        private String type;
        private String file_path;
        private String pic_n;
        private String pic_w;
        private String pic_m;
        private String pic_s;
        private int width_n;
        private int width_w;
        private int width_m;
        private int width_s;
        private int height_n;
        private int height_w;
        private int height_m;
        private int height_s;
        private String size_n;
        private String size_w;
        private String size_m;
        private String size_s;
        private String pic_path_n;
        private String pic_path_w;
        private String pic_path_m;
        private String pic_path_s;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRes_type() {
            return res_type;
        }

        public void setRes_type(String res_type) {
            this.res_type = res_type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }

        public String getPic_n() {
            return pic_n;
        }

        public void setPic_n(String pic_n) {
            this.pic_n = pic_n;
        }

        public String getPic_w() {
            return pic_w;
        }

        public void setPic_w(String pic_w) {
            this.pic_w = pic_w;
        }

        public String getPic_m() {
            return pic_m;
        }

        public void setPic_m(String pic_m) {
            this.pic_m = pic_m;
        }

        public String getPic_s() {
            return pic_s;
        }

        public void setPic_s(String pic_s) {
            this.pic_s = pic_s;
        }

        public int getWidth_n() {
            return width_n;
        }

        public void setWidth_n(int width_n) {
            this.width_n = width_n;
        }

        public int getWidth_w() {
            return width_w;
        }

        public void setWidth_w(int width_w) {
            this.width_w = width_w;
        }

        public int getWidth_m() {
            return width_m;
        }

        public void setWidth_m(int width_m) {
            this.width_m = width_m;
        }

        public int getWidth_s() {
            return width_s;
        }

        public void setWidth_s(int width_s) {
            this.width_s = width_s;
        }

        public int getHeight_n() {
            return height_n;
        }

        public void setHeight_n(int height_n) {
            this.height_n = height_n;
        }

        public int getHeight_w() {
            return height_w;
        }

        public void setHeight_w(int height_w) {
            this.height_w = height_w;
        }

        public int getHeight_m() {
            return height_m;
        }

        public void setHeight_m(int height_m) {
            this.height_m = height_m;
        }

        public int getHeight_s() {
            return height_s;
        }

        public void setHeight_s(int height_s) {
            this.height_s = height_s;
        }

        public String getSize_n() {
            return size_n;
        }

        public void setSize_n(String size_n) {
            this.size_n = size_n;
        }

        public String getSize_w() {
            return size_w;
        }

        public void setSize_w(String size_w) {
            this.size_w = size_w;
        }

        public String getSize_m() {
            return size_m;
        }

        public void setSize_m(String size_m) {
            this.size_m = size_m;
        }

        public String getSize_s() {
            return size_s;
        }

        public void setSize_s(String size_s) {
            this.size_s = size_s;
        }

        public String getPic_path_n() {
            return pic_path_n;
        }

        public void setPic_path_n(String pic_path_n) {
            this.pic_path_n = pic_path_n;
        }

        public String getPic_path_w() {
            return pic_path_w;
        }

        public void setPic_path_w(String pic_path_w) {
            this.pic_path_w = pic_path_w;
        }

        public String getPic_path_m() {
            return pic_path_m;
        }

        public void setPic_path_m(String pic_path_m) {
            this.pic_path_m = pic_path_m;
        }

        public String getPic_path_s() {
            return pic_path_s;
        }

        public void setPic_path_s(String pic_path_s) {
            this.pic_path_s = pic_path_s;
        }
    }
}