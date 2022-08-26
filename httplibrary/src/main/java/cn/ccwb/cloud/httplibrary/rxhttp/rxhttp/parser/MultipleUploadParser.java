package cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import cn.ccwb.cloud.httplibrary.rxhttp.Constants;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.AbstractParser;

/**
 * Response<List<T>> 数据解析器,解析完成对Response对象做判断,如果ok,返回数据 List<T>
 * User: ljx
 * Date: 2018/10/23
 * Time: 13:49
 */
@Parser(name = "MultipleUpload")
public class MultipleUploadParser<T> extends AbstractParser<List<T>> {

    protected MultipleUploadParser() {
        super();
    }

    public MultipleUploadParser(Type type) {
        super(type);
    }

    @Override
    public List<T> onParse(okhttp3.Response response) throws IOException {
        final Type type = ParameterizedTypeImpl.get(Response.class, List.class, mType); //获取泛型类型
        Response<List<T>> data = convert(response, type);
        List<T> list = data.getData(); //获取data字段
        if (data.getCode() == Constants.CODE_SUCCESS_UPLOAD_RESOURCE) {
            return list;
        } else if (data.getCode() == Constants.CODE_REQUEST_40402 || data.getCode() == Constants.CODE_FOR_LOGIN) {
            throw new AuthException(data.getMsg(), response);
        } else {
            throw new ParseException(String.valueOf(data.getCode()), data.getUploadFileMsg(), response);
        }
    }
}
