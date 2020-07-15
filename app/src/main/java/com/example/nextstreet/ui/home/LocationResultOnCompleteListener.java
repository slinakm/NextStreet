package com.example.nextstreet.ui.home;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;

public class LocationResultOnCompleteListener implements OnCompleteListener<Location>{

    private String TAG;
    private GoogleMap map;
    private Location lastKnownLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    protected LocationResultOnCompleteListener(String TAG, GoogleMap map) {
        this.TAG = TAG;
        this.map = map;
    }

    @Override
    public void onComplete(@NonNull Task<Location> task) {
        if (task.isSuccessful()) {
            // Set the map's camera position to the current location of the device.
            lastKnownLocation = task.getResult();
            if (lastKnownLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(),
                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                HomeFragment.setLastKnownLocation(lastKnownLocation);
            }
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            Log.e(TAG, "Exception: %s", task.getException());
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

}