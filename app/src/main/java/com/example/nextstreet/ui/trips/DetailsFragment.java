package com.example.nextstreet.ui.trips;

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
import com.parse.ParseUser;

public class DetailsFragment extends DialogFragment {

    private static final String TAG = DetailsFragment.class.getSimpleName();

    private FragmentDetailsBinding binding;

    private LatLng dest;
    private LatLng origin;

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

        dest = HomeFragment.getDestination();
        origin = HomeFragment.getOrigin();
        if (dest != null) {
            TextView destinationTextView = getActivity().findViewById(R.id.destinationTextView);
            Preconditions.checkNotNull(destinationTextView);
            destinationTextView.setText(dest.toString());
        }
        if (origin != null) {
            TextView originTextView = getActivity().findViewById(R.id.originTextView);
            Preconditions.checkNotNull(originTextView);
            originTextView.setText(origin.toString());
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
