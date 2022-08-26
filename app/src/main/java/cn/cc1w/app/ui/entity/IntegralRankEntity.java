package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 积分排名实体类
 */
public class IntegralRankEntity {
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
        private String headpic;
        private String nickname;
        private String currcredit;
        private boolean is_header;
        private int ranking;

        public String getHeadpic() {
            return headpic;
        }

        public void setHeadpic(String headpic) {
            this.headpic = headpic;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getCurrcredit() {
            return currcredit;
        }

        public void setCurrcredit(String currcredit) {
            this.currcredit = currcredit;
        }

        public boolean isIs_header() {
            return is_header;
        }

        public void setIs_header(boolean is_header) {
            this.is_header = is_header;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }
    }
}
