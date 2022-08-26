package cn.cc1w.app.ui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * App 配置 实体类
 * @author kpinfo
 */
public class AppConfigEntity {
    private int code;
    private String message;
    private boolean success;
    private AppConfigDetail data;

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

    public AppConfigDetail getData() {
        return data;
    }

    public void setData(AppConfigDetail data) {
        this.data = data;
    }

    public static class AppConfigDetail implements Serializable {
        private String qukan_userid;
        private String qukan_appkey;
        private String wx_appid;
        private String wx_appsecret;
        private String qq_appid;
        private String qq_appSecret;
        private String weibo_appid;
        private String weibo_appSecret;
        private String appid;
        private String cloud_socket_host;
        private String cloud_socket_url;
        private String cloud_resource_host;
        private String cloud_resource_url;
        private boolean allow_jump_url;
        private List<String> app_white_list;
        private String app_user_url;
        private String app_policy_url;
        private String app_cancel_user_url;

        public String getQukan_userid() {
            return qukan_userid;
        }

        public void setQukan_userid(String qukan_userid) {
            this.qukan_userid = qukan_userid;
        }

        public String getQukan_appkey() {
            return qukan_appkey;
        }

        public void setQukan_appkey(String qukan_appkey) {
            this.qukan_appkey = qukan_appkey;
        }

        public String getWx_appid() {
            return wx_appid;
        }

        public void setWx_appid(String wx_appid) {
            this.wx_appid = wx_appid;
        }

        public String getWx_appsecret() {
            return wx_appsecret;
        }

        public void setWx_appsecret(String wx_appsecret) {
            this.wx_appsecret = wx_appsecret;
        }

        public String getQq_appid() {
            return qq_appid;
        }

        public void setQq_appid(String qq_appid) {
            this.qq_appid = qq_appid;
        }

        public String getQq_appSecret() {
            return qq_appSecret;
        }

        public void setQq_appSecret(String qq_appSecret) {
            this.qq_appSecret = qq_appSecret;
        }

        public String getWeibo_appid() {
            return weibo_appid;
        }

        public void setWeibo_appid(String weibo_appid) {
            this.weibo_appid = weibo_appid;
        }

        public String getWeibo_appSecret() {
            return weibo_appSecret;
        }

        public void setWeibo_appSecret(String weibo_appSecret) {
            this.weibo_appSecret = weibo_appSecret;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getCloud_socket_host() {
            return cloud_socket_host;
        }

        public void setCloud_socket_host(String cloud_socket_host) {
            this.cloud_socket_host = cloud_socket_host;
        }

        public String getCloud_socket_url() {
            return cloud_socket_url;
        }

        public void setCloud_socket_url(String cloud_socket_url) {
            this.cloud_socket_url = cloud_socket_url;
        }

        public String getCloud_resource_host() {
            return cloud_resource_host;
        }

        public void setCloud_resource_host(String cloud_resource_host) {
            this.cloud_resource_host = cloud_resource_host;
        }

        public String getCloud_resource_url() {
            return cloud_resource_url;
        }

        public void setCloud_resource_url(String cloud_resource_url) {
            this.cloud_resource_url = cloud_resource_url;
        }

        public boolean isAllow_jump_url() {
            return allow_jump_url;
        }

        public void setAllow_jump_url(boolean allow_jump_url) {
            this.allow_jump_url = allow_jump_url;
        }

        public List<String> getApp_white_list() {
            return app_white_list;
        }

        public void setApp_white_list(List<String> app_white_list) {
            this.app_white_list = app_white_list;
        }

        public String getApp_user_url() {
            return app_user_url;
        }

        public void setApp_user_url(String app_user_url) {
            this.app_user_url = app_user_url;
        }

        public String getApp_policy_url() {
            return app_policy_url;
        }

        public void setApp_policy_url(String app_policy_url) {
            this.app_policy_url = app_policy_url;
        }

        public String getApp_cancel_user_url() {
            return app_cancel_user_url;
        }

        public void setApp_cancel_user_url(String app_cancel_user_url) {
            this.app_cancel_user_url = app_cancel_user_url;
        }

        @Override
        public String toString() {
            return "AppConfigDetail{" +
                    "qukan_userid='" + qukan_userid + '\'' +
                    ", qukan_appkey='" + qukan_appkey + '\'' +
                    ", wx_appid='" + wx_appid + '\'' +
                    ", wx_appsecret='" + wx_appsecret + '\'' +
                    ", qq_appid='" + qq_appid + '\'' +
                    ", qq_appSecret='" + qq_appSecret + '\'' +
                    ", weibo_appid='" + weibo_appid + '\'' +
                    ", weibo_appSecret='" + weibo_appSecret + '\'' +
                    ", appid='" + appid + '\'' +
                    ", cloud_socket_host='" + cloud_socket_host + '\'' +
                    ", cloud_socket_url='" + cloud_socket_url + '\'' +
                    ", cloud_resource_host='" + cloud_resource_host + '\'' +
                    ", cloud_resource_url='" + cloud_resource_url + '\'' +
                    ", allow_jump_url=" + allow_jump_url +
                    ", app_white_list=" + app_white_list +
                    ", app_user_url='" + app_user_url + '\'' +
                    ", app_policy_url='" + app_policy_url + '\'' +
                    ", app_cancel_user_url='" + app_cancel_user_url + '\'' +
                    '}';
        }
    }
}