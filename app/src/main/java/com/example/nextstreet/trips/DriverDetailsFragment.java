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
import com.example.nextstreet.databinding.ContentDriverDetailsBinding;
import com.example.nextstreet.databinding.FragmentDetailsBinding;
import com.example.nextstreet.databinding.FragmentDriverDetailsBinding;
import com.example.nextstreet.databinding.FragmentDriverRequestsBinding;
import com.example.nextstreet.home.HomeFragment;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.DetailsMaterialCard;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseGeoPoint;

public class DriverDetailsFragment extends Fragment {

    private static final String TAG = DriverDetailsFragment.class.getSimpleName();

    private FragmentDriverDetailsBinding binding;
    private ContentDetailBinding layoutDriverDetails;

    private GoogleMap map;
    private PackageRequest request;

    private Marker markerOrigin;
    private Marker markerDestination;

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
        request = (PackageRequest) getArguments().get(PackageRequest.class.getSimpleName());
        DetailsMaterialCard.setUpCard(layoutDriverDetails.card, request, getContext());
    }
}
