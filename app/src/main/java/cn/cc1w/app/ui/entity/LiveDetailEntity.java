package cn.cc1w.app.ui.entity;

/**
 * 直播详情实体类
 */
public class LiveDetailEntity {
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
        private String id;
        private String title;
        private String info;
        private String type;
        private String type_text;
        private String head_type;
        private String head_type_text;
        private String puth_rtmp;
        private String watch_rtmp;
        private String watch_hdl;
        private String watch_hls;
        private String video_hls;
        private String h5_url;
        private String video_path;
        private String pic_path;
        private String live_video_path;
        private String click_num;
        private String good_num;

        private String news_id;

        private String url;

        private boolean is_praise;
        private boolean is_collection;

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

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
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

        public String getHead_type() {
            return head_type;
        }

        public void setHead_type(String head_type) {
            this.head_type = head_type;
        }

        public String getHead_type_text() {
            return head_type_text;
        }

        public void setHead_type_text(String head_type_text) {
            this.head_type_text = head_type_text;
        }

        public String getPuth_rtmp() {
            return puth_rtmp;
        }

        public void setPuth_rtmp(String puth_rtmp) {
            this.puth_rtmp = puth_rtmp;
        }

        public String getWatch_rtmp() {
            return watch_rtmp;
        }

        public void setWatch_rtmp(String watch_rtmp) {
            this.watch_rtmp = watch_rtmp;
        }

        public String getWatch_hdl() {
            return watch_hdl;
        }

        public void setWatch_hdl(String watch_hdl) {
            this.watch_hdl = watch_hdl;
        }

        public String getWatch_hls() {
            return watch_hls;
        }

        public void setWatch_hls(String watch_hls) {
            this.watch_hls = watch_hls;
        }

        public String getVideo_hls() {
            return video_hls;
        }

        public void setVideo_hls(String video_hls) {
            this.video_hls = video_hls;
        }

        public String getH5_url() {
            return h5_url;
        }

        public void setH5_url(String h5_url) {
            this.h5_url = h5_url;
        }

        public String getVideo_path() {
            return video_path;
        }

        public void setVideo_path(String video_path) {
            this.video_path = video_path;
        }

        public String getPic_path() {
            return pic_path;
        }

        public void setPic_path(String pic_path) {
            this.pic_path = pic_path;
        }

        public String getLive_video_path() {
            return live_video_path;
        }

        public void setLive_video_path(String live_video_path) {
            this.live_video_path = live_video_path;
        }

        public String getClick_num() {
            return click_num;
        }

        public void setClick_num(String click_num) {
            this.click_num = click_num;
        }

        public String getGood_num() {
            return good_num;
        }

        public void setGood_num(String good_num) {
            this.good_num = good_num;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNews_id() {
            return news_id;
        }

        public void setNews_id(String news_id) {
            this.news_id = news_id;
        }

        public boolean isIs_praise() {
            return is_praise;
        }

        public void setIs_praise(boolean is_praise) {
            this.is_praise = is_praise;
        }

        public boolean isIs_collection() {
            return is_collection;
        }

        public void setIs_collection(boolean is_collection) {
            this.is_collection = is_collection;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", info='" + info + '\'' +
                    ", type='" + type + '\'' +
                    ", type_text='" + type_text + '\'' +
                    ", head_type='" + head_type + '\'' +
                    ", head_type_text='" + head_type_text + '\'' +
                    ", puth_rtmp='" + puth_rtmp + '\'' +
                    ", watch_rtmp='" + watch_rtmp + '\'' +
                    ", watch_hdl='" + watch_hdl + '\'' +
                    ", watch_hls='" + watch_hls + '\'' +
                    ", video_hls='" + video_hls + '\'' +
                    ", h5_url='" + h5_url + '\'' +
                    ", video_path='" + video_path + '\'' +
                    ", pic_path='" + pic_path + '\'' +
                    ", live_video_path='" + live_video_path + '\'' +
                    ", click_num='" + click_num + '\'' +
                    ", good_num='" + good_num + '\'' +
                    ", news_id='" + news_id + '\'' +
                    ", url='" + url + '\'' +
                    ", is_praise=" + is_praise +
                    ", is_collection=" + is_collection +
                    '}';
        }
    }
}
