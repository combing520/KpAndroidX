package cn.cc1w.app.ui.utils;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kpinfo
 * Json的解析工具类
 */
public class JsonUtil {
    private static final GsonBuilder gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @SuppressLint("UseValueOf")
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {
                String date = json.getAsJsonPrimitive().getAsString();
                String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
                Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
                Matcher matcher = pattern.matcher(date);
                String result = matcher.replaceAll("$2");
                try {
                    return new Date(new Long(result));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static <T> ArrayList<T> getList(String JSONString, Class<T> classOfT) {
        final ArrayList<T> data = new ArrayList<T>();

        JSONArray array = null;
        try {
            array = new JSONArray(JSONString);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        if (array != null) {
            final Gson gson = gsonBuilder.create();

            for (int i = 0; i < array.length(); i++) {
                try {
                    final JSONObject object = array.getJSONObject(i);
                    final T entity = gson.fromJson(object.toString(), classOfT);
                    data.add(entity);
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static <T> T getObject(String JSONString, Class<T> classOfT) {
        try {
            Gson gson = gsonBuilder.create();
            T entity = gson.fromJson(JSONString, classOfT);
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("e = "+ e.getMessage() +"  "+e.getCause());
            return null;
        }
    }

    public static String getStringFromObject(Object src) {
        final Gson gson = gsonBuilder.create();
        String result = gson.toJson(src);
        return result;
    }


    /**
     * 将json对象转换为map集合，通过此方法获取存放map集合键的list集合
     *
     * @return
     */
    public static List<Object> mapKeys(Map<?, ?> map) {
        List<Object> keysList = new ArrayList<Object>();
        String columnStr = "column";
        for (int i = 0; i < map.keySet().size(); i++) {
            keysList.add(columnStr + (i + 1));
        }
        System.out.println(keysList.size());
        return keysList;
    }

    /**
     * 将传入的json字符串解析为List集合
     */
    public static List<?> jsonToList(String jsonStr) {
        List<?> ObjectList = null;
        Gson gson = new Gson();
        Type type = new com.google.gson.reflect.TypeToken<List<?>>() {
        }.getType();
        ObjectList = gson.fromJson(jsonStr, type);
        return ObjectList;
    }

    /**
     * 将传入的json字符串解析为Map集合
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> ObjectMap = null;
        Gson gson = new Gson();
        Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
        }.getType();
        ObjectMap = gson.fromJson(jsonStr, type);
        return ObjectMap;
    }
}