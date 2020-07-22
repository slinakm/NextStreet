package com.example.nextstreet.ui.trips;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeBinding;
import com.example.nextstreet.databinding.FragmentDetailsBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.ui.CameraOnClickListener;
import com.example.nextstreet.ui.home.HomeFragment;
import com.example.nextstreet.ui.home.compose.ComposeFragment;
import com.example.nextstreet.ui.home.compose.ComposeViewModel;
import com.example.nextstreet.ui.home.compose.PackageSubmissionOnClickListener;
import com.example.nextstreet.utilities.DismissOnClickListener;
import com.example.nextstreet.utilities.TextObserver;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.text.MessageFormat;

public class DetailsFragment extends DialogFragment {

    private static final String TAG = DetailsFragment.class.getSimpleName();

    private FragmentDetailsBinding binding;

    private PackageRequest request;

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
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog()
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    }
}
