package cn.cc1w.app.ui.entity;

import java.io.Serializable;

/**
 * 爆料条目实体类
 */
public class ItemBrokeEntity implements Serializable {
    private String id;
    private String location;
    private String headpic;
    private String name;
    private String type;
    private String type_text;
    private String content;
    private String pic_path;
    private String audio_path;
    private String video_path;
    private int play_time;
    private String add_time;
    private String timestamp;
    private String longitude;
    private String latitude;
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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
