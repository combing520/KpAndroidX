package cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.parser;

import java.lang.reflect.Type;

import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.parse.AbstractParser;

/**
 * Response<T> 数据解析器,解析完成对Response对象做判断,如果ok,返回数据 T
 * User: ljx
 * Date: 2018/10/23
 * Time: 13:49
 */
@Parser(name = "Upload")
public class UploadParser<String> extends AbstractParser<String> {

    protected UploadParser() {
        super();
    }

    public UploadParser(Type type) {
        super(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String onParse(okhttp3.Response response) {
        if (response.code() == 200 || response.code() == 1) {
            return (String) response.message();
        } else {
            return (String) "FAIL";
        }
    }
}
