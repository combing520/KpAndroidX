package cn.cc1w.app.ui.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 连接 爆料 聊天室的实体类
 */
public class ConnectBrokeRoomEntity {
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
            private String cw_page;
            private String cw_live_id;
            private String method;
            private String cw_content;
            private String cw_type;
            private String source_id;
            public String getCwauthorization() {
                return cwauthorization;
            }

            public void setCwauthorization(String cwauthorization) {
                this.cwauthorization = cwauthorization;
            }

            public String getCw_page() {
                return cw_page;
            }

            public void setCw_page(String cw_page) {
                this.cw_page = cw_page;
            }

            public String getCw_live_id() {
                return cw_live_id;
            }

            public void setCw_live_id(String cw_live_id) {
                this.cw_live_id = cw_live_id;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public String getCw_content() {
                return cw_content;
            }

            public void setCw_content(String cw_content) {
                this.cw_content = cw_content;
            }

            public String getCw_type() {
                return cw_type;
            }

            public void setCw_type(String cw_type) {
                this.cw_type = cw_type;
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