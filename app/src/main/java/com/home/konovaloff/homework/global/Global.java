package com.home.konovaloff.homework.global;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.home.konovaloff.homework.MyApp;
import com.home.konovaloff.homework.R;

public class Global {
    public final static String APIKEY = "c1346f4fe62c16f203d047b15aeafd21";

    public final static int IMAGE_DEFAULT = R.drawable.image_default;

    public final static RequestOptions IMAGE_REQUEST_OPTIONS = new RequestOptions()
            .centerCrop()
            .placeholder(IMAGE_DEFAULT)
            .error(IMAGE_DEFAULT)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private final static boolean showDebugInfo = true;

    public static void toast(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String text){
        toast(MyApp.getContext(), text);
    }

    public static void logE(String tag, String text) {
        if (showDebugInfo) Log.e(tag, text);
    }

    public static void logW(String tag, String text) {
        if (showDebugInfo) Log.w(tag, text);
    }

    public static void logI(String tag, String text) {
        if (showDebugInfo) Log.i(tag, text);
    }
}
