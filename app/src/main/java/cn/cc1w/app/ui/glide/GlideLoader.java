package cn.cc1w.app.ui.glide;

import android.content.Context;

import androidx.annotation.AnimRes;

import android.widget.ImageView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import cn.cc1w.app.ui.utils.DensityUtils;
import cn.ccwb.cloud.httplibrary.rxhttp.BaseAppContext;


/**
 * @author kpinfo
 */
public class GlideLoader {
    private static Context context() {
        return BaseAppContext.getAppContext().getApplicationContext();
    }

    public static void loadRound(ImageView imageView, String url) {
        loadRound(imageView, url, -1);
    }

    public static void loadRound(ImageView imageView, String url, int placeHolder) {
        if (placeHolder > 0) {
            RequestOptions options = new RequestOptions()
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transforms(new CenterCrop(), new RoundedCorners(DensityUtils.dp2px(context(), 4)));
            load(options, imageView, url, -1, null);
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transforms(new CenterCrop(), new RoundedCorners(DensityUtils.dp2px(context(), 4)));
            load(options, imageView, url, -1, null);
        }

    }

    public static void loadCircle(ImageView imageView, String url) {
        loadCircle(imageView, url, -1);
    }

    public static void loadCircle(ImageView imageView, String url, int placeHolder) {
        if (placeHolder > 0) {
            RequestOptions options = new RequestOptions()
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transforms(new CenterCrop(), new CircleCrop());
            load(options, imageView, url, -1, null);
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transforms(new CenterCrop(), new CircleCrop());
            load(options, imageView, url, -1, null);
        }

    }

    public static void loadFile(ImageView imageView, File file, int placeHolder) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeHolder)
                .error(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transforms(new CenterCrop());
        RequestBuilder builder = Glide.with(imageView)
                .setDefaultRequestOptions(options)
                .load(file);
        builder.into(imageView);
    }

    public static void loadOriginal(ImageView imageView, String url) {
        loadOriginal(imageView, url, -1);
    }

    public static void loadOriginal(ImageView imageView, String url, int placeHolder) {
        if (placeHolder > 0) {
            RequestOptions options = new RequestOptions()
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transforms(new CenterCrop());
            load(options, imageView, url, -1, null);
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transforms(new CenterCrop());
            load(options, imageView, url, -1, null);
        }
    }

    private static void load(RequestOptions option, ImageView imageView, String url, @AnimRes int anim, RequestListener listener) {
        RequestBuilder builder = Glide.with(imageView)
                .setDefaultRequestOptions(option)
                .load(url);
        if (anim != -1) {
            builder.transition(GenericTransitionOptions.with(anim));
        }

        if (listener != null) {
            builder.listener(listener);
        }
        builder.into(imageView);
    }
}