package com.growapp.restaurantapp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mAdapter;
    private ArrayList<JSONObject>  mJSONObjects;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            sortListView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortListView() {
        JSONObjectComparable jsonObjectComparable = new JSONObjectComparable();
        Collections.sort(mJSONObjects, jsonObjectComparable);
        mAdapter.clear();
        for (JSONObject jsonObject: mJSONObjects){
            try {
                mAdapter.add(jsonObject.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FetchRestaurantTask task = new FetchRestaurantTask();
        task.execute();

        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_restaurant,
                R.id.list_item_restaurant_textView);

        ListView listView = (ListView) view.findViewById(R.id.listview_restaurant);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailInfo.class);

                String name = ((TextView) view).getText().toString();
                intent.putExtra(Intent.EXTRA_INTENT, name);
                startActivity(intent);
            }
        });

        return view;
    }


    private class FetchRestaurantTask extends AsyncTask<Void, Void, ArrayList<JSONObject>>{

        private  final String LOG_TAG = FetchRestaurantTask.class.getSimpleName();

        private static final String RESTAURANT_ARRAY = "items";
        private static final String RESTAURANT_ID = "id";
        private static final String RESTAURANT_NAME = "name";

        private ArrayList<JSONObject> getRestaurantDataFromJson(String restaurantJsonStr)
                throws JSONException {

            JSONObject restaurants = new JSONObject(restaurantJsonStr);
            JSONArray restaurantJSONArray = restaurants.getJSONArray(RESTAURANT_ARRAY);

            //String[] resultString = new String[restaurantJSONArray.length()];
             mJSONObjects = new ArrayList<>(restaurantJSONArray.length());


            for (int i=0; i<restaurantJSONArray.length(); ++i){
                JSONObject restaurantItem = restaurantJSONArray.getJSONObject(i);

                mJSONObjects.add(restaurantItem);
            }

            return mJSONObjects;
        }

        String getStringFromAssetFile()
        {
            AssetManager am = getActivity().getAssets();

            BufferedReader reader;
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
        protected ArrayList<JSONObject> doInBackground(Void... params) {

            String test = getStringFromAssetFile();
            try {
                return getRestaurantDataFromJson(test);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> result) {
            if (result != null){
                mAdapter.clear();
                for (JSONObject jsonObject:result){
                    try {
                        mAdapter.add(jsonObject.getString(RESTAURANT_NAME));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
