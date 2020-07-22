package com.example.nextstreet.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentDetailsBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.home.HomeFragment;
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
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.text.MessageFormat;

public class DetailsFragment extends DialogFragment implements OnMapReadyCallback {

    private static final String TAG = DetailsFragment.class.getSimpleName();

    private FragmentDetailsBinding binding;

    private PackageRequest request;
    private GoogleMap map;
    private LatLng destination;
    private LatLng origin;

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

        TextView descriptionTextView = getActivity().findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(request.getDescription());

        ParseGeoPoint destination = request.getDestination();
        ParseGeoPoint origin = request.getOrigin();
        Preconditions.checkNotNull(destination, "Destination should not be null");
        Preconditions.checkNotNull(origin, "Origin should not be null");

        binding.destinationTextView.setText(new LatLng(destination.getLatitude(), destination.getLongitude()).toString());
        binding.originTextView.setText(new LatLng(origin.getLatitude(), origin.getLongitude()).toString());

        binding.timeTextView.setText(request.getRelativeTimeAgo());
        binding.distanceTextView.setText(
                MessageFormat.format(
                        "{0} {1}",
                        (int) origin.distanceInMilesTo(destination),
                        getContext().getResources().getString(R.string.miles)));

        ParseFile image = request.getImage();
        if (image != null) {
            binding.packageImageView.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(image.getUrl())
                    .transform(new RoundedCorners(getContext().getResources()
                            .getInteger(R.integer.rounded_corners)))
                    .into(binding.packageImageView);
        } else {
            binding.packageImageView.setVisibility(View.GONE);
        }

        binding.ivCancel.setOnClickListener(new DismissOnClickListener(this));
        setMapToCurrRequest();
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
            Snackbar.make(binding.getRoot(), "Error - Map was null!!", Snackbar.LENGTH_SHORT).show();
            return;
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
        origin = newPlace;
        Preconditions.checkNotNull(newPlace, "newPlace unexpectedly null");
        setMarkerOrigin(newPlace);
    }

    protected void setDestination(LatLng newPlace) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace, HomeFragment.DEFAULT_ZOOM));
        destination = newPlace;
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
