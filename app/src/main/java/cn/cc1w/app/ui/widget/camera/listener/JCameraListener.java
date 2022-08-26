package cn.cc1w.app.ui.widget.camera.listener;

import android.graphics.Bitmap;

/**
 * @author kpinfo
 */
public interface JCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

}
