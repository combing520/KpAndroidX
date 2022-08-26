package cn.cc1w.app.ui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 拍客中的 视频 的条目实体类
 */
public class ItemVideoListEntity {
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

    public static class DataBean  implements Serializable{
        private int id;
        private int type;
        private String topic_id;
        private String category_id;
        private String uid;
        private String title;
        private String cover;
        private String video_path;
        private String nickname;
        private String head_pic_path;
        private int click_number;
        private int praise_number;
        private int comment_number;
        private int praise_select; // 点赞选中状态（1-选中，2-没选中
        private int is_promotion;// 是否推广（1-是，0-否）
        private String promotion_name;// 推广名称
        private String promotion_url;// 推广链接

        private String share_url; // 分享的 url

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

        public int getComment_number() {
            return comment_number;
        }

        public void setComment_number(int comment_number) {
            this.comment_number = comment_number;
        }

        public String getVideo_path() {
            return video_path;
        }

        public void setVideo_path(String video_path) {
            this.video_path = video_path;
        }

        public int getPraise_select() {
            return praise_select;
        }

        public void setShoot_select(int praise_select) {
            this.praise_select = praise_select;
        }

        public String getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(String topic_id) {
            this.topic_id = topic_id;
        }

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public void setPraise_select(int praise_select) {
            this.praise_select = praise_select;
        }

        public int getIs_promotion() {
            return is_promotion;
        }

        public void setIs_promotion(int is_promotion) {
            this.is_promotion = is_promotion;
        }

        public String getPromotion_name() {
            return promotion_name;
        }

        public void setPromotion_name(String promotion_name) {
            this.promotion_name = promotion_name;
        }

        public String getPromotion_url() {
            return promotion_url;
        }

        public void setPromotion_url(String promotion_url) {
            this.promotion_url = promotion_url;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }
    }
}
