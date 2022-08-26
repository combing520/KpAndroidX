package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 *
 * 推荐的话题
 * @author kpinfo
 */
public class RecommendTopicEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemRecommendTopicEntity> data;

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

    public List<ItemRecommendTopicEntity> getData() {
        return data;
    }

    public void setData(List<ItemRecommendTopicEntity> data) {
        this.data = data;
    }

    public static class ItemRecommendTopicEntity {
        private int topic_id;
        private String topic_name;
        private int participation;
        private List<ShootsBean> shoots;

        public int getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(int topic_id) {
            this.topic_id = topic_id;
        }

        public String getTopic_name() {
            return topic_name;
        }

        public void setTopic_name(String topic_name) {
            this.topic_name = topic_name;
        }

        public int getParticipation() {
            return participation;
        }

        public void setParticipation(int participation) {
            this.participation = participation;
        }

        public List<ShootsBean> getShoots() {
            return shoots;
        }

        public void setShoots(List<ShootsBean> shoots) {
            this.shoots = shoots;
        }

        public static class ShootsBean {
            private int id;
            private int type;
            private String uid;
            private String title;
            private String cover;
            private String nickname;
            private String head_pic_path;
            private int click_number;
            private int praise_number;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }


            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
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

            public int getClick_number() {
                return click_number;
            }

            public void setClick_number(int click_number) {
                this.click_number = click_number;
            }

            public int getPraise_number() {
                return praise_number;
            }

            public void setPraise_number(int praise_number) {
                this.praise_number = praise_number;
            }
        }
    }
}
