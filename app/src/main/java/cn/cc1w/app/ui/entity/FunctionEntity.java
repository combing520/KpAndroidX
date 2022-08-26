package cn.cc1w.app.ui.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * 功能区实体类
 */
public class FunctionEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemFunctionEntity> data;

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

    public List<ItemFunctionEntity> getData() {
        return data;
    }

    public void setData(List<ItemFunctionEntity> data) {
        this.data = data;
    }

    @Table(name = "homeServiceList")
    public static class ItemFunctionEntity implements Parcelable {
        @Column(name = "id", isId = true, autoGen = false, property = "NOT NULL")
        private String id;
        @Column(name = "name")
        private String name;
        @Column(name = "in_type")
        private String in_type;
        @Column(name = "action")
        private String action;
        @Column(name = "url")
        private String url;
        @Column(name = "pic_path")
        private String pic_path;
        @Column(name = "attention")
        private boolean attention;
        @Column(name = "summary")
        private String summary;

        protected ItemFunctionEntity(Parcel in) {
            id = in.readString();
            name = in.readString();
            in_type = in.readString();
            action = in.readString();
            url = in.readString();
            summary = in.readString();
            pic_path = in.readString();
            attention = in.readByte() != 0;
        }

        public ItemFunctionEntity() {

        }

        public static final Creator<ItemFunctionEntity> CREATOR = new Creator<ItemFunctionEntity>() {
            @Override
            public ItemFunctionEntity createFromParcel(Parcel in) {
                return new ItemFunctionEntity(in);
            }

            @Override
            public ItemFunctionEntity[] newArray(int size) {
                return new ItemFunctionEntity[size];
            }
        };

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

        public String getIn_type() {
            return in_type;
        }

        public void setIn_type(String in_type) {
            this.in_type = in_type;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic_path() {
            return pic_path;
        }

        public void setPic_path(String pic_path) {
            this.pic_path = pic_path;
        }

        public boolean isAttention() {
            return attention;
        }

        public void setAttention(boolean attention) {
            this.attention = attention;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(in_type);
            dest.writeString(summary);
            dest.writeString(action);
            dest.writeString(url);
            dest.writeString(pic_path);
            dest.writeByte((byte) (attention ? 1 : 0));
        }
    }
}
