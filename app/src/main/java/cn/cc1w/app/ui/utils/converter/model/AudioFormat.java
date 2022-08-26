package cn.cc1w.app.ui.utils.converter.model;

/**
 * @author kpinfo
 */

public enum AudioFormat {
    AAC,
    MP3,
    M4A,
    WMA,
    WAV,
    FLAC;
    public String getFormat() {
        return name().toLowerCase();
    }
}