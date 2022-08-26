package cn.cc1w.app.ui.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * @author kpinfo
 */
public class PSingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private final MediaScannerConnection mediaScannerConnection;
    private final String mPath;
    private final ScanListener mListener;

    public interface ScanListener {
        void onScanFinish();
    }

    public PSingleMediaScanner(Context context, String mPath, ScanListener mListener) {
        this.mPath = mPath;
        this.mListener = mListener;
        this.mediaScannerConnection = new MediaScannerConnection(context, this);
        this.mediaScannerConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mediaScannerConnection.scanFile(mPath, null);
    }

    @Override
    public void onScanCompleted(String mPath, Uri mUri) {
        mediaScannerConnection.disconnect();
        if (mListener != null) {
            mListener.onScanFinish();
        }
    }

    public static void refresh(Context context, String path, ScanListener scanListener) {
        new PSingleMediaScanner(context.getApplicationContext(), path, scanListener);
    }
}