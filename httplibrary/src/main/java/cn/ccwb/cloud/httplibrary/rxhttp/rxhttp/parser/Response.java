package cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser;

public class Response<T> {

    private int code;
    private String message;
    private T data;

    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public String getUploadFileMsg() {
        return msg;
    }

    public void setUploadFileMsg(String msg) {
        this.msg = msg;
    }
}
