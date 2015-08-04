package com.growapp.restaurantapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailInfoFragment extends Fragment {

    private  final String LOG_TAG = DetailInfo.class.getSimpleName();

    private RestaurantDBAdapter mDBAdapter;

    public DetailInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_info, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {

            String name = intent.getStringExtra(Constants.TAG_RESTAURANT_ID);

            Log.d(LOG_TAG, "Name = " + name);

            TextView nameRestaurantTextView = ((TextView)
                    view.findViewById(R.id.restaurant_name_textView));

            nameRestaurantTextView.setText(name);

            mDBAdapter = new RestaurantDBAdapter(getActivity());

            Cursor cursorRestaurant = mDBAdapter.fetchRestaurant(name);




        }
        return view;
    }



}
