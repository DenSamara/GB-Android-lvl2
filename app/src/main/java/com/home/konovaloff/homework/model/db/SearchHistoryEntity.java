package com.home.konovaloff.homework.model.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class SearchHistoryEntity implements BaseColumns {
    public static final String TABLE_NAME = "SearchHistory";

    public static final String COLUMN_ID = _ID;
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_LAST_UPDATE = "lastUpdate";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_IMAGE_URL = "imageURL";
    public static final String COLUMN_CITY_FK = "city_fk";

    public static String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_TEMPERATURE,
            COLUMN_LAST_UPDATE,
            COLUMN_DETAILS,
            COLUMN_IMAGE_URL,
            COLUMN_CITY_FK
    };

    public static final String CREATE = String.format(
            "CREATE TABLE %s ("
                    + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "  //0 - COLUMN_ID
                    + "%s REAL NOT NULL DEFAULT 0, "            //1 - COLUMN_TEMPERATURE
                    + "%s TEXT NOT NULL DEFAULT '', "           //2 - COLUMN_DETAILS
                    + "%s TEXT NOT NULL DEFAULT '',"            //3 - COLUMN_IMAGE_URL
                    + "%s LONG NOT NULL DEFAULT 0,"             //4 - COLUMN_LAST_UPDATE
                    + "%s LONG NOT NULL DEFAULT 0)",           //5 - COLUMN_CITY_FK
            SearchHistoryEntity.TABLE_NAME,
            SearchHistoryEntity.COLUMN_ID,          //0
            SearchHistoryEntity.COLUMN_TEMPERATURE, //1
            SearchHistoryEntity.COLUMN_DETAILS,     //2
            SearchHistoryEntity.COLUMN_IMAGE_URL,    //3
            SearchHistoryEntity.COLUMN_LAST_UPDATE, //4
            SearchHistoryEntity.COLUMN_CITY_FK        //5
    );

    public static final String INDEX_CITY = String.format(
            "CREATE INDEX idxSearchHistoryOnLastUpdate ON %s (%s)",
            TABLE_NAME,
            COLUMN_LAST_UPDATE);

    //Нужна при выходе пользователя
    public static void clear(SQLiteDatabase db) throws SQLException {
        db.execSQL("DELETE FROM " + SearchHistoryEntity.TABLE_NAME);
    }
}
