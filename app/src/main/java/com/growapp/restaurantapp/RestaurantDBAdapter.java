package com.growapp.restaurantapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class RestaurantDBAdapter {

    private Context context;
    private SQLiteDatabase database;
    private RestaurantDBHelper dbHelper;

    public RestaurantDBAdapter(Context context) {
        this.context = context;
    }

    public RestaurantDBAdapter open() throws SQLException {
        dbHelper = new RestaurantDBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public long createTableRestaurantItem(int id, String name, String price) {
        ContentValues initialValues = createContentValuesRestaurant(id, name, price);

        return database.insert(RestaurantDBHelper.RESTAURANTS_TABLE_NAME, null, initialValues);
    }

    public long createTablePhotosItem(String originalPhotoURL, String thumbPhotoURL,
                                      int restaurantId) {
        ContentValues initialValues = createContentValuesPhotos(originalPhotoURL, thumbPhotoURL,
                restaurantId);

        return database.insert(RestaurantDBHelper.RESTAURANTS_TABLE_NAME, null, initialValues);
    }

    public boolean updateRestaurant(int rowId, String name, String price) {
        ContentValues updateValues = createContentValuesRestaurant(rowId, name, price);

        return database.update(RestaurantDBHelper.RESTAURANTS_TABLE_NAME, updateValues, null, null) > 0;
    }



    public Cursor fetchRestaurant(long rowId) throws SQLException {
        Cursor mCursor = database.query(true, RestaurantDBHelper.RESTAURANTS_TABLE_NAME,
                null, RestaurantDBHelper.RESTAURANTS_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchRestaurant(String name) throws SQLException {
        Cursor mCursor = database.query(true, RestaurantDBHelper.RESTAURANTS_TABLE_NAME,
                null, RestaurantDBHelper.RESTAURANTS_NAME + "=" + name, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public int NameRestaurantToInt(String name){
        Cursor mCursor = database.query(true, RestaurantDBHelper.RESTAURANTS_TABLE_NAME,
                null, RestaurantDBHelper.RESTAURANTS_NAME + "=" + name, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            return mCursor.getInt(0);
        }
        return 0;
    }

    public Cursor fetchPhotos(long rowId) throws SQLException {
        Cursor mCursor = database.query(true, RestaurantDBHelper.PHOTOS_TABLE_NAME,
                null, RestaurantDBHelper.RESTAURANTS_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    private ContentValues createContentValuesRestaurant(int id, String name,
                                              String price) {
        ContentValues values = new ContentValues();
        values.put(RestaurantDBHelper.RESTAURANTS_ID, id);
        values.put(RestaurantDBHelper.RESTAURANTS_NAME, name);
        values.put(RestaurantDBHelper.PRICE, price);
        return values;
    }

    private ContentValues createContentValuesPhotos(String original, String thumb,
                                                        int idRestaurant) {
        ContentValues values = new ContentValues();
        values.put(RestaurantDBHelper.ORIGINAL_PHOTO_URL, original);
        values.put(RestaurantDBHelper.THUMB_PHOTO_URL, thumb);
        values.put(RestaurantDBHelper.RESTAURANTS_ID, idRestaurant);
        return values;
    }

}

