package com.home.konovaloff.homework;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.home.konovaloff.homework.global.Global;

public class MyApp extends Application {
    private static final String TAG = MyApp.class.getSimpleName();

    private static MyApp INSTANCE;

    @SuppressLint("StaticFieldLeak")
    public static MyApp getInstance(){return INSTANCE;}

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static Context getContext(){
        return getInstance().getApplicationContext();
    }

    public static String getName() {
        return String.format("%s %s", getLabel(), getVersionName());
    }

    public static String getLabel() {
        String applicationLabel = null;

        int labelRes = INSTANCE.getApplicationInfo().labelRes;
        if (labelRes != 0) {
            applicationLabel = INSTANCE.getString(labelRes);
        }

        return applicationLabel;
    }

    public static String getVersionName() {
        String versionName = null;

        PackageInfo packageInfo = null;
        try {
            packageInfo = INSTANCE.getPackageManager()
                    .getPackageInfo(INSTANCE.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Global.log_e(TAG, e.toString());
        }

        if (packageInfo != null) {
            versionName = packageInfo.versionName;
        }

        return versionName;
    }
}
