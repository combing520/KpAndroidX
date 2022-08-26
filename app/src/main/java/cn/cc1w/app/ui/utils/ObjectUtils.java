package cn.cc1w.app.ui.utils;

import android.content.ContentValues;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Object Utils
 *
 * @author fuweiwei 2015-9-2
 */
public class ObjectUtils {

    private ObjectUtils() {
        throw new AssertionError();
    }

    public static boolean isEquals(Object actual, Object expected) {
        return actual == expected || (actual == null ? false : actual.equals(expected));
    }

    public static String nullStrToEmpty(Object str) {
        return (str == null ? "" : (str instanceof String ? (String) str : str.toString()));
    }

    public static Long[] transformLongArray(long[] source) {
        Long[] destin = new Long[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    public static long[] transformLongArray(Long[] source) {
        long[] destin = new long[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    public static Integer[] transformIntArray(int[] source) {
        Integer[] destin = new Integer[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    public static int[] transformIntArray(Integer[] source) {
        int[] destin = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    public static <V> int compare(V v1, V v2) {
        return v1 == null ? (v2 == null ? 0 : -1) : (v2 == null ? 1 : ((Comparable) v1).compareTo(v2));
    }

    public static ContentValues getContentValues(Object o) {
        try {
            Field[] fields = o.getClass().getDeclaredFields();
            // String[] fieldNames = new String[fields.length];
            ContentValues values = new ContentValues();
            for (int i = 0; i < fields.length; i++) {
                String key = fields[i].getName();
                Object value = getFieldValueByName(key, o);
                assert value != null;
                values.put(fields[i].getName(), value.toString());
            }
            return values;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}