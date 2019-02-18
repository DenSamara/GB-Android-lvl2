package com.home.konovaloff.homework.model.DB;


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
        executeSQL(db, SearchHistoryEntity.CREATE);

        //Индексы
        executeSQL(db, SearchHistoryEntity.INDEX_CITY);
    }

    public static void executeSQL(SQLiteDatabase db, String query){
        try{
            db.execSQL(query);
        }catch (Exception e){
            Global.log_e(TAG, e.toString());
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Global.log_i(TAG, String.format("Обновление с %d версии до %d", oldVersion, newVersion));

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
                    case 11:
                    case 12:
                    case 13:
                        break;
                }
                break;
            case 10:
                switch (newVersion){
                    case 11:
                    case 12:
                    case 13:
                        break;
                }
                break;
            case 11:
                switch (newVersion){
                    case 12:
                    case 13:
                        break;
                }
                break;
            case 12:
                switch (newVersion){
                    case 13:
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
//        String addCreationDateColumn = String.format("ALTER TABLE %s ADD %s long NOT NULL DEFAULT 0",
//                SearchHistoryEntity.TABLE_NAME,
//                SearchHistoryEntity.COLUMN_CREATION);
//        executeSQL(db, addCreationDateColumn);
    }
}
