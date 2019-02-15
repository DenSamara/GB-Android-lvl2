package com.home.konovaloff.homework.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStorage {
    private static final String KEY_SETTINGS_USERNAME = "SettingsStorage.username";
    private static final String KEY_SETTINGS_LOGOPATH = "SettingsStorage.path.to.logo";
    private static final String KEY_SETTINGS_CITY = "SettingsStorage.city";

    private final SharedPreferences preferences;

    public SettingsStorage(Context context, String storageName){
        this.preferences = context.getSharedPreferences(storageName, Context.MODE_PRIVATE);
    }

    public AppSettings getSettings(){
        String userName = preferences.getString(KEY_SETTINGS_USERNAME, null);
        String logo = preferences.getString(KEY_SETTINGS_LOGOPATH, null);
        String city = preferences.getString(KEY_SETTINGS_CITY, null);

        //Если не заданы город и имя пользователя - значит, мы тут в первый раз
        return (userName == null && city == null) ? null : new AppSettings(userName, logo, city);
    }

    public void saveSettings(AppSettings data){
        SharedPreferences.Editor editor = preferences.edit();
        //Реализуем Выход из приложения
        if (data == null){
            editor.remove(KEY_SETTINGS_USERNAME);
            editor.remove(KEY_SETTINGS_LOGOPATH);
            editor.remove(KEY_SETTINGS_CITY);
        }else {
            editor.putString(KEY_SETTINGS_USERNAME, data.userName());
            editor.putString(KEY_SETTINGS_LOGOPATH, data.logoPath());
            editor.putString(KEY_SETTINGS_CITY, data.city());
        }

        editor.apply();
    }
}
