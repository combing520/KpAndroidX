package cn.cc1w.app.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    /**
     * base64转bitmap
     *
     * @param @param  base64String
     * @param @return 设定文件
     * @return Bitmap    返回类型
     * @throws
     * @Title: base64ToBitmap
     */
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decode = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }

    /**
     * 保存文件，文件名为当前日期
     *
     * @param context
     * @param bitmap
     * @param bitName
     */
    public static void saveBitmap(Context context, Bitmap bitmap, String bitName) {
        saveBitmap(context, bitmap, bitName, true);
    }

    /**
     * 保存文件，文件名为当前日期
     *
     * @param context
     * @param bitmap
     * @param bitName
     */
    public static void saveBitmap(Context context, Bitmap bitmap, String bitName, boolean needToast) {
        String fileName = DownloadUtil.getPictureDir(context) + "/" + bitName;
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
                if (needToast) {
                    ToastUtil.showLongToast("图片已保存至" + file.getAbsolutePath());
                }
                PSingleMediaScanner.refresh(context, file.getAbsolutePath(), null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void setImageDrawableRight(Context context, TextView view, int resId) {
        Drawable drawable = context.getResources().getDrawable(resId);// 找到资源图片
        // 这一步必须要做，否则不会显示。
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
        view.setCompoundDrawables(null, null, drawable, null);// 设置到控件中
    }
}