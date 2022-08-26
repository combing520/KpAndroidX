package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 账号管理中的 用户中心
 * @author kpinfo
 */
public class AccountManageUserInfoEntity {
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
        private String nickname;
        private String headpic;
        private String status;
        private String mobile;
        private List<UserRight> list;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadpic() {
            return headpic;
        }

        public void setHeadpic(String headpic) {
            this.headpic = headpic;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public List<UserRight> getList() {
            return list;
        }

        public void setList(List<UserRight> list) {
            this.list = list;
        }
    }

    public static class UserRight {
        private String nickname;
        private String headpic;
        private String source;
        private String uid;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadpic() {
            return headpic;
        }

        public void setHeadpic(String headpic) {
            this.headpic = headpic;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}