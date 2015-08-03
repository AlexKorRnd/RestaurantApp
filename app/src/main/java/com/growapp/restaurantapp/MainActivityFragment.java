package com.growapp.restaurantapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

        return view;
    }

    public class FetchRestaurantTask extends AsyncTask<Void, Void, String[]>{

        private  final String LOG_TAG = FetchRestaurantTask.class.getSimpleName();

        private final String Restaurant_BASE_URL =
                "https://www.dropbox.com/s/q9qgyyetg7zev9m/restaurant.json";
        private final String QUERY_PARAM = "dll";
        private final String QUERY_VALUE = "1";

        private String[] getRestaurantDataFromJson(String restaurantJsonStr)
                throws JSONException {
            final String RESTAURANT_ARRAY = "items";

            final String RESTAURANT_NAME = "name";


            JSONObject forecastJson = new JSONObject(restaurantJsonStr);
            JSONArray restaurantJSONArray = forecastJson.getJSONArray(RESTAURANT_ARRAY);

            String[] resultString = new String[restaurantJSONArray.length()];

            for (int i=0; i<restaurantJSONArray.length(); ++i){
                JSONObject restaurantObject = restaurantJSONArray.getJSONObject(i);

                resultString[i] = restaurantObject.getString(RESTAURANT_NAME);
            }

            return resultString;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String restaurantJsonString;

            try {
                Uri builtUri = Uri.parse(Restaurant_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, QUERY_VALUE).build();

                String jsonUrl = builtUri.toString();

                //Log.d(LOG_TAG, "jsonUrl = " + jsonUrl);

                URL url = new URL(jsonUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {

                    Toast.makeText(getActivity(), R.string.toast_error_input_data,
                            Toast.LENGTH_LONG).show();

                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                restaurantJsonString = buffer.toString();

                Log.d(LOG_TAG,"JSON string = " + restaurantJsonString);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;

            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            /*try {
                return getRestaurantDataFromJson(restaurantJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }*/

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
