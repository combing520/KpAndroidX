package cn.cc1w.app.ui.entity;

/**
 * 视频实体类
 */
public class ItemVideoEntity {
    private String coverImgUrl;// 视频的封面
    private String title;// 视频的title
    private String videoPath;// 视频的播放地址

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}

