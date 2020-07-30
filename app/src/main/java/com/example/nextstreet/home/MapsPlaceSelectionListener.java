package com.example.nextstreet.home;

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

  private static final String TAG = MapsPlaceSelectionListener.class.getSimpleName();
  private final boolean isOrigin;
  private final View view;
  private final MapsPlaceSelectionResponder mapsPlaceSelectionResponder;
  private final Context context;

  MapsPlaceSelectionListener(View view, MapsPlaceSelectionResponder mapsPlaceSelectionResponder, boolean isOrigin) {
    this.view = view;
    this.mapsPlaceSelectionResponder = mapsPlaceSelectionResponder;
    this.isOrigin = isOrigin;
    this.context = (Context) mapsPlaceSelectionResponder;
  }

  @Override
  public void onPlaceSelected(@NonNull Place place) {
    String placeSelected =
        place.getName().toString() + " " + context.getString(R.string.maps_onplace_succ);
    Snackbar.make(view, placeSelected, Snackbar.LENGTH_LONG).show();
    Log.i(TAG, "onPlaceSelected: Selected " + place.getName() + " and id: " + place.getId());

    if (isOrigin) {
      mapsPlaceSelectionResponder.setOrigin(place.getLatLng());
      mapsPlaceSelectionResponder.setOriginPlace(place);
    } else {
      mapsPlaceSelectionResponder.setDestination(place.getLatLng());
      mapsPlaceSelectionResponder.setDestinationPlace(place);
    }
  }

  @Override
  public void onError(@NonNull Status status) {
    Snackbar.make(view, context.getText(R.string.maps_onplace_err), Snackbar.LENGTH_LONG)
        .setAction("Action", null)
        .show();
    Log.e(TAG, "onError: Error choosing place " + status.toString(), null);
  }
}
