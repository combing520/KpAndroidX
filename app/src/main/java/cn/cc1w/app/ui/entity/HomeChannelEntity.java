package cn.cc1w.app.ui.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 首页的频道实体类
 * @author kpinfo
 */
public class HomeChannelEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemHomeChannelEntity> data;
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

    public List<ItemHomeChannelEntity> getData() {
        return data;
    }

    public void setData(List<ItemHomeChannelEntity> data) {
        this.data = data;
    }

    public static class ItemHomeChannelEntity  implements Parcelable {
        private String id;
        private String name;
        private String logo_pic_path;
        private boolean attention;
        private boolean is_index;
        private boolean is_fix;
        private String in_type;
        private String url;

        public ItemHomeChannelEntity(){}
        protected ItemHomeChannelEntity(Parcel in) {
            id = in.readString();
            name = in.readString();
            logo_pic_path = in.readString();
            attention = in.readByte() != 0;
            is_index = in.readByte() != 0;
            url = in.readString();
            in_type = in.readString();
        }

        public static final Creator<ItemHomeChannelEntity> CREATOR = new Creator<ItemHomeChannelEntity>() {
            @Override
            public ItemHomeChannelEntity createFromParcel(Parcel in) {
                return new ItemHomeChannelEntity(in);
            }

            @Override
            public ItemHomeChannelEntity[] newArray(int size) {
                return new ItemHomeChannelEntity[size];
            }
        };

        public String getIn_type() {
            return in_type;
        }

        public void setIn_type(String in_type) {
            this.in_type = in_type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo_pic_path() {
            return logo_pic_path;
        }

        public void setLogo_pic_path(String logo_pic_path) {
            this.logo_pic_path = logo_pic_path;
        }

        public boolean isAttention() {
            return attention;
        }

        public void setAttention(boolean attention) {
            this.attention = attention;
        }

        public boolean isIs_index() {
            return is_index;
        }

        public void setIs_index(boolean is_index) {
            this.is_index = is_index;
        }

        public boolean isIs_fix() {
            return is_fix;
        }

        public void setIs_fix(boolean is_fix) {
            this.is_fix = is_fix;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(logo_pic_path);
            dest.writeByte((byte) (attention ? 1 : 0));
            dest.writeByte((byte) (is_index ? 1 : 0));
            dest.writeString(url);
            dest.writeString(in_type);
        }
    }
}
