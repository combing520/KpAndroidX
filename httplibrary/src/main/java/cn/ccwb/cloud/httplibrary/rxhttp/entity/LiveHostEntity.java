package cn.ccwb.cloud.httplibrary.rxhttp.entity;

import java.util.List;

/**
 * @author kpinfo
 * 直播
 */
public class LiveHostEntity {
    private int code;
    private String message;
    private boolean success;
    private List<LiveHostItemEntity> data;

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

    public List<LiveHostItemEntity> getData() {
        return data;
    }

    public void setData(List<LiveHostItemEntity> data) {
        this.data = data;
    }

    public static class LiveHostItemEntity {
        private String id;
        private String location;
        private String headpic;
        private String name;
        private String type;
        private String type_text;
        private String content;
        private String title;
        private String res_type;
        private String res_type_text;
        private String anchor_name;
        private String anchor_headpic;
        private String create_time;
        private String pic_path;
        private String audio_path;
        private String video_path;
        private int play_time;
        private String add_time;
        private String timestamp;

        private List<JsonBean> json;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRes_type() {
            return res_type;
        }

        public void setRes_type(String res_type) {
            this.res_type = res_type;
        }

        public String getRes_type_text() {
            return res_type_text;
        }

        public void setRes_type_text(String res_type_text) {
            this.res_type_text = res_type_text;
        }

        public String getAnchor_name() {
            return anchor_name;
        }

        public void setAnchor_name(String anchor_name) {
            this.anchor_name = anchor_name;
        }

        public String getAnchor_headpic() {
            return anchor_headpic;
        }

        public void setAnchor_headpic(String anchor_headpic) {
            this.anchor_headpic = anchor_headpic;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public List<JsonBean> getJson() {
            return json;
        }

        public void setJson(List<JsonBean> json) {
            this.json = json;
        }

        public static class JsonBean {
            /**
             * id : 20181202095801OV43YO
             * pic_id :
             * video_id : 20181202095718UOO87E
             * pic_path : https://res.ccwb.cn/Upload/ccwb_app/video/news/20181202/20181202095717UXL8TR.jpg
             * pic_path_w :
             * url : https://res.ccwb.cn/Upload/ccwb_app/video/news/20181202/201812020957175FKGU9.mp4
             */

            private String id;
            private String pic_id;
            private String video_id;
            private String pic_path;
            private String pic_path_w;
            private String url;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPic_id() {
                return pic_id;
            }

            public void setPic_id(String pic_id) {
                this.pic_id = pic_id;
            }

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }

            public String getPic_path() {
                return pic_path;
            }

            public void setPic_path(String pic_path) {
                this.pic_path = pic_path;
            }

            public String getPic_path_w() {
                return pic_path_w;
            }

            public void setPic_path_w(String pic_path_w) {
                this.pic_path_w = pic_path_w;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getHeadpic() {
            return headpic;
        }

        public void setHeadpic(String headpic) {
            this.headpic = headpic;
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

        public String getType_text() {
            return type_text;
        }

        public void setType_text(String type_text) {
            this.type_text = type_text;
        }

        public String getPic_path() {
            return pic_path;
        }

        public void setPic_path(String pic_path) {
            this.pic_path = pic_path;
        }

        public String getAudio_path() {
            return audio_path;
        }

        public void setAudio_path(String audio_path) {
            this.audio_path = audio_path;
        }

        public String getVideo_path() {
            return video_path;
        }

        public void setVideo_path(String video_path) {
            this.video_path = video_path;
        }

        public int getPlay_time() {
            return play_time;
        }

        public void setPlay_time(int play_time) {
            this.play_time = play_time;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
