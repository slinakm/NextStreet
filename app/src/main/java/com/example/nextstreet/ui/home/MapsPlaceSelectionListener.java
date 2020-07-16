package com.example.nextstreet.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nextstreet.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

public class MapsPlaceSelectionListener implements PlaceSelectionListener {

    private boolean isOrigin;
    private String TAG;
    private View view;
    private HomeFragment fragment;

    protected MapsPlaceSelectionListener(String TAG, View view, HomeFragment fragment,
                                         boolean isOrigin) {
        this.TAG = TAG;
        this.view = view;
        this.fragment = fragment;
        this.isOrigin = isOrigin;
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        String placeSelected = place.getName().toString() + " "
                + fragment.getString(R.string.maps_onplace_succ);
        Snackbar.make(view, placeSelected, Snackbar.LENGTH_LONG).show();
        Log.i(TAG, "onPlaceSelected: Selected " + place.getName()
                + " and id: " + place.getId());

        if (isOrigin) {
            fragment.setOrigin(place.getLatLng());
        } else {
            fragment.setDestination(place.getLatLng());
        }
    }

    @Override
    public void onError(@NonNull Status status) {
        Snackbar.make(view, fragment.getText(R.string.maps_onplace_err), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Log.e(TAG, "onError: Error choosing place " + status.toString(), null);
    }
}
