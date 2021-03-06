package com.example.nextstreet.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstreet.databinding.FragmentTripsBinding;
import com.example.nextstreet.home.QueryResponder;
import com.example.nextstreet.home.RequestQueryCallback;
import com.example.nextstreet.models.PackageRequest;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TripsFragment extends Fragment implements QueryResponder {

  private final String TAG = TripsFragment.class.getSimpleName();

  private final List<PackageRequest> requests = new ArrayList<>();
  private FragmentTripsBinding binding;

  private RecyclerView rvPackages;
  private TripsAdapter adapter;

  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentTripsBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    rvPackages = binding.rvPackages;
    rvPackages.setLayoutManager(new LinearLayoutManager(getContext()));
    AppCompatActivity appCompatActivityOfThis = (AppCompatActivity) getActivity();
    adapter = new TripsAdapter(appCompatActivityOfThis, requests);
    rvPackages.setAdapter(adapter);

    queryMostRecentPackage();
  }

  private void queryMostRecentPackage() {
    ParseQuery<PackageRequest> query = ParseQuery.getQuery(PackageRequest.class);
    query.orderByDescending(PackageRequest.KEY_CREATEDAT);
    query.include(PackageRequest.KEY_USER);
    query.include(PackageRequest.KEY_IMAGE);
    query.include(PackageRequest.KEY_DESCRIPTION);
    query.include(PackageRequest.KEY_ORIGIN);
    query.include(PackageRequest.KEY_DRIVER);
    query.include(PackageRequest.KEY_DESTINATION);
    query.include(PackageRequest.KEY_IMAGE);
    query.include(PackageRequest.KEY_ISFULFILLED);

    ParseUser currUser = ParseUser.getCurrentUser();
    Log.d(TAG, "queryMostRecentPackage: currUser = " + currUser.getUsername());
    query.whereEqualTo(PackageRequest.KEY_USER, currUser);
    query.whereEqualTo(PackageRequest.KEY_ISDONE, true);

    query.findInBackground(new RequestQueryCallback(this, 0));
  }

  @Override
  public void respondToQuery(List<PackageRequest> requests, int requestCode) {
    for (PackageRequest request : requests) {
      Log.i(TAG, "respondToQuery: received " + request);
    }

    changeVisibilityOfBackgroundText(requests);

    this.requests.addAll(requests);
    this.adapter.notifyDataSetChanged();
  }

  private void changeVisibilityOfBackgroundText(List<PackageRequest> requests) {
    if (requests.size() == 0) {
      binding.noTripsTextView.setVisibility(View.VISIBLE);
    } else {
      binding.noTripsTextView.setVisibility(View.GONE);
    }
  }
}
