package cn.cc1w.app.ui.entity;

import java.io.Serializable;

/**
 * 用户信息 结果
 */
public class UserInfoResultEntity implements Serializable {
    private int code;
    private String message;
    private boolean success;
    private UserInfo data;

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

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }

    public static class UserInfo implements Serializable{
        private String nickname;
        private String headpic;
        private String money;
        private String credits;
        private String token;
        private String mobile;
        private String sid;
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

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCredits() {
            return credits;
        }

        public void setCredits(String credits) {
            this.credits = credits;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "nickname='" + nickname + '\'' +
                    ", headpic='" + headpic + '\'' +
                    ", money='" + money + '\'' +
                    ", credits='" + credits + '\'' +
                    ", token='" + token + '\'' +
                    ", mobile='" + mobile + '\'' +
                    '}';
        }
    }
}