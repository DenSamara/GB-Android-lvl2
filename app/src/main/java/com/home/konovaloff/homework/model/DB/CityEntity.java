package com.home.konovaloff.homework.model.DB;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class CityEntity implements BaseColumns {
    public static final String TABLE_NAME = "City";

    public static final String COLUMN_ID = _ID;
    public static final String COLUMN_NAME = "CityName";
    public static final String COLUMN_COUNTRY = "Country";

    public static String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_COUNTRY
    };

    public static final String CREATE = String.format(
            "CREATE TABLE %s ("
                    + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "  //0 - COLUMN_ID
                    + "%s TEXT NOT NULL DEFAULT '', "           //1 - COLUMN_NAME
                    + "%s TEXT NOT NULL DEFAULT '')",           //2 - COLUMN_COUNTRY
            CityEntity.TABLE_NAME,
            CityEntity.COLUMN_ID,          //0
            CityEntity.COLUMN_NAME,        //1
            CityEntity.COLUMN_COUNTRY      //2
    );

    //Нужна при выходе пользователя
    public static void clear(SQLiteDatabase db) throws SQLException {
        db.execSQL("DELETE FROM "+ CityEntity.TABLE_NAME);
    }
}
