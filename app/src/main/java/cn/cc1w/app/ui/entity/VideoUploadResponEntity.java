package cn.cc1w.app.ui.entity;

/**
 * 上传趣看 视频 实体类
 */
public class VideoUploadResponEntity {

    /**
     * code : 1
     * msg : 上传成功
     * data : {"id":"1584428638040922","res_type":"video","title":"video_1584428625809.mp4","type":"mp4","file_path":"qukan/user/1546852074723973/upload/9948cc3c-7d90-41d7-b972-99f7bc5489a8.mp4","file_name":"video_1584428625809.mp4","size":1673799,"time":"5","pic_path":"https://filekp.ccwb.cn/api/file/20200317150358NW4TNW.jpg","url":"https://filekp.ccwb.cn/api/file/1584428638040922.mp4","width":640,"height":360}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1584428638040922
         * res_type : video
         * title : video_1584428625809.mp4
         * type : mp4
         * file_path : qukan/user/1546852074723973/upload/9948cc3c-7d90-41d7-b972-99f7bc5489a8.mp4
         * file_name : video_1584428625809.mp4
         * size : 1673799
         * time : 5
         * pic_path : https://filekp.ccwb.cn/api/file/20200317150358NW4TNW.jpg
         * url : https://filekp.ccwb.cn/api/file/1584428638040922.mp4
         * width : 640
         * height : 360
         */

        private String id;
        private String res_type;
        private String title;
        private String type;
        private String file_path;
        private String file_name;
        private int size;
        private String time;
        private String pic_path;
        private String url;
        private int width;
        private int height;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRes_type() {
            return res_type;
        }

        public void setRes_type(String res_type) {
            this.res_type = res_type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPic_path() {
            return pic_path;
        }

        public void setPic_path(String pic_path) {
            this.pic_path = pic_path;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
