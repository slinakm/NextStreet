package com.example.nextstreet.compose;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeCurrentBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.CircularRevealDialogFragment;
import com.example.nextstreet.utilities.DismissOnClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import static com.example.nextstreet.login.SignupAbstractActivity.KEY_FIRSTNAME;
import static com.example.nextstreet.login.SignupAbstractActivity.KEY_LASTNAME;

public class ComposeCurrentFragment extends CircularRevealDialogFragment {

  private static final String TAG = ComposeHelper.class.getSimpleName();

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

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentComposeCurrentBinding.inflate(getLayoutInflater());
    request = (PackageRequest) getArguments().get(PackageRequest.class.getSimpleName());

    super.setUpOnLayoutListener(binding.getRoot(), true);

    return binding.getRoot();
  }

  // TODO: make sure to turn long click off when on this fragment

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ParseGeoPoint destGeopoint = request.getDestination();
    dest = new LatLng(destGeopoint.getLatitude(), destGeopoint.getLongitude());
    origin = new LatLng(destGeopoint.getLatitude(), destGeopoint.getLongitude());
    if (dest != null) {
      binding.destinationTextView.setText(dest.toString());
    }
    if (origin != null) {
      binding.originTextView.setText(origin.toString());
    }

    ParseUser driver = request.getDriver();
    if (driver != null && request.getBoolean(PackageRequest.KEY_ISFULFILLED)) {
      binding.driverTextView.setText(
          String.format("%s %s", driver.get(KEY_FIRSTNAME), driver.get(KEY_LASTNAME)));
    }

    setupImage();
  }

  private void setupImage() {
    binding.descriptionTextView.setText(request.getDescription());

    ParseFile image = request.getImage();
    if (image != null) {
      Glide.with(binding.getRoot())
          .load(image.getUrl())
          .transform(new RoundedCorners(getResources().getInteger(R.integer.rounded_corners)))
          .into(binding.packageImageView);
    }

    binding.ivCancel.setOnClickListener(new DismissOnClickListener(this));
  }
}
