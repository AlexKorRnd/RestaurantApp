package com.growapp.restaurantapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class RestaurantDBHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = RestaurantDBHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "applicationdata";

    public static final String RESTAURANTS_TABLE_NAME = "restaurants";
    public static final String RESTAURANTS_ID = "_id";
    public static final String RESTAURANTS_NAME = "name";
    //public static final String PHOTOS = "photos";
    public static final String PRICE = "price";

    public static final String PHOTOS_TABLE_NAME = "photos";
    public static final String PHOTOS_ID = "_id";
    public static final String ORIGINAL_PHOTO_URL = "original";
    public static final String THUMB_PHOTO_URL = "thumb";



    public RestaurantDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");

        // создаем таблицу с ресторанами
        db.execSQL("create table" + RESTAURANTS_TABLE_NAME + "("
                + RESTAURANTS_ID + " integer primary key,"
                + RESTAURANTS_NAME + " text,"
                + PRICE + " text" + ");");

        // создаем таблицу с фото
        db.execSQL("create table" + PHOTOS_TABLE_NAME + "("
                + PHOTOS_ID + " integer primary key autoincrement,"
                + " FOREIGN KEY (" + RESTAURANTS_ID + ") REFERENCES "
                + ORIGINAL_PHOTO_URL + " text,"
                + THUMB_PHOTO_URL + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RestaurantDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS todo");

        onCreate(db);
    }
}
