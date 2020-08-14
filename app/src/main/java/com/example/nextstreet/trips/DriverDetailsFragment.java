package com.example.nextstreet.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ContentDetailBinding;
import com.example.nextstreet.databinding.FragmentDriverDetailsBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.DetailsMaterialCard;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

public class DriverDetailsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = DriverDetailsFragment.class.getSimpleName();

    private FragmentDriverDetailsBinding binding;
    private ContentDetailBinding layoutDriverDetails;

    private GoogleMap map;
    private PackageRequest request;

    private Marker markerOrigin;
    private Marker markerDestination;

    private void setMarkerOrigin(LatLng latLng) {

        MarkerOptions marker =
                new MarkerOptions().position(latLng).title(getActivity().getString(R.string.origin)).flat(true);
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
                        .title(getActivity().getString(R.string.destination))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .flat(true);
        Marker markerDestination = map.addMarker(marker);

        if (this.markerDestination != null) {
            this.markerDestination.remove();
        }
        this.markerDestination = markerDestination;
    }

    public static DriverDetailsFragment newInstance(PackageRequest request) {
        Bundle args = new Bundle();
        args.putParcelable(PackageRequest.class.getSimpleName(), request);

        DriverDetailsFragment fragment = new DriverDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDriverDetailsBinding.inflate(getLayoutInflater());

        layoutDriverDetails = binding.layoutDriverDetails;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        request = (PackageRequest) getArguments().get(PackageRequest.class.getSimpleName());
        DetailsMaterialCard.setUpCard(layoutDriverDetails.card, request, getContext());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map == null) {
            Log.e(TAG, "onMapReady: Error loading map, map was null");
            return;
        }
        setMapToCurrRequest(request);
    }


    private void setMapToCurrRequest(PackageRequest request) {
        Log.i(TAG, "setMapToCurrRequest: here! ");

        ParseGeoPoint origin = request.getOrigin();
        ParseGeoPoint destination = request.getDestination();

        if (destination == null) {
            return;
        }

        Log.i(TAG, "setMapToCurrRequest: " + destination.getLatitude());

        LatLng latlngOrigin = new LatLng(origin.getLatitude(), origin.getLongitude());
        LatLng latlngDest = new LatLng(destination.getLatitude(), destination.getLongitude());

        setMarkerDestination(latlngDest);
        setMarkerOrigin(latlngOrigin);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(latlngDest);
        boundsBuilder.include(latlngOrigin);
        LatLngBounds bounds = boundsBuilder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        map.animateCamera(cu);
    }
}
