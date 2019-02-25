package com.home.konovaloff.homework.model.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.home.konovaloff.homework.global.Global;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    private static final int VERSION = 2;
    public static final String DATABASE_NAME = "weather.db";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        createSchema(db);
    }

    private void createSchema(SQLiteDatabase db) {
        //Таблицы
        executeSQL(db, CityEntity.CREATE);
        executeSQL(db, SearchHistoryEntity.CREATE);

        //Индексы
        executeSQL(db, SearchHistoryEntity.INDEX_CITY);
    }

    public static void executeSQL(SQLiteDatabase db, String query){
        try{
            db.execSQL(query);
        }catch (Exception e){
            Global.logE(TAG, e.toString());
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Global.logI(TAG, String.format("Обновление с %d версии до %d", oldVersion, newVersion));

        switch (oldVersion){
            case 1:
                switch (newVersion){
                    case 2:
                        updateVersion1to2(db);
                        break;
                    case 3:
                        //to version 2
                        updateVersion1to2(db);
                        //to version 3
                        updateVersion2to3(db);
                        break;
                }
                break;
            case 2:
                switch (newVersion){
                    case 3:
                        updateVersion2to3(db);
                        break;
                }
                break;
            case 3:
                switch (newVersion){
                    case 4:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void updateVersion2to3(SQLiteDatabase db){

    }

    private void updateVersion1to2(SQLiteDatabase db){
        executeSQL(db, CityEntity.CREATE);
        executeSQL(db, "DROP TABLE " + SearchHistoryEntity.TABLE_NAME);
        executeSQL(db, SearchHistoryEntity.CREATE);
    }
}
