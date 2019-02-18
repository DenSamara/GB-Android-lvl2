package com.home.konovaloff.homework.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.home.konovaloff.homework.global.Global;
import com.home.konovaloff.homework.model.DB.SearchHistoryEntity;

import java.util.ArrayList;

/**
 * Кортеж из БД для показа погоды
 */
public class WeatherItem {
    public static final String TAG = WeatherItem.class.getSimpleName();

    private long id;
    private long lastUpdate;
    private CityItem city;
    private String details;
    private float temperature;
    private String url;

    public WeatherItem(long id, CityItem city, String details, float temperature, String url, long lastUpdate){
        this.id = id;
        this.city = city;
        this.details = details;
        this.temperature = temperature;
        this.url = url;

        this.lastUpdate = lastUpdate;
    }

    public long lastUpdate() {
        return lastUpdate;
    }

    public CityItem city() {
        return city;
    }

    public String details() {
        return details;
    }

    public float temperature() {
        return temperature;
    }

    public String imageUrl() {
        return url;
    }

    public static long insert(SQLiteDatabase db, WeatherItem item) throws SQLException{
        return insert(db, item.details, item.temperature, item.url, item.city.id(), item.lastUpdate);
    }

    public static long insert(SQLiteDatabase db, String details, float temperature, String url, long city_fk, long lastUpdate) throws SQLException {
        ContentValues cv = new ContentValues(5);

        cv.put(SearchHistoryEntity.COLUMN_DETAILS, details);
        cv.put(SearchHistoryEntity.COLUMN_TEMPERATURE, temperature);
        cv.put(SearchHistoryEntity.COLUMN_IMAGE_URL, url);
        cv.put(SearchHistoryEntity.COLUMN_CITY_FK, city_fk);
        cv.put(SearchHistoryEntity.COLUMN_LAST_UPDATE, lastUpdate);

        return db.insertOrThrow(SearchHistoryEntity.TABLE_NAME, null, cv);
    }

    /**
     * Загружаем по добавлению в обратном порядке.
     * @param db
     * @param city
     * @return
     */
    public static ArrayList<WeatherItem> load(SQLiteDatabase db, long city){
        String where = String.format("%s = %d", SearchHistoryEntity.COLUMN_CITY_FK, city);
        return rawQuery(db, where, null, null, null, SearchHistoryEntity.COLUMN_ID+" DESC");
    }

    public static ArrayList<WeatherItem> rawQuery(SQLiteDatabase db, String where, String[] args, String groupBy, String having, String orderBy){
        ArrayList<WeatherItem> result = null;
        Cursor rows = null;
        try{
            rows = db.query(SearchHistoryEntity.TABLE_NAME, SearchHistoryEntity.COLUMNS, where, args, groupBy, having, orderBy);

            if (rows.getCount() < 1) return result;

            result = new ArrayList<>();

            int columnIDIndex = rows.getColumnIndex(SearchHistoryEntity.COLUMN_ID);
            int columnDetailsIndex = rows.getColumnIndex(SearchHistoryEntity.COLUMN_DETAILS);
            int columnTemperatureIndex = rows.getColumnIndex(SearchHistoryEntity.COLUMN_TEMPERATURE);
            int columnURLIndex = rows.getColumnIndex(SearchHistoryEntity.COLUMN_IMAGE_URL);
            int columnCityIndex = rows.getColumnIndex(SearchHistoryEntity.COLUMN_CITY_FK);
            int columnLastUpdateIndex = rows.getColumnIndex(SearchHistoryEntity.COLUMN_LAST_UPDATE);

            while (rows.moveToNext()) {
                //TODO этого здесь быть не должно
                CityItem city = CityItem.loadByID(db, rows.getLong(columnCityIndex));
                if (city != null) {
                    WeatherItem item = new WeatherItem(
                            rows.getLong(columnIDIndex),
                            city,
                            rows.getString(columnDetailsIndex),
                            rows.getFloat(columnTemperatureIndex),
                            rows.getString(columnURLIndex),
                            rows.getLong(columnLastUpdateIndex));

                    result.add(item);
                }
            }
            result.trimToSize();
        }catch (Exception e){
            Global.log_e(TAG, e.toString());
        }finally {
            if (rows != null) rows.close();
        }
        return result;
    }


}
