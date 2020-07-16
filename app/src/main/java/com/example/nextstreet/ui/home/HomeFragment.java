package com.example.nextstreet.ui.home;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.BuildConfig;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Google maps manipulation based on
 * https://developers.google.com/maps/documentation/android-sdk/
 * current-place-tutorial#get-the-location-of-the-android-device-and-position-the-map
 */
public class HomeFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    private static final String TAG = HomeFragment.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    protected static final int DEFAULT_ZOOM = 15;

    private static Location lastKnownLocation;
    private static LatLng destination;
    private static LatLng origin;

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private boolean locationPermissionGranted;

    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutocompleteSupportFragment autocompleteFragmentOrigin;
    private AutocompleteSupportFragment autocompleteFragmentDestination;
    private Marker markerOrigin;
    private Marker markerDestination;
    private GoogleMap map;

    protected static void setLastKnownLocation(Location lastKnownLocation) {
        HomeFragment.lastKnownLocation = lastKnownLocation;
    }

    public static LatLng getDestination() {
        return destination;
    }

    public static LatLng getOrigin() {
        origin = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        return origin;
    }

    protected void setOrigin(LatLng newPlace) {
        map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(newPlace, HomeFragment.DEFAULT_ZOOM));
        origin = newPlace;
        assert newPlace != null;
        setMarkerOrigin(newPlace);
    }

    protected void setDestination(LatLng newPlace) {
        map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(newPlace, HomeFragment.DEFAULT_ZOOM));
        destination = newPlace;
        assert newPlace != null;
        setMarkerDestination(newPlace);
    }

    private void setMarkerOrigin(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions().position(latLng).
                title(getString(R.string.origin)).flat(true);
        Marker markerOrigin = map.addMarker(marker);

        if (this.markerOrigin != null) {
            this.markerOrigin.remove();
        }
        this.markerOrigin = markerOrigin;
    }

    private void setMarkerDestination(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions().position(latLng).
                title(getString(R.string.destination)).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).
                flat(true);
        Marker markerDestination = map.addMarker(marker);

        if (this.markerDestination != null) {
            this.markerDestination.remove();
        }
        this.markerDestination = markerDestination;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Log.i(TAG, "onCreateView: " + mMapFragment);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        Places.initialize(getActivity().getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(getContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        autocompleteFragmentOrigin = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_origin);
        autocompleteFragmentOrigin.setHint(getString(R.string.origin));
        autocompleteFragmentDestination = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_destination);
        autocompleteFragmentDestination.setHint(getString(R.string.destination));

        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            map.setOnMapLongClickListener(this);

            assert autocompleteFragmentOrigin != null;
            autocompleteFragmentOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                    Place.Field.LAT_LNG));
            autocompleteFragmentOrigin.setOnPlaceSelectedListener(new
                    MapsPlaceSelectionListener(TAG, binding.getRoot(), this, true));

            assert autocompleteFragmentDestination!= null;
            autocompleteFragmentDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                    Place.Field.LAT_LNG));
            autocompleteFragmentDestination.setOnPlaceSelectedListener(new
                    MapsPlaceSelectionListener(TAG, binding.getRoot(), this, false));

            Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();

            getLocationPermission();
            updateLocationUI();
            getDeviceLocation();

        } else {
            Toast.makeText(getActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new LocationResultOnCompleteListener(TAG, map, this));
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.maps_no_permissions_err),
                        Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "getDeviceLocation: Location Permission not granted", null);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Snackbar.make(binding.getRoot(), getString(R.string.set_destination),
                Snackbar.LENGTH_SHORT).show();
        setDestination(latLng);
    }

}