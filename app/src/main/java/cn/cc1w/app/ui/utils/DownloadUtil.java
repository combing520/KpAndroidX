package cn.cc1w.app.ui.utils;

import android.content.Context;
import android.os.Environment;

public class DownloadUtil {

    public static String getPictureDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    public static String getMovieDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    public static String getMusicDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    public static String getDcimDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    public static String getDownloadDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public static String getDocumentsDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    }

    public static String getDbDir(Context context) {
        return context.getExternalFilesDir("Databases").getAbsolutePath();
    }
}