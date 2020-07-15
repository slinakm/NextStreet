package com.example.nextstreet.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.nextstreet.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

public class MapsPlaceSelectionListener implements PlaceSelectionListener {

    private String TAG;
    private View view;
    private Context context;

    private Place place;

    public MapsPlaceSelectionListener(String TAG, View view, Context context) {
        this.TAG = TAG;
        this.view = view;
        this.context = context;
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        this.place = place;
        String placeSelected = place.getName().toString() + " "
                + context.getString(R.string.maps_onplace_succ);
        Snackbar.make(view, placeSelected, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Log.i(TAG, "onPlaceSelected: Selected " + place.getName().toString());
    }

    @Override
    public void onError(@NonNull Status status) {
        Snackbar.make(view, context.getText(R.string.maps_onplace_err), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Log.e(TAG, "onError: Error choosing place " + status.toString(), null);
    }

    public Place getPlace() {
        return place;
    }
}
