package cn.cc1w.app.ui.utils.converter;

import android.content.Context;


import com.github.kohiyadav.libffmpeg.FFmpeg;
import com.github.kohiyadav.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.kohiyadav.libffmpeg.FFmpegLoadBinaryResponseHandler;

import java.io.File;
import java.io.IOException;

import cn.cc1w.app.ui.utils.converter.callback.IConvertCallback;
import cn.cc1w.app.ui.utils.converter.callback.ILoadCallback;
import cn.cc1w.app.ui.utils.converter.model.AudioFormat;

/**
 * @author kpinfo
 */
public class AndroidAudioConverter {
    private static boolean loaded;
    private final Context context;
    private File audioFile;
    private AudioFormat format;
    private IConvertCallback callback;

    private AndroidAudioConverter(Context context) {
        this.context = context;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void load(Context context, final ILoadCallback callback) {
        try {
            FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    loaded = true;
                    callback.onSuccess();
                }

                @Override
                public void onFailure() {
                    loaded = false;
                    callback.onFailure(new Exception("Failed to loaded FFmpeg lib"));
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            loaded = false;
            callback.onFailure(e);
        }
    }

    public static AndroidAudioConverter with(Context context) {
        return new AndroidAudioConverter(context);
    }

    public AndroidAudioConverter setFile(File originalFile) {
        this.audioFile = originalFile;
        return this;
    }

    public AndroidAudioConverter setFormat(AudioFormat format) {
        this.format = format;
        return this;
    }

    public AndroidAudioConverter setCallback(IConvertCallback callback) {
        this.callback = callback;
        return this;
    }

    public void convert() {
        if (!isLoaded()) {
            callback.onFailure(new Exception("FFmpeg not loaded"));
            return;
        }
        if (audioFile == null || !audioFile.exists()) {
            callback.onFailure(new IOException("File not exists"));
            return;
        }
        if (!audioFile.canRead()) {
            callback.onFailure(new IOException("Can't read the file. Missing permission?"));
            return;
        }
        final File convertedFile = getConvertedFile(audioFile, format);
        final String[] cmd = new String[]{"-y", "-i", audioFile.getPath(), convertedFile.getPath()};
        try {
            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onSuccess(String message) {
                    callback.onRequestSuccess(convertedFile);
                }

                @Override
                public void onFailure(String message) {
                    callback.onFailure(new IOException(message));
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    private static File getConvertedFile(File originalFile, AudioFormat format) {
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }
}