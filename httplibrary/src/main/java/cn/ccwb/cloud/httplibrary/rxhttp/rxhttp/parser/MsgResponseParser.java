package cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser;

import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import cn.ccwb.cloud.httplibrary.rxhttp.Constants;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.AbstractParser;
import rxhttp.wrapper.utils.GsonUtil;

/**
 * Response<T> 数据解析器,解析完成对Response对象做判断,如果ok,返回数据 T
 * User: ljx
 * Date: 2018/10/23
 * Time: 13:49
 */
@Parser(name = "MsgResponse")
public class MsgResponseParser<T> extends AbstractParser<T> {

    protected MsgResponseParser() {
        super();
    }

    public MsgResponseParser(Type type) {
        super(type);
    }

    @Override
    public T onParse(okhttp3.Response response) throws IOException {
        final Type type = ParameterizedTypeImpl.get(Response.class, mType);
        Response<T> data = convert(response, type);
        T t = data.getData();
        if (t == null && mType == String.class) {
            t = (T) (TextUtils.isEmpty(data.getMsg()) ? "" : data.getMsg());
        }

        if (data.getCode() == Constants.CODE_REQUEST_SUCCESS) {
            if (mType == MsgResonse.class) {
                String gsonStr = GsonUtil.toJson(data);
                t = GsonUtil.getObject(gsonStr, mType);
            }
            return t;
        } else if (data.getCode() == Constants.CODE_REQUEST_40402  || data.getCode() == Constants.CODE_FOR_LOGIN ) {
            throw new AuthException(data.getMsg(), response);
        } else {
            throw new ParseException(String.valueOf(data.getCode()), data.getMsg(), response);
        }
    }
}
