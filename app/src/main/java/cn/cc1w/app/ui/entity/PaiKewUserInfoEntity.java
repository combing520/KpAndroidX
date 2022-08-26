package cn.cc1w.app.ui.entity;

/**
 * 拍客的用户信息
 * @author kpinfo
 */
public class PaiKewUserInfoEntity {
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
        private String uid;
        private String nickname;
        private String tag;
        private String head_pic_path;
        private int fans_number;
        private int follow_number;
        private int praise_number;
        //关注选项（1-关注，2-未关注）
        private int follow_select;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getHead_pic_path() {
            return head_pic_path;
        }

        public void setHead_pic_path(String head_pic_path) {
            this.head_pic_path = head_pic_path;
        }

        public int getFans_number() {
            return fans_number;
        }

        public void setFans_number(int fans_number) {
            this.fans_number = fans_number;
        }

        public int getFollow_number() {
            return follow_number;
        }

        public void setFollow_number(int follow_number) {
            this.follow_number = follow_number;
        }

        public int getPraise_number() {
            return praise_number;
        }

        public void setPraise_number(int praise_number) {
            this.praise_number = praise_number;
        }

        public int getFollow_select() {
            return follow_select;
        }

        public void setFollow_select(int follow_select) {
            this.follow_select = follow_select;
        }


        @Override
        public String toString() {
            return "DataBean{" +
                    "uid=" + uid +
                    ", nickname='" + nickname + '\'' +
                    ", tag='" + tag + '\'' +
                    ", head_pic_path='" + head_pic_path + '\'' +
                    ", fans_number=" + fans_number +
                    ", follow_number=" + follow_number +
                    ", praise_number=" + praise_number +
                    ", follow_select=" + follow_select +
                    '}';
        }
    }
}

