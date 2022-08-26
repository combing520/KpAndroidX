package cn.cc1w.app.ui.entity;

import java.util.List;
/**
 * 新闻评论实体类
 */
public class NewsCommentEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemNewsCommentEntity> data;
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
    public List<ItemNewsCommentEntity> getData() {
        return data;
    }
    public void setData(List<ItemNewsCommentEntity> data) {
        this.data = data;
    }
    public static class ItemNewsCommentEntity {
        private String id;
        private String content;
        private String create_time;
        private String user_nickname;
        private String user_headpic;
        private int good_num;
        private boolean praise;
        private List<ReplyCommentBean> reply_comment;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getUser_headpic() {
            return user_headpic;
        }

        public void setUser_headpic(String user_headpic) {
            this.user_headpic = user_headpic;
        }

        public int getGood_num() {
            return good_num;
        }

        public void setGood_num(int good_num) {
            this.good_num = good_num;
        }

        public boolean isPraise() {
            return praise;
        }

        public void setPraise(boolean praise) {
            this.praise = praise;
        }

        public List<ReplyCommentBean> getReply_comment() {
            return reply_comment;
        }

        public void setReply_comment(List<ReplyCommentBean> reply_comment) {
            this.reply_comment = reply_comment;
        }

        public static class ReplyCommentBean {
            private String id;
            private String content;
            private String create_time;
            private String user_nickname;
            private String user_headpic;
            private String reply_nickname;
            private String reply_headpic;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getUser_nickname() {
                return user_nickname;
            }

            public void setUser_nickname(String user_nickname) {
                this.user_nickname = user_nickname;
            }

            public String getUser_headpic() {
                return user_headpic;
            }

            public void setUser_headpic(String user_headpic) {
                this.user_headpic = user_headpic;
            }

            public String getReply_nickname() {
                return reply_nickname;
            }

            public void setReply_nickname(String reply_nickname) {
                this.reply_nickname = reply_nickname;
            }

            public String getReply_headpic() {
                return reply_headpic;
            }

            public void setReply_headpic(String reply_headpic) {
                this.reply_headpic = reply_headpic;
            }
        }
    }
}