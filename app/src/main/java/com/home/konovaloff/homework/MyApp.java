package com.home.konovaloff.homework;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class MyApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static volatile Context context;

    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
    }

    public static Context getContext(){
        return context;
    }

    public static String getName() {
        return String.format("%s %s", getLabel(), getVersionName());
    }

    public static String getLabel() {
        String applicationLabel = null;

        int labelRes = context.getApplicationInfo().labelRes;
        if (labelRes != 0) {
            applicationLabel = context.getString(labelRes);
        }

        return applicationLabel;
    }

    public static String getVersionName() {
        String versionName = null;

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        }

        if (packageInfo != null) {
            versionName = packageInfo.versionName;
        }

        return versionName;
    }
}
