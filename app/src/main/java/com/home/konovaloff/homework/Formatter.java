package com.home.konovaloff.homework;

import java.text.SimpleDateFormat;

public class Formatter {
    public static String DATE_TIME = "hh:mm:ss dd-MM-yyyy";

    public static SimpleDateFormat sdfLastUpdate = new SimpleDateFormat(DATE_TIME);

    public static String formatDateTime(long datetime){
        return sdfLastUpdate.format(datetime);
    }

    public static String formatTemperature(float value){
        return String.format("%.1f", value);
    }
}
