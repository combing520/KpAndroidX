package cn.cc1w.app.ui.utils.converter.callback;

/**
 * @author kpinfo
 */
public interface ILoadCallback {
    
    void onSuccess();
    
    void onFailure(Exception error);
    
}