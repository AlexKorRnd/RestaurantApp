package com.growapp.restaurantapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;
    //private SQLiteDatabase database;
    private RestaurantDBAdapter mDBAdapter;

    public MainActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FetchRestaurantTask task = new FetchRestaurantTask();
        task.execute();

        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_restaurant,
                R.id.list_item_restaurant_textView);

        ListView listView = (ListView) view.findViewById(R.id.listview_restaurant);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailInfo.class);

                String name = ((TextView) view).getText().toString();
                intent.putExtra(Constants.TAG_RESTAURANT_ID, name);
                startActivity(intent);
            }
        });

        return view;
    }


    public class FetchRestaurantTask extends AsyncTask<Void, Void, String[]>{

        private  final String LOG_TAG = FetchRestaurantTask.class.getSimpleName();

        private String[] getRestaurantDataFromJson(String restaurantJsonStr)
                throws JSONException {
            final String RESTAURANT_ARRAY = "items";
            final String RESTAURANT_ID = "id";
            final String RESTAURANT_NAME = "name";
            final String RESTAURANT_PRICE = "price";
            final String RESTAURANT_PHOTOS = "photos";
            final String RESTAURANT_PHOTOS_ORIGINAL = "original";
            final String RESTAURANT_PHOTOS_THUMB = "thumb";


            JSONObject restaurants = new JSONObject(restaurantJsonStr);
            JSONArray restaurantJSONArray = restaurants.getJSONArray(RESTAURANT_ARRAY);

            String[] resultString = new String[restaurantJSONArray.length()];



            for (int i=0; i<restaurantJSONArray.length(); ++i){
                JSONObject restaurantObject = restaurantJSONArray.getJSONObject(i);

                int id = restaurantObject.getInt(RESTAURANT_ID);
                resultString[i] = restaurantObject.getString(RESTAURANT_NAME);
                String price = restaurantObject.getString(RESTAURANT_PRICE);

                mDBAdapter.createTableRestaurantItem(id, resultString[i], price);

                JSONArray photosJSONArray = restaurantObject.getJSONArray(RESTAURANT_PHOTOS);
                for (int j=0; j < photosJSONArray.length(); ++i){
                    JSONObject photosObject = photosJSONArray.getJSONObject(i);

                    String original = photosObject.getString(RESTAURANT_PHOTOS_ORIGINAL);
                    String thumb = photosObject.getString(RESTAURANT_PHOTOS_THUMB);

                    mDBAdapter.createTablePhotosItem(original, thumb, id);
                }

            }

            return resultString;
        }

        String getStringFromAssetFile()
        {
            AssetManager am = getActivity().getAssets();

            BufferedReader reader = null;
            InputStream inputStream = null;
            String data;

            try {

                inputStream = am.open("restaurant.json");
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                data = buffer.toString();

                return data;

            } catch (IOException e) {
                Log.d(LOG_TAG, "Невозможно прочитать restaurant.json");
                e.printStackTrace();
            }
            finally {
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            mDBAdapter = new RestaurantDBAdapter(getActivity());

            String test = getStringFromAssetFile();
            try {
                return getRestaurantDataFromJson(test);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                mForecastAdapter.clear();

                for (String nameRestaurant: result){
                    mForecastAdapter.add(nameRestaurant);
                }
            }
        }
    }
}
