package com.example.nextstreet.home;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstreet.databinding.ItemRequestBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.trips.DetailsFragment;
import com.example.nextstreet.utilities.DetailsMaterialCard;
import com.example.nextstreet.utilities.OnDoubleTapListener;

import java.util.List;

public class CurrentRequestsAdapter extends RecyclerView.Adapter<CurrentRequestsAdapter.ViewHolder> {

    private static final String TAG = CurrentRequestsAdapter.class.getSimpleName();

    private final AppCompatActivity context;
    private final List<PackageRequest> currentRequests;

    public CurrentRequestsAdapter(AppCompatActivity context, List<PackageRequest> currentRequests) {
        this.context = context;
        this.currentRequests = currentRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: creating new viewholder");
        ItemRequestBinding binding =
                ItemRequestBinding.inflate(context.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: binding");
        holder.bind(currentRequests.get(position));
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount");
        return currentRequests.size();
    }

    void clear() {
        currentRequests.clear();
        notifyDataSetChanged();
    }

    void addAll(List<PackageRequest> newPackages) {
        currentRequests.addAll(newPackages);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemRequestBinding binding;

        public ViewHolder(ItemRequestBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        private void bind(PackageRequest request) {
            DetailsMaterialCard.setUpCard(binding, request, context);

            setupOnClickListener(request);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setupOnClickListener(final PackageRequest request) {
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
    }
}
