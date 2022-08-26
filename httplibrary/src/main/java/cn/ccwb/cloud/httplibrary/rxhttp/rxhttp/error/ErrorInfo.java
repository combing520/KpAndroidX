package cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.wrapper.exception.HttpStatusCodeException;
import rxhttp.wrapper.exception.ParseException;

/**
 * Http请求错误信息
 */
public class ErrorInfo {

    private int errorCode;  //仅指服务器返回的错误码
    private String errorMsg; //错误文案，网络错误、请求失败错误、服务器返回的错误等文案
    private Throwable throwable; //异常信息

    public ErrorInfo(Throwable throwable) {
        this.throwable = throwable;
        String errorMsg = ExceptionHelper.handleNetworkException(throwable); //网络异常
        if (throwable instanceof AuthException) { //没有身份信息，需要重新登录
            this.errorMsg = "登录状态过期,请退出登录后重新登录";
//            if (!BuildConfig.DEBUG) {//开发阶段不清空token
//                BaseConstant.CW_AUTHORIZATION = "";
//                SharedPreferenceUtil.saveUserInfo(new LoginResultEntity.UserLoginResult());
//                SharedPreferenceUtil.saveLoginUserInfo(new UserInfoEntity.DataBean());
//                EventBus.getDefault().post(new EventMessage(BaseConstant.TAG_LOG_OUT, BaseConstant.TAG_LOG_OUT));
//            }
        } else if (throwable instanceof ParseException) { // ParseException异常表明请求成功，但是数据不正确
            String errorCode = throwable.getLocalizedMessage();
            this.errorCode = Integer.valueOf(errorCode);
            errorMsg = throwable.getMessage();
            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = errorCode;//errorMsg为空，显示errorCode
            }
        }
        if (errorMsg == null) {
            if (throwable instanceof HttpStatusCodeException) {
                HttpStatusCodeException exception = (HttpStatusCodeException) throwable;
                if (exception.getStatusCode().equals("500")) {
                    this.errorMsg = "服务器接口发生错误";
                } else {
                    JsonObject result = (JsonObject) new JsonParser().parse(exception.getResult());
                    String message = result.get("message").getAsString();
                    this.errorMsg = message;
                }
            }
        } else {
            this.errorMsg = errorMsg;
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
