package cn.cc1w.app.ui.utils;

import com.tencent.smtt.sdk.WebView;

/**
 * @author kpinfo
 */
public class HtmlFormatUtil {

    public static String getHtmlData(String body) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%;  height:auto;}"
                + "</style>"
                + "</head>";
        return "<html>" + head + "<body>" + body + "</body></html>";
    }

    public static void addImageClickListener(WebView webView) {
        LogUtil.d("addImageClickListener = " + System.currentTimeMillis());
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.querySelectorAll('img:not(.attachment-img)'); "
                + "var imgList = new Array();"
                + "for(var i=0;i<objs.length;i++)  "
                + "{"
                + " imgList[i] = objs[i].src;    objs[i].onclick=function()  "
                + "    {  "
                + "       window.Android.openGallery(imgList,this.src); "
                + "    }  "
                + "}"
                + "})()");
    }

}
