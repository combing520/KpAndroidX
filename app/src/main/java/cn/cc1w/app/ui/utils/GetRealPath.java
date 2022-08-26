package cn.cc1w.app.ui.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class GetRealPath {

   public static String getFPUriToPath(Context context, Uri uri) {
      try {
         List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
         if (packs != null) {
            String fileProviderClassName = FileProvider.class.getName();
            for (PackageInfo pack : packs) {
               ProviderInfo[] providers = pack.providers;
               if (providers != null) {
                  for (ProviderInfo provider : providers) {
                     if (uri.getAuthority().equals(provider.authority)) {
                        if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                           Class<FileProvider> fileProviderClass = FileProvider.class;
                           try {
                              Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                              getPathStrategy.setAccessible(true);
                              Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                              if (invoke != null) {
                                 String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                 Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                 Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                 getFileForUri.setAccessible(true);
                                 Object invoke1 = getFileForUri.invoke(invoke, uri);
                                 if (invoke1 instanceof File) {
                                    String filePath = ((File) invoke1).getAbsolutePath();
                                    return replace(filePath);
                                 }
                              }
                           } catch (NoSuchMethodException e) {
                              e.printStackTrace();
                           } catch (InvocationTargetException e) {
                              e.printStackTrace();
                           } catch (IllegalAccessException e) {
                              e.printStackTrace();
                           } catch (ClassNotFoundException e) {
                              e.printStackTrace();
                           }
                           break;
                        }
                        break;
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;

   }

   public static String replace(String filePath) {
      if (filePath.contains("%")) {
         filePath = filePath.replace("%", "%25");
      }
      if (filePath.contains("#")) {
         filePath = filePath.replace("#", "%23");
      }
      if (filePath.contains("&")) {
         filePath = filePath.replace("&", "%26");
      }
      if (filePath.contains("?")) {
         filePath = filePath.replace("?", "%3F");
      }
      return filePath;
   }

   /**
    * 查询内容解析器，找到文件存储地址
    * <p>ef: android中转换content://media/external/images/media/539163为/storage/emulated/0/DCIM/Camera/IMG_20160807_123123.jpg
    * <p>把content://media/external/images/media/X转换为file:///storage/sdcard0/Pictures/X.jpg
    * @param context
    * @param contentUri
    * @return
    */
   public static String getRealPathFromUri(Context context, Uri contentUri) {
      Cursor cursor = null;
      try {
         String[] proj = { MediaStore.Images.Media.DATA };
         cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
         if (cursor != null && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(column_index);
            return path;
         } else {
         }
      } catch (Exception e) {
      } finally {
         if (cursor != null) {
            cursor.close();
         }
      }
      return "";
   }

   /**
    * 获取完整文件名(包含扩展名)
    */
   public static String getFilenameWithExtension(String filePath) {
      if (filePath == null || filePath.length() == 0) {
         return "";
      }
      int lastIndex = filePath.lastIndexOf(File.separator);
      String filename = filePath.substring(lastIndex + 1);
      return filename;
   }

   /**
    * 判断文件路径的文件名是否存在文件扩展名 eg: /external/images/media/2283
    * @param filePath
    * @return
    */
   public static boolean isFilePathWithExtension(String filePath) {
      String filename = getFilenameWithExtension(filePath);
      return filename.contains(".");
   }
}