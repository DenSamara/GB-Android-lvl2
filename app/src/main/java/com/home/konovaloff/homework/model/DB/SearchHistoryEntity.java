package com.home.konovaloff.homework.model.DB;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class SearchHistoryEntity implements BaseColumns {
    public static final String TABLE_NAME = "SearchHistory";

    public static final String COLUMN_ID = _ID;
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_LAST_UPDATE = "lastUpdate";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_IMAGE_URL = "imageURL";

    public static String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_CITY,
            COLUMN_TEMPERATURE,
            COLUMN_LAST_UPDATE,
            COLUMN_DETAILS,
            COLUMN_IMAGE_URL
    };

    public static final String CREATE = String.format(
            "CREATE TABLE %s ("
                    + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "  //0 - COLUMN_ID
                    + "%s TEXT NOT NULL DEFAULT '', "           //1 - COLUMN_CITY
                    + "%s REAL NOT NULL DEFAULT 0, "            //2 - COLUMN_TEMPERATURE
                    + "%s LONG NOT NULL DEFAULT 0,"             //3 - COLUMN_LAST_UPDATE
                    + "%s TEXT NOT NULL DEFAULT '', "           //4 - COLUMN_CITY
                    + "%s TEXT NOT NULL DEFAULT '')",           //5 - COLUMN_IMAGE_URL
            SearchHistoryEntity.TABLE_NAME,
            SearchHistoryEntity.COLUMN_ID,          //0
            SearchHistoryEntity.COLUMN_CITY,        //1
            SearchHistoryEntity.COLUMN_TEMPERATURE, //2
            SearchHistoryEntity.COLUMN_LAST_UPDATE, //3
            SearchHistoryEntity.COLUMN_DETAILS,     //4
            SearchHistoryEntity.COLUMN_IMAGE_URL    //5
    );

    public static final String INDEX_CITY = String.format(
            "CREATE INDEX idxCity ON %s (%s)",
            TABLE_NAME,
            COLUMN_CITY);

    //Нужна при выходе пользователя
    public static void clear(SQLiteDatabase db) throws SQLException {
        db.execSQL("DELETE FROM "+SearchHistoryEntity.TABLE_NAME);
    }
}
