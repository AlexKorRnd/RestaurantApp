package com.growapp.restaurantapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class JSONObjectComparable implements Comparator<JSONObject> {

    private final static String LOG_TAG = "qqqqqqqqq";

    @Override
    public int compare(JSONObject obj1, JSONObject obj2) {


        String[] parts;
        Integer price1, price2;
        try {
            parts = obj1.getString("price").split(" ");

            price1 = Integer.valueOf(parts[0]);


            parts = obj2.getString("price").split(" ");
            price2 = Integer.valueOf(parts[0]);


            return price1.compareTo(price2);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "error comparable");
        }
        return 0;
    }
}
