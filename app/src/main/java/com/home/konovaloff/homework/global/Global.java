package com.home.konovaloff.homework.global;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.home.konovaloff.homework.MyApp;

public class Global {
    private final static boolean showDebugInfo = true;

    public static void toast(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String text){
        toast(MyApp.getContext(), text);
    }

    public static void log_e(String tag, String text) {
        if (showDebugInfo) Log.e(tag, text);
    }

    public static void log_w(String tag, String text) {
        if (showDebugInfo) Log.w(tag, text);
    }

    public static void log_i(String tag, String text) {
        if (showDebugInfo) Log.i(tag, text);
    }
}
