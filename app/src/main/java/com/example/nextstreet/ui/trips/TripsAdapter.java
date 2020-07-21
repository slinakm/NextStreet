package com.example.nextstreet.ui.trips;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstreet.models.PackageRequest;

import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {

  private List<PackageRequest> packageRequests;

  protected TripsAdapter(List<PackageRequest> packageRequests) {
    this.packageRequests = packageRequests;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(packageRequests.get(position));
  }

  @Override
  public int getItemCount() {
    return packageRequests.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    private void bind(PackageRequest request) {}
  }
}
