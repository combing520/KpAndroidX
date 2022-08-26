package cn.cc1w.app.ui.utils.converter.callback;

import java.io.File;

/**
 * @author kpinfo
 */
public interface IConvertCallback {
    
    void onRequestSuccess(File convertedFile);
    
    void onFailure(Exception error);

}