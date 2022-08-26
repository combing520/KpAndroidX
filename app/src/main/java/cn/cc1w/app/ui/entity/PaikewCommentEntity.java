package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 拍客 评论
 */
public class PaikewCommentEntity {
    private int code;
    private String message;
    private boolean success;
    private int comment_number;
    private List<ItemPaikewCommentEntity> data;
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

    public int getComment_number() {
        return comment_number;
    }

    public void setComment_number(int comment_number) {
        this.comment_number = comment_number;
    }

    public List<ItemPaikewCommentEntity> getData() {
        return data;
    }

    public void setData(List<ItemPaikewCommentEntity> data) {
        this.data = data;
    }

    public static class ItemPaikewCommentEntity {
        private int id;
        private String uid;
        private String nickname;
        private String head_pic_path;
        private String content;
        private String create_time;
        private int praise_number;
        private int praise_select;
        private int author_select;
        private int comment_reply_count;
        private List<ItemPaikewCommentReplyEntity> comment_reply;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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

        public String getHead_pic_path() {
            return head_pic_path;
        }

        public void setHead_pic_path(String head_pic_path) {
            this.head_pic_path = head_pic_path;
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

        public int getPraise_number() {
            return praise_number;
        }

        public void setPraise_number(int praise_number) {
            this.praise_number = praise_number;
        }

        public int getPraise_select() {
            return praise_select;
        }

        public void setPraise_select(int praise_select) {
            this.praise_select = praise_select;
        }

        public int getAuthor_select() {
            return author_select;
        }

        public void setAuthor_select(int author_select) {
            this.author_select = author_select;
        }

        public int getComment_reply_count() {
            return comment_reply_count;
        }

        public void setComment_reply_count(int comment_reply_count) {
            this.comment_reply_count = comment_reply_count;
        }

        public List<ItemPaikewCommentReplyEntity> getComment_reply() {
            return comment_reply;
        }

        public void setComment_reply(List<ItemPaikewCommentReplyEntity> comment_reply) {
            this.comment_reply = comment_reply;
        }

        public static class ItemPaikewCommentReplyEntity {
            private int id;
            private String uid;
            private String nickname;
            private String comment_nickname;
            private String head_pic_path;
            private String content;
            private String create_time;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

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

            public String getComment_nickname() {
                return comment_nickname;
            }

            public void setComment_nickname(String comment_nickname) {
                this.comment_nickname = comment_nickname;
            }

            public String getHead_pic_path() {
                return head_pic_path;
            }

            public void setHead_pic_path(String head_pic_path) {
                this.head_pic_path = head_pic_path;
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
        }
    }
}
