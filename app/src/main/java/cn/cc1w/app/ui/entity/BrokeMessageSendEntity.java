package cn.cc1w.app.ui.entity;
import com.google.gson.annotations.SerializedName;

/**
 * 爆料 发送信息的实体类
 * @author kpinfo
 */
public class BrokeMessageSendEntity {
    private String url;
    private ArgumentsBean arguments;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public ArgumentsBean getArguments() {
        return arguments;
    }
    public void setArguments(ArgumentsBean arguments) {
        this.arguments = arguments;
    }
    public static class ArgumentsBean {
        private PostBean post;
        public PostBean getPost() {
            return post;
        }
        public void setPost(PostBean post) {
            this.post = post;
        }
        public static class PostBean {
            @SerializedName("cw-authorization")
            private String cwauthorization;
            private int cw_type; // 爆料类型（1：纯文本 2：含图片 3：含视频 4:含音频）
            private String cw_content; // 爆料文本内容
            private String cw_pic_path; // 爆料图片路径
            private String cw_pic_id; // 爆料图片id
            private String cw_video_path; // 爆料视频路径
            private String cw_video_id; // 爆料视频id
            private String cw_audio_path; // 爆料音频路径
            private String cw_audio_id; // 爆料音频id
            private String cw_play_time; // 时常
            private String cw_longitude;
            private String cw_latitude;
            private String source_id;
            public String getCwauthorization() {
                return cwauthorization;
            }

            public void setCwauthorization(String cwauthorization) {
                this.cwauthorization = cwauthorization;
            }

            public int getCw_type() {
                return cw_type;
            }

            public void setCw_type(int cw_type) {
                this.cw_type = cw_type;
            }

            public String getCw_content() {
                return cw_content;
            }

            public void setCw_content(String cw_content) {
                this.cw_content = cw_content;
            }

            public String getCw_pic_path() {
                return cw_pic_path;
            }

            public void setCw_pic_path(String cw_pic_path) {
                this.cw_pic_path = cw_pic_path;
            }

            public String getCw_pic_id() {
                return cw_pic_id;
            }

            public void setCw_pic_id(String cw_pic_id) {
                this.cw_pic_id = cw_pic_id;
            }

            public String getCw_video_path() {
                return cw_video_path;
            }

            public void setCw_video_path(String cw_video_path) {
                this.cw_video_path = cw_video_path;
            }

            public String getCw_video_id() {
                return cw_video_id;
            }

            public void setCw_video_id(String cw_video_id) {
                this.cw_video_id = cw_video_id;
            }

            public String getCw_audio_path() {
                return cw_audio_path;
            }

            public void setCw_audio_path(String cw_audio_path) {
                this.cw_audio_path = cw_audio_path;
            }

            public String getCw_audio_id() {
                return cw_audio_id;
            }

            public void setCw_audio_id(String cw_audio_id) {
                this.cw_audio_id = cw_audio_id;
            }

            public String getCw_play_time() {
                return cw_play_time;
            }

            public void setCw_play_time(String cw_play_time) {
                this.cw_play_time = cw_play_time;
            }


            public String getCw_longitude() {
                return cw_longitude;
            }

            public void setCw_longitude(String cw_longitude) {
                this.cw_longitude = cw_longitude;
            }

            public String getCw_latitude() {
                return cw_latitude;
            }

            public void setCw_latitude(String cw_latitude) {
                this.cw_latitude = cw_latitude;
            }

            public String getSource_id() {
                return source_id;
            }

            public void setSource_id(String source_id) {
                this.source_id = source_id;
            }
        }
    }
}