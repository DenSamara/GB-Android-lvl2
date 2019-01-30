package com.home.konovaloff.homework;

import android.content.Context;
import android.widget.Toast;

public class Global {
    public static void toast(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String text){
        toast(MyApp.getContext(), text);
    }
}
