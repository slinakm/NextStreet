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
import com.example.nextstreet.databinding.ItemDriverRequestBinding;
import com.example.nextstreet.databinding.ItemRequestBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.OnDoubleTapListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import org.xmlpull.v1.sax2.Driver;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DriverRequestsAdapter extends RecyclerView.Adapter<DriverRequestsAdapter.ViewHolder> {

    private static final String TAG = TripsAdapter.class.getSimpleName();

    private final AppCompatActivity context;
    private final List<PackageRequest> packageRequests;
    private final Set<PackageRequest> rejectedRequests;

    DriverRequestsAdapter(AppCompatActivity context, List<PackageRequest> packageRequests,
                          Set<PackageRequest> rejectedRequests) {
        this.context = context;
        this.packageRequests = packageRequests;
        this.rejectedRequests = rejectedRequests;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: creating new viewholder");
        ItemDriverRequestBinding binding =
                ItemDriverRequestBinding.inflate(context.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
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

    void addAll(List<PackageRequest> requestsToAdd) {
        this.packageRequests.addAll(requestsToAdd);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemDriverRequestBinding binding;

        public ViewHolder(ItemDriverRequestBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        private void bind(PackageRequest request) {
            binding.descriptionTextView.setText(request.getDescription());

            ParseGeoPoint destination = request.getDestination();
            ParseGeoPoint origin = request.getOrigin();
            Preconditions.checkNotNull(destination, "Destination should not be null");
            Preconditions.checkNotNull(origin, "Origin should not be null");

            binding.originTextView.setText(
                    new LatLng(origin.getLatitude(), origin.getLongitude()).toString());
            binding.destinationTextView.setText(
                    new LatLng(destination.getLatitude(), destination.getLongitude()).toString());
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
                        .transform(
                                new RoundedCorners(context.getResources().getInteger(R.integer.rounded_corners)))
                        .into(binding.packageImageView);
            } else {
                binding.packageImageView.setVisibility(View.GONE);
            }

            setupOnClickListeners(request);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setupOnClickListeners(final PackageRequest request) {
            binding
                    .getRoot()
                    .setOnTouchListener(
                            new OnDoubleTapListener(context) {
                                @Override
                                public void onDoubleTap(MotionEvent e) {
                                    Log.i(TAG, "onDoubleTap: double tap performed");
                                    FragmentManager fm = context.getSupportFragmentManager();

                                    DetailsFragment detailsFragment = DetailsFragment.newInstance(request);

                                    detailsFragment.show(fm, DetailsFragment.class.getSimpleName());
                                }
                            });
            binding.yesImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: the 'yes' image view was pressed on the request"
                            + request.getObjectId());
                    request.put(PackageRequest.KEY_ISFULFILLED, true);
                    Snackbar.make(view,
                            DriverRequestsAdapter.this.context.getResources()
                                    .getText(R.string.driver_adapter_selected),
                            Snackbar.LENGTH_LONG).show();
                }
            });

      binding.noImageView.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Log.i(TAG, "onClick: the 'no' image view was pressed on the request"
                      + request.getObjectId());
              rejectedRequests.add(request);
              int indexOfRemovedPackage = packageRequests.indexOf(request);
              packageRequests.remove(indexOfRemovedPackage);
              DriverRequestsAdapter.this.notifyItemRemoved(indexOfRemovedPackage);
            }
          });
        }
    }
}
