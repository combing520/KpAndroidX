package cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 没有身份信息，需要重新登录
 */
public class AuthException extends IOException {
    private String errorCode;
    private String requestMethod; //请求方法，Get/Post等
    private Headers responseHeaders; //响应头
    private String requestResult; //请求结果

    @Deprecated
    public AuthException(String message, Response response) {
        this("-1", message, response, null);
    }

    @Deprecated
    public AuthException(String message, Response response, String result) {
        this("-1", message, response, result);
    }

    public AuthException(@NonNull String code, String message, Response response) {
        this(code, message, response, null);
    }

    public AuthException(@NonNull String code, String message, Response response, String result) {
        super(message);
        errorCode = code;
        requestResult = result;

        Request request = response.request();
        requestMethod = request.method();
        responseHeaders = response.headers();
    }

    public String getRequestResult() {
        return requestResult;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getRequestMethod() {
        return requestMethod;
    }


    public Headers getResponseHeaders() {
        return responseHeaders;
    }

    @Nullable
    @Override
    public String getLocalizedMessage() {
        return errorCode;
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" +
                " Method=" + requestMethod +
                " Code=" + errorCode +
                "\n\n" + responseHeaders +
                "\nmessage = " + getMessage();
    }
}