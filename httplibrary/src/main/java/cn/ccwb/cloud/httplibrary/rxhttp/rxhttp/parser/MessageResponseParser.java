package cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser;

import java.io.IOException;
import java.lang.reflect.Type;

import android.text.TextUtils;

import cn.ccwb.cloud.httplibrary.rxhttp.Constants;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException;
import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.AbstractParser;

/**
 * Response<T> 数据解析器,解析完成对Response对象做判断,如果ok,返回数据 T
 * User: ljx
 * Date: 2018/10/23
 * Time: 13:49
 */
@Parser(name = "MessageResponse")
public class MessageResponseParser<T> extends AbstractParser<Response<T>> {

    /**
     * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象，如：List<Student>
     * <p>
     * 用法:
     * Java: .asParser(new ResponseParser<List<Student>>(){})
     * Kotlin: .asParser(object : ResponseParser<List<Student>>() {})
     * <p>
     * 注：此构造方法一定要用protected关键字修饰，否则调用此构造方法将拿不到泛型类型
     */
    protected MessageResponseParser() {
        super();
    }

    /**
     * 此构造方法仅适用于不带泛型的Class对象，如: Student.class
     * <p>
     * 用法
     * Java: .asParser(new ResponseParser<>(Student.class))   或者  .asResponse(Student.class)
     * Kotlin: .asParser(ResponseParser(Student::class.java)) 或者  .asResponse(Student::class.java)
     */
    public MessageResponseParser(Type type) {
        super(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response<T> onParse(okhttp3.Response response) throws IOException {
        final Type type = ParameterizedTypeImpl.get(Response.class, mType); //获取泛型类型
        Response<T> data = convert(response, type);
        T t = data.getData(); //获取data字段
        if (t == null && mType == String.class) {
            /*
             * 考虑到有些时候服务端会返回：{"errorCode":0,"errorMsg":"关注成功"}  类似没有data的数据
             * 此时code正确，但是data字段为空，直接返回data的话，会报空指针错误，
             * 所以，判断泛型为String类型时，重新赋值，并确保赋值不为null
             */
            t = (T) (TextUtils.isEmpty(data.getMsg()) ? "" : data.getMsg());
        }
        if (data.getCode() ==  Constants.CODE_REQUEST_SUCCESS) {
            return data;
        } else if (data.getCode() ==  Constants.CODE_REQUEST_40402) {
            throw new AuthException(data.getMsg(), response);
        } else {
            throw new ParseException(String.valueOf(data.getCode()), data.getMsg(), response);
        }
    }
}
