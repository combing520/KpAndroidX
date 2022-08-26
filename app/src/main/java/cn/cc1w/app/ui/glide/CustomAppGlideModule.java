package cn.cc1w.app.ui.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * CustomAppGlideModule
 * @author kpinfo
 */
@GlideModule
public class CustomAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NotNull Context context, GlideBuilder builder) {
        builder.setMemoryCache(new LruResourceCache(10*1024*1024));
    }
    @Override
    public void registerComponents(@NotNull Context context, @NotNull Glide glide, Registry registry) {
        registry.append(String.class, InputStream.class,new CustomBaseGlideUrlLoader.Factory());
    }
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
