package com.example.nextstreet.ui.home.compose;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeBinding;
import com.example.nextstreet.databinding.FragmentComposeCurrentBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.ui.CameraOnClickListener;
import com.example.nextstreet.ui.home.HomeFragment;
import com.example.nextstreet.utilities.DismissOnClickListener;
import com.example.nextstreet.utilities.TextObserver;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class ComposeCurrentFragment extends DialogFragment {

    private static final String TAG = ComposeFragment.class.getSimpleName();

    private FragmentComposeCurrentBinding binding;

    private PackageRequest request;
    private LatLng dest;
    private LatLng origin;

    public static ComposeCurrentFragment newInstance(PackageRequest request) {
        Bundle args = new Bundle();

        ComposeCurrentFragment fragment = new ComposeCurrentFragment();
        args.putParcelable(PackageRequest.class.getSimpleName(), request);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComposeCurrentBinding.inflate(getLayoutInflater());
        request = (PackageRequest) getArguments().get(PackageRequest.class.getSimpleName());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dest = HomeFragment.getDestination();
        origin = HomeFragment.getOrigin();
        if (dest != null) {
            binding.tvDestination.setText(dest.toString());
        }
        if (origin != null) {
            binding.tvOrigin.setText(origin.toString());
        }

        binding.tvDescription.setText(request.getDescription());

        ParseFile image = request.getImage();
        if (image != null) {
            Glide.with(binding.getRoot())
                    .load(image.getUrl())
                    .transform(new RoundedCorners(getResources()
                            .getInteger(R.integer.rounded_corners)))
                    .into(binding.ivPackage);
        }

        binding.ivCancel.setOnClickListener(new DismissOnClickListener(TAG, this));
    }
}
