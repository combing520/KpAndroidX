package app.cloud.ccwb.cn.linlibrary.qrscan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import app.cloud.ccwb.cn.linlibrary.qrscan.camera.CameraManager;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    private final Context context;
    private final CameraManager cameraManager;
    private final Map<DecodeHintType,Object> hints;
    private Handler handler;
    private CaptureHandler captureHandler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(Context context,CameraManager cameraManager,
                 CaptureHandler captureHandler,
                 Collection<BarcodeFormat> decodeFormats,
                 Map<DecodeHintType,Object> baseHints,
                 String characterSet,
                 ResultPointCallback resultPointCallback) {

        this.context = context;
        this.cameraManager = cameraManager;
        this.captureHandler = captureHandler;
        handlerInitLatch = new CountDownLatch(1);

        hints = new EnumMap<>(DecodeHintType.class);
        if (baseHints != null) {
            hints.putAll(baseHints);
        }

        // The prefs can't change while the thread is running, so pick them up once here.
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
            if (prefs.getBoolean(Preferences.KEY_DECODE_1D_PRODUCT, true)) {
                decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_1D_INDUSTRIAL, true)) {
                decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_QR, true)) {
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_DATA_MATRIX, true)) {
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_AZTEC, false)) {
                decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_PDF417, false)) {
                decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
            }
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(context,cameraManager,captureHandler, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}