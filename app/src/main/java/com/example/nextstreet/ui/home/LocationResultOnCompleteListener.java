package com.example.nextstreet.ui.home;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationResultOnCompleteListener implements OnCompleteListener<Location> {

  private String TAG;
  private GoogleMap map;
  private Location lastKnownLocation;
  private HomeFragment fragment;
  private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

  protected LocationResultOnCompleteListener(String TAG, GoogleMap map, HomeFragment fragment) {
    this.TAG = TAG;
    this.map = map;
    this.fragment = fragment;
  }

  @Override
  public void onComplete(@NonNull Task<Location> task) {
    if (task.isSuccessful()) {
      // Set the map's camera position to the current location of the device.
      lastKnownLocation = task.getResult();
      if (lastKnownLocation != null) {
        LatLng latLng =
            new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, HomeFragment.DEFAULT_ZOOM));
        HomeFragment.setLastKnownLocation(lastKnownLocation);
        fragment.setOrigin(latLng);
      }
    } else {
      Log.d(TAG, "Current location is null. Using defaults.");
      Log.e(TAG, "Exception: %s", task.getException());
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, HomeFragment.DEFAULT_ZOOM));
      map.getUiSettings().setMyLocationButtonEnabled(false);
    }
  }
}
