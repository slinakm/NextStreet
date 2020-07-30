package com.example.nextstreet.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ContentDetailBinding;
import com.example.nextstreet.databinding.FragmentDetailsBinding;
import com.example.nextstreet.home.HomeFragment;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.DetailsMaterialCard;
import com.example.nextstreet.utilities.DismissOnClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseGeoPoint;

public class DetailsFragment extends DialogFragment implements OnMapReadyCallback {

  private static final String TAG = DetailsFragment.class.getSimpleName();

  private FragmentDetailsBinding binding;

  private PackageRequest request;
  private GoogleMap map;

  private Marker markerOrigin;
  private Marker markerDestination;

  public static DetailsFragment newInstance(PackageRequest request) {
    Bundle args = new Bundle();
    args.putParcelable(PackageRequest.class.getSimpleName(), request);

    DetailsFragment fragment = new DetailsFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentDetailsBinding.inflate(getLayoutInflater());

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    request = (PackageRequest) getArguments().get(PackageRequest.class.getSimpleName());

    ContentDetailBinding layoutDetails = binding.layoutContentDetails;
    DetailsMaterialCard.setUpCard(layoutDetails.card, request, getContext());
    binding.ivCancel.setOnClickListener(new DismissOnClickListener(this));
  }

  @Override
  public void onResume() {
    super.onResume();

    getDialog()
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    if (map == null) {
      Snackbar.make(binding.getRoot(), "Error - Map was null!!", Snackbar.LENGTH_LONG).show();
    } else {
      setMapToCurrRequest();
    }
  }

  private void setMapToCurrRequest() {
    Log.i(TAG, "setMapToCurrRequest: here! ");
    ParseGeoPoint origin = request.getOrigin();
    ParseGeoPoint destination = request.getDestination();

    if (destination == null) {
      return;
    }

    Log.i(TAG, "setMapToCurrRequest: " + destination.getLatitude());

    LatLng latlngOrigin = new LatLng(origin.getLatitude(), origin.getLongitude());
    LatLng latlngDest = new LatLng(destination.getLatitude(), destination.getLongitude());

    setOriginNoCamera(latlngOrigin);
    setDestination(latlngDest);
  }

  protected void setOriginNoCamera(LatLng newPlace) {
    Preconditions.checkNotNull(newPlace, "newPlace unexpectedly null");
    setMarkerOrigin(newPlace);
  }

  protected void setDestination(LatLng newPlace) {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace, HomeFragment.DEFAULT_ZOOM));
    Preconditions.checkNotNull(newPlace, "newPlace unexpectedly null");
    setMarkerDestination(newPlace);
  }

  private void setMarkerOrigin(LatLng latLng) {

    MarkerOptions marker =
        new MarkerOptions().position(latLng).title(getString(R.string.origin)).flat(true);
    Marker markerOrigin = map.addMarker(marker);

    if (this.markerOrigin != null) {
      this.markerOrigin.remove();
    }
    this.markerOrigin = markerOrigin;
  }

  private void setMarkerDestination(LatLng latLng) {
    MarkerOptions marker =
        new MarkerOptions()
            .position(latLng)
            .title(getString(R.string.destination))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .flat(true);
    Marker markerDestination = map.addMarker(marker);

    if (this.markerDestination != null) {
      this.markerDestination.remove();
    }
    this.markerDestination = markerDestination;
  }
}
