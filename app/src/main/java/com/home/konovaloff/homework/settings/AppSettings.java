package com.home.konovaloff.homework.settings;

import android.content.Context;

import com.home.konovaloff.homework.R;

public class AppSettings {
    private String userName;
    private String city;
    private String logoPath;

    public static AppSettings getDefault(Context ctx){
        return new AppSettings(ctx.getString(R.string.default_username), null, ctx.getString(R.string.default_city));
    }

    public AppSettings(String userName, String logoPath, String city){
        this.userName = userName;
        this.logoPath = logoPath;
        this.city = city;
    }

    public String userName() {
        return userName;
    }

    public void userName(String userName) {
        this.userName = userName;
    }

    public String city() {
        return city;
    }

    public void city(String city) {
        this.city = city;
    }

    public String logoPath() {
        return logoPath;
    }

    public void logoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
