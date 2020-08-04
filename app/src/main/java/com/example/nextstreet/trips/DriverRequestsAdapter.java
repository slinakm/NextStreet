package com.example.nextstreet.trips;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstreet.databinding.ItemDriverRequestBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.DetailsMaterialCard;
import com.example.nextstreet.utilities.DetailsMaterialCardResponder;
import com.example.nextstreet.utilities.OnDoubleTapListener;

import java.util.List;
import java.util.Set;

public class DriverRequestsAdapter extends RecyclerView.Adapter<DriverRequestsAdapter.ViewHolder> {

  private static final String TAG = TripsAdapter.class.getSimpleName();

  private final AppCompatActivity context;
  private final List<PackageRequest> packageRequests;
  private final Set<PackageRequest> rejectedRequests;
  private final View layoutDriverRequestsView;
  private final View layoutDriverDetailsView;

  DriverRequestsAdapter(
      AppCompatActivity context,
      List<PackageRequest> packageRequests,
      Set<PackageRequest> rejectedRequests,
      View layoutDriverRequestsView,
      View layoutDriverDetailsView) {
    this.context = context;
    this.packageRequests = packageRequests;
    this.rejectedRequests = rejectedRequests;
    this.layoutDriverRequestsView = layoutDriverRequestsView;
    this.layoutDriverDetailsView = layoutDriverDetailsView;
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

  void clear() {
    packageRequests.clear();
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements DetailsMaterialCardResponder {

    ItemDriverRequestBinding binding;

    public ViewHolder(ItemDriverRequestBinding binding) {
      super(binding.getRoot());

      this.binding = binding;
    }

    private void bind(PackageRequest request) {
      DetailsMaterialCard.setUpCard(binding.requestCard, request, context);
      DetailsMaterialCard.setUpButtons(binding.requestCard, request, this);
      setupOnClickListeners(request);
    }

    private static final String KEY_ISAVAILABLE = "isAvailable";

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
    }

    private void switchLayouts() {
      if (layoutDriverRequestsView.getVisibility() == View.VISIBLE) {
        layoutDriverRequestsView.setVisibility(View.GONE);
        layoutDriverDetailsView.setVisibility(View.VISIBLE);
      } else {
        layoutDriverRequestsView.setVisibility(View.VISIBLE);
        layoutDriverDetailsView.setVisibility(View.GONE);
      }
    }

    @Override
    public void respond(boolean yes, PackageRequest request) {
      if (yes) {
        switchLayouts();
      } else {
        rejectedRequests.add(request);
        int indexOfRemovedPackage = packageRequests.indexOf(request);
        packageRequests.remove(indexOfRemovedPackage);
        DriverRequestsAdapter.this.notifyItemRemoved(indexOfRemovedPackage);
      }
    }
  }
}
