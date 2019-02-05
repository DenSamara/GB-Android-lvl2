package com.home.konovaloff.homework.global;

import android.app.Activity;
import android.content.SharedPreferences;

public class Preferences {

    public static void saveStringPreference(Activity context, String preferenceName, String value){
        SharedPreferences sp = context.getPreferences(context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(preferenceName, value);
        editor.commit();
    }

    public static String loadStringPreference(Activity context, String preferenceName, String defaultValue){
        String result;

        SharedPreferences sp = context.getPreferences(context.MODE_PRIVATE);
        result = sp.getString(preferenceName, defaultValue);

        return result;
    }
}
