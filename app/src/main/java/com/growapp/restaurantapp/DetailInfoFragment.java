package com.growapp.restaurantapp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailInfoFragment extends Fragment {

    private  final String LOG_TAG = DetailInfo.class.getSimpleName();

    public DetailInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_info, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {

            String name = intent.getStringExtra(Intent.EXTRA_INTENT);

            Log.d(LOG_TAG, "Name = " + name);

            TextView nameRestaurantTextView = ((TextView)
                    rootView.findViewById(R.id.restaurant_name_textView));
            nameRestaurantTextView.setText(name);

            FetchChosenRestaurantTask task = new FetchChosenRestaurantTask(rootView);
            task.execute(name);
        }


        return rootView;
    }

    public class FetchChosenRestaurantTask extends AsyncTask<String, Void, JSONObject>{

        private  final String LOG_TAG = FetchChosenRestaurantTask.class.getSimpleName();

        private final static String RESTAURANT_ARRAY = "items";
        private final static String RESTAURANT_NAME = "name";
        private final static String RESTAURANT_PRICE = "price";
        private final static String RESTAURANT_PHOTOS = "photos";
        private final static String RESTAURANT_PHOTOS_ORIGINAL = "original";
        private final static String RESTAURANT_PHOTOS_THUMB = "thumb";

        private View rootView;

        public FetchChosenRestaurantTask(View rootView) {
            this.rootView = rootView;
        }

        private JSONObject getRestaurantJSONObjectFromJson(String restaurantJsonStr,
                                                               String name)
                throws JSONException {

            JSONObject restaurants = new JSONObject(restaurantJsonStr);
            JSONArray restaurantJSONArray = restaurants.getJSONArray(RESTAURANT_ARRAY);


            for (int i=0; i<restaurantJSONArray.length(); ++i){
                JSONObject restaurantItem = restaurantJSONArray.getJSONObject(i);

                String curName = restaurantItem.getString(RESTAURANT_NAME);
                if (curName.equals(name)){
                    return restaurantItem;
                }
            }
            Log.d(LOG_TAG, "Имя ресторана не найдено в json файле");
            return null;
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
        protected JSONObject doInBackground(String... params) {

            String test = getStringFromAssetFile();
            try {
                return getRestaurantJSONObjectFromJson(test, params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(RESTAURANT_PHOTOS);

                String tmpString;

                ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView_Photo1);
                tmpString = jsonArray.getJSONObject(0)
                            .getString(RESTAURANT_PHOTOS_ORIGINAL);

                URL url = new URL(tmpString);
                DownloadImagesTask downloadImagesTask = new DownloadImagesTask(url);
                downloadImagesTask.execute(imageView);

                imageView = (ImageView) rootView.findViewById(R.id.imageView_Photo2);
                tmpString = jsonArray.getJSONObject(0)
                            .getString(RESTAURANT_PHOTOS_THUMB);
                url = new URL(tmpString);
                downloadImagesTask = new DownloadImagesTask(url);
                downloadImagesTask.execute(imageView);



                imageView = (ImageView) rootView.findViewById(R.id.imageView_Photo3);
                tmpString = jsonArray.getJSONObject(1)
                        .getString(RESTAURANT_PHOTOS_ORIGINAL);

                url = new URL(tmpString);
                downloadImagesTask = new DownloadImagesTask(url);
                downloadImagesTask.execute(imageView);

                imageView = (ImageView) rootView.findViewById(R.id.imageView_Photo4);
                tmpString = jsonArray.getJSONObject(1)
                        .getString(RESTAURANT_PHOTOS_THUMB);
                url = new URL(tmpString);
                downloadImagesTask = new DownloadImagesTask(url);
                downloadImagesTask.execute(imageView);

                tmpString = jsonObject.getString(RESTAURANT_PRICE);
                TextView textView = (TextView) rootView.findViewById(R.id.textView_price);
                textView.setText(tmpString);

                final String tel = "+71234567899";
                textView = (TextView) rootView.findViewById(R.id.number_telephone);
                textView.setText(tel);

                rootView.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "tel:" + tel.trim();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                });


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.toast_error_load_data_from_internet,
                        Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}
