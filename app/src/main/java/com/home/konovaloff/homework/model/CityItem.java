package com.home.konovaloff.homework.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.home.konovaloff.homework.global.Global;
import com.home.konovaloff.homework.model.DB.CityEntity;

import java.util.ArrayList;

/**
 * Список городов
 */
public class CityItem implements Parcelable {
    public static final String TAG = CityItem.class.getSimpleName();

    private long id;
    private String cityName;
    private String country;

    public CityItem(long id, String city, String country){
        this.id = id;
        this.cityName = city;
        this.country = country;
    }

    protected CityItem(Parcel in) {
        id = in.readLong();
        cityName = in.readString();
        country = in.readString();
    }

    public static final Creator<CityItem> CREATOR = new Creator<CityItem>() {
        @Override
        public CityItem createFromParcel(Parcel in) {
            return new CityItem(in);
        }

        @Override
        public CityItem[] newArray(int size) {
            return new CityItem[size];
        }
    };

    public String cityName() {
        return cityName;
    }

    public void id(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    public String country() {
        return country;
    }


    public static long insert(SQLiteDatabase db, CityItem item) throws SQLException{
        return insert(db, item.cityName, item.country);
    }

    public static long insert(SQLiteDatabase db, String cityName, String country) throws SQLException {
        ContentValues cv = new ContentValues(2);

        cv.put(CityEntity.COLUMN_NAME, cityName);
        cv.put(CityEntity.COLUMN_COUNTRY, country);

        return db.insertOrThrow(CityEntity.TABLE_NAME, null, cv);
    }

    public static long update(SQLiteDatabase db, CityItem item) throws SQLException {
        return update(db, item.id, item.cityName, item.country);
    }

    public static long update(SQLiteDatabase db, long cityID, String cityName, String country) throws SQLException {
        ContentValues cv = new ContentValues(2);

        cv.put(CityEntity.COLUMN_NAME, cityName);
        cv.put(CityEntity.COLUMN_COUNTRY, country);

        String where = String.format("%s = %d", CityEntity.COLUMN_ID, cityID);

        return db.update(CityEntity.TABLE_NAME, cv, where, null);
    }

    /**
     * Загружаем по добавлению в обратном порядке.
     * @param db
     * @param cityName
     * @param country
     * @return
     */
    public static CityItem find(SQLiteDatabase db, String cityName, String country){
        CityItem result = null;
        String where = String.format("%s = '%s' AND %s = '%s'",
                CityEntity.COLUMN_NAME, cityName,
                CityEntity.COLUMN_COUNTRY, country);

        ArrayList<CityItem> items = rawQuery(db, where, null, null, null, null);
        if (items != null && items.size() > 0){
            result = items.get(0);
        }
        return result;
    }

    /**
     * Загружаем по добавлению в обратном порядке.
     * @param db
     * @param cityID
     * @return
     */
    public static CityItem loadByID(SQLiteDatabase db, long cityID){
        CityItem result = null;
        String where = String.format("%s = %d", CityEntity.COLUMN_ID, cityID);
        ArrayList<CityItem> items = rawQuery(db, where, null, null, null, null);
        if (items != null && items.size() > 0){
            result = items.get(0);
        }
        return result;
    }

    public static ArrayList<CityItem> rawQuery(SQLiteDatabase db, String where, String[] args, String groupBy, String having, String orderBy){
        ArrayList<CityItem> result = null;
        Cursor rows = null;
        try{
            rows = db.query(CityEntity.TABLE_NAME, CityEntity.COLUMNS, where, args, groupBy, having, orderBy);

            if (rows.getCount() < 1) return result;

            result = new ArrayList<>();

            int columnIDIndex = rows.getColumnIndex(CityEntity.COLUMN_ID);
            int columnCityIndex = rows.getColumnIndex(CityEntity.COLUMN_NAME);
            int columnCountryIndex = rows.getColumnIndex(CityEntity.COLUMN_COUNTRY);

            while (rows.moveToNext()) {
                CityItem item = new CityItem(
                        rows.getLong(columnIDIndex),
                        rows.getString(columnCityIndex),
                        rows.getString(columnCountryIndex));

                result.add(item);
            }
            result.trimToSize();
        }catch (Exception e){
            Global.log_e(TAG, e.toString());
        }finally {
            if (rows != null) rows.close();
        }
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(cityName);
        dest.writeString(country);
    }
}
