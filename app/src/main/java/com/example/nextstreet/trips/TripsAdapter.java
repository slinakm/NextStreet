package com.example.nextstreet.trips;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ItemRequestBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.OnDoubleTapListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.text.MessageFormat;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {

  private static final String TAG = TripsAdapter.class.getSimpleName();

  private final AppCompatActivity context;
  private final List<PackageRequest> packageRequests;

  protected TripsAdapter(AppCompatActivity context, List<PackageRequest> packageRequests) {
    this.context = context;
    this.packageRequests = packageRequests;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Log.i(TAG, "onCreateViewHolder: creating new viewholder");
    ItemRequestBinding binding = ItemRequestBinding.inflate(context.getLayoutInflater(),
            parent, false);
    return new TripsAdapter.ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Log.i(TAG, "onBindViewHolder: binding");
    holder.bind(packageRequests.get(position));
  }

  @Override
  public int getItemCount() {
    Log.i(TAG, "getItemCount");
    return packageRequests.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    ItemRequestBinding binding;
    public ViewHolder(ItemRequestBinding binding) {
      super(binding.getRoot());

      this.binding = binding;
    }

    private void bind(PackageRequest request) {
      binding.descriptionTextView.setText(request.getDescription());

      ParseGeoPoint destination = request.getDestination();
      ParseGeoPoint origin = request.getOrigin();
      Preconditions.checkNotNull(destination, "Destination should not be null");
      Preconditions.checkNotNull(origin, "Origin should not be null");

      binding.originTextView.setText(new LatLng(origin.getLatitude(), origin.getLongitude()).toString());
      binding.destinationTextView.setText(new LatLng(destination.getLatitude(), destination.getLongitude()).toString());
      binding.timeTextView.setText(request.getRelativeTimeAgo());
      binding.distanceTextView.setText(
          MessageFormat.format(
              "{0} {1}",
              (int) origin.distanceInMilesTo(destination),
              context.getResources().getString(R.string.miles)));

      ParseFile image = request.getImage();
      if (image != null) {
        binding.packageImageView.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(image.getUrl())
                .transform(new RoundedCorners(context.getResources()
                        .getInteger(R.integer.rounded_corners)))
                .into(binding.packageImageView);
      } else {
        binding.packageImageView.setVisibility(View.GONE);
      }

      setupOnClickListener(request);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupOnClickListener(final PackageRequest request) {
      binding.getRoot()
          .setOnTouchListener(
              new OnDoubleTapListener(context) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                  Log.i(TAG, "onDoubleTap: double tap performed");
                  FragmentManager fm = context.getSupportFragmentManager();

                  DetailsFragment detailsFragment =
                          DetailsFragment.newInstance(request);

                  detailsFragment.show(fm, DetailsFragment.class.getSimpleName());
                }
              });
    }
  }
}
