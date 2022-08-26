package cn.cc1w.app.ui.entity.entity_public_use_js;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * 弹窗广告 adapter
 */
public class DialogAdEntity {
    private int code;
    private String message;
    private boolean success;
    private List<ItemDialogAdEntity> data;
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
    public List<ItemDialogAdEntity> getData() {
        return data;
    }
    public void setData(List<ItemDialogAdEntity> data) {
        this.data = data;
    }
    public static class ItemDialogAdEntity implements Parcelable {
        private String id;
        private String title;
        private String pic_path;
        private String url;
        private int time;
        private String in_type;
        private String action;
        private String in_id;
        public ItemDialogAdEntity(Parcel in) {
            id = in.readString();
            title = in.readString();
            pic_path = in.readString();
            url = in.readString();
            time = in.readInt();
            in_type = in.readString();
            action = in.readString();
            in_id = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(title);
            dest.writeString(pic_path);
            dest.writeString(url);
            dest.writeInt(time);
            dest.writeString(in_type);
            dest.writeString(action);
            dest.writeString(in_id);
        }

        public static final Creator<ItemDialogAdEntity> CREATOR = new Creator<ItemDialogAdEntity>() {
            @Override
            public ItemDialogAdEntity createFromParcel(Parcel in) {
                return new ItemDialogAdEntity(in);
            }

            @Override
            public ItemDialogAdEntity[] newArray(int size) {
                return new ItemDialogAdEntity[size];
            }
        };


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

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
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

        public String getIn_id() {
            return in_id;
        }

        public void setIn_id(String in_id) {
            this.in_id = in_id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public String toString() {
            return "ItemDialogAdEntity{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", pic_path='" + pic_path + '\'' +
                    ", url='" + url + '\'' +
                    ", time=" + time +
                    ", in_type='" + in_type + '\'' +
                    ", action='" + action + '\'' +
                    ", in_id='" + in_id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DialogAdEntity{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }
}
