package cn.cc1w.app.ui.entity;

/**
 * App 的版本更新
 * @author kpinfo
 */
public class AppUpdateEntity {
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
        private String url;
        private String name;
        private String type;
        private String version;
        private String remark;
        private int grade;
        private double size;
        private String add_time;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "url='" + url + '\'' +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", version='" + version + '\'' +
                    ", remark='" + remark + '\'' +
                    ", grade=" + grade +
                    ", size=" + size +
                    ", add_time='" + add_time + '\'' +
                    '}';
        }
    }
}
