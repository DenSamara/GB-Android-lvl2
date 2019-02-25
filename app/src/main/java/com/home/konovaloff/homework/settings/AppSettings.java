package com.home.konovaloff.homework.settings;

import android.content.Context;

import com.home.konovaloff.homework.R;
import com.home.konovaloff.homework.model.CityItem;

public class AppSettings {
    private String userName;
    private CityItem city;
    private String logoPath;

    public static AppSettings getDefault(Context ctx){
        return new AppSettings(ctx.getString(R.string.default_username), null, new CityItem(-1, ctx.getString(R.string.default_city), ctx.getString(R.string.default_country)));
    }

    public AppSettings(String userName, String logoPath, CityItem city){
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

    public CityItem city() {
        return city;
    }

    public void city(CityItem city) {
        this.city = city;
    }

    public String logoPath() {
        return logoPath;
    }

    public void logoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
